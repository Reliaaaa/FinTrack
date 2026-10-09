import { useState, type ReactNode, type FormEvent } from "react"
import SmartWidgets from "./SmartWidgets"
import {
  BudgetCalendar,
  SpendingLocations,
  ControlCenter,
  BrokerAccount,
  BrokerSummary,
  ReportPreview,
} from "./ReferenceFeatures"
import {
  createBrowserRouter,
  RouterProvider,
  NavLink,
  useLocation,
  useNavigate,
} from "react-router"
import {
  Portfolio,
  ReceiptStudio,
  Profile,
  Widgets,
  Markets,
  ReportBreakdown,
} from "./Features"
import {
  BudgetManager,
  QuickExpense,
  SavingsGoals,
  Reminders,
  AdvancedExport,
  StatementReader,
  StockMarket,
  loadLocal,
  defaultBudgetConfig,
  type BudgetConfig,
} from "./AdvancedFeatures"
import {
  Activity,
  LayoutDashboard,
  ArrowLeftRight,
  ChartNoAxesCombined,
  Wallet,
  PieChart,
  Settings,
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  Plus,
  ArrowUpRight,
  ArrowDownLeft,
  Bell,
  Search,
  Download,
  Upload,
  X,
  Check,
  ShieldCheck,
  Coffee,
  ShoppingBag,
  Car,
  Utensils,
  Building2,
  MoreHorizontal,
  Eye,
  EyeOff,
  LogOut,
  CircleHelp,
  Sparkles,
  TrendingUp,
  FileText,
  ScanLine,
  Coins,
  Smartphone,
  UserRound,
  Target,
  Zap,
  CalendarDays,
  MapPin,
} from "lucide-react"

type Transaction = {
  id: number
  name: string
  category: string
  amount: number
  date: string
  account: string
  note?: string
}
const initialTransactions: Transaction[] = [
  {
    id: 1,
    name: "Gaji bulanan",
    category: "Pemasukan",
    amount: 15000000,
    date: "2024-10-24",
    account: "BCA",
  },
  {
    id: 2,
    name: "Kopi sore di Starbucks",
    category: "Makanan & Minuman",
    amount: -65000,
    date: "2024-10-24",
    account: "GoPay",
  },
  {
    id: 3,
    name: "Belanja kebutuhan bulanan",
    category: "Belanja",
    amount: -850000,
    date: "2024-10-23",
    account: "BCA",
  },
  {
    id: 4,
    name: "Grab ke kantor",
    category: "Transportasi",
    amount: -45000,
    date: "2024-10-23",
    account: "GoPay",
  },
  {
    id: 5,
    name: "Makan siang",
    category: "Makanan & Minuman",
    amount: -75000,
    date: "2024-10-22",
    account: "BCA",
  },
]
const money = (amount: number) =>
  "Rp " + Math.abs(amount).toLocaleString("id-ID")
const navigation = [
  { path: "/", label: "Ringkasan", icon: LayoutDashboard },
  { path: "/transaksi", label: "Transaksi", icon: ArrowLeftRight },
  { path: "/anggaran", label: "Anggaran", icon: PieChart },
  { path: "/rekening", label: "Rekening & Aset", icon: Wallet },
  { path: "/laporan", label: "Laporan", icon: ChartNoAxesCombined },
  { path: "/struk", label: "Struk & Koreksi OCR", icon: ScanLine },
  { path: "/pasar", label: "Emas & Valas", icon: Coins },
  { path: "/widget", label: "Widget Personal", icon: Smartphone },
  { path: "/profil", label: "Profil & Preferensi", icon: UserRound },
  { path: "/instan", label: "Transaksi Instan", icon: Zap },
  { path: "/target", label: "Target Impian", icon: Target },
  { path: "/sim", label: "Kuota & SIM", icon: Smartphone },
  { path: "/langganan", label: "Pengingat Langganan", icon: CalendarDays },
  { path: "/saham", label: "Saham & Portofolio", icon: TrendingUp },
  { path: "/statement", label: "E-Statement Bank", icon: FileText },
  { path: "/lokasi", label: "Lokasi Pengeluaran", icon: MapPin },
  { path: "/kontrol", label: "Pusat Kontrol", icon: Bell },
]
const budgetVisuals = [
  {
    name: "Makanan & Minuman",
    used: 1850000,
    limit: 2500000,
    percent: 74,
    icon: Utensils,
    color: "bg-primary",
  },
  {
    name: "Belanja",
    used: 2100000,
    limit: 2500000,
    percent: 84,
    icon: ShoppingBag,
    color: "bg-amber-400",
  },
  {
    name: "Transportasi",
    used: 650000,
    limit: 1000000,
    percent: 65,
    icon: Car,
    color: "bg-secondary",
  },
]
const fieldClass =
  "w-full rounded-lg border border-border bg-background px-3 py-3 text-sm"
function Panel({
  children,
  className = "",
}: {
  children: ReactNode
  className?: string
}) {
  return (
    <section
      className={`rounded-2xl border border-border/70 bg-card ${className}`}
    >
      {children}
    </section>
  )
}

function FinTrack() {
  const location = useLocation()
  const navigate = useNavigate()
  const [profile, setProfile] = useState(() => {
    try {
      return (
        JSON.parse(localStorage.getItem("fintrack-profile") || "null") || {
          name: "Sarah Anderson",
          nickname: "Sarah",
          primaryAccount: "BCA",
          avatar: "",
        }
      )
    } catch {
      return {
        name: "Sarah Anderson",
        nickname: "Sarah",
        primaryAccount: "BCA",
        avatar: "",
      }
    }
  })
  const current =
    navigation.find((item) => item.path === location.pathname) || navigation[0]
  const [transactions, setTransactions] = useState<Transaction[]>(() => {
    try {
      return (
        JSON.parse(localStorage.getItem("fintrack-transactions") || "null") ||
        initialTransactions
      )
    } catch {
      return initialTransactions
    }
  })
  const [accounts, setAccounts] = useState<{
    name: string
    type: string
    amount: number
  }[]>(() => {
    try {
      return (
        JSON.parse(localStorage.getItem("fintrack-accounts") || "null") || [
          { name: "BCA", type: "Rekening Bank", amount: 18500000 },
          { name: "GoPay", type: "E-Wallet", amount: 1500000 },
          { name: "Bibit", type: "Investasi", amount: 4500000 },
        ]
      )
    } catch {
      return []
    }
  })
  const [modal, setModal] = useState("")
  const [draftAmount, setDraftAmount] = useState("")
  const [draftCategory, setDraftCategory] = useState("Makanan & Minuman")
  const [draftType, setDraftType] = useState("expense")
  const [toast, setToast] = useState("")
  const [hidden, setHidden] = useState(false)
  const [search, setSearch] = useState("")
  const [filter, setFilter] = useState("Semua transaksi")
  const [period, setPeriod] = useState<string>(() =>
    loadLocal("fintrack-period", "2024-10"),
  )
  const [chartPeriod, setChartPeriod] = useState("Mingguan")
  const [notifications, setNotifications] = useState(false)
  const [file, setFile] = useState<File | null>(null)
  const [budgetConfig, setBudgetConfig] = useState<BudgetConfig[]>(() =>
    loadLocal("fintrack-budgets", defaultBudgetConfig),
  )
  const budgets = budgetConfig
    .filter(
      (item) =>
        item.month === period || (item.recurring && item.month <= period),
    )
    .map((item) => {
      const used = transactions
        .filter(
          (transaction) =>
            transaction.amount < 0 &&
            transaction.category === item.name &&
            transaction.date.startsWith(period),
        )
        .reduce((sum, transaction) => sum - transaction.amount, 0)
      const visual = budgetVisuals.find((value) => value.name === item.name)
      return {
        ...item,
        used,
        percent: Math.round((used / item.limit) * 100),
        icon: visual?.icon || PieChart,
        color: visual?.color || "bg-primary",
      }
    })
  function record(transaction: Omit<Transaction, "id">) {
    setPeriod(transaction.date.slice(0, 7))
    localStorage.setItem(
      "fintrack-period",
      JSON.stringify(transaction.date.slice(0, 7)),
    )
    setTransactions((current) => {
      const next = [{ ...transaction, id: Date.now() }, ...current]
      localStorage.setItem("fintrack-transactions", JSON.stringify(next))
      return next
    })
  }
  const notify = (message: string) => {
    setToast(message)
    window.setTimeout(() => setToast(""), 4000)
  }
  const additions = transactions.filter((item) => item.id > 5)
  const monthlyTransactions = transactions.filter((item) =>
    item.date.startsWith(period),
  )
  const income = monthlyTransactions
    .filter((item) => item.amount > 0)
    .reduce((total, item) => total + item.amount, 0)
  const expense = -monthlyTransactions
    .filter((item) => item.amount < 0)
    .reduce((total, item) => total + item.amount, 0)
  const warnedBudgets = budgets.filter((item) => item.percent >= item.threshold)
  const budgetRemaining = budgets.reduce(
    (sum, item) => sum + Math.max(0, item.limit - item.used),
    0,
  )
  const currentMonth = new Date().toISOString().slice(0, 7)
  const availableMonths = [
    ...new Set([
      "2024-10",
      "2024-09",
      "2024-08",
      currentMonth,
      ...transactions.map((item) => item.date.slice(0, 7)),
    ]),
  ]
    .sort()
    .reverse()
  const balance =
    accounts.reduce((total, item) => total + item.amount, 0) +
    additions.reduce((total, item) => total + item.amount, 0)
  const displayed = transactions.filter(
    (item) =>
      item.name.toLowerCase().includes(search.toLowerCase()) &&
      (filter === "Semua transaksi" ||
        (filter === "Pemasukan" ? item.amount > 0 : item.amount < 0)),
  )
  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    if (modal === "transaction") {
      const item = {
        id: Date.now(),
        name: String(data.get("name")),
        category: String(data.get("category")),
        amount:
          Number(data.get("amount")) *
          (data.get("type") === "expense" ? -1 : 1),
        date: String(data.get("date")),
        account: String(data.get("account")),
        note: String(data.get("note") || ""),
      }
      const next = [item, ...transactions]
      setTransactions(next)
      localStorage.setItem("fintrack-transactions", JSON.stringify(next))
      notify("Transaksi berhasil ditambahkan")
      setPeriod(item.date.slice(0, 7))
      localStorage.setItem(
        "fintrack-period",
        JSON.stringify(item.date.slice(0, 7)),
      )
    } else {
      const next = [
        ...accounts,
        {
          name: String(data.get("name")),
          type: String(data.get("type")),
          amount: Number(data.get("amount")),
        },
      ]
      setAccounts(next)
      localStorage.setItem("fintrack-accounts", JSON.stringify(next))
      notify("Rekening berhasil ditambahkan")
    }
    setModal("")
    setDraftAmount("")
  }
  function exportReport() {
    const content =
      "Tanggal,Nama,Kategori,Rekening,Jumlah,Catatan\n" +
      transactions
        .map((item) =>
          [
            item.date,
            item.name,
            item.category,
            item.account,
            item.amount,
            item.note || "",
          ]
            .map((value) => `"${String(value).replace(/"/g, '""')}"`)
            .join(","),
        )
        .join("\n")
    const url = URL.createObjectURL(
      new Blob(["\uFEFF" + content], { type: "text/csv;charset=utf-8" }),
    )
    const anchor = document.createElement("a")
    anchor.href = url
    anchor.download = "FinTrack-laporan.csv"
    anchor.click()
    URL.revokeObjectURL(url)
    notify("Laporan CSV berhasil diekspor")
  }
  const renderBudgets = () =>
    budgets.map((item) => (
      <div key={item.name} className="py-4">
        <div className="flex items-center justify-between">
          <span className="flex items-center gap-2.5 text-sm">
            <span className="rounded-lg bg-muted p-2 text-muted-foreground">
              <item.icon size={15} />
            </span>
            {item.name}
          </span>
          <span
            className={`text-xs font-medium ${
              item.percent >= item.threshold
                ? "text-amber-300"
                : "text-muted-foreground"
            }`}
          >
            {item.percent}%
          </span>
        </div>
        <progress
          max="100"
          value={Math.min(100, item.percent)}
          aria-label={`Penggunaan anggaran ${item.name}`}
          className="mt-3 h-1.5 w-full overflow-hidden rounded-full [&::-webkit-progress-bar]:bg-muted [&::-webkit-progress-value]:rounded-full [&::-webkit-progress-value]:bg-primary [&::-moz-progress-bar]:bg-primary"
        />
        <div className="mt-2 flex justify-between text-[11px]">
          <span className="text-foreground">{money(item.used)}</span>
          <span className="text-muted-foreground">
            dari {money(item.limit)}
          </span>
        </div>
      </div>
    ))
  return (
    <div className="min-h-screen">
      <aside className="fixed inset-y-0 left-0 z-30 hidden w-[232px] flex-col border-r border-border/70 bg-[#0d1629] px-5 py-6 lg:flex">
        <NavLink to="/" className="flex shrink-0 items-center gap-2.5 px-3">
          <span className="flex size-9 items-center justify-center rounded-xl bg-primary text-white">
            <Activity size={23} strokeWidth={2.6} />
          </span>
          <span className="font-display text-[23px] font-bold">
            fintrack<span className="text-secondary">.</span>
          </span>
        </NavLink>
        <div className="mt-7 shrink-0 px-3 text-[10px] font-semibold tracking-[.18em] text-muted-foreground">
          WORKSPACE
        </div>
        <nav
          aria-label="Menu utama FinTrack"
          className="mt-3 min-h-0 flex-1 space-y-1 overflow-y-auto"
        >
          {navigation.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-lg px-3 py-2.5 text-[12px] font-medium transition-colors ${
                  isActive
                    ? "bg-primary/15 text-[#92adff]"
                    : "text-muted-foreground hover:bg-muted hover:text-foreground"
                }`
              }
            >
              <item.icon size={19} />
              {item.label}
              {item.path === "/transaksi" && (
                <span className="ml-auto rounded bg-muted px-1.5 text-[10px] text-muted-foreground">
                  {transactions.length}
                </span>
              )}
            </NavLink>
          ))}
        </nav>
        <div className="mt-4 shrink-0">
          <div className="hidden rounded-xl border border-secondary/15 bg-secondary/5 p-4 2xl:block">
            <div className="flex items-center gap-2 text-secondary">
              <ShieldCheck size={18} />
              <span className="text-xs font-semibold">Keuanganmu, aman.</span>
            </div>
            <p className="mt-2 text-[11px] leading-5 text-muted-foreground">
              Data tersimpan secara lokal di perangkat ini.
            </p>
            <div className="mt-3 flex items-center gap-1.5 text-[10px] text-secondary">
              <span className="size-1.5 rounded-full bg-secondary" />
              Mode personal aktif
            </div>
          </div>
          <button
            onClick={() => navigate("/profil")}
            className="mt-5 flex w-full items-center gap-3 px-3 py-3 text-xs text-muted-foreground"
          >
            <Settings size={18} />
            Pengaturan
          </button>
          <button
            onClick={() => setModal("help")}
            className="flex w-full items-center gap-3 px-3 py-3 text-xs text-muted-foreground"
          >
            <CircleHelp size={18} />
            Bantuan & dukungan
            <ArrowUpRight size={13} className="ml-auto" />
          </button>
          <div className="mt-5 flex items-center gap-3 border-t border-border pt-5">
            <div className="flex size-9 items-center justify-center rounded-full bg-[#343d61] text-xs font-semibold text-[#c9d4ff]">
              {profile.name.slice(0, 2).toUpperCase()}
            </div>
            <div>
              <p className="text-xs font-medium">{profile.name}</p>
              <p className="mt-1 text-[10px] text-muted-foreground">
                Personal account
              </p>
            </div>
            <button
              aria-label="Profil"
              onClick={() => navigate("/profil")}
              className="ml-auto text-muted-foreground"
            >
              <MoreHorizontal size={17} />
            </button>
          </div>
        </div>
      </aside>
      <div className="lg:ml-[232px]">
        <header className="flex h-[76px] items-center justify-between border-b border-border/70 px-5 sm:px-9">
          <div className="flex items-center gap-2 text-xs text-muted-foreground">
            <span className="lg:hidden font-display text-lg font-bold text-foreground">
              fintrack<span className="text-secondary">.</span>
            </span>
            <span className="hidden lg:inline">Workspace</span>
            <ChevronRight size={14} />
            <span className="text-foreground">{current.label}</span>
          </div>
          <div className="flex items-center gap-5">
            <span className="hidden items-center gap-2 text-[11px] text-muted-foreground sm:flex">
              <span className="size-1.5 rounded-full bg-secondary" />
              Tersimpan di perangkat
            </span>
            <div className="relative">
              <button
                onClick={() => setNotifications(!notifications)}
                aria-label="Notifikasi"
                className="relative rounded-lg p-2 text-muted-foreground"
              >
                <Bell size={19} />
                <span className="absolute right-2 top-1 size-1.5 rounded-full bg-primary" />
              </button>
              {notifications && (
                <div className="absolute right-0 top-12 z-40 w-72 rounded-xl border border-border bg-card p-5 shadow-xl">
                  <h3 className="text-sm font-semibold">Notifikasi</h3>
                  <p className="mt-3 text-xs leading-6 text-muted-foreground">
                    {warnedBudgets.length
                      ? warnedBudgets
                          .map(
                            (item) =>
                              `${item.name}: ${item.percent}% dari batas`,
                          )
                          .join(" · ")
                      : "Belum ada peringatan anggaran untuk periode ini."}
                    <NavLink
                      to="/sim"
                      className="mt-3 block text-primary"
                      onClick={() => setNotifications(false)}
                    >
                      Lihat pengingat kuota & SIM →
                    </NavLink>
                    <NavLink
                      to="/langganan"
                      className="mt-2 block text-primary"
                      onClick={() => setNotifications(false)}
                    >
                      Lihat jadwal langganan →
                    </NavLink>
                  </p>
                </div>
              )}
            </div>
            <span className="h-6 w-px bg-border" />
            <button
              aria-label="Edit profil"
              onClick={() => navigate("/profil")}
              className="flex size-8 items-center justify-center overflow-hidden rounded-full bg-[#343d61] text-[11px] font-semibold"
            >
              {profile.avatar ? (
                <img
                  src={profile.avatar}
                  alt="Profil"
                  className="size-full object-cover"
                />
              ) : (
                profile.name.slice(0, 2).toUpperCase()
              )}
            </button>
          </div>
        </header>
        <main className="mx-auto max-w-[1500px] px-5 pb-24 pt-8 sm:px-9 lg:pb-10">
          <div className="mb-7 flex flex-wrap items-center justify-between gap-5">
            <div>
              <div className="mb-2 flex items-center gap-2 text-xs text-muted-foreground">
                <span className="text-amber-200">☀</span>Kamis, 24 Oktober 2024
              </div>
              <h1 className="font-display text-[27px] font-bold sm:text-[30px]">
                {current.path === "/"
                  ? `Halo, ${profile.nickname}`
                  : current.label}
                {current.path === "/" && (
                  <span className="ml-2 text-[26px]">👋</span>
                )}
              </h1>
              <p className="mt-2 text-[13px] text-muted-foreground">
                {current.path === "/"
                  ? "Langkah kecil hari ini, keuangan lebih baik esok hari."
                  : "Kelola dan pantau keuanganmu dalam satu tempat."}
              </p>
            </div>
            <div className="flex gap-3">
              <select
                aria-label="Periode laporan"
                value={period}
                onChange={(event) => {
                  setPeriod(event.target.value)
                  localStorage.setItem(
                    "fintrack-period",
                    JSON.stringify(event.target.value),
                  )
                }}
                className="rounded-lg border border-border bg-card px-3 py-2.5 text-xs"
              >
                {availableMonths.map((month) => (
                  <option key={month} value={month}>
                    {new Date(month + "-01T12:00:00").toLocaleDateString(
                      "id-ID",
                      { month: "long", year: "numeric" },
                    )}
                  </option>
                ))}
              </select>
              <button
                onClick={() => setModal("transaction")}
                className="flex items-center gap-2 rounded-lg bg-primary px-4 py-2.5 text-xs font-semibold text-white shadow-lg shadow-primary/10 hover:bg-primary/85"
              >
                <Plus size={16} />
                Tambah Transaksi
              </button>
            </div>
          </div>
          {!monthlyTransactions.length &&
            (current.path === "/" || current.path === "/laporan") && (
              <div className="mb-5 rounded-lg border border-primary/30 bg-primary/10 p-3 text-xs text-[#b4c5ff]">
                Belum ada transaksi tercatat untuk periode {period}. Saldo total
                tetap mencakup semua rekening dan transaksi.
              </div>
            )}
          {(current.path === "/" || current.path === "/laporan") && (
            <>
              <div className="grid gap-4 md:grid-cols-3">
                <section className="relative overflow-hidden rounded-2xl border border-[#466ac3] bg-[#203c78] p-5">
                  <div className="absolute -right-8 -top-14 size-48 rounded-full border-[24px] border-white/[.035]" />
                  <div className="relative flex items-center justify-between">
                    <span className="flex items-center gap-2 text-xs text-[#c9d6ff]">
                      <Wallet size={16} />
                      Total Saldo Tersedia
                    </span>
                    <button
                      onClick={() => setHidden(!hidden)}
                      aria-label={
                        hidden ? "Tampilkan saldo" : "Sembunyikan saldo"
                      }
                      className="text-[#b9c9ed]"
                    >
                      {hidden ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                  <p className="relative mt-5 font-display text-[29px] font-bold tabular-nums">
                    {hidden ? "Rp ••••••••" : money(balance)}
                  </p>
                  <div className="mt-5 flex items-center justify-between text-[10px]">
                    <span className="flex items-center gap-1 text-[#a7f2cb]">
                      <TrendingUp size={12} />
                      +12,8%{" "}
                      <span className="ml-1 text-[#b9c9ed]">
                        dari bulan lalu
                      </span>
                    </span>
                    <span className="rounded-full bg-white/10 px-2 py-1">
                      {accounts.length} rekening aktif
                    </span>
                  </div>
                </section>
                {[
                  {
                    title: "Pemasukan",
                    amount: income,
                    icon: ArrowDownLeft,
                    green: true,
                    change: "+8,2%",
                    line: "M0 30 L12 33 L25 21 L37 24 L49 15 L61 19 L75 7 L89 12 L105 2",
                  },
                  {
                    title: "Pengeluaran",
                    amount: expense,
                    icon: ArrowUpRight,
                    green: false,
                    change: "−4,5%",
                    line: "M0 6 L14 14 L26 8 L38 21 L50 16 L63 26 L75 20 L89 30 L105 26",
                  },
                ].map((item) => (
                  <Panel key={item.title} className="p-5">
                    <div className="flex items-center justify-between">
                      <span className="text-xs text-muted-foreground">
                        {item.title} bulan ini
                      </span>
                      <span
                        className={`rounded-lg p-2 ${
                          item.green
                            ? "bg-secondary/10 text-secondary"
                            : "bg-destructive/10 text-destructive"
                        }`}
                      >
                        <item.icon size={17} />
                      </span>
                    </div>
                    <p className="mt-3 font-display text-[27px] font-bold tabular-nums">
                      {hidden ? "Rp ••••••••" : money(item.amount)}
                    </p>
                    <div className="mt-4 flex items-end justify-between">
                      <p className="text-[10px]">
                        <span className="font-medium text-secondary">
                          {item.change}
                        </span>
                        <span className="ml-1.5 text-muted-foreground">
                          dari bulan lalu
                        </span>
                      </p>
                      <svg
                        viewBox="0 0 110 38"
                        className={`h-9 w-24 ${
                          item.green ? "text-secondary" : "text-destructive"
                        }`}
                      >
                        <path
                          d={item.line}
                          fill="none"
                          stroke="currentColor"
                          strokeWidth="2"
                        />
                      </svg>
                    </div>
                  </Panel>
                ))}
              </div>
              <div className="mt-5 grid gap-5 xl:grid-cols-[1.8fr_1fr]">
                <Panel className="p-5 sm:p-6">
                  <div className="flex flex-wrap items-center justify-between gap-3">
                    <div>
                      <h2 className="font-display text-[15px] font-semibold">
                        Arus Kas
                      </h2>
                      <p className="mt-1.5 text-[11px] text-muted-foreground">
                        Ilustrasi tren · Angka ringkasan dihitung dari transaksi
                      </p>
                    </div>
                    <div className="flex rounded-lg bg-background p-1">
                      {["Mingguan", "Bulanan"].map((item) => (
                        <button
                          key={item}
                          onClick={() => setChartPeriod(item)}
                          className={`rounded-md px-3 py-1.5 text-[10px] ${
                            chartPeriod === item
                              ? "bg-muted text-foreground"
                              : "text-muted-foreground"
                          }`}
                        >
                          {item}
                        </button>
                      ))}
                    </div>
                  </div>
                  <div className="mt-5 flex gap-5 text-[10px] text-muted-foreground">
                    <span className="flex items-center gap-1.5">
                      <span className="size-1.5 rounded-full bg-primary" />
                      Pemasukan
                    </span>
                    <span className="flex items-center gap-1.5">
                      <span className="size-1.5 rounded-full bg-secondary" />
                      Pengeluaran
                    </span>
                  </div>
                  <div className="mt-5 flex h-[205px] gap-3">
                    <div className="flex w-9 flex-col justify-between pb-6 text-[9px] text-muted-foreground">
                      {["15 jt", "10 jt", "5 jt", "0"].map((item) => (
                        <span key={item}>{item}</span>
                      ))}
                    </div>
                    <div className="relative flex-1">
                      <div className="absolute inset-0 bottom-6 flex flex-col justify-between">
                        {[0, 1, 2, 3].map((item) => (
                          <div
                            key={item}
                            className="border-t border-dashed border-border"
                          />
                        ))}
                      </div>
                      <svg
                        viewBox="0 0 620 180"
                        preserveAspectRatio="none"
                        className="absolute inset-0 h-[calc(100%-24px)] w-full"
                        role="img"
                        aria-label={`Grafik arus kas ${chartPeriod.toLowerCase()}`}
                      >
                        <defs>
                          <linearGradient
                            id="chartFill"
                            x1="0"
                            y1="0"
                            x2="0"
                            y2="1"
                          >
                            <stop
                              offset="0%"
                              stopColor="#5b83ff"
                              stopOpacity=".22"
                            />
                            <stop
                              offset="100%"
                              stopColor="#5b83ff"
                              stopOpacity="0"
                            />
                          </linearGradient>
                        </defs>
                        <path
                          d={
                            chartPeriod === "Mingguan"
                              ? "M0 132 C40 135 48 70 100 84 S145 122 200 68 S252 60 300 80 S348 34 400 47 S450 64 500 20 S570 36 620 12 L620 180 L0 180Z"
                              : "M0 140 C90 145 110 80 200 94 S330 25 410 43 S530 12 620 22 L620 180 L0 180Z"
                          }
                          fill="url(#chartFill)"
                        />
                        <path
                          d={
                            chartPeriod === "Mingguan"
                              ? "M0 132 C40 135 48 70 100 84 S145 122 200 68 S252 60 300 80 S348 34 400 47 S450 64 500 20 S570 36 620 12"
                              : "M0 140 C90 145 110 80 200 94 S330 25 410 43 S530 12 620 22"
                          }
                          fill="none"
                          stroke="#7395ff"
                          strokeWidth="2.7"
                        />
                        <path
                          d={
                            chartPeriod === "Mingguan"
                              ? "M0 155 C38 155 53 132 100 143 S150 155 200 126 S253 138 300 113 S346 142 400 125 S453 98 500 112 S570 94 620 104"
                              : "M0 160 C100 157 120 130 200 140 S330 97 410 118 S530 88 620 94"
                          }
                          fill="none"
                          stroke="#4edea3"
                          strokeWidth="2.5"
                        />
                      </svg>
                      <div className="absolute inset-x-0 bottom-0 flex justify-between text-[9px] text-muted-foreground">
                        {(chartPeriod === "Mingguan"
                          ? ["Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"]
                          : [
                              "1 Okt",
                              "5 Okt",
                              "10 Okt",
                              "15 Okt",
                              "20 Okt",
                              "25 Okt",
                              "31 Okt",
                            ]
                        ).map((item) => (
                          <span key={item}>{item}</span>
                        ))}
                      </div>
                    </div>
                  </div>
                  <div className="mt-5 flex items-center gap-2 rounded-lg bg-secondary/5 px-3 py-2.5 text-[11px] text-muted-foreground">
                    <Sparkles size={14} className="shrink-0 text-secondary" />
                    <span>
                      Kamu berhasil menyimpan{" "}
                      <strong className="font-medium text-secondary">
                        {income > 0
                          ? (((income - expense) / income) * 100).toFixed(1)
                          : "0"}
                        %
                      </strong>{" "}
                      dari pemasukan bulan ini. Pertahankan!
                    </span>
                  </div>
                </Panel>
                <Panel className="p-5">
                  <div className="flex items-center justify-between">
                    <h2 className="font-display text-[15px] font-semibold">
                      Anggaran Bulanan
                    </h2>
                    <NavLink
                      to="/anggaran"
                      className="text-[10px] text-[#92adff]"
                    >
                      Kelola
                      <ChevronRight size={12} className="ml-1 inline" />
                    </NavLink>
                  </div>
                  <p className="mt-1.5 text-[11px] text-muted-foreground">
                    Tetap terarah, tanpa khawatir berlebihan.
                  </p>
                  <div className="mt-2">{renderBudgets()}</div>
                  <div className="mt-1 flex gap-2 rounded-lg border border-amber-400/10 bg-amber-400/5 p-2.5 text-[10px] leading-4 text-amber-200">
                    <CircleHelp size={14} className="shrink-0" />
                    {warnedBudgets.length
                      ? `${warnedBudgets.length} kategori mendekati atau melebihi batas. Periksa kembali sebelum berbelanja.`
                      : "Pengeluaran tercatat masih dalam batas anggaran."}
                  </div>
                </Panel>
              </div>
            </>
          )}
          {(current.path === "/" || current.path === "/transaksi") && (
            <Panel className="mt-5 overflow-hidden">
              <div className="flex flex-wrap items-center justify-between gap-4 px-5 py-5">
                <div>
                  <h2 className="font-display text-[15px] font-semibold">
                    {current.path === "/"
                      ? "Transaksi Terbaru"
                      : "Semua Transaksi"}
                  </h2>
                  <p className="mt-1.5 text-[11px] text-muted-foreground">
                    Setiap transaksi, satu langkah lebih terencana.
                  </p>
                </div>
                {current.path === "/" ? (
                  <NavLink
                    to="/transaksi"
                    className="flex items-center gap-1 text-[11px] text-[#92adff]"
                  >
                    Lihat semua
                    <ChevronRight size={13} />
                  </NavLink>
                ) : (
                  <button
                    onClick={() => navigate("/statement")}
                    className="flex items-center gap-2 rounded-lg border border-border px-3 py-2 text-xs"
                  >
                    <Upload size={14} />
                    Impor statement
                  </button>
                )}
              </div>
              {current.path === "/transaksi" && (
                <div className="flex gap-3 px-5 pb-4">
                  <div className="relative flex-1">
                    <Search
                      size={16}
                      className="absolute left-3 top-3 text-muted-foreground"
                    />
                    <input
                      aria-label="Cari transaksi"
                      placeholder="Cari transaksi..."
                      value={search}
                      onChange={(event) => setSearch(event.target.value)}
                      className={`${fieldClass} pl-10`}
                    />
                  </div>
                  <select
                    aria-label="Filter transaksi"
                    value={filter}
                    onChange={(event) => setFilter(event.target.value)}
                    className="rounded-lg border border-border bg-background px-3 text-xs"
                  >
                    {["Semua transaksi", "Pemasukan", "Pengeluaran"].map(
                      (item) => (
                        <option key={item}>{item}</option>
                      ),
                    )}
                  </select>
                </div>
              )}
              <div className="overflow-x-auto">
                <table className="w-full min-w-[620px] text-left text-xs">
                  <thead className="border-y border-border/70 bg-background/30 text-[10px] text-muted-foreground">
                    <tr>
                      {[
                        "TRANSAKSI",
                        "KATEGORI",
                        "TANGGAL",
                        "REKENING",
                        "JUMLAH",
                        "",
                      ].map((item) => (
                        <th
                          key={item}
                          className="px-5 py-3 font-medium tracking-[.04em]"
                        >
                          {item}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {displayed
                      .slice(0, current.path === "/" ? 4 : undefined)
                      .map((item) => {
                        const Icon =
                          item.amount > 0
                            ? ArrowDownLeft
                            : item.category === "Belanja"
                              ? ShoppingBag
                              : item.category === "Transportasi"
                                ? Car
                                : Coffee
                        return (
                          <tr
                            key={item.id}
                            className="border-b border-border/50 last:border-0 hover:bg-muted/30"
                          >
                            <td className="px-5 py-3.5">
                              <span className="flex items-center gap-3">
                                <span
                                  className={`rounded-lg p-2.5 ${
                                    item.amount > 0
                                      ? "bg-secondary/10 text-secondary"
                                      : item.category === "Belanja"
                                        ? "bg-purple-400/10 text-purple-300"
                                        : item.category === "Transportasi"
                                          ? "bg-amber-400/10 text-amber-300"
                                          : "bg-orange-400/10 text-orange-300"
                                  }`}
                                >
                                  <Icon size={16} />
                                </span>
                                <span className="font-medium">{item.name}</span>
                              </span>
                            </td>
                            <td className="px-5">
                              <span className="rounded-md bg-muted px-2 py-1 text-[10px] text-muted-foreground">
                                {item.category}
                              </span>
                            </td>
                            <td className="px-5 text-muted-foreground">
                              {new Date(
                                item.date + "T12:00:00",
                              ).toLocaleDateString("id-ID", {
                                day: "numeric",
                                month: "short",
                                year: "numeric",
                              })}
                            </td>
                            <td className="px-5 text-muted-foreground">
                              {item.account}
                            </td>
                            <td
                              className={`whitespace-nowrap px-5 font-medium tabular-nums ${
                                item.amount > 0
                                  ? "text-secondary"
                                  : "text-foreground"
                              }`}
                            >
                              {item.amount > 0 ? "+" : "−"}
                              {money(item.amount)}
                            </td>
                            <td className="pr-5">
                              <button
                                aria-label={`Detail ${item.name}`}
                                onClick={() =>
                                  notify(
                                    `${item.name} · ${item.account} · ${money(item.amount)}${
                                      item.note ? " · " + item.note : ""
                                    }`,
                                  )
                                }
                                className="text-muted-foreground"
                              >
                                <MoreHorizontal size={17} />
                              </button>
                            </td>
                          </tr>
                        )
                      })}
                  </tbody>
                </table>
                {displayed.length === 0 && (
                  <p className="p-8 text-center text-sm text-muted-foreground">
                    Tidak ada transaksi yang cocok.
                  </p>
                )}
              </div>
            </Panel>
          )}
          {current.path === "/anggaran" && (
            <BudgetCalendar
              transactions={transactions}
              config={budgetConfig}
              period={period}
            />
          )}
          {current.path === "/lokasi" && (
            <SpendingLocations transactions={transactions} notify={notify} />
          )}
          {current.path === "/kontrol" && (
            <ControlCenter
              accounts={accounts}
              transactions={transactions}
              config={budgetConfig}
              record={record}
              notify={notify}
            />
          )}
          {current.path === "/anggaran" && (
            <BudgetManager
              config={budgetConfig}
              transactions={transactions}
              notify={notify}
              onChange={(next) => {
                setBudgetConfig(next)
                localStorage.setItem("fintrack-budgets", JSON.stringify(next))
              }}
            />
          )}
          {current.path === "/instan" && (
            <QuickExpense accounts={accounts} record={record} notify={notify} />
          )}
          {current.path === "/target" && (
            <SavingsGoals accounts={accounts} record={record} notify={notify} />
          )}
          {current.path === "/sim" && (
            <Reminders
              mode="sim"
              accounts={accounts}
              record={record}
              notify={notify}
            />
          )}
          {current.path === "/langganan" && (
            <Reminders
              mode="subscriptions"
              accounts={accounts}
              record={record}
              notify={notify}
            />
          )}
          {current.path === "/saham" && (
            <>
              <BrokerSummary accounts={accounts} />
              <StockMarket notify={notify} />
            </>
          )}
          {current.path === "/statement" && (
            <StatementReader
              accounts={accounts}
              transactions={transactions}
              notify={notify}
              importRows={(rows) => {
                setTransactions((current) => {
                  const next = [
                    ...rows.map((item, index) => ({
                      ...item,
                      id: Date.now() + index,
                    })),
                    ...current,
                  ]
                  localStorage.setItem(
                    "fintrack-transactions",
                    JSON.stringify(next),
                  )
                  return next
                })
              }}
            />
          )}
          {current.path === "/laporan" && (
            <ReportPreview
              transactions={transactions}
              period={period}
              notify={notify}
            />
          )}
          {current.path === "/laporan" && (
            <AdvancedExport
              accounts={accounts}
              transactions={transactions}
              notify={notify}
            />
          )}
          {current.path === "/rekening" && (
            <BrokerAccount
              notify={notify}
              add={(account) => {
                if (
                  accounts.some(
                    (item) =>
                      item.name.toLowerCase() === account.name.toLowerCase(),
                  )
                ) {
                  notify("Label rekening sudah digunakan. Pilih label lain.")
                  return false
                }
                const next = [...accounts, account]
                setAccounts(next)
                localStorage.setItem("fintrack-accounts", JSON.stringify(next))
                notify("Rekening sekuritas tersimpan dalam portofolio")
                return true
              }}
            />
          )}
          {current.path === "/rekening" && (
            <Portfolio
              accounts={accounts}
              add={() => setModal("account")}
              notify={notify}
            />
          )}
          {current.path === "/struk" && (
            <ReceiptStudio
              accounts={accounts}
              notify={notify}
              save={(value) => {
                const item: Transaction = {
                  id: Date.now(),
                  name: value.name,
                  category: "Belanja",
                  amount: -value.amount,
                  date: value.date,
                  account: value.account,
                  note: value.note,
                }
                const next = [item, ...transactions]
                setTransactions(next)
                localStorage.setItem(
                  "fintrack-transactions",
                  JSON.stringify(next),
                )
                notify("Struk berhasil dicatat sebagai transaksi")
                setPeriod(value.date.slice(0, 7))
                localStorage.setItem(
                  "fintrack-period",
                  JSON.stringify(value.date.slice(0, 7)),
                )
                navigate("/transaksi")
              }}
            />
          )}
          {current.path === "/profil" && (
            <Profile
              accounts={accounts}
              notify={notify}
              onSave={() =>
                setProfile(
                  JSON.parse(localStorage.getItem("fintrack-profile") || "{}"),
                )
              }
            />
          )}
          {current.path === "/widget" && (
            <SmartWidgets
              transactions={transactions}
              balance={balance}
              income={income}
              expense={expense}
              budgetLimit={budgets.reduce((sum, b) => sum + b.limit, 0)}
              budgetRemaining={budgetRemaining}
              period={period}
              create={(type) => {
                setDraftType(type)
                setDraftCategory(
                  type === "income" ? "Pemasukan" : "Makanan & Minuman",
                )
                setModal("transaction")
              }}
            />
          )}
          {current.path === "/widget" && (
            <Widgets
              balance={balance}
              income={income}
              expense={expense}
              budgetRemaining={budgetRemaining}
              notify={notify}
            />
          )}
          {current.path === "/pasar" && <Markets notify={notify} />}
          {current.path === "/laporan" && (
            <ReportBreakdown transactions={transactions} />
          )}
          {current.path === "/laporan" && (
            <Panel className="mt-5 flex flex-wrap items-center justify-between gap-5 p-6">
              <div className="flex items-center gap-4">
                <span className="rounded-xl bg-primary/15 p-4 text-primary">
                  <FileText size={27} />
                </span>
                <div>
                  <h2 className="font-display font-semibold">
                    Laporan keuangan bulanan
                  </h2>
                  <p className="mt-2 text-xs text-muted-foreground">
                    {transactions.length} transaksi · Oktober 2024 · Format CSV
                  </p>
                </div>
              </div>
              <button
                onClick={exportReport}
                className="flex items-center gap-2 rounded-lg bg-primary px-4 py-3 text-xs font-semibold"
              >
                <Download size={16} />
                Ekspor Laporan
              </button>
            </Panel>
          )}
          {current.path === "/" && (
            <div className="mt-5 grid gap-4 sm:grid-cols-3">
              {[
                {
                  title: "Rekening & investasi",
                  text: "Semua asetmu, dalam satu tempat",
                  icon: Wallet,
                  action: () => setModal("account"),
                },
                {
                  title: "Impor e-statement",
                  text: "Catat otomatis dari rekening bank",
                  icon: Upload,
                  action: () => navigate("/statement"),
                },
                {
                  title: "Ekspor laporan",
                  text: "Simpan rekap keuangan kapan saja",
                  icon: Download,
                  action: exportReport,
                },
              ].map((item) => (
                <button
                  key={item.title}
                  onClick={item.action}
                  className="group flex items-center gap-3 rounded-xl border border-border/70 bg-card px-4 py-4 text-left transition-colors hover:border-primary/50"
                >
                  <span className="rounded-lg bg-muted p-2.5 text-[#92adff]">
                    <item.icon size={18} />
                  </span>
                  <span>
                    <span className="block text-xs font-medium">
                      {item.title}
                    </span>
                    <span className="mt-1 block text-[10px] text-muted-foreground">
                      {item.text}
                    </span>
                  </span>
                  <ArrowUpRight
                    size={16}
                    className="ml-auto shrink-0 text-muted-foreground group-hover:text-primary"
                  />
                </button>
              ))}
            </div>
          )}
          <footer className="mt-7 flex items-center justify-between text-[10px] text-muted-foreground">
            <span>© 2024 FinTrack. Make every rupiah count.</span>
            <span className="flex items-center gap-1.5">
              <ShieldCheck size={12} />
              Privasi adalah prioritas.
            </span>
          </footer>
        </main>
      </div>
      <nav className="fixed inset-x-0 bottom-0 z-30 flex gap-5 overflow-x-auto border-t border-border bg-background/95 px-4 py-3 backdrop-blur-xl lg:hidden">
        {navigation.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `flex min-w-16 shrink-0 flex-col items-center gap-1.5 whitespace-nowrap text-[9px] ${
                isActive ? "text-primary" : "text-muted-foreground"
              }`
            }
          >
            <item.icon size={20} />
            {item.label}
          </NavLink>
        ))}
      </nav>
      {toast && (
        <div
          role="status"
          className="fixed bottom-24 left-1/2 z-50 flex -translate-x-1/2 items-center gap-2 rounded-xl border border-secondary/30 bg-card px-5 py-4 text-xs shadow-2xl lg:bottom-8"
        >
          <Check size={16} className="text-secondary" />
          {toast}
        </div>
      )}
      {modal && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-[#040916]/80 p-5 backdrop-blur-sm"
          onClick={() => setModal("")}
        >
          <section
            role="dialog"
            aria-modal="true"
            aria-label={
              modal === "transaction"
                ? "Tambah Transaksi"
                : modal === "account"
                  ? "Tambah Rekening & Investasi"
                  : modal === "import"
                    ? "Impor E-Statement"
                    : "Informasi"
            }
            className="max-h-[90vh] w-full max-w-md overflow-y-auto rounded-2xl border border-border bg-card p-6 shadow-2xl"
            onClick={(event) => event.stopPropagation()}
          >
            <div className="mb-6 flex items-center justify-between">
              <h2 className="font-display text-lg font-semibold">
                {modal === "transaction"
                  ? "Tambah Transaksi"
                  : modal === "account"
                    ? "Tambah Rekening & Investasi"
                    : modal === "import"
                      ? "Impor E-Statement"
                      : modal === "settings"
                        ? "Pengaturan Akun"
                        : "Bantuan FinTrack"}
              </h2>
              <button
                aria-label="Tutup"
                onClick={() => setModal("")}
                className="rounded-lg p-1 text-muted-foreground"
              >
                <X size={20} />
              </button>
            </div>
            {(modal === "transaction" || modal === "account") && (
              <form onSubmit={submit} className="space-y-4">
                <label className="block space-y-2 text-xs text-muted-foreground">
                  <span>
                    {modal === "transaction"
                      ? "Nama transaksi"
                      : "Nama rekening / investasi"}
                  </span>
                  <input
                    name="name"
                    required
                    className={fieldClass}
                    placeholder={
                      modal === "transaction"
                        ? "Contoh: Belanja bulanan"
                        : "Contoh: Bank Mandiri"
                    }
                  />
                </label>
                <label className="block space-y-2 text-xs text-muted-foreground">
                  <span>Jenis</span>
                  <select
                    name="type"
                    className={fieldClass}
                    value={modal === "transaction" ? draftType : undefined}
                    onChange={(event) => {
                      if (modal === "transaction")
                        setDraftType(event.target.value)
                    }}
                  >
                    {modal === "transaction" ? (
                      <>
                        <option value="expense">Pengeluaran</option>
                        <option value="income">Pemasukan</option>
                      </>
                    ) : (
                      <>
                        <option>Rekening Bank</option>
                        <option>E-Wallet</option>
                        <option>Investasi</option>
                        <option>Kas Tunai</option>
                      </>
                    )}
                  </select>
                </label>
                <label className="block space-y-2 text-xs text-muted-foreground">
                  <span>
                    {modal === "transaction"
                      ? "Jumlah (Rp)"
                      : "Saldo awal (Rp)"}
                  </span>
                  <input
                    type="number"
                    name="amount"
                    value={draftAmount}
                    onChange={(event) => setDraftAmount(event.target.value)}
                    min={modal === "transaction" ? 1 : 0}
                    step="1"
                    required
                    className={fieldClass}
                    placeholder="0"
                  />
                </label>
                {modal === "transaction" && (
                  <>
                    <div className="flex flex-wrap gap-2">
                      {[25000, 50000, 100000, 500000].map((amount) => (
                        <button
                          key={amount}
                          type="button"
                          onClick={() => setDraftAmount(String(amount))}
                          className="rounded-full border border-border bg-muted px-3 py-1.5 text-[10px] text-muted-foreground hover:text-primary"
                        >
                          {money(amount)}
                        </button>
                      ))}
                    </div>
                    <label className="block space-y-2 text-xs text-muted-foreground">
                      <span>Kategori</span>
                      <select
                        name="category"
                        className={fieldClass}
                        value={draftCategory}
                        onChange={(event) =>
                          setDraftCategory(event.target.value)
                        }
                      >
                        {[
                          "Makanan & Minuman",
                          "Belanja",
                          "Transportasi",
                          "Tagihan & Utilitas",
                          "Hiburan",
                          "Pemasukan",
                          "Lainnya",
                        ].map((item) => (
                          <option key={item}>{item}</option>
                        ))}
                      </select>
                    </label>
                    {draftType === "expense" &&
                      budgets
                        .filter(
                          (budget) =>
                            budget.name === draftCategory &&
                            budget.used + Number(draftAmount) >=
                              (budget.limit * budget.threshold) / 100,
                        )
                        .map((budget) => (
                          <div
                            key={budget.name}
                            className="rounded-lg border border-amber-400/20 bg-amber-400/5 p-3 text-xs leading-5 text-amber-200"
                          >
                            Anggaran {budget.name} akan mendekati atau melebihi
                            batas {money(budget.limit)}. Periksa nominal sebelum
                            menyimpan.
                          </div>
                        ))}
                    <div className="grid grid-cols-2 gap-3">
                      <label className="block space-y-2 text-xs text-muted-foreground">
                        <span>Tanggal</span>
                        <input
                          type="date"
                          name="date"
                          defaultValue={new Date().toISOString().slice(0, 10)}
                          required
                          className={fieldClass}
                        />
                      </label>
                      <label className="block space-y-2 text-xs text-muted-foreground">
                        <span>Rekening</span>
                        <select
                          name="account"
                          defaultValue={profile.primaryAccount}
                          className={fieldClass}
                        >
                          {accounts.map((item, index) => (
                            <option key={index}>{item.name}</option>
                          ))}
                        </select>
                      </label>
                    </div>
                  </>
                )}
                {modal === "transaction" && (
                  <>
                    <label className="block space-y-2 text-xs text-muted-foreground">
                      <span>Catatan (opsional)</span>
                      <textarea
                        name="note"
                        rows={2}
                        className={fieldClass}
                        placeholder="Tambahkan detail transaksi..."
                      />
                    </label>
                    <button
                      type="button"
                      onClick={() => {
                        setModal("")
                        navigate("/struk")
                      }}
                      className="flex w-full items-center justify-center gap-2 rounded-lg border border-dashed border-primary/40 bg-primary/5 py-3 text-xs text-primary"
                    >
                      <ScanLine size={16} />
                      Catat dari foto struk & rincian item
                    </button>
                  </>
                )}
                <button className="mt-2 w-full rounded-lg bg-primary py-3 text-sm font-semibold">
                  Simpan {modal === "transaction" ? "Transaksi" : "Rekening"}
                </button>
              </form>
            )}
            {modal === "import" && (
              <>
                <p className="mb-4 text-xs leading-6 text-muted-foreground">
                  Unggah CSV dengan kolom tanggal, nama, kategori, rekening, dan
                  jumlah. Jumlah negatif dicatat sebagai pengeluaran. Parsing
                  PDF bank memerlukan layanan backend.
                </p>
                <label className="flex cursor-pointer flex-col items-center gap-3 rounded-xl border border-dashed border-primary/50 bg-primary/5 p-8">
                  <Upload size={30} className="text-primary" />
                  <span className="text-sm">
                    {file?.name || "Pilih berkas CSV"}
                  </span>
                  <input
                    type="file"
                    accept=".csv"
                    className="hidden"
                    onChange={(event) =>
                      setFile(event.target.files?.[0] || null)
                    }
                  />
                </label>
                <button
                  disabled={!file}
                  onClick={async () => {
                    if (!file) return
                    const text = await file.text()
                    const rows = text.trim().split(/\r?\n/).slice(1)
                    const imported = rows.map((row, index) => {
                      const values =
                        row
                          .match(/("(?:[^"]|"")*"|[^,]+)(?=,|$)/g)
                          ?.map((value) =>
                            value.replace(/^"|"$/g, "").replace(/""/g, '"'),
                          ) || []
                      return {
                        id: Date.now() + index,
                        date: values[0],
                        name: values[1],
                        category: values[2],
                        account: values[3],
                        amount: Number(values[4]),
                        note: values[5] || "",
                      }
                    })
                    if (
                      !imported.length ||
                      imported.some(
                        (item) =>
                          !/^\d{4}-\d{2}-\d{2}$/.test(item.date || "") ||
                          !item.name ||
                          !Number.isFinite(item.amount),
                      )
                    ) {
                      notify(
                        "Format CSV tidak valid. Gunakan format dari ekspor FinTrack.",
                      )
                      return
                    }
                    const next = [
                      ...imported.filter(
                        (item) =>
                          !transactions.some(
                            (existing) =>
                              existing.date === item.date &&
                              existing.name === item.name &&
                              existing.amount === item.amount,
                          ),
                      ),
                      ...transactions,
                    ]
                    setTransactions(next)
                    localStorage.setItem(
                      "fintrack-transactions",
                      JSON.stringify(next),
                    )
                    setModal("")
                    setFile(null)
                    notify(
                      `${next.length - transactions.length} transaksi berhasil diimpor; duplikat dilewati`,
                    )
                  }}
                  className="mt-5 w-full rounded-lg bg-primary py-3 text-sm font-semibold"
                >
                  Impor Transaksi
                </button>
              </>
            )}
            {modal === "settings" && (
              <div className="space-y-5 text-sm">
                <div>
                  <span className="text-xs text-muted-foreground">
                    Nama akun
                  </span>
                  <p className="mt-1">Sarah Anderson</p>
                </div>
                <div>
                  <span className="text-xs text-muted-foreground">
                    Mata uang
                  </span>
                  <p className="mt-1">Rupiah Indonesia (IDR)</p>
                </div>
                <p className="rounded-lg bg-secondary/5 p-4 text-xs leading-6 text-muted-foreground">
                  Ini adalah versi personal berbasis browser. Data tersimpan di
                  perangkat; sinkronisasi cloud dan autentikasi biometrik belum
                  terhubung.
                </p>
              </div>
            )}
            {modal === "help" && (
              <p className="text-sm leading-7 text-muted-foreground">
                Mulai dengan menambahkan rekening, kemudian catat pemasukan dan
                pengeluaran. Pantau batas kategori di Anggaran, dan unduh rekap
                CSV dari halaman Laporan. Data tetap tersedia setelah halaman
                dimuat ulang pada browser ini.
              </p>
            )}
          </section>
        </div>
      )}
    </div>
  )
}
const router = createBrowserRouter([
  ...navigation.map((item) => ({ path: item.path, Component: FinTrack })),
  { path: "*", Component: FinTrack },
])
export default function App() {
  return <RouterProvider router={router} />
}
