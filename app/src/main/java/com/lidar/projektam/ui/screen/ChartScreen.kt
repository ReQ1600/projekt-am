package com.lidar.projektam.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
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
import com.lidar.projektam.R
import com.lidar.projektam.model.TransactionType
import com.lidar.projektam.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.runtime.derivedStateOf

enum class Range(val key: String, @StringRes val labelRes: Int) {
    WEEK("week", R.string.chart_w),
    MONTH("month", R.string.chart_m),
    YEAR("year", R.string.chart_y),
    ALL("all", R.string.chart_a)
}


@Composable
fun ChartScreen(navController: NavController, viewModel: TransactionViewModel)
{
    val transactions by viewModel.transactions.collectAsState()

    var selectedRange by remember { mutableStateOf(Range.WEEK) }

    val filteredTrans = when (selectedRange) {
        Range.WEEK -> transactions.filter { it.date >= nowMinusDays(7) }
        Range.MONTH -> transactions.filter { it.date >= nowMinusDays(30) }
        Range.YEAR -> transactions.filter { it.date >= nowMinusDays(365) }
        else -> transactions
    }

    //calculating balance over time
    val sortedTrans = filteredTrans.reversed()
    val balanceOverRange = mutableListOf<Point>()
    val dates = mutableListOf<String>()
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val saveSdf = SimpleDateFormat("dd.MM", Locale.getDefault())

    //calculating budget in time points
    var it = 0f
    var prevTransDate = ""
    var last_added_id : Int = -1
    //summing all older transactions
    var tmp_balance = (transactions - filteredTrans).sumOf { if (it.type == TransactionType.INCOME) it.amount else -it.amount }

    for (transaction in sortedTrans){
        tmp_balance += if (transaction.type == TransactionType.INCOME) transaction.amount else -transaction.amount

        val transDate = sdf.format(transaction.date)
        if (transDate != prevTransDate || it == 0f) {
            balanceOverRange.add(Point(it++, tmp_balance.toFloat()))
            dates.add(saveSdf.format(transaction.date))
            ++last_added_id
        }
        else
        {
            //updating balance if date doesnt change
            balanceOverRange[last_added_id] = Point(last_added_id.toFloat(), tmp_balance.toFloat())
        }
        prevTransDate = transDate

    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (goBackRef, titleRef, rangeSelectRef, chartRef) = createRefs()

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(goBackRef){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }.height(80.dp),
            color = Color.DarkGray
        ){
            Box (
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ){
                IconButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.ret))
                }

                Text(
                    text = stringResource(R.string.menu_charts),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = stringResource(R.string.balance_over_time),
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(goBackRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            style = MaterialTheme.typography.headlineSmall
        )

        //range select
        Row(
            modifier = Modifier.constrainAs(rangeSelectRef) {
                top.linkTo(titleRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ){
            Range.entries.forEach { range ->
                Button(
                    onClick = { selectedRange = range },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRange == range) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(range.labelRes))
                }
            }
        }

        //chart
        val pointsData: List<Point> = balanceOverRange
        val sortedPointsDataByVal by remember(pointsData) {
            derivedStateOf {
                if (pointsData.isNotEmpty()) pointsData.sortedBy { it.y } else emptyList()
            }
        }

        val datesData: List<String> = dates
        val steps = pointsData.size - 1
        val scale by remember(pointsData) {
            derivedStateOf {
                if (pointsData.isNotEmpty()) (sortedPointsDataByVal.last().y - sortedPointsDataByVal.first().y) / steps else 0f
            }
        }

        val xAxisData = AxisData.Builder()
            .axisStepSize(50.dp)
            .backgroundColor(Color.White)
            .steps(steps)
            .labelData { i ->
                "%.2f".format(i * scale + sortedPointsDataByVal.first().y) }
            .labelAndAxisLinePadding(20.dp)
            .build()

        val yAxisData = AxisData.Builder()
            .axisStepSize(50.dp)
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
            Box(modifier = Modifier.fillMaxWidth().constrainAs(chartRef) {
                top.linkTo(rangeSelectRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }){
                LineChart(
                    modifier = Modifier.fillMaxWidth().height(500.dp),
                    lineChartData = lineChartData
                )
            }
        } else {
            Text(
                stringResource(R.string.no_data),
                modifier = Modifier.constrainAs(chartRef) {
                    top.linkTo(rangeSelectRef.bottom, margin = 16.dp)
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