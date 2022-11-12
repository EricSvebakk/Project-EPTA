package no.tepohi.projectepta.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.customTextFieldColors

@Composable
fun CustomAutoComplete(
    value: String,
    label: String,
    textSize: TextUnit = 14.sp,
    dropdownItems: List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>,
    dropdownHeight: Dp = 180.dp,
    onValueChange: (String) -> Unit,
    onDoneAction: (StopPlacesByBoundaryQuery.StopPlacesByBbox?) -> Unit,
    focusRequester: FocusRequester,
    nextFocusRequester: FocusRequester? = null,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    enabled: Boolean = true,
//    trailingIcon: @Composable () -> Unit,
) {

    val borderColor = customTextFieldColors().placeholderColor(enabled = true).value

    val view = LocalView.current
    val state = rememberScrollState()

    var itemsFiltered by remember { mutableStateOf(dropdownItems) }
    var isSearching by remember { mutableStateOf(false) }

    itemsFiltered = dropdownItems.autoCompleteFilter(value)

    Column(
        modifier = Modifier
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colors.onBackground,
                shape = RoundedCornerShape(Constants.CORNER_RADIUS)
            )
            .background(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(Constants.CORNER_RADIUS)
            )
            .clip(RoundedCornerShape(Constants.CORNER_RADIUS))
    ) {

        if (view.hasFocus()) {
            BackHandler(enabled = true) {
                view.clearFocus()
            }
        }

        TextField(
            value = value,
            placeholder = { Text(text = label, color = borderColor, fontSize = textSize) },
            colors = customTextFieldColors(),
            singleLine = true,
            interactionSource = interactionSource,
            enabled = enabled,
            textStyle = TextStyle(
                fontSize = textSize
            ),
            shape = RoundedCornerShape(Constants.CORNER_RADIUS),
            onValueChange = { result ->
                onValueChange(result)

                itemsFiltered = if (result.isEmpty()) {
                    dropdownItems
                } else {
                    dropdownItems.autoCompleteFilter(value)
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (itemsFiltered.isNotEmpty()) {
//                        onValueChange(itemsFiltered[0].getPrimaryText(null).toString())
                        onValueChange(itemsFiltered[0]?.name ?: "")
                        onDoneAction(itemsFiltered[0])
                    }
                    if (nextFocusRequester != null) {
                        nextFocusRequester.requestFocus()
                    } else {
                        view.clearFocus()
                    }
                },
                onPrevious = {
                    view.clearFocus()
                }
            ),
            trailingIcon = {
                if (value != "") {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colors.primary
                            )
                        },
                        onClick = {
//                            view.clearFocus()
                            onValueChange("")
//                            enturViewModel.tripsData.postValue(emptyList())
//                            settingsViewModel.showTripsData.postValue(false)
                        },
//                        modifier = Modifier.border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    )
                }
//                trailingIcon()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .onFocusChanged { state ->
                    isSearching = state.isFocused
                }
                .focusRequester(focusRequester)
        )

        Divider()

        AnimatedVisibility(
            visible = isSearching,
            enter = expandVertically(),
            exit = shrinkVertically(),
//            enter = expandVertically(animationSpec = tween(2000)),
//            exit = shrinkVertically(animationSpec = tween(2000)),
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


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dropdownHeight)
                    .verticalScroll(state)
            ) {

                if (itemsFiltered.isEmpty() && value.isNotEmpty()) {
                    Text(
                        text = "no results",
                        fontSize = textSize,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Constants.PADDING_INNER)
                    )
                }

                if (value != "") {
                    itemsFiltered.forEach { content ->
                        Text(
//                            text = content.getPrimaryText(null).toString(),
                            text = content?.name ?: "",
                            fontSize = textSize,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(
                                        content?.name ?: ""
//                                            .getPrimaryText(null)
//                                            .toString()
                                    )
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

}

fun List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>.autoCompleteFilter(query: String): List<StopPlacesByBoundaryQuery.StopPlacesByBbox?> {

    return if (query.isBlank()) {
        this
    } else {
        this.filter { text: StopPlacesByBoundaryQuery.StopPlacesByBbox? ->

            text?.name?.toLowerCase(Locale.current)?.startsWith(query.toLowerCase(Locale.current)) ?: false

//            text.getPrimaryText(null).toString().toLowerCase(Locale.current)
//                .startsWith(query.toLowerCase(Locale.current))
        }
    }

}