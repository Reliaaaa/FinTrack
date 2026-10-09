import { useState, type ReactNode } from "react"
import {
  Wallet,
  TrendingUp,
  Plus,
  Trash2,
  ScanLine,
  Check,
  Upload,
  ShieldCheck,
  Bell,
  Coins,
  ArrowLeftRight,
  Calculator,
  Save,
  ChevronRight,
  FileText,
} from "lucide-react"

type Account = {
  name: string
  type: string
  amount: number
}
type ReceiptItem = {
  id: number
  name: string
  quantity: number
  price: number
  category: string
}
const rupiah = (value: number) =>
  "Rp " + value.toLocaleString("id-ID", { maximumFractionDigits: 0 })
const input =
  "w-full rounded-lg border border-border bg-background px-3 py-2.5 text-sm"
const primary =
  "flex items-center justify-center gap-2 rounded-lg bg-primary px-4 py-3 text-xs font-semibold text-white hover:bg-primary/85"
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
function stored<T>(key: string, fallback: T): T {
  try {
    return JSON.parse(localStorage.getItem(key) || "null") || fallback
  } catch {
    return fallback
  }
}

export function Portfolio({
  accounts,
  add,
  notify,
}: {
  accounts: Account[]
  add: () => void
  notify: (value: string) => void
}) {
  const [filter, setFilter] = useState("Semua")
  const [calculator, setCalculator] = useState(false)
  const [principal, setPrincipal] = useState(10000000)
  const [rate, setRate] = useState(3.5)
  const [months, setMonths] = useState(12)
  const [tax, setTax] = useState(20)
  const [config, setConfig] = useState<Record<string, {
    rate: number
    fee: number
  }>>(() => stored("fintrack-account-config", {}))
  const [editing, setEditing] = useState<string | null>(null)
  const total = accounts.reduce((sum, account) => sum + account.amount, 0)
  const interest = accounts.reduce(
    (sum, account) =>
      sum + ((account.amount * (config[account.name]?.rate || 0)) / 1200) * 0.8,
    0,
  )
  const fees = accounts.reduce(
    (sum, account) => sum + (config[account.name]?.fee || 0),
    0,
  )
  const filtered = accounts.filter(
    (account) =>
      filter === "Semua" ||
      (filter === "Bank"
        ? account.type === "Rekening Bank"
        : filter === "E-Wallet"
          ? account.type === "E-Wallet"
          : account.type === "Investasi"),
  )
  return (
    <div className="space-y-5">
      <div className="grid gap-5 md:grid-cols-[1.6fr_1fr]">
        <Card className="relative overflow-hidden bg-gradient-to-br from-[#203c78] to-card">
          <span className="text-xs text-[#b4c5ff]">
            TOTAL PORTOFOLIO & ASET
          </span>
          <h2 className="mt-4 font-display text-4xl font-bold">
            {rupiah(total)}
          </h2>
          <p className="mt-3 text-xs text-muted-foreground">
            {accounts.length} rekening · Nilai saldo awal yang dicatat
          </p>
          <div className="mt-7 flex flex-wrap gap-3">
            <button className={primary} onClick={add}>
              <Plus size={15} />
              Tambah rekening
            </button>
            <button
              className={secondary}
              onClick={() => setCalculator(!calculator)}
            >
              <Calculator size={15} />
              Kalkulator bunga
            </button>
          </div>
        </Card>
        <Card>
          <h3 className="flex items-center gap-2 font-display font-semibold">
            <TrendingUp size={18} className="text-secondary" />
            Proyeksi kas pasif / bulan
          </h3>
          <div className="mt-6 grid grid-cols-2 gap-4">
            <div>
              <p className="text-xs text-muted-foreground">
                Bunga bersih estimasi
              </p>
              <p className="mt-2 text-lg font-semibold text-secondary">
                {rupiah(interest)}
              </p>
            </div>
            <div>
              <p className="text-xs text-muted-foreground">
                Biaya administrasi
              </p>
              <p className="mt-2 text-lg font-semibold text-destructive">
                {rupiah(fees)}
              </p>
            </div>
          </div>
          <div className="mt-6 flex justify-between rounded-lg bg-muted p-3 text-xs">
            <span>Hasil bersih bulanan</span>
            <strong
              className={
                interest >= fees ? "text-secondary" : "text-destructive"
              }
            >
              {rupiah(interest - fees)}
            </strong>
          </div>
          <p className="mt-3 text-[10px] leading-5 text-muted-foreground">
            Estimasi bunga sederhana, pajak 20%. Atur bunga dan biaya tiap
            rekening; bukan imbal hasil terjamin.
          </p>
        </Card>
      </div>
      {calculator && (
        <Card>
          <h3 className="mb-5 font-display font-semibold">
            Kalkulator bunga simpanan
          </h3>
          <div className="grid gap-4 sm:grid-cols-4">
            <Label title="Saldo simpanan (Rp)">
              <input
                type="number"
                min="0"
                value={principal}
                onChange={(event) =>
                  setPrincipal(Math.max(0, Number(event.target.value)))
                }
                className={input}
              />
            </Label>
            <Label title="Bunga per tahun (%)">
              <input
                type="number"
                min="0"
                step=".1"
                value={rate}
                onChange={(event) =>
                  setRate(Math.max(0, Number(event.target.value)))
                }
                className={input}
              />
            </Label>
            <Label title="Jangka waktu (bulan)">
              <input
                type="number"
                min="1"
                value={months}
                onChange={(event) =>
                  setMonths(Math.max(1, Number(event.target.value)))
                }
                className={input}
              />
            </Label>
            <Label title="Pajak (%)">
              <input
                type="number"
                min="0"
                max="100"
                value={tax}
                onChange={(event) =>
                  setTax(Math.min(100, Math.max(0, Number(event.target.value))))
                }
                className={input}
              />
            </Label>
          </div>
          <div className="mt-5 rounded-xl bg-secondary/10 p-4">
            <p className="text-xs text-muted-foreground">
              Estimasi bunga bersih selama {months} bulan
            </p>
            <p className="mt-2 text-2xl font-semibold text-secondary">
              {rupiah(
                ((((principal * rate) / 100) * months) / 12) * (1 - tax / 100),
              )}
            </p>
          </div>
        </Card>
      )}
      <div className="flex flex-wrap gap-2">
        {["Semua", "Bank", "E-Wallet", "Sekuritas"].map((item) => (
          <button
            key={item}
            onClick={() => setFilter(item)}
            className={`rounded-lg px-4 py-2.5 text-xs ${
              filter === item
                ? "bg-primary text-white"
                : "bg-muted text-muted-foreground"
            }`}
          >
            {item}
          </button>
        ))}
      </div>
      <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
        {filtered.map((account, index) => (
          <Card key={index}>
            <div className="flex justify-between">
              <span className="rounded-xl bg-primary/15 p-3 text-[#92adff]">
                <Wallet size={22} />
              </span>
              <span className="text-[10px] text-muted-foreground">
                {account.type}
              </span>
            </div>
            <h3 className="mt-5 font-display text-lg font-semibold">
              {account.name}
            </h3>
            <p className="mt-2 text-2xl font-semibold">
              {rupiah(account.amount)}
            </p>
            <div className="mt-5 flex justify-between border-t border-border pt-4 text-xs text-muted-foreground">
              <span>{config[account.name]?.rate || 0}% p.a.</span>
              <span>Admin {rupiah(config[account.name]?.fee || 0)}/bln</span>
            </div>
            <button
              onClick={() =>
                setEditing(editing === account.name ? null : account.name)
              }
              className="mt-4 text-xs text-primary"
            >
              Konfigurasi rekening <ChevronRight size={12} className="inline" />
            </button>
            {editing === account.name && (
              <form
                className="mt-4 space-y-3"
                onSubmit={(event) => {
                  event.preventDefault()
                  const data = new FormData(event.currentTarget)
                  const next = {
                    ...config,
                    [account.name]: {
                      rate: Number(data.get("rate")),
                      fee: Number(data.get("fee")),
                    },
                  }
                  setConfig(next)
                  localStorage.setItem(
                    "fintrack-account-config",
                    JSON.stringify(next),
                  )
                  setEditing(null)
                  notify("Konfigurasi rekening disimpan")
                }}
              >
                <Label title="Bunga tahunan (%)">
                  <input
                    name="rate"
                    type="number"
                    min="0"
                    step=".01"
                    defaultValue={config[account.name]?.rate || 0}
                    className={input}
                    required
                  />
                </Label>
                <Label title="Biaya admin bulanan (Rp)">
                  <input
                    name="fee"
                    type="number"
                    min="0"
                    defaultValue={config[account.name]?.fee || 0}
                    className={input}
                    required
                  />
                </Label>
                <button className={primary}>Simpan</button>
              </form>
            )}
          </Card>
        ))}
      </div>
      {!filtered.length && (
        <Card>
          <p className="text-sm text-muted-foreground">
            Belum ada rekening dalam kategori ini.
          </p>
        </Card>
      )}
    </div>
  )
}

export function ReceiptStudio({
  accounts,
  save,
  notify,
}: {
  accounts: Account[]
  save: (value: {
    name: string
    amount: number
    account: string
    date: string
    note: string
  }) => void
  notify: (value: string) => void
}) {
  const [items, setItems] = useState<ReceiptItem[]>(() =>
    stored("fintrack-receipt-draft", [
      {
        id: 1,
        name: "Beras premium 5 kg",
        quantity: 1,
        price: 75000,
        category: "Kebutuhan Pokok",
      },
      {
        id: 2,
        name: "Susu UHT",
        quantity: 2,
        price: 18000,
        category: "Minuman",
      },
      {
        id: 3,
        name: "Telur ayam",
        quantity: 1,
        price: 32000,
        category: "Lauk Pauk",
      },
    ]),
  )
  const [merchant, setMerchant] = useState("Grand Lucky Superstore")
  const [receiptTotal, setReceiptTotal] = useState(143000)
  const [discount, setDiscount] = useState(0)
  const [tax, setTax] = useState(0)
  const [date, setDate] = useState("2024-10-24")
  const [account, setAccount] = useState(accounts[0]?.name || "")
  const [image, setImage] = useState("")
  const [zoom, setZoom] = useState(false)
  const subtotal = items.reduce(
    (sum, item) => sum + item.quantity * item.price,
    0,
  )
  const total = Math.max(0, subtotal - discount) * (1 + tax / 100)
  const difference = Math.round(total - receiptTotal)
  const update = (id: number, patch: Partial<ReceiptItem>) =>
    setItems((current) =>
      current.map((item) => (item.id === id ? { ...item, ...patch } : item)),
    )
  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-4 rounded-xl border border-primary/20 bg-primary/5 p-4">
        <div className="flex gap-3">
          <ScanLine size={22} className="text-primary" />
          <div>
            <h2 className="text-sm font-semibold">
              Detail struk & koreksi item
            </h2>
            <p className="mt-1 text-xs leading-5 text-muted-foreground">
              Contoh data struk. Unggah foto sebagai referensi dan masukkan
              hasil secara manual; OCR otomatis belum terhubung.
            </p>
          </div>
        </div>
        <button
          className={secondary}
          onClick={() => {
            localStorage.setItem(
              "fintrack-receipt-draft",
              JSON.stringify(items),
            )
            notify("Draf item struk disimpan")
          }}
        >
          <Save size={15} />
          Simpan draf
        </button>
      </div>
      <div className="grid gap-5 xl:grid-cols-[1fr_1.8fr]">
        <div className="space-y-5">
          <Card>
            <h3 className="mb-4 font-display font-semibold">Foto struk asli</h3>
            <label className="flex cursor-pointer flex-col items-center gap-3 rounded-xl border border-dashed border-border bg-background p-7">
              <Upload className="text-primary" size={27} />
              <span className="text-xs text-muted-foreground">
                Unggah JPG / PNG · Maks. 5 MB
              </span>
              <input
                type="file"
                accept="image/jpeg,image/png"
                className="hidden"
                onChange={(event) => {
                  const file = event.target.files?.[0]
                  if (!file) return
                  if (file.size > 5 * 1024 * 1024) {
                    notify("Ukuran foto maksimal 5 MB")
                    return
                  }
                  const reader = new FileReader()
                  reader.onload = () => setImage(String(reader.result))
                  reader.readAsDataURL(file)
                }}
              />
            </label>
            {image && (
              <button className="mt-4 w-full" onClick={() => setZoom(true)}>
                <img
                  src={image}
                  alt="Foto struk yang diunggah"
                  className="max-h-72 w-full rounded-lg object-contain"
                />
                <span className="mt-2 block text-[10px] text-muted-foreground">
                  Klik untuk memperbesar
                </span>
              </button>
            )}
            <div className="mt-5 space-y-4">
              <Label title="Nama toko">
                <input
                  value={merchant}
                  onChange={(event) => setMerchant(event.target.value)}
                  className={input}
                />
              </Label>
              <Label title="Total pada struk (Rp)">
                <input
                  type="number"
                  min="0"
                  value={receiptTotal}
                  onChange={(event) =>
                    setReceiptTotal(Math.max(0, Number(event.target.value)))
                  }
                  className={input}
                />
              </Label>
              <Label title="Tanggal transaksi">
                <input
                  type="date"
                  value={date}
                  onChange={(event) => setDate(event.target.value)}
                  className={input}
                  required
                />
              </Label>
              <Label title="Rekening pembayaran">
                <select
                  value={account}
                  onChange={(event) => setAccount(event.target.value)}
                  className={input}
                >
                  {accounts.map((item, index) => (
                    <option key={index}>{item.name}</option>
                  ))}
                </select>
              </Label>
            </div>
          </Card>
          <Card>
            <p className="text-xs text-muted-foreground">Status rekonsiliasi</p>
            <p
              className={`mt-3 text-xl font-semibold ${
                difference === 0 ? "text-secondary" : "text-amber-300"
              }`}
            >
              {difference === 0
                ? "Total sesuai"
                : "Selisih " + rupiah(difference)}
            </p>
            <p className="mt-3 text-xs leading-6 text-muted-foreground">
              Periksa kuantitas, harga, diskon, dan pajak. Total transaksi
              mengikuti perhitungan item yang disimpan.
            </p>
          </Card>
        </div>
        <Card>
          <div className="mb-5 flex justify-between">
            <h3 className="font-display font-semibold">Rincian item</h3>
            <span className="text-xs text-muted-foreground">
              {items.length} item
            </span>
          </div>
          <div className="space-y-3">
            {items.map((item, index) => (
              <div
                key={item.id}
                className="rounded-xl border border-border bg-background/40 p-4"
              >
                <div className="mb-3 flex items-center justify-between">
                  <span className="text-[10px] tracking-wider text-muted-foreground">
                    ITEM {String(index + 1).padStart(2, "0")}
                  </span>
                  <button
                    aria-label={`Hapus ${item.name}`}
                    onClick={() =>
                      setItems((current) =>
                        current.filter((value) => value.id !== item.id),
                      )
                    }
                    className="p-1 text-muted-foreground hover:text-destructive"
                  >
                    <Trash2 size={15} />
                  </button>
                </div>
                <input
                  aria-label={`Nama item ${index + 1}`}
                  value={item.name}
                  onChange={(event) =>
                    update(item.id, { name: event.target.value })
                  }
                  className={input}
                />
                <div className="mt-3 grid grid-cols-2 gap-3 sm:grid-cols-[1fr_1fr_1.2fr]">
                  <Label title="Kuantitas">
                    <input
                      type="number"
                      min="1"
                      step="1"
                      value={item.quantity}
                      onChange={(event) =>
                        update(item.id, {
                          quantity: Math.max(
                            1,
                            Math.floor(Number(event.target.value)),
                          ),
                        })
                      }
                      className={input}
                    />
                  </Label>
                  <Label title="Harga satuan (Rp)">
                    <input
                      type="number"
                      min="0"
                      value={item.price}
                      onChange={(event) =>
                        update(item.id, {
                          price: Math.max(0, Number(event.target.value)),
                        })
                      }
                      className={input}
                    />
                  </Label>
                  <Label title="Kategori">
                    <select
                      value={item.category}
                      onChange={(event) =>
                        update(item.id, { category: event.target.value })
                      }
                      className={input}
                    >
                      {[
                        "Kebutuhan Pokok",
                        "Lauk Pauk",
                        "Minuman",
                        "Camilan",
                        "Lainnya",
                      ].map((value) => (
                        <option key={value}>{value}</option>
                      ))}
                    </select>
                  </Label>
                </div>
                <p className="mt-3 text-right text-xs font-semibold text-secondary">
                  {rupiah(item.quantity * item.price)}
                </p>
              </div>
            ))}
          </div>
          <button
            onClick={() =>
              setItems((current) => [
                ...current,
                {
                  id: Date.now(),
                  name: "",
                  quantity: 1,
                  price: 0,
                  category: "Kebutuhan Pokok",
                },
              ])
            }
            className={`${secondary} mt-4 w-full border-dashed`}
          >
            <Plus size={16} />
            Tambah item manual
          </button>
          <div className="mt-6 grid grid-cols-2 gap-4">
            <Label title="Diskon (Rp)">
              <input
                type="number"
                min="0"
                value={discount}
                onChange={(event) =>
                  setDiscount(Math.max(0, Number(event.target.value)))
                }
                className={input}
              />
            </Label>
            <Label title="Pajak (%)">
              <input
                type="number"
                min="0"
                max="100"
                value={tax}
                onChange={(event) =>
                  setTax(Math.min(100, Math.max(0, Number(event.target.value))))
                }
                className={input}
              />
            </Label>
          </div>
          <div className="my-5 space-y-3 border-t border-border pt-5 text-sm">
            <div className="flex justify-between text-muted-foreground">
              <span>Subtotal</span>
              <span>{rupiah(subtotal)}</span>
            </div>
            <div className="flex justify-between font-semibold">
              <span>Total transaksi</span>
              <span className="text-xl text-secondary">{rupiah(total)}</span>
            </div>
          </div>
          <button
            disabled={
              !items.length ||
              total <= 0 ||
              !merchant.trim() ||
              !date ||
              !account ||
              items.some((item) => !item.name.trim())
            }
            onClick={() => {
              save({
                name: merchant,
                amount: Math.round(total),
                account,
                date,
                note: items
                  .map(
                    (item) =>
                      `${item.name} (${item.quantity} × ${rupiah(item.price)})`,
                  )
                  .join("; "),
              })
            }}
            className={`${primary} w-full`}
          >
            <Check size={16} />
            Simpan sebagai transaksi
          </button>
          <p className="mt-3 text-center text-[10px] text-muted-foreground">
            Periksa kembali sebelum menyimpan untuk menghindari duplikasi.
          </p>
        </Card>
      </div>
      {zoom && (
        <div
          className="fixed inset-0 z-[60] flex flex-col items-center justify-center bg-black/90 p-8"
          onClick={() => setZoom(false)}
        >
          <img
            alt="Foto struk ukuran penuh"
            src={image}
            className="max-h-[85vh] max-w-full object-contain"
          />
          <button className="mt-4 rounded-lg bg-muted px-5 py-2 text-sm">
            Tutup foto
          </button>
        </div>
      )}
    </div>
  )
}

export function Profile({
  accounts,
  notify,
  onSave,
}: {
  accounts: Account[]
  notify: (value: string) => void
  onSave: () => void
}) {
  const [profile, setProfile] = useState(() =>
    stored("fintrack-profile", {
      name: "Sarah Anderson",
      nickname: "Sarah",
      email: "sarah@example.com",
      phone: "",
      birthday: "",
      currency: "IDR",
      language: "Indonesia",
      primaryAccount: accounts[0]?.name || "",
      avatar: "",
    }),
  )
  return (
    <form
      onSubmit={(event) => {
        event.preventDefault()
        localStorage.setItem("fintrack-profile", JSON.stringify(profile))
        onSave()
        notify("Profil dan preferensi berhasil disimpan")
      }}
      className="grid gap-5 xl:grid-cols-[1fr_1.6fr]"
    >
      <Card>
        <div className="mx-auto flex size-24 items-center justify-center overflow-hidden rounded-full bg-primary/20 font-display text-3xl text-[#b4c5ff]">
          {profile.avatar ? (
            <img
              src={profile.avatar}
              className="size-full object-cover"
              alt="Foto profil"
            />
          ) : (
            profile.name.slice(0, 2).toUpperCase()
          )}
        </div>
        <h2 className="mt-5 text-center font-display text-xl font-semibold">
          {profile.name}
        </h2>
        <p className="mt-2 text-center text-xs text-muted-foreground">
          Akun personal · Data disimpan lokal
        </p>
        <label className={`${secondary} mt-5 cursor-pointer`}>
          <Upload size={14} />
          Ganti foto
          <input
            className="hidden"
            type="file"
            accept="image/jpeg,image/png"
            onChange={(event) => {
              const file = event.target.files?.[0]
              if (!file) return
              if (file.size > 2 * 1024 * 1024) {
                notify("Foto profil maksimal 2 MB")
                return
              }
              const reader = new FileReader()
              reader.onload = () =>
                setProfile({ ...profile, avatar: String(reader.result) })
              reader.readAsDataURL(file)
            }}
          />
        </label>
        <button
          type="button"
          onClick={() => setProfile({ ...profile, avatar: "" })}
          className="mt-3 w-full text-xs text-muted-foreground"
        >
          Hapus foto
        </button>
        <div className="mt-7 rounded-xl bg-secondary/5 p-4 text-xs leading-6 text-muted-foreground">
          <ShieldCheck size={20} className="mb-2 text-secondary" />
          Email dan nomor telepon hanya disimpan sebagai informasi profil.
          Verifikasi OTP dan autentikasi belum terhubung.
        </div>
      </Card>
      <div className="space-y-5">
        <Card>
          <h3 className="mb-5 font-display font-semibold">Informasi pribadi</h3>
          <div className="grid gap-4 sm:grid-cols-2">
            {[
              { key: "name", title: "Nama lengkap", type: "text" },
              { key: "nickname", title: "Nama panggilan", type: "text" },
              { key: "email", title: "Email", type: "email" },
              { key: "phone", title: "Nomor telepon", type: "tel" },
              { key: "birthday", title: "Tanggal lahir", type: "date" },
            ].map((field) => (
              <Label key={field.key} title={field.title}>
                <input
                  className={input}
                  type={field.type}
                  required={
                    field.key === "name" ||
                    field.key === "nickname" ||
                    field.key === "email"
                  }
                  value={profile[(field.key as keyof typeof profile)]}
                  onChange={(event) =>
                    setProfile({ ...profile, [field.key]: event.target.value })
                  }
                />
              </Label>
            ))}
          </div>
        </Card>
        <Card>
          <h3 className="mb-5 font-display font-semibold">
            Preferensi finansial & regional
          </h3>
          <div className="grid gap-4 sm:grid-cols-2">
            <Label title="Mata uang">
              <select
                className={input}
                value={profile.currency}
                onChange={(event) =>
                  setProfile({ ...profile, currency: event.target.value })
                }
              >
                <option value="IDR">IDR · Rupiah Indonesia</option>
              </select>
            </Label>
            <Label title="Bahasa">
              <select className={input}>
                <option>Indonesia</option>
              </select>
            </Label>
          </div>
          <div className="mt-5">
            <Label title="Rekening utama">
              <select
                className={input}
                value={profile.primaryAccount}
                onChange={(event) =>
                  setProfile({ ...profile, primaryAccount: event.target.value })
                }
              >
                {accounts.map((account, index) => (
                  <option key={index}>{account.name}</option>
                ))}
              </select>
            </Label>
          </div>
          <p className="mt-3 text-[10px] text-muted-foreground">
            Rekening utama digunakan sebagai pilihan awal transaksi baru.
          </p>
        </Card>
        <button className={`${primary} w-full`}>
          <Save size={15} />
          Simpan perubahan
        </button>
      </div>
    </form>
  )
}

export function Widgets({
  balance,
  income,
  expense,
  budgetRemaining,
  notify,
}: {
  balance: number
  income: number
  expense: number
  budgetRemaining: number
  notify: (value: string) => void
}) {
  const [config, setConfig] = useState(() =>
    stored("fintrack-widgets", {
      size: "Saldo cepat",
      theme: "Navy",
      private: false,
      opacity: 100,
    }),
  )
  const [guide, setGuide] = useState(false)
  return (
    <div className="grid gap-5 xl:grid-cols-[1.5fr_1fr]">
      <Card>
        <div className="flex items-center justify-between">
          <h2 className="font-display font-semibold">Pratinjau widget</h2>
          <span className="rounded-full bg-secondary/10 px-2.5 py-1 text-[10px] text-secondary">
            LIVE PREVIEW
          </span>
        </div>
        <div className="mt-6 flex flex-wrap gap-2">
          {["Saldo cepat", "Batas anggaran", "Tren finansial"].map((size) => (
            <button
              key={size}
              onClick={() => setConfig({ ...config, size })}
              className={`rounded-lg px-3 py-2 text-xs ${
                config.size === size
                  ? "bg-primary"
                  : "bg-muted text-muted-foreground"
              }`}
            >
              {size}
            </button>
          ))}
        </div>
        <div className="my-8 flex min-h-72 items-center justify-center rounded-2xl border border-dashed border-border bg-background p-6">
          <div
            className={`w-full max-w-sm rounded-3xl border p-6 shadow-2xl ${
              config.theme === "Mint"
                ? "border-secondary/40 bg-[#113b35] text-white"
                : config.theme === "Terang"
                  ? "border-white bg-[#e7ecfa] text-[#0b1326]"
                  : "border-primary/30 bg-[#1c2e53] text-white"
            } ${
              config.opacity < 40
                ? "opacity-40"
                : config.opacity < 75
                  ? "opacity-70"
                  : "opacity-100"
            }`}
          >
            <div className="flex items-center justify-between">
              <span className="flex items-center gap-2 text-sm font-bold">
                <Wallet size={18} />
                FinTrack
              </span>
              <span className="text-[10px]">PERSONAL</span>
            </div>
            <p className="mt-5 text-xs opacity-70">
              {config.size === "Batas anggaran"
                ? "Anggaran tersisa"
                : "Saldo total tersedia"}
            </p>
            <p
              className={`mt-2 font-display text-3xl font-bold ${
                config.private ? "blur-md select-none" : ""
              }`}
            >
              {rupiah(
                config.size === "Batas anggaran" ? budgetRemaining : balance,
              )}
            </p>
            {config.size === "Tren finansial" && (
              <svg
                viewBox="0 0 300 70"
                className="mt-5 h-16 w-full"
                aria-label="Ilustrasi tren finansial"
              >
                <path
                  d="M0 60 Q30 65 60 40 T120 30 T180 20 T240 15 T300 5"
                  stroke="#7c9cff"
                  strokeWidth="3"
                  fill="none"
                />
                <path
                  d="M0 65 Q35 55 70 60 T140 48 T210 50 T300 30"
                  stroke="#4edea3"
                  strokeWidth="2"
                  fill="none"
                />
              </svg>
            )}
            <div
              className={`mt-5 flex justify-between border-t border-current/15 pt-4 text-xs ${
                config.private ? "blur-md select-none" : ""
              }`}
            >
              <span>↓ {rupiah(income)}</span>
              <span>↑ {rupiah(expense)}</span>
            </div>
          </div>
        </div>
        <p className="text-xs leading-6 text-muted-foreground">
          Widget ini adalah pratinjau dalam aplikasi web, bukan widget native
          iOS atau Android.
        </p>
      </Card>
      <Card>
        <h3 className="font-display font-semibold">Kustomisasi tampilan</h3>
        <div className="mt-6">
          <p className="mb-3 text-xs text-muted-foreground">Tema widget</p>
          <div className="grid grid-cols-3 gap-2">
            {["Navy", "Mint", "Terang"].map((theme) => (
              <button
                key={theme}
                onClick={() => setConfig({ ...config, theme })}
                className={`rounded-lg border px-3 py-3 text-xs ${
                  config.theme === theme
                    ? "border-primary bg-primary/15 text-primary"
                    : "border-border"
                }`}
              >
                {theme}
              </button>
            ))}
          </div>
        </div>
        <div className="mt-6">
          <Label title={`Opasitas latar: ${config.opacity}%`}>
            <input
              type="range"
              min="20"
              max="100"
              value={config.opacity}
              onChange={(event) =>
                setConfig({ ...config, opacity: Number(event.target.value) })
              }
              className="w-full accent-primary"
            />
          </Label>
        </div>
        <label className="mt-6 flex items-center justify-between gap-3 rounded-lg bg-background p-4">
          <span>
            <span className="text-xs">Sembunyikan nominal</span>
            <span className="mt-1 block text-[10px] text-muted-foreground">
              Privasi saat layar dibagikan
            </span>
          </span>
          <input
            type="checkbox"
            checked={config.private}
            onChange={(event) =>
              setConfig({ ...config, private: event.target.checked })
            }
            className="size-4 accent-primary"
          />
        </label>
        <button
          onClick={() => {
            localStorage.setItem("fintrack-widgets", JSON.stringify(config))
            notify("Preferensi widget berhasil disimpan")
          }}
          className={`${primary} mt-6 w-full`}
        >
          <Save size={15} />
          Simpan konfigurasi
        </button>
        <button
          onClick={() => setGuide(!guide)}
          className="mt-5 flex w-full items-center justify-between text-xs text-muted-foreground"
        >
          Panduan akses cepat
          <ChevronRight size={15} />
        </button>
        {guide && (
          <p className="mt-3 rounded-lg bg-muted p-4 text-xs leading-6 text-muted-foreground">
            Buka menu browser, lalu pilih “Tambahkan ke layar utama” jika
            tersedia. Pintasan membuka aplikasi web; pemasangan widget native
            membutuhkan aplikasi mobile.
          </p>
        )}
      </Card>
    </div>
  )
}

export function Markets({ notify }: { notify: (value: string) => void }) {
  const [weight, setWeight] = useState(1)
  const [currency, setCurrency] = useState("USD")
  const [amount, setAmount] = useState(100)
  const [reverse, setReverse] = useState(false)
  const [alerts, setAlerts] = useState<{
    asset: string
    price: number
    direction: string
  }[]>(() => stored("fintrack-price-alerts", []))
  const rates: Record<string, number> = {
    USD: 15620,
    EUR: 16880,
    SGD: 11830,
    JPY: 103,
    AUD: 10390,
    SAR: 4165,
    GBP: 20250,
  }
  return (
    <div className="space-y-5">
      <div className="rounded-xl border border-amber-400/20 bg-amber-400/5 px-4 py-3 text-xs leading-5 text-amber-200">
        Data contoh · Snapshot Oktober 2024, bukan harga terkini. Konversi hanya
        simulasi. Peringatan disimpan lokal; pemantauan harga otomatis belum
        terhubung.
      </div>
      <div className="grid gap-5 xl:grid-cols-2">
        <Card className="border-amber-300/20 bg-gradient-to-br from-[#302b25] to-card">
          <div className="flex justify-between">
            <span className="flex items-center gap-2 text-sm font-semibold text-amber-200">
              <Coins size={22} />
              Emas Antam 24K
            </span>
            <span className="rounded-full bg-amber-300/10 px-3 py-1 text-[10px] text-amber-200">
              99,99% MURNI
            </span>
          </div>
          <p className="mt-7 text-xs text-muted-foreground">
            Harga jual · {weight} gram
          </p>
          <h2 className="mt-3 font-display text-4xl font-bold text-amber-100">
            {rupiah(1485000 * weight)}
          </h2>
          <div className="mt-6 flex flex-wrap gap-2">
            {[0.5, 1, 5, 10, 25, 100].map((value) => (
              <button
                key={value}
                onClick={() => setWeight(value)}
                className={`rounded-lg border px-3 py-2 text-xs ${
                  weight === value
                    ? "border-amber-300/40 bg-amber-300/15 text-amber-200"
                    : "border-border text-muted-foreground"
                }`}
              >
                {value} g
              </button>
            ))}
          </div>
          <div className="mt-6 grid grid-cols-2 gap-3 rounded-xl bg-background/40 p-4">
            <div>
              <p className="text-[10px] text-muted-foreground">
                Buyback (contoh)
              </p>
              <p className="mt-2 text-sm">{rupiah(1321000 * weight)}</p>
            </div>
            <div>
              <p className="text-[10px] text-muted-foreground">
                PPh 22 (simulasi 0,25%)
              </p>
              <p className="mt-2 text-sm">
                {rupiah(1485000 * weight * 0.0025)}
              </p>
            </div>
          </div>
          <p className="mt-4 text-[10px] text-muted-foreground">
            Harga dan pajak final mengikuti ketentuan penyedia.
          </p>
        </Card>
        <Card>
          <div className="flex items-center gap-2">
            <ArrowLeftRight size={20} className="text-primary" />
            <h3 className="font-display font-semibold">
              Kalkulator kurs valas
            </h3>
          </div>
          <div className="mt-6 flex gap-3">
            <input
              aria-label="Jumlah konversi"
              type="number"
              min="0"
              value={amount}
              onChange={(event) =>
                setAmount(Math.max(0, Number(event.target.value)))
              }
              className={input}
            />
            <select
              aria-label="Mata uang"
              value={currency}
              onChange={(event) => setCurrency(event.target.value)}
              className="rounded-lg border border-border bg-background px-3 text-sm"
            >
              {Object.keys(rates).map((value) => (
                <option key={value}>{value}</option>
              ))}
            </select>
          </div>
          <button
            onClick={() => setReverse(!reverse)}
            className="mt-4 flex items-center gap-2 text-xs text-primary"
          >
            <ArrowLeftRight size={14} />
            {reverse ? `IDR → ${currency}` : `${currency} → IDR`} · Tukar arah
          </button>
          <div className="mt-5 rounded-xl bg-primary/10 p-5">
            <p className="text-xs text-muted-foreground">
              Hasil konversi estimasi
            </p>
            <p className="mt-3 text-3xl font-semibold">
              {reverse
                ? `${(amount / rates[currency]).toLocaleString("id-ID", { maximumFractionDigits: 2 })} ${currency}`
                : rupiah(amount * rates[currency])}
            </p>
            <p className="mt-3 text-[10px] text-muted-foreground">
              1 {currency} = {rupiah(rates[currency])} · Belum termasuk spread /
              biaya
            </p>
          </div>
        </Card>
      </div>
      <Card>
        <h3 className="mb-5 font-display font-semibold">
          Mata uang utama dunia
        </h3>
        <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          {Object.entries(rates).map(([name, value]) => (
            <button
              key={name}
              onClick={() => {
                setCurrency(name)
                setReverse(false)
              }}
              className="flex items-center justify-between rounded-xl border border-border bg-background/40 p-4 text-left hover:border-primary/40"
            >
              <span>
                <span className="text-sm font-semibold">{name}</span>
                <span className="mt-1 block text-[10px] text-muted-foreground">
                  Terhadap IDR
                </span>
              </span>
              <span className="text-sm">{rupiah(value)}</span>
            </button>
          ))}
        </div>
      </Card>
      <Card>
        <h3 className="flex items-center gap-2 font-display font-semibold">
          <Bell size={18} className="text-primary" />
          Target harga personal
        </h3>
        <form
          className="mt-5 grid items-end gap-3 sm:grid-cols-4"
          onSubmit={(event) => {
            event.preventDefault()
            const data = new FormData(event.currentTarget)
            const next = [
              ...alerts,
              {
                asset: String(data.get("asset")),
                price: Number(data.get("price")),
                direction: String(data.get("direction")),
              },
            ]
            setAlerts(next)
            localStorage.setItem("fintrack-price-alerts", JSON.stringify(next))
            notify("Target harga disimpan. Pemantauan otomatis belum aktif.")
          }}
        >
          <Label title="Aset">
            <select name="asset" className={input}>
              <option>Emas (1 gram)</option>
              {Object.keys(rates).map((value) => (
                <option key={value}>{value}</option>
              ))}
            </select>
          </Label>
          <Label title="Kondisi">
            <select name="direction" className={input}>
              <option>Di bawah</option>
              <option>Di atas</option>
            </select>
          </Label>
          <Label title="Harga target (Rp)">
            <input
              name="price"
              type="number"
              min="1"
              required
              className={input}
              placeholder="1500000"
            />
          </Label>
          <button className={primary}>
            <Plus size={15} />
            Simpan target
          </button>
        </form>
        <div className="mt-5 space-y-2">
          {alerts.map((alert, index) => (
            <div
              key={index}
              className="flex items-center justify-between rounded-lg bg-muted p-3 text-xs"
            >
              <span>
                {alert.asset} · {alert.direction.toLowerCase()}{" "}
                {rupiah(alert.price)}
              </span>
              <button
                aria-label="Hapus target harga"
                onClick={() => {
                  const next = alerts.filter(
                    (_, itemIndex) => itemIndex !== index,
                  )
                  setAlerts(next)
                  localStorage.setItem(
                    "fintrack-price-alerts",
                    JSON.stringify(next),
                  )
                }}
                className="p-1 text-muted-foreground"
              >
                <Trash2 size={14} />
              </button>
            </div>
          ))}
        </div>
      </Card>
    </div>
  )
}

export function ReportBreakdown({
  transactions,
}: {
  transactions: {
    amount: number
    category: string
  }[]
}) {
  const [selected, setSelected] = useState("")
  const totals = transactions
    .filter((item) => item.amount < 0)
    .reduce<Record<string, number>>(
      (result, item) => ({
        ...result,
        [item.category]: (result[item.category] || 0) - item.amount,
      }),
      {},
    )
  const total = Object.values(totals).reduce((sum, amount) => sum + amount, 0)
  const colors = [
    "#5b83ff",
    "#4edea3",
    "#fbbf24",
    "#c084fc",
    "#fb923c",
    "#94a3b8",
  ]
  let offset = 0
  const income = transactions
    .filter((item) => item.amount > 0)
    .reduce((sum, item) => sum + item.amount, 0)
  return (
    <div className="mt-5 grid gap-5 xl:grid-cols-[1.4fr_1fr]">
      <Card>
        <h3 className="font-display font-semibold">Breakdown pengeluaran</h3>
        <p className="mt-2 text-xs text-muted-foreground">
          Dihitung dari transaksi yang tercatat, bukan saldo awal contoh.
        </p>
        <div className="mt-6 flex flex-wrap items-center gap-7">
          <div className="relative size-44 shrink-0">
            <svg
              viewBox="0 0 120 120"
              className="size-full -rotate-90"
              aria-label="Distribusi pengeluaran"
            >
              <circle
                cx="60"
                cy="60"
                r="48"
                fill="none"
                stroke="#253047"
                strokeWidth="12"
              />
              {Object.entries(totals).map(([category, value], index) => {
                const length = total ? (value / total) * 301.59 : 0
                const start = offset
                offset += length
                return (
                  <circle
                    key={category}
                    cx="60"
                    cy="60"
                    r="48"
                    fill="none"
                    stroke={colors[index % colors.length]}
                    strokeWidth={selected === category ? 16 : 12}
                    strokeDasharray={`${length} ${301.59 - length}`}
                    strokeDashoffset={-start}
                  />
                )
              })}
            </svg>
            <div className="absolute inset-0 flex flex-col items-center justify-center">
              <span className="text-[10px] text-muted-foreground">
                {selected || "Total pengeluaran"}
              </span>
              <span className="mt-2 text-sm font-semibold">
                {rupiah(selected ? totals[selected] : total)}
              </span>
            </div>
          </div>
          <div className="min-w-52 flex-1 space-y-2">
            {Object.entries(totals).map(([category, value], index) => (
              <button
                key={category}
                onClick={() =>
                  setSelected(selected === category ? "" : category)
                }
                className={`flex w-full items-center justify-between gap-3 rounded-lg p-3 text-xs ${
                  selected === category ? "bg-muted" : "hover:bg-muted/50"
                }`}
              >
                <span className="flex items-center gap-2">
                  <svg width="8" height="8">
                    <circle
                      cx="4"
                      cy="4"
                      r="4"
                      fill={colors[index % colors.length]}
                    />
                  </svg>
                  {category}
                </span>
                <span className="text-muted-foreground">
                  {((value / total) * 100).toFixed(1)}%
                </span>
              </button>
            ))}
          </div>
        </div>
      </Card>
      <Card>
        <h3 className="font-display font-semibold">
          Panduan alokasi 50 / 30 / 20
        </h3>
        <p className="mt-2 text-xs leading-6 text-muted-foreground">
          Target ideal berdasarkan pemasukan tercatat {rupiah(income)}. Panduan,
          bukan hasil klasifikasi otomatis.
        </p>
        <div className="mt-5 flex h-3 overflow-hidden rounded-full">
          <span className="w-1/2 bg-primary" />
          <span className="w-[30%] bg-amber-400" />
          <span className="w-1/5 bg-secondary" />
        </div>
        <div className="mt-6 space-y-5">
          {[
            { name: "Kebutuhan", fraction: 0.5 },
            { name: "Keinginan", fraction: 0.3 },
            { name: "Tabungan & investasi", fraction: 0.2 },
          ].map((item) => (
            <div key={item.name} className="flex justify-between text-xs">
              <span className="text-muted-foreground">
                {item.name} · {item.fraction * 100}%
              </span>
              <span>{rupiah(income * item.fraction)}</span>
            </div>
          ))}
        </div>
        <button
          onClick={() => window.print()}
          className={`${secondary} mt-7 w-full`}
        >
          <FileText size={15} />
          Cetak / simpan PDF via browser
        </button>
      </Card>
    </div>
  )
}
