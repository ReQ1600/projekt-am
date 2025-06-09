package com.lidar.projektam.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lidar.projektam.R
import com.lidar.projektam.database.TransactionRoomDatabase
import com.lidar.projektam.model.Transaction
import com.lidar.projektam.model.TransactionType
import com.lidar.projektam.repository.TransactionRepo
import com.lidar.projektam.viewmodel.TransactionViewModel
import com.lidar.projektam.viewmodel.TransactionViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, viewModel: TransactionViewModel) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_trans)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.trans_screen_close))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text(stringResource(R.string.trans_amnt)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.trans_desc)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row (modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    viewModel.addTransaction(
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        type = TransactionType.INCOME,
                        description = description.ifBlank { null }
                    )
                    //viewModel.populateWithDummyData()
                    navController.navigate("transactions")
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.add_income))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    viewModel.addTransaction(
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        type = TransactionType.EXPENSE,
                        description = description.ifBlank { null }
                    )
                    navController.navigate("transactions")
                },
                    modifier = Modifier.weight(1f)
                    ) {
                    Text(stringResource(R.string.add_expense))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(navController: NavController) {
    val context = LocalContext.current
    val dao = TransactionRoomDatabase.getDB(context).transactionDao()
    val repo = TransactionRepo(dao)
    val factory = TransactionViewModelFactory(repo)
    val viewModel: TransactionViewModel = viewModel(factory = factory)

    val balance by viewModel.balance.collectAsState()

    val transactions by viewModel.transactions.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.trans_list)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                                R.string.ret)
                        )
                    }
                }
            )
        },
        //add transaction btn
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addTransaction")
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_trans))
            }
        }
    ) { padding ->
        Column ( modifier = Modifier.padding(padding) ) {
            //current balance
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${stringResource(R.string.current_balance)}: %.2f ${stringResource(R.string.currency)}".format(balance),
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (balance >= 0) colorResource(R.color.ok) else colorResource(R.color.broke)
                )
            }


            LazyColumn(modifier = Modifier
                .padding(padding)
                .padding(16.dp)) {
                items(transactions, key = {it.id}) { transaction ->
                    Text(
                        text = "${if (transaction.type == TransactionType.INCOME) "💰" else "💸"} " +
                                "${transaction.amount} ${stringResource(R.string.currency)} - ${
                                    transaction.description ?: "(${
                                        stringResource(
                                            R.string.no_desc
                                        )
                                    })"
                                }" +
                                " - " + java.text.SimpleDateFormat(
                            "dd.MM.yyyy",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date(transaction.date)),
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        selectedTransaction = transaction
                                        showDialog = true
                                    }
                                )
                            }
                    )
                }
            }

            if (showDialog && selectedTransaction != null) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = stringResource(R.string.delete_trans_title)) },
                    text = { Text(text = stringResource(R.string.delete_confirm_text)) },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.deleteTransactionById(selectedTransaction!!.id)
                            showDialog = false
                            selectedTransaction = null
                        }) {
                            Text(stringResource(R.string.delete))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDialog = false
                        }) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}