package no.tepohi.projectepta.ui.components

import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import no.tepohi.projectepta.R
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel
import java.util.*

@Composable
fun CustomTimePicker(
//    settingsViewModel: SettingsViewModel,
    timeShown: Calendar = Calendar.getInstance(),
    onTimeSelected: (Calendar) -> Unit,
    onDismissRequest: () -> Unit
) {

    var selectedTime by remember { mutableStateOf(timeShown) }


//    val eqHour = timeShown.get(Calendar.HOUR_OF_DAY) == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
//    val eqMin = timeShown.get(Calendar.MINUTE) == Calendar.getInstance().get(Calendar.MINUTE)
//    settingsViewModel.isNow.postValue(eqHour && eqMin)
//
//    Log.d("is now tag", "$eqHour $eqMin")


    Dialog(
        properties = DialogProperties(),
        onDismissRequest = { onDismissRequest() },
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(Constants.CORNER_RADIUS)
                )
                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                .clip(RoundedCornerShape(Constants.CORNER_RADIUS))
                .zIndex(300f)
            ,
            contentAlignment = Alignment.BottomEnd
        ) {

            CustomClock(
                timeShown = selectedTime,
                onTimeSelected = {
                    selectedTime = it
                }
            )

            Row(
                modifier = Modifier
                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
            ) {


                Button(
                    modifier = Modifier
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    ,
                    onClick = { onDismissRequest() },
                    colors = ButtonDefaults.textButtonColors(),
                    elevation = null,
                ) {
                    Text(
                        text = "cancel",
                        color = MaterialTheme.colors.secondary
                    )
                }

                Button(
                    modifier = Modifier
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    ,
                    onClick = {

                        val temp = Calendar.getInstance().apply {
                            set(Calendar.MONTH, timeShown.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, timeShown.get(Calendar.DAY_OF_MONTH))
                        }

                        onTimeSelected(temp)
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.textButtonColors(),
                    elevation = null
                ) {
                    Text(text = "now")
                }

                Button(
                    modifier = Modifier
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    ,
                    onClick = {
                        onTimeSelected(selectedTime)
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.textButtonColors(),
                    elevation = null,
                ) { Text(text = "go") }

            }
        }
    }
}

@Composable
fun CustomClock(
    timeShown: Calendar,
    onTimeSelected: (Calendar) -> Unit
) {

    val isDark: Boolean = isSystemInDarkTheme()

    AndroidView(
        modifier = Modifier
            .wrapContentSize()
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
        ,
        factory = { context ->
            TimePicker(
                ContextThemeWrapper(
                    context,
                    if (isDark) R.style.TimePickerNight else R.style.TimePickerDay
                )
            ).apply {
                this.hour = timeShown.get(Calendar.HOUR_OF_DAY)
                this.minute = timeShown.get(Calendar.MINUTE)
                this.setIs24HourView(true)
            }
        },
        update = { view ->
            view.setOnTimeChangedListener { _, hour, min ->
                onTimeSelected(
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, min)
                    }
                )
            }
        }
    )

}