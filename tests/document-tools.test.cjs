const assert = require("node:assert/strict")
const test = require("node:test")
const fs = require("node:fs")
const path = require("node:path")
const Module = require("node:module")
const ts = require("typescript")

// Load the exported data helpers without mounting browser-only React controls.
const filename = path.resolve("src/app/DocumentTools.tsx")
const compiled = ts.transpileModule(fs.readFileSync(filename, "utf8"), {
  compilerOptions: {
    module: ts.ModuleKind.CommonJS,
    target: ts.ScriptTarget.ES2020,
    jsx: ts.JsxEmit.ReactJSX,
  },
}).outputText
const helperModule = new Module(filename, module)
helperModule.filename = filename
helperModule.paths = Module._nodeModulePaths(path.dirname(filename))
const originalRequire = helperModule.require.bind(helperModule)
helperModule.require = (id) =>
  id === "./Controls" || id === "./AdvancedFeatures" || id.endsWith("?url")
    ? {}
    : originalRequire(id)
helperModule._compile(compiled, filename)
const { parseStatementLines, createReportPDF } = helperModule.exports

test("parser recognizes explicit debit/credit and ignores uncertain or invalid rows", () => {
  const rows = parseStatementLines(
    [
      "24/10/2024 KOPI KENANGAN 25.000,00 DB",
      "25/10 GAJI BULANAN 15.000.000,00 CR",
      "26/10/2024 PARKIR 5.000,00 D 1.000.000,00",
      "27/10/2024 REFUND 10.000,00 K",
      "31/02/2024 INVALID 10.000,00 DB",
      "28/10/2024 UNCERTAIN 20.000,00",
    ].join("\n"),
    "BCA",
    "2024",
  )
  assert.equal(rows.length, 4)
  assert.deepEqual(
    rows.map((r) => r.amount),
    [-25000, 15000000, -5000, 10000],
  )
  assert.equal(rows[1].date, "2024-10-25")
  assert.equal(rows[1].category, "Pemasukan")
  assert.ok(rows.every((r) => r.account === "BCA"))
})

test("report exports a real multipage PDF including journal pagination", async () => {
  const rows = Array.from({ length: 120 }, (_, i) => ({
    id: i,
    date: "2024-10-24",
    name: "Transaksi " + i,
    category: "Belanja",
    account: "BCA",
    amount: -25000,
    note: "Catatan uji",
  }))
  const blob = await createReportPDF(
    rows,
    "2024-10-01",
    "2024-10-31",
    true,
    true,
  )
  assert.equal(blob.type, "application/pdf")
  const text = Buffer.from(await blob.arrayBuffer()).toString("latin1")
  assert.ok(text.startsWith("%PDF-"))
  assert.ok(text.includes("Transaksi 119"))
  assert.ok((text.match(/\/Type \/Page\b/g) || []).length > 3)
  const empty = await createReportPDF(
    [],
    "2024-10-01",
    "2024-10-31",
    false,
    false,
  )
  assert.ok(empty.size > 0)
})

test("PDF.js extracts statement text locally before parsing mutasi", async () => {
  const { jsPDF } = require("jspdf")
  const { getDocument } = await import("pdfjs-dist/legacy/build/pdf.mjs")
  const pdf = new jsPDF()
  pdf.text("24/10/2024 KOPI KENANGAN 25.000,00 DB", 14, 20)
  pdf.text("25/10/2024 GAJI 15.000.000,00 CR", 14, 30)
  const task = getDocument({
    data: new Uint8Array(pdf.output("arraybuffer")),
    useSystemFonts: true,
  })
  try {
    const document = await task.promise
    const content = await (await document.getPage(1)).getTextContent()
    const text = content.items
      .filter((item) => "str" in item)
      .map((item) => item.str)
      .join("\n")
    const rows = parseStatementLines(text, "Mandiri", "2024")
    assert.deepEqual(
      rows.map((row) => row.amount),
      [-25000, 15000000],
    )
    assert.ok(rows.every((row) => row.account === "Mandiri"))
  } finally {
    await task.destroy()
  }
})
