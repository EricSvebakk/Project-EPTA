package no.tepohi.projectepta.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.api.model.AutocompletePrediction
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.customTextFieldColors

@Composable
fun CustomAutoComplete(
    value: String,
    label: String,
    items: List<AutocompletePrediction>,
    dropDownSize: Dp,
    focusRequester: FocusRequester,
    nextFocusRequester: FocusRequester? = null,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onValueChange: (String) -> Unit,
    onDoneAction: (AutocompletePrediction) -> Unit,
    trailingIcon: @Composable () -> Unit,
) {

    val borderColor = customTextFieldColors().placeholderColor(enabled = true).value

    val view = LocalView.current
    val state = rememberScrollState()

    var bottomBorderRadius by remember { mutableStateOf(Constants.CORNER_RADIUS) }
    var itemsFiltered by remember { mutableStateOf(items) }
    var isSearching by remember { mutableStateOf(false) }

    itemsFiltered = items.autoCompleteFilter(value)

    Column(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colors.onBackground,
                shape = RoundedCornerShape(Constants.CORNER_RADIUS)
            )
    ) {

        TextField(
            value = value,
            label = { Text(text = label, color = borderColor) },
            colors = customTextFieldColors(),
            singleLine = true,
            interactionSource = interactionSource,
            shape = RoundedCornerShape(
                topStart = Constants.CORNER_RADIUS,
                topEnd = Constants.CORNER_RADIUS,
                bottomStart = bottomBorderRadius,
                bottomEnd = bottomBorderRadius
            ),
            onValueChange = { result ->
                onValueChange(result)

                itemsFiltered = if (result.isEmpty()) {
                    items
                } else {
                    items.autoCompleteFilter(value)
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (itemsFiltered.isNotEmpty()) {
                        onValueChange(itemsFiltered[0].getPrimaryText(null).toString())
                        onDoneAction(itemsFiltered[0])
                    }
                    if (nextFocusRequester != null) {
                        nextFocusRequester.requestFocus()
                    } else {
                        view.clearFocus()
                    }
                }
            ),
            trailingIcon = {
                trailingIcon()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state ->
                    isSearching = state.isFocused
                    bottomBorderRadius = if (state.isFocused) { 0.dp } else { Constants.CORNER_RADIUS }
                }
                .focusRequester(focusRequester)
        )

        AnimatedVisibility(
            visible = isSearching,
            enter = expandVertically(),
            exit = shrinkVertically(),
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = Constants.CORNER_RADIUS,
                        bottomEnd = Constants.CORNER_RADIUS,
                    )
                )
        ) {

            Divider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dropDownSize)
                    .verticalScroll(state)
            ) {

                if (itemsFiltered.isEmpty() && value.isNotEmpty()) {
                    Text(
                        text = "no results",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Constants.PADDING_INNER)
                    )
                }

                itemsFiltered.forEach { content ->
                    Text(
                        text = content.getPrimaryText(null).toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onValueChange(content.getPrimaryText(null).toString())
                                onDoneAction(content)
                                view.clearFocus()
                            }
                            .padding(Constants.PADDING_INNER)
                    )
                    Divider()
                }
            }


        }

    }

}

private fun List<AutocompletePrediction>.autoCompleteFilter(query: String): List<AutocompletePrediction> {

    return if (query.isBlank()) {
        this
    } else {
        this.filter { text: AutocompletePrediction ->

            text.getPrimaryText(null).toString().toLowerCase(Locale.current)
                .startsWith(query.toLowerCase(Locale.current))
        }
    }

}