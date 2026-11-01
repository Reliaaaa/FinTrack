package id.fintrack.app.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS bill_payments (obligationId INTEGER NOT NULL, period TEXT NOT NULL, transactionId INTEGER, origin TEXT NOT NULL, PRIMARY KEY(obligationId, period))")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_bill_payments_transactionId ON bill_payments(transactionId)")
        db.execSQL("INSERT INTO bill_payments (obligationId, period, transactionId, origin) SELECT id, lastPaidMonth, NULL, 'LEGACY_MARKER' FROM obligations WHERE lastPaidMonth IS NOT NULL")
    }
}
