package com.lidar.projektam.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NbpClient {
    private const val BASE_URL = "https://api.nbp.pl/api/"

    val api: NbpApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NbpApi::class.java)
    }
}
