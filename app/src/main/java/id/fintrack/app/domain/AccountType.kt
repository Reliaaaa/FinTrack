package id.fintrack.app.domain

enum class AccountType(val label: String) {
    TUNAI("Tunai"),
    BANK("Rekening bank"),
    DOMPET("Dompet digital"),
    LAINNYA("Lainnya");

    companion object {
        fun fromName(name: String): AccountType = values().firstOrNull { it.name == name } ?: LAINNYA
    }
}
