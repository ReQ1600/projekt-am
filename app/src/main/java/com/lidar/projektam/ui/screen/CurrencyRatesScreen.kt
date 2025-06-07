package com.lidar.projektam.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lidar.projektam.R
import com.lidar.projektam.viewmodel.NbpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyRatesScreen(navController: NavController, viewModel: NbpViewModel = viewModel()) {
    val rates = viewModel.rates.toList().sortedBy { it.first }
    val error = viewModel.error

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.rates_header)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.ret)
                        )
                    }
                }
            )
        }) { padding ->
        if (error != null)
        {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
            {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        else
        {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding))
            {
                //header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.rates_currency),
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "${stringResource(R.string.rates_rate)} (PLN)",
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(rates, key = { it.second }) { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Text(
                                text = currency.first,
                                fontSize = 18.sp,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = currency.second.toString(),
                                fontSize = 18.sp,
                                color = colorResource(R.color.ok),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}