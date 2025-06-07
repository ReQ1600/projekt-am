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
    var euroRate by mutableStateOf<Double?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        fetchCurrencyRate("EUR")
    }

    //fetches provided currency from NBP database
    fun fetchCurrencyRate(currency : String) {
        viewModelScope.launch {
            try {
                val response = NbpClient.api.getExchangeRate(currency)
                euroRate = response.rates.firstOrNull()?.mid
            } catch (e: Exception) {
                error = "err: ${e.message}"
            }
        }
    }
}