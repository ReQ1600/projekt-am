package com.lidar.projektam.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
        //TODO: tu trzeba zmienic zeby on sobie zbieral ten tmp_balance i jak znajdzie ostatni element który ma taką samą datę to niech w tedy doda do listy bo teraz dodaje pierwszy z unikalną datą i go dodaje a reszta przepada
        //TODO: a tak wgl to dobrze by było wszystkie pkt tak zsumować na starcie żeby on nie startował z 0 za każdym razem jak sie zakres zmieni tylko z tego co miał w pierwszym pkt danego zakresu można na przykład użyć LaunchedEffect(transactions){tworzenie listy wszystkich pkt zsumowanych} w tedy bedzie sie robic tylko jak zmienia sie punkty
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