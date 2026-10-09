import { useState } from "react"
import { useNavigate } from "react-router"
import {
  ArrowDownLeft,
  ArrowUpRight,
  Coins,
  PieChart,
  Plus,
  ScanLine,
  Settings2,
  TrendingUp,
  Wallet,
} from "lucide-react"
import { loadLocal, type FinanceTransaction } from "./AdvancedFeatures"
import { Button, Card, Field, Heading, Input, Progress } from "./Controls"

export default function SmartWidgets({
  transactions,
  balance,
  income,
  expense,
  budgetLimit,
  budgetRemaining,
  period,
  create,
}: {
  transactions: FinanceTransaction[]
  balance: number
  income: number
  expense: number
  budgetLimit: number
  budgetRemaining: number
  period: string
  create: (type: "income" | "expense") => void
}) {
  const navigate = useNavigate()
  const [settings, setSettings] = useState(() =>
    loadLocal("fintrack-smart-widgets", {
      private: false,
      market: true,
      stocks: true,
    }),
  )
  const [editing, setEditing] = useState(false)
  const money = (value: number) =>
    settings.private
      ? "Rp ———"
      : "Rp " + value.toLocaleString("id-ID", { maximumFractionDigits: 0 })
  const monthRows = transactions.filter((t) => t.date.startsWith(period))
  const dates = monthRows.map((t) => t.date).sort()
  const latest = dates[dates.length - 1] || period + "-01"
  const anchor = new Date(latest + "T12:00:00")
  const days = Array.from({ length: 7 }, (_, i) => {
    const date = new Date(anchor)
    date.setDate(date.getDate() - 6 + i)
    const key = date.toLocaleDateString("sv-SE")
    return {
      key,
      label: date.toLocaleDateString("id-ID", { weekday: "short" }),
      total: -transactions
        .filter((t) => t.date === key && t.amount < 0)
        .reduce((s, t) => s + t.amount, 0),
    }
  })
  const max = Math.max(1, ...days.map((d) => d.total))
  const accounts = loadLocal<{
    name: string
    amount: number
  }[]>("fintrack-accounts", [])
  const brokers = loadLocal<Record<string, unknown>>("fintrack-brokers", {})
  const portfolio = accounts
    .filter((a) => brokers[a.name])
    .reduce((s, a) => s + a.amount, 0)
  const used = Math.max(0, budgetLimit - budgetRemaining)
  return (
    <div className="mb-5 space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <Heading>FinTrack Smart Cashflow</Heading>
          <p className="mt-2 text-xs text-muted-foreground">
            Widget web interaktif · Data lokal · Periode {period}
          </p>
        </div>
        <Button onClick={() => setEditing(!editing)}>
          <Settings2 size={16} />
          Kustomisasi widget
        </Button>
      </div>
      {editing && (
        <Card>
          <div className="grid gap-4 sm:grid-cols-3">
            {([
              ["private", "Sembunyikan nominal"],
              ["market", "Tampilkan pasar & valas"],
              ["stocks", "Tampilkan portofolio"],
            ] as const).map(([key, label]) => (
              <Field title={label} key={key}>
                <Input
                  type="checkbox"
                  checked={settings[key]}
                  onChange={(e) => {
                    const next = { ...settings, [key]: e.target.checked }
                    setSettings(next)
                    localStorage.setItem(
                      "fintrack-smart-widgets",
                      JSON.stringify(next),
                    )
                  }}
                />
              </Field>
            ))}
          </div>
        </Card>
      )}
      <div className="grid gap-5 lg:grid-cols-2">
        <Card className="bg-gradient-to-br from-primary/20 to-card">
          <div className="flex items-center justify-between">
            <span className="flex items-center gap-2 text-sm">
              <Wallet size={20} />
              Saldo kas aktif
            </span>
            <span className="text-xs text-secondary">Lokal</span>
          </div>
          <p className="my-5 font-display text-3xl font-bold tabular-nums">
            {money(balance)}
          </p>
          <div className="flex flex-wrap gap-5 text-sm">
            <span className="flex gap-2 text-secondary">
              <ArrowDownLeft size={16} />
              {money(income)}
            </span>
            <span className="flex gap-2 text-destructive">
              <ArrowUpRight size={16} />
              {money(expense)}
            </span>
          </div>
          <Button
            className="mt-5 bg-primary text-primary-foreground"
            onClick={() => create("expense")}
          >
            <Plus size={15} />
            Catat pengeluaran
          </Button>
        </Card>
        <Card>
          <div className="flex items-center justify-between">
            <Heading>Batas anggaran</Heading>
            <PieChart className="text-primary" />
          </div>
          <p className="my-5 text-3xl font-semibold">
            {settings.private
              ? "—"
              : budgetLimit
                ? Math.round((used / budgetLimit) * 100) + "%"
                : "Tanpa pagu"}
          </p>
          <Progress value={budgetLimit ? (used / budgetLimit) * 100 : 0} />
          <p className="mt-4 text-sm text-muted-foreground">
            Sisa {money(budgetRemaining)} dari {money(budgetLimit)}
          </p>
          <Button className="mt-4" onClick={() => navigate("/anggaran")}>
            Tinjau anggaran
          </Button>
        </Card>
        <Card>
          <Heading>Pengeluaran 7 hari terakhir</Heading>
          <p className="mt-2 text-xs text-muted-foreground">
            Hingga {latest}, berdasarkan catatan pada periode terpilih.
          </p>
          <div className="mt-5 grid grid-cols-7 gap-2">
            {days.map((d) => (
              <div key={d.key} className="text-center">
                <Progress
                  value={settings.private ? 0 : (d.total / max) * 100}
                />
                <p className="mt-2 text-xs text-muted-foreground">{d.label}</p>
                <p className="mt-1 text-xs">
                  {settings.private ? "—" : Math.round(d.total / 1000) + "rb"}
                </p>
              </div>
            ))}
          </div>
          <div className="mt-6 grid grid-cols-2 gap-2">
            <Button onClick={() => navigate("/struk")}>
              <ScanLine size={16} />
              Scan struk
            </Button>
            <Button onClick={() => create("income")}>
              <Plus size={16} />
              Pemasukan
            </Button>
          </div>
        </Card>
        {settings.market && (
          <Card>
            <Heading>Pasar & valas</Heading>
            <div className="my-5 flex items-center gap-3">
              <Coins className="text-primary" />
              <div>
                <p className="text-sm">Emas 24K · {money(1485000)} / gr</p>
                <p className="mt-2 text-sm">USD/IDR · {money(15620)}</p>
              </div>
            </div>
            <p className="text-xs text-muted-foreground">
              Harga contoh Oktober 2024, bukan spot live atau TradingView API.
            </p>
            <Button className="mt-4" onClick={() => navigate("/pasar")}>
              Buka emas & valas
            </Button>
          </Card>
        )}
        {settings.stocks && (
          <Card className="lg:col-span-2">
            <div className="flex items-center gap-3">
              <TrendingUp className="text-secondary" />
              <Heading>Saham & portofolio</Heading>
            </div>
            <p className="my-4 font-display text-2xl font-semibold">
              {money(portfolio)}
            </p>
            <p className="text-xs text-muted-foreground">
              Total valuasi awal rekening sekuritas yang dicatat manual,
              terpisah dari simulasi perdagangan. Tidak tersambung IHSG.
            </p>
            <Button className="mt-4" onClick={() => navigate("/saham")}>
              Buka saham & portofolio
            </Button>
          </Card>
        )}
      </div>
      <p className="text-xs text-muted-foreground">
        Pratinjau dalam aplikasi web. Tidak memasang widget native, auto-sync
        cloud, atau elemen layar utama perangkat.
      </p>
    </div>
  )
}
