package com.lidar.projektam.network

import com.lidar.projektam.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface NbpApi {
    @GET("exchangerates/rates/A/{currency}/?format=json")
    suspend fun getExchangeRate(@Path("currency") currency: String): ExchangeRateResponse
}
