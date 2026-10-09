# Spesifikasi Produk & Arsitektur Teknis: FinTrack (Personal Finance App)

## 1. Ikhtisar Produk (Product Overview)
**FinTrack** adalah aplikasi pengelola keuangan pribadi cerdas berbasis mobile (iOS & Android) yang dirancang untuk membantu pengguna melacak arus kas, menyusun anggaran bulanan, memantau tren pengeluaran, serta menyinkronkan data secara real-time ke cloud dengan keamanan biometrik tingkat tinggi.

---

## 2. Arsitektur Teknis Skala Tinggi (High-Scale Architecture)
Dirancang untuk melayani **10+ juta pengguna aktif bulanan (MAU)** dengan latensi rendah (<100ms) dan ketersediaan 99.99%:

```
[Mobile Clients (React Native)]
         │ HTTPS / WSS
         ▼
[Cloudflare CDN & WAF / API Gateway (Kong / Envoy)]
         │
    ┌────┴──────────────────────────┬─────────────────────────┐
    ▼                               ▼                         ▼
[Auth Service]              [Transaction Service]      [Budget & Analytics]
(OAuth2, JWT, Biometrics)   (Node.js / Fastify Cluster) (Go / Python Worker)
    │                               │                         │
    ├───────────┐                   ├───────────┐             │
    ▼           ▼                   ▼           ▼             ▼
[Redis Cache] [PostgreSQL Auth] [Redis Cluster] [PostgreSQL] [ClickHouse OLAP]
(Sessions)    (Read-Replicas)   (Hot data/Rate) (Master/Repl) (Grafik & Tren)
                                    │
                             [Kafka / RabbitMQ]
                                    │
                                    ▼
                         [Notification & Audit Service]
                         (FCM/APNS Push Warning)
```

- **Client**: React Native (Offline-first dengan WatermelonDB / SQLite local sync).
- **API Gateway**: Rate limiting, token verification, dynamic routing.
- **Backend Services**: Stateless Node.js (Fastify/Express) microservices dengan clustering container di Kubernetes (EKS/GKE).
- **Databases**:
  - **PostgreSQL 16**: Penyimpanan transaksional ACID (Read replicas + Sharding by User ID).
  - **ClickHouse / TimescaleDB**: Agregasi analitik tren harian dan riwayat jutaan record.
  - **Redis 7 Cluster**: Caching saldo, limit anggaran aktif, session store.
  - **Apache Kafka**: Event streaming asinkron untuk push notification anggaran & sinkronisasi data.

---

## 3. Skema Basis Data (Database Entity-Relationship Schema)

```sql
-- 1. Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    biometric_public_key TEXT,
    currency VARCHAR(3) DEFAULT 'IDR',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Categories Table
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(10) CHECK (type IN ('income', 'expense')) NOT NULL,
    icon VARCHAR(50),
    color VARCHAR(20),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. Transactions Table (Sharded / Partitioned by user_id or date)
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
    amount NUMERIC(15, 2) NOT NULL,
    type VARCHAR(10) CHECK (type IN ('income', 'expense')) NOT NULL,
    transaction_date DATE NOT NULL,
    note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
CREATE INDEX idx_transactions_user_date ON transactions (user_id, transaction_date DESC);

-- 4. Budgets Table
CREATE TABLE budgets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    amount_limit NUMERIC(15, 2) NOT NULL,
    period_month INT NOT NULL CHECK (period_month BETWEEN 1 AND 12),
    period_year INT NOT NULL,
    warning_threshold_pct INT DEFAULT 80, -- misal notifikasi saat 80% dan 100%
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(user_id, category_id, period_month, period_year)
);
```

---

## 4. Contoh Backend Endpoint Transaksi (Node.js / Express + PostgreSQL + Redis)
Endpoint berkinerja tinggi dengan validasi, database transaction, dan pembaruan cache agregat:

```javascript
// routes/transactions.js
const express = require('express');
const router = express.Router();
const { body, validationResult } = require('express-validator');
const db = require('../db'); // Pool pg
const redis = require('../redis');

// POST /api/v1/transactions
router.post(
  '/',
  [
    body('amount').isFloat({ gt: 0 }).withMessage('Nominal harus lebih besar dari 0'),
    body('type').isIn(['income', 'expense']).withMessage('Tipe harus income atau expense'),
    body('category_id').isUUID().withMessage('Category ID tidak valid'),
    body('transaction_date').isISO8601().withMessage('Format tanggal harus YYYY-MM-DD'),
  ],
  async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ success: false, errors: errors.array() });
    }

    const userId = req.user.id; // Diambil dari JWT Auth Middleware
    const { amount, type, category_id, transaction_date, note } = req.body;

    const client = await db.connect();
    try {
      await client.query('BEGIN');

      // 1. Simpan transaksi
      const insertQuery = `
        INSERT INTO transactions (user_id, category_id, amount, type, transaction_date, note)
        VALUES ($1, $2, $3, $4, $5, $6)
        RETURNING *;
      `;
      const result = await client.query(insertQuery, [
        userId, category_id, amount, type, transaction_date, note || null
      ]);
      const newTransaction = result.rows[0];

      // 2. Evaluasi Anggaran jika tipe 'expense'
      if (type === 'expense') {
        const month = new Date(transaction_date).getMonth() + 1;
        const year = new Date(transaction_date).getFullYear();

        const budgetQuery = `
          SELECT b.amount_limit, b.warning_threshold_pct,
                 COALESCE(SUM(t.amount), 0) AS current_spent
          FROM budgets b
          LEFT JOIN transactions t ON t.category_id = b.category_id 
               AND t.user_id = b.user_id 
               AND EXTRACT(MONTH FROM t.transaction_date) = $3
               AND EXTRACT(YEAR FROM t.transaction_date) = $4
          WHERE b.user_id = $1 AND b.category_id = $2 AND b.period_month = $3 AND b.period_year = $4
          GROUP BY b.amount_limit, b.warning_threshold_pct;
        `;
        const budgetRes = await client.query(budgetQuery, [userId, category_id, month, year]);
        
        if (budgetRes.rows.length > 0) {
          const { amount_limit, warning_threshold_pct, current_spent } = budgetRes.rows[0];
          const totalSpent = parseFloat(current_spent);
          const limit = parseFloat(amount_limit);
          const ratio = (totalSpent / limit) * 100;

          if (ratio >= 100) {
            // Publish event limit exceeded ke Kafka/Queue
            console.log(`[ALERT] User ${userId} melebihi limit anggaran untuk kategori ${category_id}`);
          } else if (ratio >= warning_threshold_pct) {
            console.log(`[WARNING] User ${userId} mendekati batas anggaran (${ratio.toFixed(1)}%)`);
          }
        }
      }

      await client.query('COMMIT');

      // Invalidate Redis dashboard cache
      await redis.del(`dashboard:${userId}:${new Date(transaction_date).toISOString().slice(0, 7)}`);

      return res.status(201).json({
        success: true,
        message: 'Transaksi berhasil disimpan',
        data: newTransaction,
      });
    } catch (err) {
      await client.query('ROLLBACK');
      console.error('Transaction error:', err);
      return res.status(500).json({ success: false, message: 'Internal Server Error' });
    } finally {
      client.release();
    }
  }
);

module.exports = router;
```

---

## 5. Contoh Kode Antarmuka Dashboard (React Native)
Implementasi antarmuka layar utama dengan total saldo, kartu pemasukan/pengeluaran, grafik tren pengeluaran, dan tombol aksi:

```tsx
// screens/DashboardScreen.tsx
import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Dimensions,
  SafeAreaView,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';

const screenWidth = Dimensions.get('window').width;

export default function DashboardScreen({ navigation }) {
  const [balance, setBalance] = useState(24500000);
  const [income, setIncome] = useState(15000000);
  const [expense, setExpense] = useState(6250000);

  // Contoh data titik tren harian (7 hari terakhir)
  const dailyTrends = [450000, 1200000, 300000, 850000, 950000, 1500000, 980000];
  const maxExpense = Math.max(...dailyTrends);

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.content}>
        {/* Header User */}
        <View style={styles.header}>
          <View>
            <Text style={styles.greeting}>Halo, Sarah 👋</Text>
            <Text style={styles.subGreeting}>Kelola arus kas dengan cerdas</Text>
          </View>
          <TouchableOpacity style={styles.biometricBtn}>
            <Ionicons name="finger-print" size={24} color="#2563EB" />
          </TouchableOpacity>
        </View>

        {/* Total Balance Card */}
        <View style={styles.balanceCard}>
          <Text style={styles.balanceLabel}>Total Saldo Tersedia</Text>
          <Text style={styles.balanceAmount}>Rp {balance.toLocaleString('id-ID')}</Text>
          
          <View style={styles.cashflowRow}>
            <View style={styles.cashflowItem}>
              <View style={[styles.arrowBadge, { backgroundColor: '#DCFCE7' }]}>
                <Ionicons name="arrow-down" size={16} color="#16A34A" />
              </View>
              <View>
                <Text style={styles.cashflowTitle}>Pemasukan (Bulan ini)</Text>
                <Text style={styles.incomeAmount}>+Rp {income.toLocaleString('id-ID')}</Text>
              </View>
            </View>

            <View style={styles.cashflowItem}>
              <View style={[styles.arrowBadge, { backgroundColor: '#FEE2E2' }]}>
                <Ionicons name="arrow-up" size={16} color="#DC2626" />
              </View>
              <View>
                <Text style={styles.cashflowTitle}>Pengeluaran (Bulan ini)</Text>
                <Text style={styles.expenseAmount}>-Rp {expense.toLocaleString('id-ID')}</Text>
              </View>
            </View>
          </View>
        </View>

        {/* Grafik Tren Pengeluaran Harian */}
        <View style={styles.sectionCard}>
          <View style={styles.sectionHeader}>
            <Text style={styles.sectionTitle}>Tren Pengeluaran 7 Hari</Text>
            <Text style={styles.sectionBadge}>Harian</Text>
          </View>

          <View style={styles.barChartContainer}>
            {dailyTrends.map((val, idx) => {
              const heightPercent = (val / maxExpense) * 100;
              const days = ['Sen', 'Sel', 'Rab', 'Kam', 'Jum', 'Sab', 'Min'];
              return (
                <View key={idx} style={styles.barColumn}>
                  <View style={styles.barTrack}>
                    <View style={[styles.barFill, { height: `${heightPercent}%` }]} />
                  </View>
                  <Text style={styles.dayLabel}>{days[idx]}</Text>
                </View>
              );
            })}
          </View>
        </View>

        {/* Quick Action Buttons */}
        <View style={styles.actionGrid}>
          <TouchableOpacity style={styles.actionCard} onPress={() => {}}>
            <Ionicons name="add-circle" size={28} color="#2563EB" />
            <Text style={styles.actionText}>Catat Baru</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.actionCard} onPress={() => {}}>
            <Ionicons name="pie-chart" size={28} color="#059669" />
            <Text style={styles.actionText}>Anggaran</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.actionCard} onPress={() => {}}>
            <Ionicons name="document-text" size={28} color="#7C3AED" />
            <Text style={styles.actionText}>Laporan</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.actionCard} onPress={() => {}}>
            <Ionicons name="cloud-upload" size={28} color="#D97706" />
            <Text style={styles.actionText}>Sinkronisasi</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F8FAFC' },
  content: { padding: 20 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  greeting: { fontSize: 22, fontWeight: '700', color: '#0F172A' },
  subGreeting: { fontSize: 14, color: '#64748B' },
  biometricBtn: { padding: 10, backgroundColor: '#EFF6FF', borderRadius: 12 },
  balanceCard: { backgroundColor: '#1E293B', borderRadius: 20, padding: 20, marginBottom: 20 },
  balanceLabel: { color: '#94A3B8', fontSize: 13, fontWeight: '500' },
  balanceAmount: { color: '#FFFFFF', fontSize: 30, fontWeight: '800', marginVertical: 10 },
  cashflowRow: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 14, paddingTop: 14, borderTopWidth: 1, borderColor: '#334155' },
  cashflowItem: { flexDirection: 'row', alignItems: 'center', gap: 10 },
  arrowBadge: { width: 32, height: 32, borderRadius: 16, justifyContent: 'center', alignItems: 'center' },
  cashflowTitle: { fontSize: 11, color: '#94A3B8' },
  incomeAmount: { fontSize: 14, fontWeight: '700', color: '#4ADE80' },
  expenseAmount: { fontSize: 14, fontWeight: '700', color: '#F87171' },
  sectionCard: { backgroundColor: '#FFFFFF', borderRadius: 16, padding: 18, marginBottom: 20, elevation: 2, shadowColor: '#000', shadowOpacity: 0.05, shadowRadius: 10 },
  sectionHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 },
  sectionTitle: { fontSize: 16, fontWeight: '700', color: '#1E293B' },
  sectionBadge: { fontSize: 12, color: '#2563EB', fontWeight: '600', backgroundColor: '#EFF6FF', paddingHorizontal: 8, paddingVertical: 4, borderRadius: 8 },
  barChartContainer: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-end', height: 140, paddingBottom: 6 },
  barColumn: { alignItems: 'center', flex: 1 },
  barTrack: { width: 14, height: 110, backgroundColor: '#F1F5F9', borderRadius: 7, justifyContent: 'flex-end', overflow: 'hidden' },
  barFill: { width: '100%', backgroundColor: '#3B82F6', borderRadius: 7 },
  dayLabel: { marginTop: 6, fontSize: 11, color: '#64748B' },
  actionGrid: { flexDirection: 'row', justifyContent: 'space-between' },
  actionCard: { backgroundColor: '#FFFFFF', flex: 1, marginHorizontal: 4, paddingVertical: 16, borderRadius: 14, alignItems: 'center', gap: 8, shadowColor: '#000', shadowOpacity: 0.04, shadowRadius: 8, elevation: 1 },
  actionText: { fontSize: 12, fontWeight: '600', color: '#334155' },
});
```
