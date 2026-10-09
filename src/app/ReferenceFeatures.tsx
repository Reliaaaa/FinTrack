import { useEffect, useRef, useState } from "react"
import { useNavigate } from "react-router"
import {
  CalendarDays,
  MapPin,
  Search,
  ShieldCheck,
  Printer,
  FileText,
  Bell,
  ScanLine,
  Zap,
  Wallet,
  Check,
  ChevronLeft,
  ChevronRight,
  Trash2,
} from "lucide-react"
import {
  loadLocal,
  QuickExpense,
  type FinanceTransaction,
  type BudgetConfig,
} from "./AdvancedFeatures"
import {
  Button,
  Card,
  Field,
  Heading,
  Input,
  Progress,
  Select,
} from "./Controls"

type Account = {
  name: string
  type: string
  amount: number
}
type Notify = (text: string) => void
const money = (value: number) =>
  "Rp " + value.toLocaleString("id-ID", { maximumFractionDigits: 0 })
const dateLabel = (date: string) =>
  new Date(date + "T12:00:00").toLocaleDateString("id-ID", {
    day: "numeric",
    month: "long",
    year: "numeric",
  })

export function BudgetCalendar({
  transactions,
  config,
  period,
}: {
  transactions: FinanceTransaction[]
  config: BudgetConfig[]
  period: string
}) {
  const [month, setMonth] = useState(period)
  const [day, setDay] = useState(period + "-01")
  const [filter, setFilter] = useState("Semua")
  useEffect(() => {
    setMonth(period)
    setDay(period + "-01")
  }, [period])
  const rows = transactions.filter(
    (t) => t.amount < 0 && t.date.startsWith(month),
  )
  const allocations = config
    .filter((b) => b.month === month || (b.recurring && b.month <= month))
    .map((b) => ({
      ...b,
      used: -rows
        .filter((t) => t.category === b.name)
        .reduce((sum, t) => sum + t.amount, 0),
    }))
  const limit = allocations.reduce((sum, b) => sum + b.limit, 0)
  const used = -rows.reduce((sum, t) => sum + t.amount, 0)
  const [year, monthNumber] = month.split("-").map(Number)
  const days = new Date(year, monthNumber, 0).getDate()
  const offset = (new Date(year, monthNumber - 1, 1).getDay() + 6) % 7
  const now = new Date()
  const remainingDays =
    month ===
    `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`
      ? days - now.getDate() + 1
      : month >
          `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`
        ? days
        : 0
  const daily = rows.filter((t) => t.date === day)
  function shift(delta: number) {
    const date = new Date(year, monthNumber - 1 + delta, 1)
    const next = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}`
    setMonth(next)
    setDay(next + "-01")
  }
  return (
    <div className="mb-5 space-y-5">
      <Card className="bg-gradient-to-br from-primary/15 to-card">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <Heading>Kalender & realisasi pengeluaran</Heading>
          <div className="flex items-center gap-3">
            <Button aria-label="Bulan sebelumnya" onClick={() => shift(-1)}>
              <ChevronLeft size={16} />
            </Button>
            <span className="text-sm">
              {new Date(month + "-01T12:00:00").toLocaleDateString("id-ID", {
                month: "long",
                year: "numeric",
              })}
            </span>
            <Button aria-label="Bulan berikutnya" onClick={() => shift(1)}>
              <ChevronRight size={16} />
            </Button>
          </div>
        </div>
        <div className="my-6 grid gap-5 sm:grid-cols-3">
          {[
            ["Total pagu", money(limit)],
            ["Realisasi", money(used)],
            [
              "Pagu harian aman",
              remainingDays && limit > 0
                ? money(Math.max(0, limit - used) / remainingDays) + " / hari"
                : "Periode selesai / tanpa pagu",
            ],
          ].map(([label, value]) => (
            <div key={label}>
              <p className="text-xs text-muted-foreground">{label}</p>
              <p className="mt-2 font-display text-xl font-semibold tabular-nums">
                {value}
              </p>
            </div>
          ))}
        </div>
        <Progress value={limit ? (used / limit) * 100 : 0} />
        <p className="mt-2 text-xs text-muted-foreground">
          Sisa {money(Math.max(0, limit - used))} ·{" "}
          {
            allocations.filter((b) => b.used >= (b.limit * b.threshold) / 100)
              .length
          }{" "}
          kategori perlu perhatian
        </p>
      </Card>
      <div className="grid gap-5 xl:grid-cols-2">
        <Card>
          <div className="grid grid-cols-7 gap-1 text-center text-xs">
            {["Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"].map((d) => (
              <span className="py-2 text-muted-foreground" key={d}>
                {d}
              </span>
            ))}
            {Array.from({ length: offset }, (_, i) => (
              <span key={"blank" + i} />
            ))}
            {Array.from({ length: days }, (_, i) => {
              const date = `${month}-${String(i + 1).padStart(2, "0")}`
              const total = -rows
                .filter((t) => t.date === date)
                .reduce((sum, t) => sum + t.amount, 0)
              return (
                <Button
                  key={date}
                  aria-label={`${dateLabel(date)}, ${money(total)}`}
                  aria-pressed={date === day}
                  onClick={() => setDay(date)}
                  className={`flex-col px-1 ${
                    date === day ? "bg-primary text-primary-foreground" : ""
                  }`}
                >
                  <span>{i + 1}</span>
                  <span className="text-xs">
                    {total ? `${Math.round(total / 1000)}rb` : "—"}
                  </span>
                </Button>
              )
            })}
          </div>
        </Card>
        <Card>
          <Heading>{dateLabel(day)}</Heading>
          <p className="my-3 text-sm text-primary">
            {money(-daily.reduce((s, t) => s + t.amount, 0))} · {daily.length}{" "}
            transaksi
          </p>
          {daily.map((t) => (
            <div
              key={t.id}
              className="flex justify-between gap-3 border-b border-border py-4 text-sm"
            >
              <div>
                {t.name}
                <p className="mt-1 text-xs text-muted-foreground">
                  {t.category} · {t.account}
                </p>
              </div>
              <span className="whitespace-nowrap text-destructive">
                {money(t.amount)}
              </span>
            </div>
          ))}
          {!daily.length && (
            <p className="py-8 text-sm text-muted-foreground">
              Tidak ada pengeluaran pada tanggal ini.
            </p>
          )}
        </Card>
      </div>
      <div className="flex gap-2">
        {["Semua", "Perlu perhatian", "Aman"].map((f) => (
          <Button
            key={f}
            onClick={() => setFilter(f)}
            aria-pressed={filter === f}
            className={filter === f ? "text-primary border-primary" : ""}
          >
            {f}
          </Button>
        ))}
      </div>
      <div className="grid gap-3 sm:grid-cols-3">
        {allocations
          .filter(
            (b) =>
              filter === "Semua" ||
              (filter === "Aman"
                ? b.used < (b.limit * b.threshold) / 100
                : b.used >= (b.limit * b.threshold) / 100),
          )
          .map((b) => (
            <Card key={b.name}>
              <p className="text-sm font-semibold">{b.name}</p>
              <p
                className={`my-3 text-xs ${
                  b.used >= (b.limit * b.threshold) / 100
                    ? "text-destructive"
                    : "text-secondary"
                }`}
              >
                {Math.round((b.used / b.limit) * 100)}% terpakai · Sisa{" "}
                {money(Math.max(0, b.limit - b.used))}
              </p>
              <Progress value={(b.used / b.limit) * 100} />
              <p className="mt-3 text-xs text-muted-foreground">
                {money(b.used)} / {money(b.limit)}
              </p>
            </Card>
          ))}
      </div>
    </div>
  )
}

type Place = {
  transactionId: number
  name: string
  address: string
  area: string
}
export function SpendingLocations({
  transactions,
  notify,
}: {
  transactions: FinanceTransaction[]
  notify: Notify
}) {
  const [places, setPlaces] = useState<Place[]>(() =>
    loadLocal("fintrack-locations", []),
  )
  const [search, setSearch] = useState("")
  const [category, setCategory] = useState("Semua")
  const [sort, setSort] = useState("Terbesar")
  const [selected, setSelected] = useState("")
  const expenses = transactions.filter((t) => t.amount < 0)
  const rows = places
    .flatMap((p) => {
      const t = expenses.find((t) => t.id === p.transactionId)
      return t ? [{ ...p, transaction: t }] : []
    })
    .filter(
      (p) =>
        `${p.name} ${p.address} ${p.area}`
          .toLowerCase()
          .includes(search.toLowerCase()) &&
        (category === "Semua" || p.transaction.category === category),
    )
  rows.sort((a, b) =>
    sort === "Terbesar"
      ? a.transaction.amount - b.transaction.amount
      : b.transaction.date.localeCompare(a.transaction.date),
  )
  const hotspots = Object.entries(
    rows.reduce<Record<string, number>>(
      (all, p) => ({
        ...all,
        [p.area]: (all[p.area] || 0) - p.transaction.amount,
      }),
      {},
    ),
  ).sort((a, b) => b[1] - a[1])
  const persist = (next: Place[]) => {
    setPlaces(next)
    localStorage.setItem("fintrack-locations", JSON.stringify(next))
  }
  return (
    <div className="grid gap-5 xl:grid-cols-3">
      <div className="space-y-5 xl:col-span-2">
        <Card>
          <div className="flex items-center gap-3">
            <MapPin className="text-primary" />
            <Heading>Peta & lokasi pengeluaran</Heading>
          </div>
          <p className="mt-3 text-sm text-muted-foreground">
            Tautkan lokasi secara manual ke transaksi. Buka peta atau rute
            melalui penyedia peta eksternal; tidak ada pelacakan GPS otomatis.
          </p>
          <div className="mt-5 grid gap-3 sm:grid-cols-3">
            <Field title="Cari lokasi">
              <Input
                placeholder="Nama, alamat, wilayah"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </Field>
            <Field title="Kategori">
              <Select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
              >
                {["Semua", ...new Set(expenses.map((t) => t.category))].map(
                  (c) => (
                    <option key={c}>{c}</option>
                  ),
                )}
              </Select>
            </Field>
            <Field title="Urutkan">
              <Select value={sort} onChange={(e) => setSort(e.target.value)}>
                <option>Terbesar</option>
                <option>Terbaru</option>
              </Select>
            </Field>
          </div>
        </Card>
        {rows.map((p) => (
          <Card key={p.transactionId}>
            <div className="flex justify-between gap-3">
              <div>
                <p className="font-semibold">{p.name}</p>
                <p className="mt-2 text-xs text-muted-foreground">
                  {p.address} · {p.area}
                </p>
              </div>
              <span className="text-primary tabular-nums">
                {money(-p.transaction.amount)}
              </span>
            </div>
            <p className="my-4 text-xs text-muted-foreground">
              {p.transaction.name} · {dateLabel(p.transaction.date)} ·{" "}
              {p.transaction.category}
            </p>
            <div className="flex flex-wrap gap-2">
              <Button
                onClick={() =>
                  window.open(
                    `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(p.name + " " + p.address)}`,
                    "_blank",
                    "noopener,noreferrer",
                  )
                }
              >
                <MapPin size={15} />
                Buka peta
              </Button>
              <Button
                onClick={() =>
                  window.open(
                    `https://www.google.com/maps/dir/?api=1&destination=${encodeURIComponent(p.address)}`,
                    "_blank",
                    "noopener,noreferrer",
                  )
                }
              >
                Rute
              </Button>
              <Button
                aria-label={"Hapus lokasi " + p.name}
                onClick={() => {
                  persist(
                    places.filter((v) => v.transactionId !== p.transactionId),
                  )
                  notify("Tautan lokasi dihapus")
                }}
              >
                <Trash2 size={15} />
              </Button>
            </div>
          </Card>
        ))}
        {!rows.length && (
          <Card>
            <Search className="mb-3 text-muted-foreground" />
            <Heading>Belum ada lokasi yang cocok</Heading>
            <p className="mt-2 text-sm text-muted-foreground">
              Tambahkan lokasi transaksi melalui formulir di samping.
            </p>
          </Card>
        )}
        <Card>
          <Heading>Hotspot belanja</Heading>
          {hotspots.map(([area, total], i) => (
            <div
              key={area}
              className="flex justify-between border-b border-border py-4 text-sm"
            >
              <span>
                #{i + 1} {area}
              </span>
              <span>{money(total)}</span>
            </div>
          ))}
        </Card>
      </div>
      <div className="space-y-5">
        <Card>
          <Heading>Tautkan lokasi</Heading>
          <form
            className="mt-5 space-y-4"
            onSubmit={(e) => {
              e.preventDefault()
              const data = new FormData(e.currentTarget)
              const id = Number(selected)
              if (!expenses.some((t) => t.id === id))
                return notify("Pilih transaksi terlebih dahulu")
              const place = {
                transactionId: id,
                name: String(data.get("name")).trim(),
                address: String(data.get("address")).trim(),
                area: String(data.get("area")).trim(),
              }
              if (!place.name || !place.address || !place.area)
                return notify("Lengkapi nama, alamat, dan wilayah")
              persist([...places.filter((p) => p.transactionId !== id), place])
              e.currentTarget.reset()
              setSelected("")
              notify("Lokasi tersimpan")
            }}
          >
            <Field title="Transaksi pengeluaran">
              <Select
                required
                value={selected}
                onChange={(e) => setSelected(e.target.value)}
              >
                <option value="">Pilih transaksi</option>
                {expenses.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.name} · {money(-t.amount)}
                  </option>
                ))}
              </Select>
            </Field>
            <Field title="Nama tempat">
              <Input name="name" required />
            </Field>
            <Field title="Alamat">
              <Input name="address" required />
            </Field>
            <Field title="Wilayah / kawasan">
              <Input name="area" required placeholder="Contoh: SCBD" />
            </Field>
            <Button
              type="submit"
              className="w-full bg-primary text-primary-foreground"
            >
              Simpan lokasi
            </Button>
          </form>
        </Card>
        <Card>
          <ShieldCheck className="mb-3 text-secondary" />
          <Heading>Privasi lokasi</Heading>
          <p className="mt-3 text-xs leading-6 text-muted-foreground">
            Data lokasi disimpan di browser ini, bukan terenkripsi AES-256.
            Membuka peta akan mengirim alamat terpilih ke penyedia peta.
          </p>
          <Button
            className="mt-4"
            onClick={() => {
              if (window.confirm("Hapus semua riwayat lokasi?")) {
                persist([])
                notify("Riwayat lokasi dihapus")
              }
            }}
          >
            Hapus seluruh riwayat
          </Button>
        </Card>
      </div>
    </div>
  )
}

type Sim = {
  id: number
  name: string
  date: string
  quota: number
  remaining: number
  enabled: boolean
}
export function ControlCenter({
  accounts,
  transactions,
  config,
  record,
  notify,
}: {
  accounts: Account[]
  transactions: FinanceTransaction[]
  config: BudgetConfig[]
  record: (t: Omit<FinanceTransaction, "id">) => void
  notify: Notify
}) {
  const navigate = useNavigate()
  const [snoozed, setSnoozed] = useState<Record<string, number>>(() =>
    loadLocal("fintrack-snoozed", {}),
  )
  const [clock, setClock] = useState(Date.now())
  useEffect(() => {
    const timer = window.setInterval(() => setClock(Date.now()), 60000)
    return () => clearInterval(timer)
  }, [])
  const sims = loadLocal<Sim[]>("fintrack-reminders-sim", [])
  const settings = loadLocal("fintrack-reminders-sim-settings", {
    critical: 20,
    days: 3,
    enabled: true,
  })
  const alerts = sims.filter(
    (s) =>
      settings.enabled &&
      s.enabled &&
      (snoozed[s.id] || 0) <= clock &&
      ((s.quota > 0 && (s.remaining / s.quota) * 100 <= settings.critical) ||
        new Date(s.date + "T23:59:59").getTime() - clock <=
          settings.days * 86400000),
  )
  const month = new Date(clock).toLocaleDateString("sv-SE").slice(0, 7)
  const budgetAlerts = config
    .filter((b) => b.month === month || (b.recurring && b.month <= month))
    .map((b) => ({
      ...b,
      used: -transactions
        .filter(
          (t) =>
            t.amount < 0 && t.category === b.name && t.date.startsWith(month),
        )
        .reduce((s, t) => s + t.amount, 0),
    }))
    .filter((b) => b.used >= (b.limit * b.threshold) / 100)
  return (
    <div className="space-y-5">
      <Card>
        <div className="flex items-center gap-3">
          <Zap className="text-primary" />
          <Heading>Pusat kontrol FinTrack</Heading>
        </div>
        <p className="mt-2 text-sm text-muted-foreground">
          Catat cepat dan tinjau peringatan tanpa berpindah halaman. Panel web,
          bukan bilah notifikasi perangkat.
        </p>
        <div className="mt-5 grid grid-cols-3 gap-3">
          {[
            ["Catat instan", "/instan", Zap],
            ["Pindai struk", "/struk", ScanLine],
            ["Kuota & SIM", "/sim", Bell],
          ].map(([title, path, Icon]) => {
            const Symbol = Icon as typeof Bell
            return (
              <Button
                key={String(path)}
                className="flex-col py-5"
                onClick={() => navigate(String(path))}
              >
                <Symbol size={22} />
                {String(title)}
              </Button>
            )
          })}
        </div>
      </Card>
      <div className="grid gap-5 xl:grid-cols-2">
        <QuickExpense accounts={accounts} record={record} notify={notify} />
        <div className="space-y-4">
          <Card>
            <Heading>Peringatan kuota & masa aktif</Heading>
            {alerts.map((s) => (
              <div
                key={s.id}
                className="mt-4 rounded-xl border border-destructive/30 bg-destructive/5 p-4"
              >
                <p className="font-semibold">{s.name}</p>
                <p className="my-3 text-xs text-muted-foreground">
                  {s.remaining} GB / {s.quota} GB · Berakhir {dateLabel(s.date)}
                </p>
                <Progress value={s.quota ? (s.remaining / s.quota) * 100 : 0} />
                <div className="mt-4 flex gap-2">
                  <Button onClick={() => navigate("/sim")}>
                    Detail & isi ulang manual
                  </Button>
                  <Button
                    onClick={() => {
                      const next = { ...snoozed, [s.id]: Date.now() + 7200000 }
                      setSnoozed(next)
                      localStorage.setItem(
                        "fintrack-snoozed",
                        JSON.stringify(next),
                      )
                      notify("Peringatan ditunda 2 jam")
                    }}
                  >
                    Nanti (2 jam)
                  </Button>
                </div>
              </div>
            ))}
            {!alerts.length && (
              <p className="mt-4 text-sm text-muted-foreground">
                Tidak ada peringatan aktif. Atur SIM dan ambang batas di Kuota &
                SIM.
              </p>
            )}
          </Card>
          <Card>
            <Heading>Smart Budget</Heading>
            {budgetAlerts.map((b) => (
              <p key={b.name} className="mt-3 text-sm text-destructive">
                {b.name}: {money(b.used)} / {money(b.limit)}
              </p>
            ))}
            {!budgetAlerts.length && (
              <p className="mt-3 text-sm text-secondary">
                Belum ada kategori melewati ambang peringatan bulan ini.
              </p>
            )}
            <Button className="mt-4" onClick={() => navigate("/anggaran")}>
              Tinjau anggaran
            </Button>
          </Card>
        </div>
      </div>
    </div>
  )
}

export function BrokerAccount({
  add,
  notify,
}: {
  add: (account: Account) => boolean
  notify: Notify
}) {
  const [broker, setBroker] = useState("Stockbit")
  const [cash, setCash] = useState(0)
  const [stocks, setStocks] = useState(0)
  const [buyFee, setBuyFee] = useState(0.15)
  const [sellFee, setSellFee] = useState(0.25)
  const [dividend, setDividend] = useState(0)
  return (
    <Card className="mt-5">
      <div className="flex items-center gap-3">
        <Wallet className="text-primary" />
        <Heading>Tambah rekening sekuritas & RDN</Heading>
      </div>
      <p className="mt-3 text-sm text-muted-foreground">
        Konfigurasi portofolio manual. Tidak menghubungkan rekening broker atau
        memverifikasi SID/KSEI.
      </p>
      <form
        className="mt-5 space-y-5"
        onSubmit={(e) => {
          e.preventDefault()
          const data = new FormData(e.currentTarget)
          const name = String(data.get("label")).trim()
          if (
            !name ||
            ![cash, stocks, buyFee, sellFee, dividend].every(
              (v) => Number.isFinite(v) && v >= 0,
            ) ||
            buyFee > 100 ||
            sellFee > 100
          )
            return notify("Periksa nama dan nilai konfigurasi")
          if (!add({ name, type: "Investasi", amount: cash + stocks })) return
          const configs = loadLocal<Record<string, unknown>>(
            "fintrack-brokers",
            {},
          )
          localStorage.setItem(
            "fintrack-brokers",
            JSON.stringify({
              ...configs,
              [name]: {
                broker,
                bank: data.get("bank"),
                cash,
                stocks,
                buyFee,
                sellFee,
                dividend,
              },
            }),
          )
          e.currentTarget.reset()
          setCash(0)
          setStocks(0)
          setDividend(0)
        }}
      >
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          <Field title="Perusahaan sekuritas">
            <Select value={broker} onChange={(e) => setBroker(e.target.value)}>
              {[
                "Stockbit",
                "Ajaib",
                "Mirae Asset",
                "IPOT",
                "Bibit Plus",
                "Mandiri Sekuritas",
              ].map((b) => (
                <option key={b}>{b}</option>
              ))}
            </Select>
          </Field>
          <Field title="Bank RDN">
            <Select name="bank">
              {["BCA", "Mandiri", "BRI", "CIMB Niaga", "Permata"].map((b) => (
                <option key={b}>{b}</option>
              ))}
            </Select>
          </Field>
          <Field title="Label portofolio unik">
            <Input name="label" required placeholder="Portofolio saham utama" />
          </Field>
          <Field title="Saldo kas RDN (Rp)">
            <Input
              type="number"
              min={0}
              step={1}
              required
              value={cash}
              onChange={(e) => setCash(Number(e.target.value))}
            />
          </Field>
          <Field title="Nilai saham manual (Rp)">
            <Input
              type="number"
              min={0}
              step={1}
              required
              value={stocks}
              onChange={(e) => setStocks(Number(e.target.value))}
            />
          </Field>
          <Field title="Dividen bruto estimasi (Rp)">
            <Input
              type="number"
              min={0}
              step={1}
              value={dividend}
              onChange={(e) => setDividend(Number(e.target.value))}
            />
          </Field>
          <Field title="Fee beli (%)">
            <Input
              type="number"
              min={0}
              max={100}
              step={0.01}
              value={buyFee}
              onChange={(e) => setBuyFee(Number(e.target.value))}
            />
          </Field>
          <Field title="Fee jual termasuk PPh (%)">
            <Input
              type="number"
              min={0}
              max={100}
              step={0.01}
              value={sellFee}
              onChange={(e) => setSellFee(Number(e.target.value))}
            />
          </Field>
        </div>
        <div className="grid gap-4 rounded-xl bg-muted p-5 sm:grid-cols-3">
          <div>
            <p className="text-xs text-muted-foreground">Total valuasi</p>
            <p className="mt-2 text-xl tabular-nums">{money(cash + stocks)}</p>
          </div>
          <div>
            <p className="text-xs text-muted-foreground">Estimasi fee jual</p>
            <p className="mt-2">{money((stocks * sellFee) / 100)}</p>
          </div>
          <div>
            <p className="text-xs text-muted-foreground">
              Dividen net simulasi (pajak 10%)
            </p>
            <p className="mt-2 text-secondary">{money(dividend * 0.9)}</p>
          </div>
        </div>
        <p className="text-xs text-muted-foreground">
          Asumsi pajak hanya untuk simulasi, bukan nasihat pajak. Data tersimpan
          lokal, tanpa enkripsi khusus. Nilai saham tidak mengikuti harga pasar
          secara otomatis.
        </p>
        <Button type="submit" className="bg-primary text-primary-foreground">
          <Check size={16} />
          Simpan aset & konfigurasi
        </Button>
      </form>
    </Card>
  )
}

export function BrokerSummary({ accounts }: { accounts: Account[] }) {
  const configurations = loadLocal<Record<string, {
    broker: string
    bank: string
    cash: number
    stocks: number
    buyFee: number
    sellFee: number
    dividend: number
  }>>("fintrack-brokers", {})
  const linked = accounts.filter((a) => configurations[a.name])
  if (!linked.length) return null
  return (
    <Card className="mb-5">
      <Heading>Rekening sekuritas tercatat</Heading>
      <div className="mt-4 grid gap-3 sm:grid-cols-2">
        {linked.map((a) => {
          const c = configurations[a.name]
          return (
            <div key={a.name} className="rounded-xl bg-muted p-4">
              <p className="font-semibold">{a.name}</p>
              <p className="mt-2 text-xs text-muted-foreground">
                {c.broker} · RDN {c.bank}
              </p>
              <p className="mt-3 tabular-nums">{money(a.amount)}</p>
              <p className="mt-2 text-xs text-muted-foreground">
                Kas {money(c.cash)} · Saham manual {money(c.stocks)}
              </p>
              <p className="mt-2 text-xs text-primary">
                Fee beli {c.buyFee}% · Fee jual {c.sellFee}%
              </p>
              <p className="mt-2 text-xs text-secondary">
                Estimasi dividen net {money(c.dividend * 0.9)}
              </p>
            </div>
          )
        })}
      </div>
      <p className="mt-4 text-xs text-muted-foreground">
        Nilai awal manual, terpisah dari simulasi saham di bawah. Tidak dihitung
        ulang dari harga pasar.
      </p>
    </Card>
  )
}

export function ReportPreview({
  transactions,
  period,
  notify,
}: {
  transactions: FinanceTransaction[]
  period: string
  notify: Notify
}) {
  const [tab, setTab] = useState("Ringkasan")
  const [filter, setFilter] = useState("Semua")
  const [stage, setStage] = useState("")
  const [hash, setHash] = useState("")
  const report = useRef<HTMLDivElement>(null)
  const rows = transactions.filter((t) => t.date.startsWith(period))
  const income = rows
    .filter((t) => t.amount > 0)
    .reduce((s, t) => s + t.amount, 0)
  const expense = -rows
    .filter((t) => t.amount < 0)
    .reduce((s, t) => s + t.amount, 0)
  const categories = Object.entries(
    rows
      .filter((t) => t.amount < 0)
      .reduce<Record<string, number>>(
        (all, t) => ({
          ...all,
          [t.category]: (all[t.category] || 0) - t.amount,
        }),
        {},
      ),
  ).sort((a, b) => b[1] - a[1])
  const visibleInJournal = (t: FinanceTransaction) =>
    filter === "Semua" || (filter === "Pemasukan" ? t.amount > 0 : t.amount < 0)
  const journal = [...rows].sort((a, b) =>
    filter === "Pengeluaran terbesar"
      ? a.amount - b.amount
      : b.date.localeCompare(a.date),
  )
  useEffect(() => {
    setHash("")
  }, [transactions, period])
  async function print() {
    const popup = window.open("", "_blank")
    if (!popup)
      return notify("Izinkan pop-up untuk mencetak atau menyimpan PDF")
    try {
      setStage("Mengambil transaksi & menyusun ringkasan")
      await new Promise<void>((resolve) =>
        requestAnimationFrame(() => resolve()),
      )
      const digest = await crypto.subtle.digest(
        "SHA-256",
        new TextEncoder().encode(JSON.stringify(rows)),
      )
      const fingerprint = Array.from(new Uint8Array(digest), (v) =>
        v.toString(16).padStart(2, "0"),
      ).join("")
      setHash(fingerprint)
      setStage("Menyiapkan halaman cetak")
      popup.document.title = `FinTrack Laporan ${period}`
      const style = popup.document.createElement("style")
      style.textContent =
        "body{font-family:Arial,sans-serif} section{margin-bottom:2em} table{width:100%;border-collapse:collapse}td,th{text-align:left;border-bottom:1px solid;padding:.5em} button,.journal-empty{display:none} .report-row{display:table-row!important} .report-page{display:block!important;break-after:page} .report-page:last-child{break-after:auto}"
      popup.document.head.append(style)
      if (report.current)
        popup.document.body.append(
          popup.document.importNode(report.current, true),
        )
      const footer = popup.document.createElement("p")
      footer.textContent = `Sidik data SHA-256: ${fingerprint} · Data lokal, bukan sertifikasi digital.`
      popup.document.body.append(footer)
      popup.focus()
      popup.print()
      notify("Gunakan Simpan sebagai PDF pada dialog cetak")
    } catch {
      popup.close()
      notify(
        "Laporan gagal disiapkan. Coba lagi pada browser yang mendukung SHA-256.",
      )
    } finally {
      setStage("")
    }
  }
  return (
    <Card className="mt-5">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <div className="mb-2 flex items-center gap-2 text-xs text-primary">
            <FileText size={15} />
            PRATINJAU DOKUMEN · {period}
          </div>
          <Heading>Laporan keuangan bulanan</Heading>
        </div>
        <Button disabled={!!stage} onClick={print}>
          <Printer size={16} />
          Cetak / simpan PDF
        </Button>
      </div>
      <p className="mt-3 text-xs text-muted-foreground">
        Dihitung dari {rows.length} catatan lokal. Tanpa sertifikasi, verifikasi
        bank, sinkronisasi cloud, atau lampiran struk otomatis.
      </p>
      <div className="my-5 flex flex-wrap gap-2">
        {["Ringkasan", "Alokasi", "Jurnal"].map((t, i) => (
          <Button
            key={t}
            aria-pressed={t === tab}
            onClick={() => setTab(t)}
            className={t === tab ? "bg-primary text-primary-foreground" : ""}
          >
            Hal {i + 1}: {t}
          </Button>
        ))}
      </div>
      {stage && (
        <div role="status" className="mb-5 rounded-xl bg-muted p-4">
          <p className="text-sm">{stage}</p>
          <p className="mt-2 text-xs text-muted-foreground">
            Sidik data SHA-256 dibuat sebelum dialog cetak dibuka.
          </p>
        </div>
      )}
      <div ref={report}>
        <section
          className={`report-page ${tab === "Ringkasan" ? "" : "hidden"}`}
        >
          <Heading>FinTrack · Ringkasan {period}</Heading>
          <div className="my-5 grid gap-4 sm:grid-cols-3">
            {[
              ["Pemasukan", income],
              ["Pengeluaran", expense],
              ["Surplus / defisit", income - expense],
            ].map(([label, value]) => (
              <div key={label} className="rounded-xl bg-muted p-5">
                <p className="text-xs text-muted-foreground">{label}</p>
                <p className="mt-3 text-xl font-semibold tabular-nums">
                  {money(Number(value))}
                </p>
              </div>
            ))}
          </div>
          <p className="text-sm">
            Rasio surplus:{" "}
            {income
              ? (((income - expense) / income) * 100).toFixed(1) + "%"
              : "Tidak tersedia (tanpa pemasukan)"}
          </p>
          <p className="mt-3 text-xs text-muted-foreground">
            Panduan 50/30/20: kebutuhan maksimal {money(income * 0.5)},
            keinginan maksimal {money(income * 0.3)}, tabungan minimal{" "}
            {money(income * 0.2)}. Kategori belum diklasifikasikan otomatis.
          </p>
        </section>
        <section className={`report-page ${tab === "Alokasi" ? "" : "hidden"}`}>
          <Heading>Alokasi pengeluaran · {period}</Heading>
          {categories.map(([category, total]) => (
            <div key={category} className="my-4 rounded-xl bg-muted p-4">
              <div className="mb-3 flex justify-between text-sm">
                <span>{category}</span>
                <span>
                  {money(total)} ·{" "}
                  {expense ? ((total / expense) * 100).toFixed(1) : 0}%
                </span>
              </div>
              <Progress value={expense ? (total / expense) * 100 : 0} />
            </div>
          ))}
          {!categories.length && (
            <p className="mt-4 text-muted-foreground">
              Tidak ada pengeluaran pada periode ini.
            </p>
          )}
        </section>
        <section className={`report-page ${tab === "Jurnal" ? "" : "hidden"}`}>
          <Heading>Jurnal transaksi · {period}</Heading>
          <div className="my-4 flex flex-wrap gap-2">
            {["Semua", "Pengeluaran terbesar", "Pemasukan"].map((f) => (
              <Button
                key={f}
                onClick={() => setFilter(f)}
                aria-pressed={filter === f}
              >
                {f}
              </Button>
            ))}
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead>
                <tr>
                  {[
                    "Tanggal / ID",
                    "Transaksi",
                    "Kategori / Rekening",
                    "Jumlah",
                  ].map((h) => (
                    <th key={h} className="p-3 text-muted-foreground">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {journal.map((t) => (
                  <tr
                    key={t.id}
                    className={`report-row border-t border-border ${
                      visibleInJournal(t) ? "" : "hidden"
                    }`}
                  >
                    <td className="p-3">
                      {t.date}
                      <p className="mt-1 font-mono">TX-{t.id}</p>
                    </td>
                    <td className="p-3">
                      {t.name}
                      <p className="mt-1 text-muted-foreground">{t.note}</p>
                    </td>
                    <td className="p-3">
                      {t.category} · {t.account}
                    </td>
                    <td className="whitespace-nowrap p-3 tabular-nums">
                      {money(t.amount)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {!journal.some(visibleInJournal) && (
            <p className="journal-empty py-5 text-muted-foreground">
              Tidak ada catatan pada filter ini.
            </p>
          )}
        </section>
      </div>
      {hash && (
        <p className="mt-5 break-all font-mono text-xs text-muted-foreground">
          Sidik data SHA-256: {hash}
        </p>
      )}
    </Card>
  )
}

export default ReportPreview
