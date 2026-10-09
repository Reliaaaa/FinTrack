import { useEffect, useState } from "react"
import {
  Check,
  Copy,
  Download,
  FileText,
  Share2,
  Upload,
  ShieldCheck,
} from "lucide-react"
import { Button, Card, Field, Heading, Input, Select } from "./Controls"
import { loadLocal, type FinanceTransaction } from "./AdvancedFeatures"
import pdfWorker from "pdfjs-dist/build/pdf.worker.min.mjs?url"

type Row = Omit<FinanceTransaction, "id">
type Account = {
  name: string
  type: string
  amount: number
}
export type ExportArtifact = {
  blob: Blob
  filename: string
  count: number
  hash: string
  created: string
}
export async function createReportPDF(
  rows: FinanceTransaction[],
  start: string,
  end: string,
  notes: boolean,
  evaluation: boolean,
) {
  const [{ jsPDF }, { default: autoTable }] = await Promise.all([
    import("jspdf"),
    import("jspdf-autotable"),
  ])
  const doc = new jsPDF()
  const rupiah = (n: number) => "Rp " + n.toLocaleString("id-ID")
  const income = rows
    .filter((t) => t.amount > 0)
    .reduce((s, t) => s + t.amount, 0)
  const expense = -rows
    .filter((t) => t.amount < 0)
    .reduce((s, t) => s + t.amount, 0)
  doc.setFontSize(20)
  doc.text("FinTrack | Laporan Keuangan", 14, 22)
  doc.setFontSize(11)
  doc.text(`${start} - ${end} | ${rows.length} transaksi`, 14, 32)
  autoTable(doc, {
    startY: 42,
    head: [["Ringkasan", "Nominal"]],
    body: [
      ["Pemasukan", rupiah(income)],
      ["Pengeluaran", rupiah(expense)],
      ["Surplus / defisit", rupiah(income - expense)],
    ],
  })
  if (evaluation)
    autoTable(doc, {
      startY: 105,
      head: [["Panduan 50/30/20", "Target berdasarkan pemasukan"]],
      body: [
        ["Kebutuhan maksimal 50%", rupiah(income * 0.5)],
        ["Keinginan maksimal 30%", rupiah(income * 0.3)],
        ["Tabungan minimal 20%", rupiah(income * 0.2)],
      ],
    })
  doc.addPage()
  doc.setFontSize(18)
  doc.text("Alokasi Pengeluaran", 14, 22)
  const categories = rows
    .filter((t) => t.amount < 0)
    .reduce<Record<string, number>>(
      (all, t) => ({ ...all, [t.category]: (all[t.category] || 0) - t.amount }),
      {},
    )
  autoTable(doc, {
    startY: 32,
    head: [["Kategori", "Nominal", "Proporsi"]],
    body: Object.entries(categories).map(([name, total]) => [
      name,
      rupiah(total),
      expense ? ((total / expense) * 100).toFixed(1) + "%" : "0%",
    ]),
  })
  doc.addPage()
  doc.text("Jurnal Transaksi", 14, 22)
  autoTable(doc, {
    startY: 32,
    styles: { fontSize: 8 },
    head: [
      [
        "Tanggal",
        "Transaksi",
        "Kategori",
        "Rekening",
        "Jumlah",
        ...(notes ? ["Catatan"] : []),
      ],
    ],
    body: rows.map((t) => [
      t.date,
      t.name,
      t.category,
      t.account,
      rupiah(t.amount),
      ...(notes ? [t.note || ""] : []),
    ]),
  })
  for (let page = 1; page <= doc.getNumberOfPages(); page++) {
    doc.setPage(page)
    doc.setFontSize(8)
    doc.text(
      `Data lokal FinTrack, bukan dokumen tersertifikasi. Hal ${page}/${doc.getNumberOfPages()}`,
      14,
      285,
    )
  }
  return doc.output("blob")
}

export function ExportReady({
  artifact,
  notify,
}: {
  artifact: ExportArtifact
  notify: (text: string) => void
}) {
  const [url, setUrl] = useState("")
  useEffect(() => {
    const value = URL.createObjectURL(artifact.blob)
    setUrl(value)
    return () => URL.revokeObjectURL(value)
  }, [artifact])
  function save() {
    const anchor = document.createElement("a")
    anchor.href = url
    anchor.download = artifact.filename
    anchor.click()
    notify("Unduhan diminta; periksa folder unduhan browser")
  }
  async function share() {
    const file = new File([artifact.blob], artifact.filename, {
      type: artifact.blob.type,
    })
    try {
      if (navigator.canShare?.({ files: [file] }))
        await navigator.share({ files: [file], title: "Laporan FinTrack" })
      else
        notify(
          "Berbagi berkas tidak didukung browser ini. Unduh lalu lampirkan secara manual.",
        )
    } catch (error) {
      if (!(error instanceof DOMException && error.name === "AbortError"))
        notify("Berkas gagal dibagikan")
    }
  }
  return (
    <Card className="mt-5 border-secondary/30 bg-secondary/5">
      <div className="flex items-center gap-3">
        <Check className="text-secondary" />
        <Heading>Berkas siap dibuka & dibagikan</Heading>
      </div>
      <p className="mt-3 font-semibold">{artifact.filename}</p>
      <p className="mt-2 text-xs text-muted-foreground">
        {(artifact.blob.size / 1024).toFixed(1)} KB · {artifact.count} transaksi
        · {new Date(artifact.created).toLocaleString("id-ID")}
      </p>
      <div className="mt-5 flex flex-wrap gap-2">
        <Button
          disabled={!url}
          onClick={() => window.open(url, "_blank", "noopener,noreferrer")}
        >
          <FileText size={16} />
          Buka dokumen
        </Button>
        <Button disabled={!url} onClick={save}>
          <Download size={16} />
          Simpan berkas
        </Button>
        <Button onClick={() => void share()}>
          <Share2 size={16} />
          Bagikan berkas
        </Button>
        <Button
          onClick={() => {
            window.open(
              `https://wa.me/?text=${encodeURIComponent("Laporan FinTrack: " + artifact.filename + ". Lampirkan berkas setelah mengunduh.")}`,
              "_blank",
              "noopener,noreferrer",
            )
          }}
        >
          WhatsApp (pesan)
        </Button>
        <Button
          onClick={() => {
            window.location.href = `mailto:?subject=${encodeURIComponent("Laporan FinTrack")}&body=${encodeURIComponent("Laporan: " + artifact.filename + ". Silakan lampirkan berkas yang sudah diunduh.")}`
          }}
        >
          Email (manual)
        </Button>
        <Button
          onClick={() =>
            window.open(
              "https://drive.google.com",
              "_blank",
              "noopener,noreferrer",
            )
          }
        >
          Drive (unggah manual)
        </Button>
      </div>
      <p className="mt-4 text-xs text-muted-foreground">
        Email tidak dikirim otomatis. SHA-256 adalah sidik berkas, bukan tanda
        tangan atau stempel digital.
      </p>
      <p className="mt-3 break-all font-mono text-xs text-muted-foreground">
        {artifact.hash || "SHA-256 tidak tersedia pada browser ini."}
      </p>
      <Button
        className="mt-3"
        disabled={!artifact.hash}
        onClick={async () => {
          try {
            await navigator.clipboard.writeText(artifact.hash)
            notify("SHA-256 disalin")
          } catch {
            notify("Clipboard tidak tersedia")
          }
        }}
      >
        <Copy size={14} />
        Salin sidik berkas
      </Button>
    </Card>
  )
}

export function parseStatementLines(
  text: string,
  account: string,
  year: string,
): Row[] {
  return text.split("\n").flatMap((line) => {
    const match = line.match(
      /^\s*(\d{2})[/-](\d{2})(?:[/-](\d{4}))?\s+(.+?)\s+([\d.]+,\d{2})\s+(DB|CR|D|K)(?:\s|$)/i,
    )
    if (!match) return []
    const date = `${match[3] || year}-${match[2]}-${match[1]}`
    const amount = Number(match[5].replace(/\./g, "").replace(",", "."))
    if (
      !Number.isFinite(amount) ||
      amount <= 0 ||
      !/^\d{4}$/.test(year) ||
      Number.isNaN(Date.parse(date)) ||
      new Date(date).toISOString().slice(0, 10) !== date
    )
      return []
    return [
      {
        date,
        name: match[4].trim(),
        category: /^(CR|K)$/i.test(match[6]) ? "Pemasukan" : "Lainnya",
        account,
        amount: amount * (/^(DB|D)$/i.test(match[6]) ? -1 : 1),
        note: "Diimpor dari PDF; ditinjau manual",
      },
    ]
  })
}

export function PDFStatement({
  accounts,
  transactions,
  importRows,
  notify,
}: {
  accounts: Account[]
  transactions: FinanceTransaction[]
  importRows: (rows: Row[]) => void
  notify: (text: string) => void
}) {
  const [file, setFile] = useState<File | null>(null)
  const [account, setAccount] = useState(accounts[0]?.name || "")
  const [bank, setBank] = useState("BCA")
  const [password, setPassword] = useState("")
  const [year, setYear] = useState(String(new Date().getFullYear()))
  const [rows, setRows] = useState<Row[]>([])
  const [text, setText] = useState("")
  const [busy, setBusy] = useState(false)
  const [status, setStatus] = useState("")
  const [pages, setPages] = useState(0)
  const [history, setHistory] = useState<{
    name: string
    count: number
    date: string
    bank: string
  }[]>(() => loadLocal("fintrack-statement-history", []))
  const duplicate = (r: Row, index: number) =>
    [...transactions, ...rows.slice(0, index)].some(
      (t) =>
        t.date === r.date &&
        t.amount === r.amount &&
        t.account === r.account &&
        t.name.trim().toLowerCase() === r.name.trim().toLowerCase(),
    )
  async function process() {
    if (
      !file ||
      file.size > 25 * 1024 * 1024 ||
      !accounts.some((a) => a.name === account)
    )
      return notify("Pilih PDF maksimal 25 MB dan rekening tujuan")
    setBusy(true)
    setRows([])
    setText("")
    setStatus("Membaca PDF di perangkat…")
    let task: import("pdfjs-dist").PDFDocumentLoadingTask | undefined
    try {
      const pdfjs = await import("pdfjs-dist")
      pdfjs.GlobalWorkerOptions.workerSrc = pdfWorker
      task = pdfjs.getDocument({
        data: new Uint8Array(await file.arrayBuffer()),
        password,
      })
      const doc = await task.promise
      if (doc.numPages > 100) throw new Error("PDF maksimal 100 halaman")
      setPages(doc.numPages)
      const lines: string[] = []
      for (let page = 1; page <= doc.numPages; page++) {
        setStatus(`Membaca halaman ${page} / ${doc.numPages}`)
        const content = await (await doc.getPage(page)).getTextContent()
        const grouped = new Map<number, {
          x: number
          value: string
        }[]>()
        for (const item of content.items)
          if ("str" in item) {
            const y = Math.round(item.transform[5] / 3) * 3
            grouped.set(y, [
              ...(grouped.get(y) || []),
              { x: item.transform[4], value: item.str },
            ])
          }
        lines.push(
          ...[...grouped]
            .sort((a, b) => b[0] - a[0])
            .map(([, items]) =>
              items
                .sort((a, b) => a.x - b.x)
                .map((i) => i.value)
                .join(" "),
            ),
        )
      }
      const extracted = lines.join("\n")
      setText(extracted)
      const parsed = parseStatementLines(extracted, account, year)
      setRows(parsed)
      setStatus(
        parsed.length
          ? `${parsed.length} kandidat mutasi. Periksa setiap tanggal, nominal, dan arah debit/kredit.`
          : "Tidak ada baris yang dikenali. PDF gambar membutuhkan OCR yang belum tersedia; gunakan CSV atau tambah mutasi manual.",
      )
    } catch (error) {
      setStatus(
        error instanceof Error && error.name === "PasswordException"
          ? "PDF terkunci: sandi kosong atau salah. Masukkan sandi lalu coba lagi."
          : error instanceof Error
            ? error.message
            : "PDF gagal dibaca",
      )
    } finally {
      setPassword("")
      try {
        await task?.destroy()
      } finally {
        setBusy(false)
      }
    }
  }
  return (
    <div
      className="mb-5 space-y-5"
      onDragOver={(e) => e.preventDefault()}
      onDrop={(e) => {
        e.preventDefault()
        if (busy) return
        const dropped = e.dataTransfer.files[0]
        if (
          !dropped ||
          !dropped.name.toLowerCase().endsWith(".pdf") ||
          dropped.size > 25 * 1024 * 1024
        ) {
          notify("Letakkan berkas PDF maksimal 25 MB")
          return
        }
        setFile(dropped)
        setRows([])
        setText("")
        setStatus("")
        setPages(0)
      }}
    >
      <Card>
        <div className="flex items-center gap-3">
          <FileText className="text-primary" />
          <Heading>Unggah e-statement PDF</Heading>
        </div>
        <p className="mt-3 text-xs text-primary">
          Letakkan PDF di panel ini atau pilih berkas di bawah.
          {file ? ` Berkas terpilih: ${file.name}` : ""}
        </p>
        <p className="mt-3 text-sm text-muted-foreground">
          PDF berbasis teks diproses lokal. Parser konservatif mengenali tanggal
          DD/MM/YYYY, nominal 1.000,00, dan penanda DB/CR atau D/K. Format bank
          lain wajib dikoreksi manual; bukan OCR atau AI.
        </p>
        <div className="mt-5 grid gap-4 sm:grid-cols-2">
          <Field title="Dokumen PDF · maksimal 25 MB">
            <Input
              type="file"
              accept=".pdf,application/pdf"
              disabled={busy}
              onChange={(e) => {
                setFile(e.target.files?.[0] || null)
                setRows([])
                setText("")
                setStatus("")
                setPages(0)
              }}
            />
          </Field>
          <Field title="Bank penerbit (label arsip)">
            <Select
              value={bank}
              disabled={busy || rows.length > 0}
              onChange={(e) => setBank(e.target.value)}
            >
              {[
                "BCA",
                "Mandiri",
                "BRI",
                "BNI",
                "Jago",
                "Jenius",
                "Blu",
                "Lainnya",
              ].map((b) => (
                <option key={b}>{b}</option>
              ))}
            </Select>
          </Field>
          <Field title="Simpan ke rekening">
            <Select
              value={account}
              disabled={busy || rows.length > 0}
              onChange={(e) => setAccount(e.target.value)}
            >
              {accounts.map((a) => (
                <option key={a.name}>{a.name}</option>
              ))}
            </Select>
          </Field>
          <Field title="Sandi PDF (tidak disimpan)">
            <Input
              type="password"
              autoComplete="off"
              value={password}
              disabled={busy}
              onChange={(e) => setPassword(e.target.value)}
            />
          </Field>
          <Field title="Tahun untuk tanggal tanpa tahun">
            <Input
              type="number"
              min={1900}
              max={2100}
              value={year}
              disabled={busy}
              onChange={(e) => setYear(e.target.value)}
            />
          </Field>
        </div>
        <Button
          className="mt-5 bg-primary text-primary-foreground"
          disabled={!file || busy}
          onClick={() => void process()}
        >
          <Upload size={16} />
          {busy ? "Memproses…" : "Baca & proses PDF"}
        </Button>
        <p role="status" className="mt-4 text-sm text-muted-foreground">
          {status}
        </p>
        {text && (
          <details className="mt-4 text-xs text-muted-foreground">
            <summary className="cursor-pointer">
              Teks hasil ekstraksi · {pages} halaman
            </summary>
            <pre className="mt-3 max-h-60 overflow-auto whitespace-pre-wrap rounded-lg bg-background p-4">
              {text}
            </pre>
          </details>
        )}
      </Card>
      {file && !busy && (
        <Card>
          <Heading>Tinjau & koreksi mutasi</Heading>
          <p className="mt-2 text-xs text-muted-foreground">
            Nilai negatif = pengeluaran. Baris duplikat tidak diimpor. Baris
            kosong/tidak valid harus dihapus atau diperbaiki.
          </p>
          {rows.map((r, index) => (
            <div
              key={index}
              className="mt-4 grid gap-3 rounded-xl border border-border p-4 sm:grid-cols-2"
            >
              <Field title="Tanggal">
                <Input
                  type="date"
                  value={r.date}
                  onChange={(e) =>
                    setRows(
                      rows.map((v, i) =>
                        i === index ? { ...v, date: e.target.value } : v,
                      ),
                    )
                  }
                />
              </Field>
              <Field title="Nama transaksi">
                <Input
                  value={r.name}
                  onChange={(e) =>
                    setRows(
                      rows.map((v, i) =>
                        i === index ? { ...v, name: e.target.value } : v,
                      ),
                    )
                  }
                />
              </Field>
              <Field title="Nominal bertanda (Rp)">
                <Input
                  type="number"
                  value={r.amount}
                  onChange={(e) =>
                    setRows(
                      rows.map((v, i) =>
                        i === index
                          ? { ...v, amount: Number(e.target.value) }
                          : v,
                      ),
                    )
                  }
                />
              </Field>
              <Field title="Kategori">
                <Select
                  value={r.category}
                  onChange={(e) =>
                    setRows(
                      rows.map((v, i) =>
                        i === index ? { ...v, category: e.target.value } : v,
                      ),
                    )
                  }
                >
                  {[
                    "Pemasukan",
                    "Lainnya",
                    "Makanan & Minuman",
                    "Belanja",
                    "Transportasi",
                    "Tagihan & Utilitas",
                    "Biaya Bank",
                  ].map((c) => (
                    <option key={c}>{c}</option>
                  ))}
                </Select>
              </Field>
              <p className="text-xs text-destructive">
                {duplicate(r, index) ? "Duplikat: akan dilewati" : ""}
              </p>
              <Button
                onClick={() => setRows(rows.filter((_, i) => i !== index))}
              >
                Hapus baris
              </Button>
            </div>
          ))}
          <div className="mt-5 flex flex-wrap gap-3">
            <Button
              onClick={() =>
                setRows([
                  ...rows,
                  {
                    date: "",
                    name: "",
                    category: "Lainnya",
                    account,
                    amount: 0,
                  },
                ])
              }
            >
              Tambah mutasi manual
            </Button>
            <Button
              disabled={!rows.length}
              className="bg-primary text-primary-foreground"
              onClick={() => {
                if (
                  rows.some(
                    (r) =>
                      !r.name.trim() ||
                      !Number.isFinite(r.amount) ||
                      r.amount === 0 ||
                      !/^\d{4}-\d{2}-\d{2}$/.test(r.date) ||
                      Number.isNaN(Date.parse(r.date)) ||
                      new Date(r.date).toISOString().slice(0, 10) !== r.date,
                  )
                )
                  return notify(
                    "Perbaiki nama, tanggal, dan nominal semua baris",
                  )
                const unique = rows.filter((r, i) => !duplicate(r, i))
                if (!unique.length) return notify("Semua mutasi sudah tercatat")
                importRows(unique)
                const next = [
                  {
                    name: file!.name,
                    count: unique.length,
                    date: new Date().toISOString(),
                    bank,
                  },
                  ...history,
                ].slice(0, 30)
                localStorage.setItem(
                  "fintrack-statement-history",
                  JSON.stringify(next),
                )
                setHistory(next)
                setRows([])
                setFile(null)
                setText("")
                setStatus("")
                notify(`${unique.length} mutasi PDF diimpor`)
              }}
            >
              Konfirmasi & impor mutasi
            </Button>
          </div>
        </Card>
      )}
      <Card>
        <Heading>Riwayat impor PDF</Heading>
        {history.length ? (
          history.map((h, i) => (
            <div key={i} className="mt-3 border-b border-border pb-3 text-sm">
              {h.name}
              <p className="mt-1 text-xs text-muted-foreground">
                {h.bank} · {h.count} mutasi ·{" "}
                {new Date(h.date).toLocaleDateString("id-ID")}
              </p>
            </div>
          ))
        ) : (
          <p className="mt-3 text-sm text-muted-foreground">
            Belum ada PDF yang diimpor.
          </p>
        )}
        <p className="mt-4 flex items-center gap-2 text-xs text-muted-foreground">
          <ShieldCheck size={15} />
          Sandi dan berkas PDF tidak disimpan. Riwayat hanya menyimpan metadata
          impor.
        </p>
      </Card>
    </div>
  )
}

export default PDFStatement
