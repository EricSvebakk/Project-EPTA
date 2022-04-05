package no.tepohi.projectepta.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.customTextFieldColors

@Composable
fun CustomTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onFocusChanged: (FocusState) -> Unit = {},
    onDoneActionClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
) {

    var bottomBorderRadius by remember { mutableStateOf(Constants.CORNER_RADIUS) }
    val borderColor = customTextFieldColors().placeholderColor(enabled = true).value

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { state ->
                onFocusChanged(state)
                bottomBorderRadius = if (state.isFocused) {
                    0.dp
                } else {
                    Constants.CORNER_RADIUS
                }
            }
        ,
        value = value,
        colors = customTextFieldColors(),
        label = { Text(text = label, color = borderColor) },
        shape = RoundedCornerShape(
            topStart = Constants.CORNER_RADIUS,
            topEnd = Constants.CORNER_RADIUS,
            bottomStart = bottomBorderRadius,
            bottomEnd = bottomBorderRadius

        ),

        onValueChange = { result -> onValueChange(result) },

        keyboardActions = KeyboardActions(onDone = { onDoneActionClick() }),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        ),
        trailingIcon = {
            IconButton(
                onClick = { onClearClick() }
            ) {
                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
            }
        },
    )

}