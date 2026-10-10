package com.example.fintrack.network.model

import com.google.gson.annotations.SerializedName

data class MarketQuote(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("change_percent") val changePercent: Double,
    @SerializedName("last_updated") val lastUpdated: String
)

data class GoldPriceResponse(
    @SerializedName("buy_price_per_gram") val buyPricePerGram: Long,
    @SerializedName("sell_price_per_gram") val sellPricePerGram: Long,
    @SerializedName("change_amount") val changeAmount: Long,
    @SerializedName("change_percent") val changePercent: Double,
    @SerializedName("source") val source: String = "Antam LM Official",
    @SerializedName("as_of") val asOf: String
)

data class ForexRatesResponse(
    @SerializedName("base") val base: String = "IDR",
    @SerializedName("rates") val rates: Map<String, Double>,
    @SerializedName("timestamp") val timestamp: Long
)
