package com.lidar.projektam.ui.screen

import android.net.Uri
import android.widget.Toast
import com.lidar.projektam.viewmodel.ReceiptViewModel

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.lidar.projektam.R
import com.lidar.projektam.model.TransactionType
import com.lidar.projektam.viewmodel.TransactionViewModel

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(navController: NavController, receiptViewModel: ReceiptViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val amount by receiptViewModel.amount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.rec_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                                R.string.ret)
                        )
                    }
                }
            )
        }) { padding ->

        //prompting user to allow camera usage
        LaunchedEffect(Unit) {
            if (!cameraPermissionState.status.isGranted) {
                cameraPermissionState.launchPermissionRequest()
            }
        }

        if (cameraPermissionState.status.isGranted) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                AndroidView(factory = { previewView }, modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp))

                LaunchedEffect(previewView) {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().apply {
                        surfaceProvider = previewView.surfaceProvider
                    }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        receiptViewModel.imageCapture
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { receiptViewModel.takePhotoAndProcessOCR() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.rec_btn_scan))
                }

                Button(
                    onClick = {
                        amount?.let { parsedAmount ->
                            val en_amnt = Uri.encode(parsedAmount)
                            navController.navigate("addReceipt/$en_amnt")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = amount != null
                ) {
                    Text(stringResource(R.string.rec_btn_add))
                }


                Spacer(modifier = Modifier.height(16.dp))

                Text("${stringResource(R.string.rec_amnt)}: ${amount ?: stringResource(R.string.rec_not_found)}", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
            {
                Text(
                    text = stringResource(R.string.rec_no_access),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReceiptScreen(navController: NavController, receiptViewModel: ReceiptViewModel = viewModel(), transactionViewModel: TransactionViewModel, amnt: String) {
    var amount by remember { mutableStateOf(amnt) }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current
    val rec_added = stringResource(R.string.rec_added)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_trans)) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack("scanner", inclusive = false)
                    }) {
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

            Row {
                Button(onClick = {
                    transactionViewModel.addTransaction(
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        type = TransactionType.EXPENSE,
                        description = description.ifBlank { null }
                    )
                    Toast.makeText(context, rec_added, Toast.LENGTH_SHORT).show()
                    navController.popBackStack("scanner", inclusive = false)
                },
                    modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.add_expense))
                }
            }
        }
    }
}