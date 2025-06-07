package com.lidar.projektam.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.lidar.projektam.network.NbpClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel



class NbpViewModel : ViewModel() {
    var rates by mutableStateOf<Map<String, Double>>(emptyMap())
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        listOf("EUR", "USD", "GBP", "JPY", "AUD", "CAD",
            "NOK", "SEK", "DKK", "CZK", "HUF", "PLN", "TRY",
            "NZD", "MXN", "ZAR", "SGD", "KRW", "INR","BRL",
            "ILS", "PHP", "MYR", "THB").forEach{ fetchCurrencyRate(it) }
    }

    //fetches provided currency from NBP database
    fun fetchCurrencyRate(currency : String) {
        viewModelScope.launch {
            try {
                val response = NbpClient.api.getExchangeRate(currency)
                val rate = response.rates.firstOrNull()?.mid
                if (rate != null)
                    rates = rates.toMutableMap().apply{ put(currency, rate) }
            } catch (e: Exception) {
                error = "err: ${e.message}"
            }
        }
    }
}