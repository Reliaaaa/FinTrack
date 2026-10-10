package com.example.fintrack.network.api

import com.example.fintrack.network.model.GoldPriceResponse
import com.example.fintrack.network.model.MarketQuote
import retrofit2.http.GET
import retrofit2.http.Path

interface MarketApiService {
    @GET("api/v1/gold/antam/latest")
    suspend fun getAntamGoldPrice(): GoldPriceResponse

    @GET("api/v1/stocks/idx/popular")
    suspend fun getPopularIdxStocks(): List<MarketQuote>

    @GET("api/v1/forex/latest/{base}")
    suspend fun getForexRates(@Path("base") base: String = "IDR"): Map<String, Double>
}
