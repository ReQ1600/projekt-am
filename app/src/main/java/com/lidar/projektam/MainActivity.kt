package com.lidar.projektam

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.camera.core.Preview
import androidx.compose.ui.unit.dp
import com.lidar.projektam.ui.theme.ProjektAMTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.lidar.projektam.model.MenuItem
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.lidar.projektam.data.MenuSource
import com.lidar.projektam.database.TransactionRoomDatabase
import com.lidar.projektam.model.TransactionType
import com.lidar.projektam.repository.TransactionRepo
import com.lidar.projektam.viewmodel.TransactionViewModel
import com.lidar.projektam.model.Transaction
import com.lidar.projektam.viewmodel.NbpViewModel
import com.lidar.projektam.viewmodel.TransactionViewModelFactory
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.lidar.projektam.ui.screen.AddReceiptScreen
import com.lidar.projektam.ui.screen.AddTransactionScreen
import com.lidar.projektam.ui.screen.ChartScreen
import com.lidar.projektam.ui.screen.CurrencyRatesScreen
import com.lidar.projektam.ui.screen.Menu
import com.lidar.projektam.ui.screen.ReceiptScreen
import com.lidar.projektam.ui.screen.TransactionScreen
import java.io.File
import java.util.concurrent.Executor


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjektAMTheme {
                Surface (
                    modifier = Modifier.fillMaxSize()
                ){
                    AMApp()
                }
            }
        }
    }
}

@Composable
fun AMApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home"){
        composable("home") { Menu(navController) }
        composable("transactions") { TransactionScreen(navController) }
        composable("addTransaction") {
            val context = LocalContext.current
            val dao = TransactionRoomDatabase.getDB(context).transactionDao()
            val repo = TransactionRepo(dao)
            val factory = TransactionViewModelFactory(repo)
            val viewModel: TransactionViewModel = viewModel(factory = factory)

            AddTransactionScreen(navController, viewModel)
        }
        composable("charts") {
            val context = LocalContext.current
            val dao = TransactionRoomDatabase.getDB(context).transactionDao()
            val repo = TransactionRepo(dao)
            val factory = TransactionViewModelFactory(repo)
            val viewModel: TransactionViewModel = viewModel(factory = factory)

            ChartScreen(navController, viewModel = viewModel)
        }
        composable("rates") { CurrencyRatesScreen(navController) }
        composable("scanner") { ReceiptScreen(navController) }
        composable(
            "addReceipt/{amount}",
            arguments = listOf(navArgument("amount") { type = NavType.StringType })
        ) { backStackEntry ->
            val context = LocalContext.current
            val dao = TransactionRoomDatabase.getDB(context).transactionDao()
            val repo = TransactionRepo(dao)
            val factory = TransactionViewModelFactory(repo)
            val transactionViewModel: TransactionViewModel = viewModel(factory = factory)

            val amount = backStackEntry.arguments?.getString("amount") ?: "0.0"

            AddReceiptScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                amnt = amount
            )
        }

    }
}