package no.tepohi.projectepta.ui.components

import android.text.format.DateFormat
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.ktor.util.date.*
import no.tepohi.projectepta.R
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.testColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomDatePicker(
    dateShown: Calendar = Calendar.getInstance(),
    addDays: Int = 14,
    onDateSelected: (Calendar) -> Unit,
    onDismissRequest: () -> Unit
) {

    val selectedDate = remember { mutableStateOf(dateShown) }

    val minDate: Calendar = Calendar.getInstance()
    val maxDate: Calendar = Calendar.getInstance()
    maxDate.add(Calendar.DAY_OF_YEAR, addDays)

    Dialog(
        properties = DialogProperties(),
        onDismissRequest = { onDismissRequest() },
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(Constants.CORNER_RADIUS)
                )
                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(
                            topStart = Constants.CORNER_RADIUS,
                            topEnd = Constants.CORNER_RADIUS,
                        )
                    )
                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    .padding(Constants.PADDING_INNER)
            ) {

                Text(
                    modifier = Modifier
                        .padding(Constants.PADDING_INNER)
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    ,
                    fontSize = 30.sp,
                    color = MaterialTheme.colors.onPrimary,
                    text = DateFormat.format(
                            "MMM d, yyyy",
                            selectedDate.value
                        ).toString()
                    ,
                )
            }

            CustomCalendar(
                dateShown = dateShown,
                minDate = minDate,
                maxDate = maxDate,
                onDateSelected = {
                    selectedDate.value = it
                }
            )

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(Constants.PADDING_OUTER)
                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    .align(Alignment.End)
            ) {
                Button(
                    modifier = Modifier
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    ,
                    onClick = { onDismissRequest() },
                    colors = ButtonDefaults.textButtonColors(),
                    elevation = null,
                ) { Text(text = "Cancel") }

                Button(
                    onClick = {
                        onDateSelected(
                            Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, dateShown.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, dateShown.get(Calendar.MINUTE))
                            }
                        )
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.textButtonColors(),
                    elevation = null
                ) {
                    Text(text = "today")
                }

                Button(
                    modifier = Modifier
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    ,
                    onClick = {
                        onDateSelected(selectedDate.value)
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.textButtonColors(),
                    elevation = null,
                ) { Text(text = "Go") }

            }
        }
    }
}

@Composable
fun CustomCalendar(
    dateShown: Calendar,
    minDate: Calendar,
    maxDate: Calendar,
    onDateSelected: (Calendar) -> Unit
) {

    val isDark: Boolean = isSystemInDarkTheme()

    AndroidView(
        modifier = Modifier
            .wrapContentSize()
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
        ,
        factory = { context ->
            CalendarView(
                ContextThemeWrapper(
                    context,
                    if (isDark) R.style.CalenderViewNight else R.style.CalenderViewDay
                )
            ).apply {

                var h = dateShown.get(Calendar.DAY_OF_MONTH)
                var e = SimpleDateFormat("dd MMM", Locale.getDefault())
                var l = e.format(h)
                var p = e.parse(l).time

                Log.d("date stuff", l + " XXX " + e.parse(l) + " XXX " + dateShown)

                this.date = p
                this.minDate = minDate.time.time
                this.maxDate = maxDate.time.time
            }
        },
        update = { view ->
            view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                onDateSelected(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                )
            }
        }
    )

}