package com.lidar.projektam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.tooling.preview.Preview
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
import com.lidar.projektam.viewmodel.TransactionViewModelFactory
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


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

            ChartScreen(viewModel = viewModel)
        }
    }

}

@Composable
fun Menu(navController: NavController,
         modifier: Modifier = Modifier
             .fillMaxSize()
             .wrapContentSize(Alignment.Center))
{
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopNavBelt()
        Button(onClick = { navController.navigate("charts") }) {
            Text("Go test")
        }

        val layoutDirection = LocalLayoutDirection.current

        //setting menu item padding and creating the menu item list
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    start = WindowInsets.safeDrawing.asPaddingValues()
                        .calculateStartPadding(layoutDirection),
                    end = WindowInsets.safeDrawing.asPaddingValues()
                        .calculateEndPadding(layoutDirection)
                )
        ) {
            MenuItemList(
                itemList = MenuSource().loadMenuItems(),
                navController = navController
            )
        }

    }
}

@Composable
fun TopNavBelt(modifier: Modifier = Modifier){
    Surface(
       modifier = Modifier
           .fillMaxWidth(),
        color = Color.Gray
    ){
        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = stringResource(R.string.menu),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
//creating the menu item tile
fun MenuCard(item : MenuItem, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        //Spendings will be changed to switch later on
                        when(item.index){
                            1 -> navController.navigate("transactions")
                            2 -> navController.navigate("charts")
                        }
                    }
                )
            }
    ) {
        Column {
            Image(
                painter = painterResource(item.imageId),
                contentDescription = stringResource(item.titleId),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = LocalContext.current.getString(item.titleId),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall

            )
        }
    }
}

@Composable
fun MenuItemList(itemList: List<MenuItem>, navController: NavController, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(itemList){ item ->
            MenuCard(
                item = item,
                navController = navController,
                modifier = Modifier.padding(8.dp)
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, viewModel: TransactionViewModel) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_trans))},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                label = { Text(stringResource(R.string.trans_amnt)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.trans_desc)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(onClick = {
                    viewModel.addTransaction(
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        type = TransactionType.INCOME,
                        description = description.ifBlank { null }
                    )
                    //viewModel.populateWithDummyData(100)//TODO: delete
                    navController.popBackStack()
                }) {
                    Text(stringResource(R.string.add_income))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    viewModel.addTransaction(
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        type = TransactionType.EXPENSE,
                        description = description.ifBlank { null }
                    )
                    navController.popBackStack()
                }) {
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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.trans_ret))
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
                    text = "${stringResource(R.string.current_balance)}: %.2f ${stringResource(R.string.currency)}".format(
                        balance
                    ),
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

//TODO: add return btn at the top
@Composable
fun ChartScreen(viewModel: TransactionViewModel)
{
    val transactions by viewModel.transactions.collectAsState()

    var selectedRange by remember { mutableStateOf("week") }

    val filteredTrans = when (selectedRange) {
        "week" -> transactions.filter { it.date >= nowMinusDays(7) }
        "month" -> transactions.filter { it.date >= nowMinusDays(30) }
        "year" -> transactions.filter { it.date >= nowMinusDays(365) }
        else -> transactions
    }

    //calculating balance over time
    val sortedTrans = filteredTrans.reversed()
    val balanceOverRange = mutableListOf<Point>()
    val dates = mutableListOf<String>()
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val saveSdf = SimpleDateFormat("dd.MM", Locale.getDefault())
    var tmp_balance = 0.0


    //calculating budget in time points
    var it = 0f
    var prevTransDate = sdf.format(0L).toString()
    for (transaction in sortedTrans){
        tmp_balance += if (transaction.type == TransactionType.INCOME) transaction.amount else -transaction.amount
    //TODO: tu trzeba zmienic zeby on sobie zbieral ten tmp balance i dodawał do listy jak znajdzie ostatni element który ma taką samą datę bo teraz dodaje pierwszy z unikalną datą i go dodaje a reszta przepada
        //TODO: a tak wgl to dobrze by było wszystkie pkt tak zsumować na starcie żeby on nie startował z 0 za każdym razem jak sie zakres zmieni tylko z tego co miał w pierwszym pkt danego zakresu
        val transDate = sdf.format(transaction.date)
        if (transDate != prevTransDate) {
            balanceOverRange.add(Point(it++, tmp_balance.toFloat()))
            dates.add(saveSdf.format(transaction.date))
        }
        prevTransDate = transDate

    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (titleRef, rangeSelectRef, chartRef) = createRefs()

        Text(
            text = stringResource(R.string.balance_over_time),
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        //range select
        Row(
            modifier = Modifier.constrainAs(rangeSelectRef) {
                top.linkTo(titleRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ){
            listOf("week", "month", "year", "all").forEach { range ->
                Button(
                    onClick = { selectedRange = range },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRange == range) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(range)
                }
            }
        }

        //chart
        val pointsData: List<Point> = balanceOverRange
        val datesData: List<String> = dates
        val steps = pointsData.size - 1

        val xAxisData = AxisData.Builder()
            .axisStepSize(100.dp)
            .backgroundColor(Color.Transparent)
            .steps(steps)
            .labelData { i -> i.toString() }
            .labelAndAxisLinePadding(15.dp)
            .build()

        val yAxisData = AxisData.Builder()
            .axisStepSize(100.dp)
            .steps(steps)
            .backgroundColor(Color.Transparent)
            .labelAndAxisLinePadding(15.dp)
            .axisLabelAngle(20f)
            .labelData { i -> datesData[i]
            }.build()

        val lineChartData = LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = pointsData,
                        LineStyle(),
                        IntersectionPoint(),
                        SelectionHighlightPoint(),
                        ShadowUnderLine(),
                        SelectionHighlightPopUp()
                    )
                ),
            ),
            yAxisData = xAxisData,
            xAxisData = yAxisData,
            gridLines = GridLines(),
            backgroundColor = Color.White
        )

        if (pointsData.isNotEmpty()) {
            LineChart(
                modifier = Modifier.fillMaxWidth().padding(vertical = 50.dp).constrainAs(chartRef) {
                    top.linkTo(rangeSelectRef.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                lineChartData = lineChartData
            )
        } else {
            Text(
                stringResource(R.string.no_data),
                modifier = Modifier.constrainAs(chartRef) {
                    top.linkTo(parent.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }
    }
}

//returns current date minus arg days in milis
fun nowMinusDays(days: Int) : Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -days)
    return calendar.timeInMillis
}