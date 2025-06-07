package com.lidar.projektam.model

data class ExchangeRateResponse(
    val table: String,
    val currency: String,
    val code: String,
    val rates: List<Rate>
)

data class Rate(
    val no: String,
    val effectiveDate: String,
    val mid: Double
)
