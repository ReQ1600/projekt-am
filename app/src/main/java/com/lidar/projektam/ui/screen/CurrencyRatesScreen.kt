package com.lidar.projektam.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lidar.projektam.viewmodel.NbpViewModel

@Composable
fun CurrencyRatesScreen(navController: NavController, viewModel: NbpViewModel = viewModel()) {
    val rate = viewModel.euroRate
    val error = viewModel.error

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (rate != null) {
            Text(text = "Kurs EUR: $rate")
        } else if (error != null) {
            Text(error, color = Color.Red)
        } else {
            CircularProgressIndicator()
        }
    }
}