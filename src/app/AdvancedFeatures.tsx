import { useState, type ReactNode } from "react"
import {
  createReportPDF,
  ExportReady,
  PDFStatement,
  type ExportArtifact,
} from "./DocumentTools"
import {
  Plus,
  Trash2,
  Save,
  Bell,
  Check,
  Download,
  Upload,
  Target,
  Smartphone,
  CalendarDays,
  ArrowUpRight,
  ArrowDownRight,
  Zap,
  Delete,
  Settings2,
  FileText,
  TrendingUp,
} from "lucide-react"

export type FinanceTransaction = {
  id: number
  name: string
  category: string
  amount: number
  date: string
  account: string
  note?: string
}
type Account = {
  name: string
  type: string
  amount: number
}
type SharedProps = {
  accounts: Account[]
  notify: (text: string) => void
  record: (transaction: Omit<FinanceTransaction, "id">) => void
}
export type BudgetConfig = {
  name: string
  limit: number
  threshold: number
  recurring: boolean
  month: string
}
export const defaultBudgetConfig: BudgetConfig[] = [
  {
    name: "Makanan & Minuman",
    limit: 2500000,
    threshold: 80,
    recurring: true,
    month: "2024-10",
  },
  {
    name: "Belanja",
    limit: 2500000,
    threshold: 80,
    recurring: true,
    month: "2024-10",
  },
  {
    name: "Transportasi",
    limit: 1000000,
    threshold: 80,
    recurring: true,
    month: "2024-10",
  },
]
export function loadLocal<T>(key: string, fallback: T): T {
  try {
    return JSON.parse(localStorage.getItem(key) || "null") ?? fallback
  } catch {
    return fallback
  }
}
const money = (value: number) =>
  "Rp " + value.toLocaleString("id-ID", { maximumFractionDigits: 0 })
const today = () => {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`
}
const field =
  "w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm"
const primary =
  "flex items-center justify-center gap-2 rounded-lg bg-primary px-4 py-3 text-xs font-semibold text-white hover:bg-primary/85 disabled:opacity-40"
const secondary =
  "flex items-center justify-center gap-2 rounded-lg border border-border bg-muted px-4 py-3 text-xs hover:border-primary/50"
function Card({
  children,
  className = "",
}: {
  children: ReactNode
  className?: string
}) {
  return (
    <section
      className={`rounded-2xl border border-border bg-card p-5 sm:p-6 ${className}`}
    >
      {children}
    </section>
  )
}
type LabelProps = {
  title: string
  children: ReactNode
}
function Label({ title, children }: LabelProps) {
  return (
    <label className="block space-y-2 text-xs text-muted-foreground">
      <span>{title}</span>
      {children}
    </label>
  )
}
function download(data: BlobPart, filename: string, type: string) {
  const url = URL.createObjectURL(new Blob([data], { type }))
  const link = document.createElement("a")
  link.href = url
  link.download = filename
  link.click()
  window.setTimeout(() => URL.revokeObjectURL(url), 1000)
}
function Progress({ value }: { value: number }) {
  return (
    <progress
      max="100"
      value={Math.min(100, Math.max(0, value))}
      className="h-2 w-full overflow-hidden rounded-full [&::-webkit-progress-bar]:bg-muted [&::-webkit-progress-value]:rounded-full [&::-webkit-progress-value]:bg-primary [&::-moz-progress-bar]:bg-primary"
    />
  )
}

export function BudgetManager({
  config,
  transactions,
  onChange,
  notify,
}: {
  config: BudgetConfig[]
  transactions: FinanceTransaction[]
  onChange: (value: BudgetConfig[]) => void
  notify: (text: string) => void
}) {
  const [selected, setSelected] = useState(
    config[0]?.name || "Makanan & Minuman",
  )
  const current = config.find((item) => item.name === selected)
  const [limit, setLimit] = useState(current?.limit || 2500000)
  const [threshold, setThreshold] = useState(current?.threshold || 80)
  const [recurring, setRecurring] = useState(current?.recurring ?? true)
  const [month, setMonth] = useState(current?.month || "2024-10")
  const used = transactions
    .filter(
      (item) =>
        item.category === selected &&
        item.amount < 0 &&
        item.date.startsWith(month),
    )
    .reduce((sum, item) => sum - item.amount, 0)
  const total =
    config
      .filter(
        (item) =>
          item.month === month || (item.recurring && item.month <= month),
      )
      .filter((item) => item.name !== selected)
      .reduce((sum, item) => sum + item.limit, 0) + limit
  function select(name: string) {
    const item = config.find((value) => value.name === name)
    setSelected(name)
    setLimit(item?.limit || 1000000)
    setThreshold(item?.threshold || 80)
    setRecurring(item?.recurring ?? true)
    setMonth(item?.month || month)
  }
  return (
    <div className="grid gap-5 xl:grid-cols-[1.5fr_1fr]">
      <Card>
        <h2 className="font-display text-lg font-semibold">
          Atur batas anggaran
        </h2>
        <p className="mt-2 text-xs text-muted-foreground">
          Pagu, ambang peringatan, dan pengulangan per kategori.
        </p>
        <form
          onSubmit={(event) => {
            event.preventDefault()
            onChange([
              ...config.filter((item) => item.name !== selected),
              { name: selected, limit, threshold, recurring, month },
            ])
            notify("Batas anggaran berhasil disimpan")
          }}
          className="mt-6 space-y-5"
        >
          <div className="flex flex-wrap gap-2">
            {[
              "Makanan & Minuman",
              "Belanja",
              "Transportasi",
              "Tagihan & Utilitas",
              "Hiburan",
              "Lainnya",
            ].map((name) => (
              <button
                type="button"
                key={name}
                onClick={() => select(name)}
                className={`rounded-full px-3 py-2 text-xs ${
                  selected === name
                    ? "bg-primary text-white"
                    : "bg-muted text-muted-foreground"
                }`}
              >
                {name}
              </button>
            ))}
          </div>
          <Label title="Batas anggaran (Rp)">
            <input
              value={limit}
              onChange={(event) => setLimit(Number(event.target.value))}
              type="number"
              min="1"
              required
              className={`${field} font-display text-2xl`}
            />
          </Label>
          <div className="flex gap-2">
            {[1000000, 2500000, 5000000].map((value) => (
              <button
                type="button"
                key={value}
                onClick={() => setLimit(value)}
                className={secondary}
              >
                {money(value)}
              </button>
            ))}
          </div>
          <div>
            <p className="mb-3 text-xs text-muted-foreground">
              Ambang peringatan
            </p>
            <div className="flex gap-2">
              {[70, 80, 90, 100].map((value) => (
                <button
                  type="button"
                  key={value}
                  onClick={() => setThreshold(value)}
                  className={`rounded-lg px-5 py-2 text-xs ${
                    threshold === value
                      ? "bg-primary"
                      : "bg-muted text-muted-foreground"
                  }`}
                >
                  {value}%
                </button>
              ))}
            </div>
            <p className="mt-3 rounded-lg bg-primary/5 p-3 text-xs text-[#b4c5ff]">
              Peringatan mulai muncul pada {money((limit * threshold) / 100)}.
            </p>
          </div>
          <Label title="Periode anggaran">
            <input
              type="month"
              value={month}
              onChange={(event) => setMonth(event.target.value)}
              required
              className={field}
            />
          </Label>
          <label className="flex items-center justify-between rounded-lg bg-muted p-4 text-xs">
            <span>Ulangi setiap bulan mulai periode ini</span>
            <input
              type="checkbox"
              checked={recurring}
              onChange={(event) => setRecurring(event.target.checked)}
              className="size-4 accent-primary"
            />
          </label>
          <button className={`${primary} w-full`}>
            <Save size={15} />
            Simpan batas anggaran
          </button>
          {current && (
            <button
              type="button"
              onClick={() => {
                onChange(config.filter((item) => item.name !== selected))
                notify("Batas kategori dihapus")
              }}
              className="flex w-full justify-center gap-2 py-2 text-xs text-destructive"
            >
              <Trash2 size={14} />
              Hapus batas kategori
            </button>
          )}
        </form>
      </Card>
      <div className="space-y-5">
        <Card>
          <p className="text-xs text-muted-foreground">
            Simulasi total anggaran bulanan
          </p>
          <h3 className="mt-4 font-display text-3xl font-bold">
            {money(total)}
          </h3>
          <p className="mt-4 text-xs leading-6 text-muted-foreground">
            Dampak pengaturan kategori terhadap total pagu pada periode {month}.
          </p>
        </Card>
        <Card>
          <h3 className="font-display font-semibold">Pengeluaran tercatat</h3>
          <p className="mt-4 text-2xl font-semibold">{money(used)}</p>
          <div className="mt-5">
            <Progress value={limit > 0 ? (used / limit) * 100 : 0} />
          </div>
          <p
            className={`mt-4 text-xs ${
              used >= (limit * threshold) / 100
                ? "text-amber-300"
                : "text-secondary"
            }`}
          >
            {used >= limit
              ? "Batas anggaran terlampaui"
              : used >= (limit * threshold) / 100
                ? "Pengeluaran mendekati batas"
                : "Masih dalam batas anggaran"}
          </p>
          <p className="mt-3 text-[10px] leading-5 text-muted-foreground">
            Berdasarkan transaksi aktual yang tersimpan; saldo dan pengeluaran
            contoh dashboard tidak termasuk.
          </p>
        </Card>
      </div>
    </div>
  )
}

export function QuickExpense({ accounts, record, notify }: SharedProps) {
  const [amount, setAmount] = useState("")
  const [category, setCategory] = useState("Makanan & Minuman")
  const [account, setAccount] = useState(
    () =>
      loadLocal("fintrack-profile", { primaryAccount: accounts[0]?.name || "" })
        .primaryAccount,
  )
  const [note, setNote] = useState("")
  return (
    <div className="mx-auto max-w-xl">
      <Card>
        <span className="flex items-center gap-2 text-xs font-semibold tracking-wider text-secondary">
          <Zap size={17} />
          QUICK EXPENSE
        </span>
        <h2 className="mt-3 font-display text-xl font-semibold">
          Catat transaksi instan
        </h2>
        <div className="my-7">
          <Label title="Nominal pengeluaran">
            <input
              aria-label="Nominal transaksi instan"
              type="text"
              inputMode="numeric"
              value={amount}
              onChange={(event) =>
                setAmount(event.target.value.replace(/\D/g, "").slice(0, 12))
              }
              placeholder="0"
              className={`${field} py-5 font-display text-4xl font-bold`}
            />
          </Label>
          <p className="mt-2 text-xs text-muted-foreground">
            {money(Number(amount))}
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          {[
            "Makanan & Minuman",
            "Transportasi",
            "Belanja",
            "Hiburan",
            "Lainnya",
          ].map((value) => (
            <button
              key={value}
              onClick={() => setCategory(value)}
              className={`rounded-full px-3 py-2 text-xs ${
                category === value
                  ? "bg-primary"
                  : "bg-muted text-muted-foreground"
              }`}
            >
              {value}
            </button>
          ))}
        </div>
        <div className="mt-5 grid gap-4 sm:grid-cols-2">
          <Label title="Sumber dana">
            <select
              className={field}
              value={account}
              onChange={(event) => setAccount(event.target.value)}
            >
              {accounts.map((item, index) => (
                <option key={index}>{item.name}</option>
              ))}
            </select>
          </Label>
          <Label title="Catatan singkat">
            <input
              value={note}
              onChange={(event) => setNote(event.target.value)}
              placeholder="Contoh: Kopi pagi"
              className={field}
            />
          </Label>
        </div>
        <div className="my-6 grid grid-cols-3 gap-2">
          {["1", "2", "3", "4", "5", "6", "7", "8", "9", "000", "0", "⌫"].map(
            (key) => (
              <button
                key={key}
                aria-label={
                  key === "⌫" ? "Hapus digit terakhir" : `Masukkan ${key}`
                }
                onClick={() =>
                  setAmount(
                    key === "⌫"
                      ? amount.slice(0, -1)
                      : (amount + key).replace(/^0+/, "").slice(0, 12),
                  )
                }
                className="flex h-14 items-center justify-center rounded-xl bg-muted text-xl font-medium hover:bg-accent"
              >
                {key === "⌫" ? <Delete size={21} /> : key}
              </button>
            ),
          )}
        </div>
        <button
          disabled={
            !Number(amount) || !accounts.some((item) => item.name === account)
          }
          onClick={() => {
            record({
              name: note.trim() || category,
              amount: -Number(amount),
              category,
              date: today(),
              account,
              note,
            })
            setAmount("")
            setNote("")
            notify("Pengeluaran instan tersimpan")
          }}
          className={`${primary} w-full`}
        >
          <Check size={16} />
          Simpan pengeluaran
        </button>
      </Card>
    </div>
  )
}

type Goal = {
  id: number
  name: string
  target: number
  saved: number
  date: string
  account: string
  category: string
}
export function SavingsGoals({ accounts, notify }: SharedProps) {
  const [goals, setGoals] = useState<Goal[]>(() =>
    loadLocal("fintrack-goals", []),
  )
  const [adding, setAdding] = useState(false)
  const [contributing, setContributing] = useState<number | null>(null)
  const persist = (next: Goal[]) => {
    setGoals(next)
    localStorage.setItem("fintrack-goals", JSON.stringify(next))
  }
  return (
    <div className="space-y-5">
      <div className="grid gap-5 sm:grid-cols-3">
        <Card>
          <p className="text-xs text-muted-foreground">Total target impian</p>
          <h3 className="mt-3 text-2xl font-semibold">
            {money(goals.reduce((sum, item) => sum + item.target, 0))}
          </h3>
        </Card>
        <Card>
          <p className="text-xs text-muted-foreground">
            Dana dialokasikan (manual)
          </p>
          <h3 className="mt-3 text-2xl font-semibold text-secondary">
            {money(goals.reduce((sum, item) => sum + item.saved, 0))}
          </h3>
        </Card>
        <button
          className={`${primary} min-h-28 rounded-2xl`}
          onClick={() => setAdding(!adding)}
        >
          <Plus size={19} />
          Target barang impian
        </button>
      </div>
      {adding && (
        <Card>
          <h2 className="mb-5 font-display font-semibold">
            Target barang impian
          </h2>
          <form
            onSubmit={(event) => {
              event.preventDefault()
              const data = new FormData(event.currentTarget)
              const target = Number(data.get("target"))
              const saved = Number(data.get("saved"))
              if (saved > target) {
                notify("Dana awal tidak boleh melebihi target")
                return
              }
              persist([
                ...goals,
                {
                  id: Date.now(),
                  name: String(data.get("name")),
                  target,
                  saved,
                  date: String(data.get("date")),
                  account: String(data.get("account")),
                  category: String(data.get("category")),
                },
              ])
              setAdding(false)
              notify("Target impian berhasil dibuat")
            }}
            className="grid gap-4 sm:grid-cols-2"
          >
            <Label title="Nama barang impian">
              <input
                name="name"
                required
                placeholder="Contoh: MacBook Pro"
                className={field}
              />
            </Label>
            <Label title="Kategori">
              <select name="category" className={field}>
                <option>Gadget</option>
                <option>Liburan</option>
                <option>Kendaraan</option>
                <option>Dana darurat</option>
                <option>Lainnya</option>
              </select>
            </Label>
            <Label title="Harga target (Rp)">
              <input
                type="number"
                name="target"
                min="1"
                required
                className={field}
              />
            </Label>
            <Label title="Dana sudah terkumpul (Rp)">
              <input
                type="number"
                name="saved"
                min="0"
                defaultValue="0"
                required
                className={field}
              />
            </Label>
            <Label title="Target tanggal">
              <input
                type="date"
                name="date"
                min={today()}
                required
                className={field}
              />
            </Label>
            <Label title="Rekening alokasi">
              <select name="account" className={field}>
                {accounts.map((item, index) => (
                  <option key={index}>{item.name}</option>
                ))}
              </select>
            </Label>
            <button className={primary}>
              <Save size={15} />
              Simpan target
            </button>
          </form>
        </Card>
      )}
      {!goals.length && !adding && (
        <Card className="py-14 text-center">
          <Target size={38} className="mx-auto text-primary" />
          <h3 className="mt-4 font-display text-lg font-semibold">
            Wujudkan impian, satu langkah setiap hari.
          </h3>
          <p className="mt-3 text-xs text-muted-foreground">
            Buat target dan pantau dana yang kamu alokasikan.
          </p>
        </Card>
      )}
      <div className="grid gap-5 md:grid-cols-2">
        {goals.map((goal) => {
          const remaining = Math.max(0, goal.target - goal.saved)
          const months = Math.max(
            1,
            Math.ceil(
              (new Date(goal.date + "T12:00:00").getTime() - Date.now()) /
                (30 * 86400000),
            ),
          )
          return (
            <Card key={goal.id}>
              <div className="flex justify-between">
                <span className="rounded-xl bg-primary/10 p-3 text-primary">
                  <Target size={24} />
                </span>
                <button
                  aria-label={`Hapus target ${goal.name}`}
                  onClick={() => {
                    if (window.confirm(`Hapus target ${goal.name}?`))
                      persist(goals.filter((item) => item.id !== goal.id))
                  }}
                  className="text-muted-foreground"
                >
                  <Trash2 size={17} />
                </button>
              </div>
              <p className="mt-4 text-[10px] tracking-wider text-primary">
                {goal.category.toUpperCase()}
              </p>
              <h3 className="mt-2 font-display text-xl font-semibold">
                {goal.name}
              </h3>
              <div className="mt-5">
                <Progress value={(goal.saved / goal.target) * 100} />
              </div>
              <div className="mt-3 flex justify-between text-xs">
                <span className="text-secondary">{money(goal.saved)}</span>
                <span className="text-muted-foreground">
                  dari {money(goal.target)}
                </span>
              </div>
              <p className="mt-5 text-xs text-muted-foreground">
                Target {goal.date} · {goal.account}
              </p>
              <p className="mt-3 rounded-lg bg-muted p-3 text-xs">
                {remaining
                  ? `Alokasi ideal ${money(Math.ceil(remaining / months))} / bulan`
                  : "Selamat! Target impian tercapai."}
              </p>
              <button
                onClick={() =>
                  setContributing(contributing === goal.id ? null : goal.id)
                }
                className={`${secondary} mt-4 w-full`}
                disabled={!remaining}
              >
                <Plus size={15} />
                Tambah alokasi dana
              </button>
              {contributing === goal.id && (
                <form
                  className="mt-3 flex gap-2"
                  onSubmit={(event) => {
                    event.preventDefault()
                    const value = Number(
                      new FormData(event.currentTarget).get("amount"),
                    )
                    persist(
                      goals.map((item) =>
                        item.id === goal.id
                          ? { ...item, saved: item.saved + value }
                          : item,
                      ),
                    )
                    setContributing(null)
                    notify("Alokasi dana diperbarui (tanpa mendebit rekening)")
                  }}
                >
                  <input
                    name="amount"
                    aria-label="Jumlah alokasi dana"
                    type="number"
                    min="1"
                    max={remaining}
                    required
                    className={field}
                  />
                  <button className={primary}>Simpan</button>
                </form>
              )}
            </Card>
          )
        })}
      </div>
      <p className="text-xs leading-6 text-muted-foreground">
        Target dan alokasi adalah pencatatan manual; tidak memindahkan atau
        mendebit dana rekening.
      </p>
    </div>
  )
}

type Reminder = {
  id: number
  name: string
  price: number
  date: string
  cycle: string
  account: string
  enabled: boolean
  auto: boolean
  quota: number
  remaining: number
  phone: string
  trial: boolean
  lastPaid?: string
}
export function Reminders({
  mode,
  accounts,
  notify,
  record,
}: SharedProps & { mode: "sim" | "subscriptions" }) {
  const key = "fintrack-reminders-" + mode
  const [items, setItems] = useState<Reminder[]>(() => loadLocal(key, []))
  const [editor, setEditor] = useState<number | "new" | null>(null)
  const [filter, setFilter] = useState("Semua")
  const [settings, setSettings] = useState(() =>
    loadLocal(key + "-settings", {
      days: 3,
      critical: 20,
      time: "09:00",
      enabled: true,
    }),
  )
  const persist = (next: Reminder[]) => {
    setItems(next)
    localStorage.setItem(key, JSON.stringify(next))
  }
  const editing = items.find((item) => item.id === editor)
  const monthly = items.reduce(
    (sum, item) =>
      sum +
      item.price *
        (item.cycle === "Tahunan"
          ? 1 / 12
          : item.cycle === "Mingguan"
            ? 52 / 12
            : 1),
    0,
  )
  const filtered = items.filter(
    (item) =>
      filter === "Semua" ||
      (filter === "Segera jatuh tempo"
        ? new Date(item.date + "T12:00:00").getTime() - Date.now() <=
          settings.days * 86400000
        : item.trial),
  )
  function calendar() {
    const escape = (text: string) =>
      text
        .replace(/\\/g, "\\\\")
        .replace(/\n/g, "\\n")
        .replace(/,/g, "\\,")
        .replace(/;/g, "\\;")
    const body = items
      .map(
        (item) =>
          `BEGIN:VEVENT\r\nUID:${mode}-${item.id}@fintrack.local\r\nDTSTAMP:${new Date().toISOString().replace(/[-:]/g, "").split(".")[0]}Z\r\nDTSTART;VALUE=DATE:${item.date.replace(/-/g, "")}\r\nSUMMARY:${escape(item.name + " · " + money(item.price))}\r\nDESCRIPTION:${escape("Pengingat FinTrack. Pembayaran manual melalui " + item.account)}\r\nRRULE:FREQ=${
            item.cycle === "Tahunan"
              ? "YEARLY"
              : item.cycle === "Mingguan"
                ? "WEEKLY"
                : "MONTHLY"
          }\r\nEND:VEVENT`,
      )
      .join("\r\n")
    download(
      `BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//FinTrack//Reminders//ID\r\n${body}\r\nEND:VCALENDAR`,
      "FinTrack-pengingat.ics",
      "text/calendar",
    )
    notify("Kalender diunduh. Impor ke aplikasi kalender untuk pengingat.")
  }
  return (
    <div className="space-y-5">
      <div className="grid gap-5 md:grid-cols-[1.5fr_1fr]">
        <Card className="bg-gradient-to-br from-[#203c78] to-card">
          <span className="text-xs tracking-wider text-[#b4c5ff]">
            {mode === "sim" ? "PENGELUARAN KUOTA & SIM" : "KOMITMEN LANGGANAN"}
          </span>
          <h2 className="mt-4 font-display text-4xl font-bold">
            {money(monthly)}
            <span className="ml-2 text-sm font-normal text-muted-foreground">
              / bulan
            </span>
          </h2>
          <p className="mt-3 text-xs text-muted-foreground">
            {items.length} {mode === "sim" ? "kartu SIM" : "langganan"} tercatat
            · Estimasi biaya bulanan
          </p>
          <button
            onClick={() => setEditor(editor === "new" ? null : "new")}
            className={`${primary} mt-6`}
          >
            <Plus size={16} />
            {mode === "sim" ? "Tambah kartu SIM" : "Tambah langganan"}
          </button>
        </Card>
        <Card>
          <h3 className="flex items-center gap-2 font-display font-semibold">
            <Bell size={18} className="text-secondary" />
            Pengingat personal
          </h3>
          <p className="mt-4 text-xs leading-6 text-muted-foreground">
            Jadwal ditampilkan dalam aplikasi saat dibuka. Notifikasi push,
            auto-debit, pembelian paket, dan sinkronisasi operator/bank belum
            terhubung.
          </p>
          <button
            onClick={calendar}
            disabled={!items.length}
            className={`${secondary} mt-5 w-full`}
          >
            <CalendarDays size={15} />
            Ekspor ke kalender (.ics)
          </button>
        </Card>
      </div>
      {editor !== null && (
        <Card>
          <h3 className="mb-5 font-display font-semibold">
            {editing ? "Edit" : "Tambah"}{" "}
            {mode === "sim" ? "kartu SIM & siklus kuota" : "langganan"}
          </h3>
          <form
            key={String(editor)}
            className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3"
            onSubmit={(event) => {
              event.preventDefault()
              const data = new FormData(event.currentTarget)
              const quota = Number(data.get("quota") || 0)
              const remaining = Number(data.get("remaining") || 0)
              if (remaining > quota) {
                notify("Sisa kuota tidak boleh lebih besar dari total kuota")
                return
              }
              const item: Reminder = {
                id: editing?.id || Date.now(),
                name: String(data.get("name")),
                price: Number(data.get("price")),
                date: String(data.get("date")),
                cycle: String(data.get("cycle")),
                account: String(data.get("account")),
                enabled: data.get("enabled") === "on",
                auto: data.get("auto") === "on",
                quota,
                remaining,
                phone: String(data.get("phone") || ""),
                trial: data.get("trial") === "on",
                lastPaid: editing?.lastPaid,
              }
              persist(
                editing
                  ? items.map((value) => (value.id === item.id ? item : value))
                  : [...items, item],
              )
              setEditor(null)
              notify("Jadwal pengingat tersimpan")
            }}
          >
            <Label
              title={mode === "sim" ? "Operator / nama SIM" : "Nama layanan"}
            >
              <input
                name="name"
                required
                defaultValue={editing?.name}
                placeholder={mode === "sim" ? "Telkomsel SIM 1" : "Netflix"}
                className={field}
              />
            </Label>
            <Label title="Biaya (Rp)">
              <input
                name="price"
                type="number"
                min="0"
                required
                defaultValue={editing?.price}
                className={field}
              />
            </Label>
            <Label
              title={
                mode === "sim"
                  ? "Tanggal reset / jatuh tempo"
                  : "Jatuh tempo berikutnya"
              }
            >
              <input
                name="date"
                type="date"
                required
                defaultValue={editing?.date || today()}
                className={field}
              />
            </Label>
            <Label title="Siklus">
              <select
                name="cycle"
                defaultValue={editing?.cycle || "Bulanan"}
                className={field}
              >
                <option>Bulanan</option>
                <option>Mingguan</option>
                <option>Tahunan</option>
              </select>
            </Label>
            <Label title="Rekening pembayaran">
              <select
                name="account"
                defaultValue={editing?.account}
                className={field}
              >
                {accounts.map((item, index) => (
                  <option key={index}>{item.name}</option>
                ))}
              </select>
            </Label>
            {mode === "sim" ? (
              <>
                <Label title="Nomor telepon">
                  <input
                    name="phone"
                    type="tel"
                    defaultValue={editing?.phone}
                    className={field}
                  />
                </Label>
                <Label title="Total kuota (GB)">
                  <input
                    type="number"
                    min="0"
                    step=".1"
                    name="quota"
                    defaultValue={editing?.quota || 0}
                    className={field}
                  />
                </Label>
                <Label title="Sisa kuota (GB)">
                  <input
                    type="number"
                    min="0"
                    step=".1"
                    name="remaining"
                    defaultValue={editing?.remaining || 0}
                    className={field}
                  />
                </Label>
              </>
            ) : (
              <label className="flex items-center gap-3 text-xs">
                <input
                  name="trial"
                  type="checkbox"
                  defaultChecked={editing?.trial}
                  className="accent-primary"
                />
                Masa percobaan / trial
              </label>
            )}
            <label className="flex items-center gap-3 text-xs">
              <input
                name="enabled"
                type="checkbox"
                defaultChecked={editing?.enabled ?? true}
                className="accent-primary"
              />
              Tampilkan pengingat
            </label>
            <label className="flex items-center gap-3 text-xs">
              <input
                name="auto"
                type="checkbox"
                defaultChecked={editing?.auto}
                className="accent-primary"
              />
              Tandai pembayaran auto-debit (informasi saja)
            </label>
            <button className={primary}>
              <Save size={15} />
              Simpan jadwal
            </button>
            <button
              type="button"
              onClick={() => setEditor(null)}
              className={secondary}
            >
              Batal
            </button>
          </form>
        </Card>
      )}
      <div className="flex flex-wrap gap-2">
        {[
          "Semua",
          "Segera jatuh tempo",
          ...(mode === "subscriptions" ? ["Trial"] : []),
        ].map((value) => (
          <button
            key={value}
            onClick={() => setFilter(value)}
            className={`rounded-full px-4 py-2 text-xs ${
              filter === value ? "bg-primary" : "bg-muted text-muted-foreground"
            }`}
          >
            {value}
          </button>
        ))}
      </div>
      <div className="grid gap-5 md:grid-cols-2">
        {filtered.map((item) => {
          const days = Math.ceil(
            (new Date(item.date + "T00:00:00").getTime() -
              new Date(today() + "T00:00:00").getTime()) /
              86400000,
          )
          const critical =
            mode === "sim" &&
            item.quota > 0 &&
            (item.remaining / item.quota) * 100 <= settings.critical
          return (
            <Card key={item.id}>
              <div className="flex items-start justify-between">
                <span className="rounded-xl bg-primary/15 p-3 text-primary">
                  {mode === "sim" ? (
                    <Smartphone size={23} />
                  ) : (
                    <Bell size={23} />
                  )}
                </span>
                <label className="flex items-center gap-2 text-[10px] text-muted-foreground">
                  <input
                    aria-label={`Pengingat ${item.name}`}
                    type="checkbox"
                    checked={item.enabled}
                    onChange={(event) =>
                      persist(
                        items.map((value) =>
                          value.id === item.id
                            ? { ...value, enabled: event.target.checked }
                            : value,
                        ),
                      )
                    }
                    className="accent-primary"
                  />
                  Pengingat
                </label>
              </div>
              <h3 className="mt-4 font-display text-lg font-semibold">
                {item.name}
              </h3>
              <p className="mt-2 text-xl font-semibold">
                {money(item.price)}{" "}
                <span className="text-xs font-normal text-muted-foreground">
                  / {item.cycle.toLowerCase()}
                </span>
              </p>
              <p className="mt-3 text-xs text-muted-foreground">
                {item.date} · {item.account}
                {item.phone && " · " + item.phone}
              </p>
              {mode === "sim" && (
                <div className="mt-5">
                  <div className="mb-3 flex justify-between text-xs">
                    <span
                      className={critical ? "text-amber-300" : "text-secondary"}
                    >
                      Sisa {item.remaining} GB
                    </span>
                    <span className="text-muted-foreground">
                      dari {item.quota} GB
                    </span>
                  </div>
                  <Progress
                    value={item.quota ? (item.remaining / item.quota) * 100 : 0}
                  />
                </div>
              )}
              {item.enabled &&
                settings.enabled &&
                (days <= settings.days || critical) && (
                  <p className="mt-4 rounded-lg bg-amber-400/10 p-3 text-xs text-amber-200">
                    {critical ? "Kuota mendekati batas minimum. " : ""}
                    {days < 0
                      ? `Jadwal lewat ${Math.abs(days)} hari`
                      : days === 0
                        ? "Jatuh tempo hari ini"
                        : `Jatuh tempo dalam ${days} hari`}
                    {item.trial ? " · Trial segera berakhir" : ""}
                  </p>
                )}
              <p className="mt-3 text-[10px] text-muted-foreground">
                {item.auto
                  ? "Auto-debit ditandai; tidak dijalankan oleh FinTrack."
                  : "Pembayaran dicatat manual."}
              </p>
              <div className="mt-5 flex flex-wrap gap-2">
                <button
                  onClick={() => setEditor(item.id)}
                  className={secondary}
                >
                  <Settings2 size={14} />
                  Atur
                </button>
                <button
                  onClick={() => {
                    const nextDate = new Date(item.date + "T12:00:00")
                    if (item.cycle === "Mingguan")
                      nextDate.setDate(nextDate.getDate() + 7)
                    else if (item.cycle === "Tahunan") {
                      const originalMonth = nextDate.getMonth()
                      nextDate.setFullYear(nextDate.getFullYear() + 1)
                      if (nextDate.getMonth() !== originalMonth)
                        nextDate.setDate(0)
                    } else {
                      const originalDay = nextDate.getDate()
                      nextDate.setDate(1)
                      nextDate.setMonth(nextDate.getMonth() + 1)
                      const maxDay = new Date(
                        nextDate.getFullYear(),
                        nextDate.getMonth() + 1,
                        0,
                      ).getDate()
                      nextDate.setDate(Math.min(originalDay, maxDay))
                    }
                    const newDate = `${nextDate.getFullYear()}-${String(nextDate.getMonth() + 1).padStart(2, "0")}-${String(nextDate.getDate()).padStart(2, "0")}`
                    if (item.price > 0)
                      record({
                        name: item.name,
                        amount: -item.price,
                        category: "Tagihan & Utilitas",
                        date: today(),
                        account: item.account,
                        note: `Pembayaran periode ${item.date}`,
                      })
                    persist(
                      items.map((value) =>
                        value.id === item.id
                          ? {
                              ...value,
                              date: newDate,
                              remaining:
                                mode === "sim" ? value.quota : value.remaining,
                              lastPaid: today(),
                              trial: false,
                            }
                          : value,
                      ),
                    )
                    notify("Pembayaran tercatat; jadwal dimajukan satu siklus")
                  }}
                  disabled={
                    !accounts.some((account) => account.name === item.account)
                  }
                  className={primary}
                >
                  <Check size={14} />
                  Catat pembayaran
                </button>
                <button
                  aria-label={`Hapus ${item.name}`}
                  onClick={() => {
                    if (window.confirm(`Hapus jadwal ${item.name}?`))
                      persist(items.filter((value) => value.id !== item.id))
                  }}
                  className="p-2 text-muted-foreground hover:text-destructive"
                >
                  <Trash2 size={15} />
                </button>
              </div>
            </Card>
          )
        })}
      </div>
      {!filtered.length && (
        <Card>
          <p className="text-center text-sm text-muted-foreground">
            Belum ada jadwal yang cocok. Tambahkan pengingat pertamamu.
          </p>
        </Card>
      )}
      <Card>
        <h3 className="mb-5 font-display font-semibold">
          Siklus notifikasi kustom
        </h3>
        <div className="grid gap-4 sm:grid-cols-3">
          <Label title="Ingatkan sebelum jatuh tempo (hari)">
            <input
              type="number"
              min="0"
              max="30"
              value={settings.days}
              onChange={(event) =>
                setSettings({
                  ...settings,
                  days: Math.min(30, Math.max(0, Number(event.target.value))),
                })
              }
              className={field}
            />
          </Label>
          {mode === "sim" && (
            <Label title="Ambang kuota kritis (%)">
              <input
                type="number"
                min="0"
                max="100"
                value={settings.critical}
                onChange={(event) =>
                  setSettings({
                    ...settings,
                    critical: Math.min(
                      100,
                      Math.max(0, Number(event.target.value)),
                    ),
                  })
                }
                className={field}
              />
            </Label>
          )}
          <Label title="Waktu preferensi (metadata)">
            <input
              type="time"
              value={settings.time}
              onChange={(event) =>
                setSettings({ ...settings, time: event.target.value })
              }
              className={field}
            />
          </Label>
        </div>
        <label className="mt-5 flex items-center gap-3 text-xs">
          <input
            type="checkbox"
            checked={settings.enabled}
            onChange={(event) =>
              setSettings({ ...settings, enabled: event.target.checked })
            }
            className="accent-primary"
          />
          Aktifkan pengingat dalam aplikasi
        </label>
        <button
          onClick={() => {
            localStorage.setItem(key + "-settings", JSON.stringify(settings))
            notify("Preferensi pengingat disimpan")
          }}
          className={`${primary} mt-5`}
        >
          <Save size={15} />
          Simpan preferensi
        </button>
      </Card>
    </div>
  )
}

export function AdvancedExport({
  transactions,
  accounts,
  notify,
}: {
  transactions: FinanceTransaction[]
  accounts: Account[]
  notify: (text: string) => void
}) {
  const [format, setFormat] = useState("CSV")
  const [start, setStart] = useState("2024-10-01")
  const [end, setEnd] = useState(today())
  const [account, setAccount] = useState("Semua rekening")
  const [category, setCategory] = useState("Semua kategori")
  const [notes, setNotes] = useState(true)
  const [evaluation, setEvaluation] = useState(true)
  const [recipient, setRecipient] = useState("")
  const [busy, setBusy] = useState(false)
  const [artifact, setArtifact] = useState<ExportArtifact | null>(null)
  const filtered = transactions.filter(
    (item) =>
      item.date >= start &&
      item.date <= end &&
      (account === "Semua rekening" || item.account === account) &&
      (category === "Semua kategori" || item.category === category),
  )
  async function exportFile() {
    if (start > end) {
      notify("Tanggal awal harus sebelum tanggal akhir")
      return
    }
    setBusy(true)
    setArtifact(null)
    try {
      async function finish(blob: Blob, filename: string) {
        let hash = ""
        if (crypto.subtle) {
          const digest = await crypto.subtle.digest(
            "SHA-256",
            await blob.arrayBuffer(),
          )
          hash = Array.from(new Uint8Array(digest), (v) =>
            v.toString(16).padStart(2, "0"),
          ).join("")
        }
        setArtifact({
          blob,
          filename,
          hash,
          count: filtered.length,
          created: new Date().toISOString(),
        })
      }
      const headers = [
        "Tanggal",
        "Transaksi",
        "Kategori",
        "Rekening",
        "Jumlah",
        ...(notes ? ["Catatan"] : []),
      ]
      const rows = filtered.map((item) => [
        item.date,
        item.name,
        item.category,
        item.account,
        item.amount,
        ...(notes ? [item.note || ""] : []),
      ])
      if (format === "XLSX") {
        const { default: ExcelJS } = await import("exceljs")
        const workbook = new ExcelJS.Workbook()
        const sheet = workbook.addWorksheet("Transaksi")
        sheet.addRow(headers)
        rows.forEach((row) => sheet.addRow(row))
        sheet.getRow(1).font = { bold: true }
        sheet.columns.forEach((column) => {
          column.width = 24
        })
        sheet.getColumn(5).numFmt = "#,##0;[Red]-#,##0"
        if (evaluation) {
          const summary = workbook.addWorksheet("Evaluasi")
          const income = filtered
            .filter((item) => item.amount > 0)
            .reduce((sum, item) => sum + item.amount, 0)
          summary.addRows([
            ["Panduan 50/30/20", "Target (Rp)"],
            ["Kebutuhan 50%", income * 0.5],
            ["Keinginan 30%", income * 0.3],
            ["Tabungan 20%", income * 0.2],
          ])
        }
        const data = await workbook.xlsx.writeBuffer()
        await finish(
          new Blob([data as BlobPart], {
            type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          }),
          "FinTrack-laporan.xlsx",
        )
      } else if (format === "PDF") {
        await finish(
          await createReportPDF(filtered, start, end, notes, evaluation),
          `FinTrack-laporan-${start}-${end}.pdf`,
        )
      } else {
        const cell = (value: unknown) => {
          const text = String(value)
          return `"${(typeof value === "string" && /^\s*[=+\-@\t\r]/.test(text) ? "'" : "") + text.replace(/"/g, '""')}"`
        }
        await finish(
          new Blob(
            [
              "\uFEFF" +
                [headers, ...rows]
                  .map((row) => row.map(cell).join(","))
                  .join("\r\n"),
            ],
            { type: "text/csv;charset=utf-8" },
          ),
          "FinTrack-laporan.csv",
        )
      }
      notify(`Laporan ${format} siap dibuka, disimpan, atau dibagikan`)
    } catch {
      notify("Ekspor gagal. Silakan coba lagi.")
    } finally {
      setBusy(false)
    }
  }
  return (
    <>
      <Card className="mt-5">
        <div className="flex items-center gap-3">
          <span className="rounded-xl bg-primary/10 p-3 text-primary">
            <FileText size={24} />
          </span>
          <div>
            <h2 className="font-display font-semibold">
              Kustomisasi ekspor laporan
            </h2>
            <p className="mt-1 text-xs text-muted-foreground">
              Format, periode, rekening, dan kelengkapan data.
            </p>
          </div>
        </div>
        <div className="mt-6 grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
          <Label title="Format dokumen">
            <select
              value={format}
              onChange={(event) => setFormat(event.target.value)}
              className={field}
            >
              <option>CSV</option>
              <option>XLSX</option>
              <option>PDF</option>
            </select>
          </Label>
          <Label title="Tanggal mulai">
            <input
              type="date"
              value={start}
              onChange={(event) => setStart(event.target.value)}
              required
              className={field}
            />
          </Label>
          <Label title="Tanggal akhir">
            <input
              type="date"
              value={end}
              onChange={(event) => setEnd(event.target.value)}
              required
              className={field}
            />
          </Label>
          <Label title="Filter rekening">
            <select
              value={account}
              onChange={(event) => setAccount(event.target.value)}
              className={field}
            >
              <option>Semua rekening</option>
              {accounts.map((item, index) => (
                <option key={index}>{item.name}</option>
              ))}
            </select>
          </Label>
          <Label title="Filter kategori">
            <select
              value={category}
              onChange={(event) => setCategory(event.target.value)}
              className={field}
            >
              <option>Semua kategori</option>
              {[...new Set(transactions.map((item) => item.category))].map(
                (value) => (
                  <option key={value}>{value}</option>
                ),
              )}
            </select>
          </Label>
          <div className="space-y-3 pt-2">
            <label className="flex items-center gap-2 text-xs">
              <input
                type="checkbox"
                checked={notes}
                onChange={(event) => setNotes(event.target.checked)}
                className="accent-primary"
              />
              Sertakan catatan
            </label>
            <label className="flex items-center gap-2 text-xs">
              <input
                type="checkbox"
                checked={evaluation}
                onChange={(event) => setEvaluation(event.target.checked)}
                className="accent-primary"
              />
              Panduan 50/30/20 (PDF/XLSX)
            </label>
          </div>
        </div>
        <div className="mt-6 flex flex-wrap items-center justify-between gap-3 border-t border-border pt-5">
          <p className="text-xs text-muted-foreground">
            {filtered.length} transaksi terpilih · Foto struk tidak disertakan
          </p>
          <button
            onClick={exportFile}
            disabled={busy || !start || !end || start > end || !filtered.length}
            className={primary}
          >
            <Download size={16} />
            {busy ? "Menyiapkan berkas..." : `Ekspor ${format}`}
          </button>
        </div>
        <div className="mt-5 flex flex-wrap items-end gap-3">
          <div className="min-w-56 flex-1">
            <Label title="Kirim melalui aplikasi email (opsional)">
              <input
                type="email"
                value={recipient}
                onChange={(event) => setRecipient(event.target.value)}
                placeholder="nama@email.com"
                className={field}
              />
            </Label>
          </div>
          <button
            disabled={!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(recipient)}
            onClick={() => {
              window.location.href = `mailto:${encodeURIComponent(recipient)}?subject=${encodeURIComponent("Laporan FinTrack " + start + " — " + end)}&body=${encodeURIComponent(`Laporan keuangan periode ${start} — ${end}.\n${filtered.length} transaksi tercatat.\n\nLampirkan berkas laporan yang telah diunduh dari FinTrack.`)}`
            }}
            className={secondary}
          >
            Buka draf email
          </button>
        </div>
        <p className="mt-3 text-[10px] text-muted-foreground">
          Pengiriman email tidak otomatis; unduh berkas lalu lampirkan melalui
          aplikasi email.
        </p>
      </Card>
      {artifact && <ExportReady artifact={artifact} notify={notify} />}
    </>
  )
}

export function StatementReader({
  transactions,
  accounts,
  notify,
  importRows,
}: {
  transactions: FinanceTransaction[]
  accounts: Account[]
  notify: (text: string) => void
  importRows: (rows: Omit<FinanceTransaction, "id">[]) => void
}) {
  const [rows, setRows] = useState<Omit<FinanceTransaction, "id">[]>([])
  const [selected, setSelected] = useState<number[]>([])
  const [name, setName] = useState("")
  const [filter, setFilter] = useState("Semua")
  const [opening, setOpening] = useState(0)
  const [error, setError] = useState("")
  const duplicate = (row: Omit<FinanceTransaction, "id">) =>
    transactions.some(
      (item) =>
        item.date === row.date &&
        item.amount === row.amount &&
        item.name === row.name &&
        item.account === row.account,
    )
  async function parse(file: File) {
    setError("")
    if (file.size > 5 * 1024 * 1024) {
      setError("Maksimal ukuran CSV 5 MB.")
      return
    }
    if (!file.name.toLowerCase().endsWith(".csv")) {
      setError(
        "Gunakan panel PDF di atas untuk dokumen PDF, atau pilih CSV berformat FinTrack.",
      )
      return
    }
    const text = (await file.text()).replace(/^\uFEFF/, "")
    const table: string[][] = []
    let cell = ""
    let row: string[] = []
    let quoted = false
    for (let index = 0; index < text.length; index++) {
      const char = text[index]
      if (char === '"') {
        if (quoted && text[index + 1] === '"') {
          cell += '"'
          index++
        } else quoted = !quoted
      } else if (char === "," && !quoted) {
        row.push(cell)
        cell = ""
      } else if (char === "\n" && !quoted) {
        row.push(cell.replace(/\r$/, ""))
        if (row.some((value) => value.trim())) table.push(row)
        row = []
        cell = ""
      } else cell += char
    }
    if (cell || row.length) {
      row.push(cell.replace(/\r$/, ""))
      table.push(row)
    }
    if (quoted) {
      setError("Tanda kutip dalam CSV tidak seimbang.")
      return
    }
    const parsed = table.slice(1).map((value) => ({
      date: value[0],
      name: value[1],
      category: value[2] || "Lainnya",
      account: value[3] || accounts[0]?.name || "Kas",
      amount: Number(value[4]),
      note: value[5] || "",
    }))
    if (
      !parsed.length ||
      parsed.some(
        (value) =>
          !/^\d{4}-\d{2}-\d{2}$/.test(value.date || "") ||
          Number.isNaN(Date.parse(value.date)) ||
          new Date(value.date).toISOString().slice(0, 10) !== value.date ||
          !value.name?.trim() ||
          !Number.isFinite(value.amount) ||
          value.amount === 0,
      )
    ) {
      setError(
        "Format tidak valid. Kolom: Tanggal,Nama,Kategori,Rekening,Jumlah,Catatan (opsional).",
      )
      return
    }
    setRows(parsed)
    setName(file.name)
    const seen = new Set<string>()
    setSelected(
      parsed.flatMap((item, index) => {
        const fingerprint = JSON.stringify([
          item.date,
          item.name,
          item.amount,
          item.account,
        ])
        if (duplicate(item) || seen.has(fingerprint)) return []
        seen.add(fingerprint)
        return [index]
      }),
    )
  }
  const incoming = rows
    .filter((item) => item.amount > 0)
    .reduce((sum, item) => sum + item.amount, 0)
  const outgoing = rows
    .filter((item) => item.amount < 0)
    .reduce((sum, item) => sum - item.amount, 0)
  return (
    <div className="space-y-5">
      <PDFStatement
        accounts={accounts}
        transactions={transactions}
        importRows={importRows}
        notify={notify}
      />
      <Card>
        <h2 className="font-display font-semibold">Pembaca e-statement bank</h2>
        <p className="mt-2 text-xs leading-6 text-muted-foreground">
          Pratinjau mutasi, koreksi kategori, dan cek duplikasi sebelum impor.
          Impor CSV tersedia di bawah; pembacaan PDF lokal tersedia di panel
          atas.
        </p>
        <label className="mt-5 flex cursor-pointer items-center justify-center gap-3 rounded-xl border border-dashed border-primary/40 bg-primary/5 p-7 text-sm">
          <Upload size={23} className="text-primary" />
          {name || "Pilih e-statement CSV"}
          <input
            type="file"
            accept=".csv"
            className="hidden"
            onChange={(event) => {
              const file = event.target.files?.[0]
              if (file) void parse(file)
            }}
          />
        </label>
        {error && (
          <p role="alert" className="mt-3 text-xs text-destructive">
            {error}
          </p>
        )}
        <p className="mt-3 text-[10px] text-muted-foreground">
          Format: Tanggal,Nama,Kategori,Rekening,Jumlah,Catatan. Nilai negatif =
          pengeluaran.
        </p>
      </Card>
      {rows.length > 0 && (
        <>
          <div className="grid gap-4 sm:grid-cols-3">
            <Card>
              <Label title="Saldo awal (manual)">
                <input
                  type="number"
                  value={opening}
                  onChange={(event) => setOpening(Number(event.target.value))}
                  className={field}
                />
              </Label>
              <p className="mt-3 text-xs text-muted-foreground">
                Saldo akhir estimasi {money(opening + incoming - outgoing)}
              </p>
            </Card>
            <Card>
              <p className="text-xs text-muted-foreground">Pemasukan dokumen</p>
              <p className="mt-3 text-xl font-semibold text-secondary">
                {money(incoming)}
              </p>
            </Card>
            <Card>
              <p className="text-xs text-muted-foreground">
                Pengeluaran dokumen
              </p>
              <p className="mt-3 text-xl font-semibold text-destructive">
                {money(outgoing)}
              </p>
            </Card>
          </div>
          <Card>
            <div className="mb-5 flex flex-wrap justify-between gap-3">
              <h3 className="font-display font-semibold">
                Mutasi terurai · {rows.length}
              </h3>
              <div className="flex gap-2">
                {["Semua", "Pemasukan", "Pengeluaran", "Duplikat"].map(
                  (value) => (
                    <button
                      key={value}
                      onClick={() => setFilter(value)}
                      className={`rounded-full px-3 py-1.5 text-[10px] ${
                        filter === value
                          ? "bg-primary"
                          : "bg-muted text-muted-foreground"
                      }`}
                    >
                      {value}
                    </button>
                  ),
                )}
              </div>
            </div>
            <div className="space-y-3">
              {rows.map(
                (item, index) =>
                  (filter === "Semua" ||
                    (filter === "Pemasukan"
                      ? item.amount > 0
                      : filter === "Pengeluaran"
                        ? item.amount < 0
                        : duplicate(item))) && (
                    <div
                      key={index}
                      className="flex flex-wrap items-center gap-3 rounded-xl border border-border p-4"
                    >
                      <input
                        type="checkbox"
                        aria-label={`Pilih ${item.name}`}
                        checked={selected.includes(index)}
                        disabled={duplicate(item)}
                        onChange={(event) =>
                          setSelected(
                            event.target.checked
                              ? [...selected, index]
                              : selected.filter((value) => value !== index),
                          )
                        }
                        className="accent-primary"
                      />
                      <div className="min-w-36 flex-1">
                        <p className="text-sm">{item.name}</p>
                        <p className="mt-1 text-[10px] text-muted-foreground">
                          {item.date} · {item.account}
                          {duplicate(item) ? " · Sudah tercatat" : ""}
                        </p>
                      </div>
                      <select
                        aria-label={`Kategori ${item.name}`}
                        value={item.category}
                        onChange={(event) =>
                          setRows(
                            rows.map((value, rowIndex) =>
                              rowIndex === index
                                ? { ...value, category: event.target.value }
                                : value,
                            ),
                          )
                        }
                        className="rounded-lg border border-border bg-background px-2 py-2 text-xs"
                      >
                        {[
                          ...new Set([
                            item.category,
                            "Pemasukan",
                            "Makanan & Minuman",
                            "Belanja",
                            "Transportasi",
                            "Tagihan & Utilitas",
                            "Biaya Bank",
                            "Lainnya",
                          ]),
                        ].map((value) => (
                          <option key={value}>{value}</option>
                        ))}
                      </select>
                      <span
                        className={`text-sm font-semibold ${
                          item.amount > 0
                            ? "text-secondary"
                            : "text-destructive"
                        }`}
                      >
                        {money(item.amount)}
                      </span>
                    </div>
                  ),
              )}
            </div>
            <button
              disabled={!selected.length}
              onClick={() => {
                const chosen = rows
                  .filter((_, index) => selected.includes(index))
                  .filter((item) => !duplicate(item))
                const seen = new Set<string>()
                const unique = chosen.filter((item) => {
                  const fingerprint = JSON.stringify([
                    item.date,
                    item.name,
                    item.amount,
                    item.account,
                  ])
                  if (seen.has(fingerprint)) return false
                  seen.add(fingerprint)
                  return true
                })
                importRows(unique)
                setSelected([])
                notify(`${unique.length} transaksi diimpor tanpa duplikat`)
              }}
              className={`${primary} mt-5 w-full`}
            >
              <Check size={16} />
              Impor {selected.length} mutasi terpilih
            </button>
          </Card>
        </>
      )}
    </div>
  )
}

type Holding = {
  ticker: string
  shares: number
  cost: number
}
export function StockMarket({ notify }: { notify: (value: string) => void }) {
  const stocks = [
    { ticker: "BBCA", name: "Bank Central Asia", price: 10450, change: 1.21 },
    {
      ticker: "BBRI",
      name: "Bank Rakyat Indonesia",
      price: 4850,
      change: 0.83,
    },
    { ticker: "TLKM", name: "Telkom Indonesia", price: 2950, change: -1.67 },
    { ticker: "ASII", name: "Astra International", price: 5100, change: 0.49 },
  ]
  const [ticker, setTicker] = useState("BBCA")
  const [period, setPeriod] = useState("1M")
  const [chart, setChart] = useState("Candlestick")
  const [holdings, setHoldings] = useState<Holding[]>(() =>
    loadLocal("fintrack-stock-holdings", []),
  )
  const [order, setOrder] = useState("")
  const [lots, setLots] = useState(1)
  const stock = stocks.find((item) => item.ticker === ticker)!
  const holding = holdings.find((item) => item.ticker === ticker)
  const portfolio = holdings.reduce(
    (sum, item) =>
      sum +
      item.shares *
        (stocks.find((value) => value.ticker === item.ticker)?.price || 0),
    0,
  )
  const cost = holdings.reduce((sum, item) => sum + item.cost, 0)
  const candles = Array.from({ length: 26 }, (_, index) => {
    const seed =
      period === "1D" ? 1 : period === "1W" ? 2 : period === "1M" ? 3 : 5
    const open = 165 - index * 3 + Math.sin(index * seed) * 23
    const close = open + Math.cos(index * 1.7) * 24
    return {
      open,
      close,
      top: Math.min(open, close) - 7,
      bottom: Math.max(open, close) + 9,
    }
  })
  return (
    <div className="space-y-5">
      <div className="rounded-xl border border-amber-400/20 bg-amber-400/5 p-4 text-xs leading-6 text-amber-200">
        Pasar simulasi · Harga contoh Oktober 2024. Bukan data real-time atau
        rekomendasi investasi. Order hanya mengubah portofolio simulasi, tidak
        mengeksekusi perdagangan atau mendebit rekening.
      </div>
      <div className="grid gap-4 sm:grid-cols-3">
        <Card>
          <p className="text-xs text-muted-foreground">
            Nilai portofolio simulasi
          </p>
          <h2 className="mt-3 font-display text-2xl font-bold">
            {money(portfolio)}
          </h2>
        </Card>
        <Card>
          <p className="text-xs text-muted-foreground">
            Modal simulasi tersisa
          </p>
          <h2 className="mt-3 text-2xl font-semibold">{money(cost)}</h2>
        </Card>
        <Card>
          <p className="text-xs text-muted-foreground">
            Untung / rugi belum terealisasi
          </p>
          <h2
            className={`mt-3 text-2xl font-semibold ${
              portfolio >= cost ? "text-secondary" : "text-destructive"
            }`}
          >
            {money(portfolio - cost)}
          </h2>
        </Card>
      </div>
      <div className="grid gap-5 xl:grid-cols-[1.7fr_1fr]">
        <Card>
          <div className="flex items-start justify-between">
            <div>
              <h2 className="font-display text-xl font-bold">{ticker}</h2>
              <p className="mt-1 text-xs text-muted-foreground">
                {stock.name} · IDX
              </p>
              <p className="mt-5 text-3xl font-semibold">
                {money(stock.price)}
              </p>
            </div>
            <span
              className={
                stock.change > 0 ? "text-secondary" : "text-destructive"
              }
            >
              {stock.change > 0 ? "+" : ""}
              {stock.change}%
            </span>
          </div>
          <div className="mt-6 flex flex-wrap justify-between gap-3">
            <div className="flex gap-1">
              {["1D", "1W", "1M", "1Y"].map((value) => (
                <button
                  key={value}
                  onClick={() => setPeriod(value)}
                  className={`rounded-lg px-3 py-2 text-xs ${
                    value === period
                      ? "bg-primary"
                      : "bg-muted text-muted-foreground"
                  }`}
                >
                  {value}
                </button>
              ))}
            </div>
            <select
              aria-label="Jenis grafik saham"
              className="rounded-lg bg-muted px-3 py-2 text-xs"
              value={chart}
              onChange={(event) => setChart(event.target.value)}
            >
              <option>Candlestick</option>
              <option>Garis</option>
            </select>
          </div>
          <svg
            viewBox="0 0 600 230"
            className="mt-5 h-64 w-full"
            role="img"
            aria-label={`Grafik simulasi ${ticker} periode ${period}`}
          >
            <g stroke="#253047" strokeDasharray="3 5">
              {[30, 80, 130, 180].map((value) => (
                <path key={value} d={`M0 ${value}H600`} />
              ))}
            </g>
            {chart === "Candlestick" ? (
              candles.map((item, index) => (
                <g
                  key={index}
                  stroke={item.close <= item.open ? "#4edea3" : "#ff9c9c"}
                  fill={item.close <= item.open ? "#4edea3" : "#ff9c9c"}
                >
                  <path d={`M${index * 22 + 14} ${item.top}V${item.bottom}`} />
                  <rect
                    x={index * 22 + 8}
                    y={Math.min(item.open, item.close)}
                    width="12"
                    height={Math.max(2, Math.abs(item.open - item.close))}
                    rx="1"
                  />
                </g>
              ))
            ) : (
              <polyline
                points={candles
                  .map((item, index) => `${index * 22 + 14},${item.close}`)
                  .join(" ")}
                stroke="#7c9cff"
                strokeWidth="3"
                fill="none"
              />
            )}
          </svg>
          <div className="flex justify-between text-[10px] text-muted-foreground">
            <span>Awal periode</span>
            <span>Data ilustrasi · {period}</span>
            <span>Akhir periode</span>
          </div>
          <div className="mt-6 flex gap-3">
            <button
              onClick={() => setOrder("Beli")}
              className={`${primary} flex-1`}
            >
              <ArrowUpRight size={15} />
              Beli simulasi
            </button>
            <button
              onClick={() => setOrder("Jual")}
              className={`${secondary} flex-1`}
            >
              <ArrowDownRight size={15} />
              Jual simulasi
            </button>
          </div>
          {order && (
            <form
              className="mt-5 rounded-xl bg-background p-4"
              onSubmit={(event) => {
                event.preventDefault()
                const shares = lots * 100
                if (order === "Jual" && (!holding || holding.shares < shares)) {
                  notify("Jumlah saham simulasi tidak mencukupi")
                  return
                }
                const next =
                  order === "Beli"
                    ? holding
                      ? holdings.map((item) =>
                          item.ticker === ticker
                            ? {
                                ...item,
                                shares: item.shares + shares,
                                cost: item.cost + shares * stock.price,
                              }
                            : item,
                        )
                      : [
                          ...holdings,
                          { ticker, shares, cost: shares * stock.price },
                        ]
                    : holdings
                        .map((item) =>
                          item.ticker === ticker
                            ? {
                                ...item,
                                shares: item.shares - shares,
                                cost:
                                  (item.cost * (item.shares - shares)) /
                                  item.shares,
                              }
                            : item,
                        )
                        .filter((item) => item.shares > 0)
                setHoldings(next)
                localStorage.setItem(
                  "fintrack-stock-holdings",
                  JSON.stringify(next),
                )
                setOrder("")
                notify(`${order} simulasi ${shares} saham ${ticker} tersimpan`)
              }}
            >
              <Label title={`${order} ${ticker} · Lot (1 lot = 100 saham)`}>
                <input
                  type="number"
                  min="1"
                  step="1"
                  required
                  value={lots}
                  onChange={(event) =>
                    setLots(Math.max(1, Math.floor(Number(event.target.value))))
                  }
                  className={field}
                />
              </Label>
              <p className="my-3 text-xs text-muted-foreground">
                Nilai order {money(lots * 100 * stock.price)} · Tanpa biaya
                simulasi
              </p>
              <div className="flex gap-2">
                <button className={primary}>Konfirmasi simulasi</button>
                <button
                  type="button"
                  onClick={() => setOrder("")}
                  className={secondary}
                >
                  Batal
                </button>
              </div>
            </form>
          )}
        </Card>
        <div className="space-y-5">
          <Card>
            <h3 className="mb-5 font-display font-semibold">Pantauan pasar</h3>
            <div className="space-y-2">
              {stocks.map((item) => (
                <button
                  key={item.ticker}
                  onClick={() => {
                    setTicker(item.ticker)
                    setOrder("")
                  }}
                  className={`flex w-full items-center justify-between rounded-xl p-4 text-left ${
                    item.ticker === ticker
                      ? "bg-primary/10"
                      : "bg-background/50 hover:bg-muted"
                  }`}
                >
                  <span>
                    <strong className="text-sm">{item.ticker}</strong>
                    <span className="mt-1 block text-[10px] text-muted-foreground">
                      {item.name}
                    </span>
                  </span>
                  <span className="text-right">
                    <span className="block text-sm">{money(item.price)}</span>
                    <span
                      className={`mt-1 block text-[10px] ${
                        item.change > 0 ? "text-secondary" : "text-destructive"
                      }`}
                    >
                      {item.change}%
                    </span>
                  </span>
                </button>
              ))}
            </div>
          </Card>
          <Card>
            <h3 className="font-display font-semibold">Kepemilikan simulasi</h3>
            {holdings.length ? (
              holdings.map((item) => (
                <div
                  key={item.ticker}
                  className="mt-4 flex justify-between border-t border-border pt-4 text-xs"
                >
                  <span>{item.ticker}</span>
                  <span className="text-muted-foreground">
                    {item.shares} saham
                  </span>
                </div>
              ))
            ) : (
              <p className="mt-4 text-xs leading-6 text-muted-foreground">
                Belum ada saham. Gunakan beli simulasi untuk mencoba pencatatan
                portofolio.
              </p>
            )}
          </Card>
        </div>
      </div>
    </div>
  )
}
