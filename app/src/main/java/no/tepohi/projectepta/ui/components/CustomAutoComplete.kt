package no.tepohi.projectepta.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import no.tepohi.projectepta.ui.theme.Constants

@Composable
fun <T> CustomAutoComplete(
    value: String,
    label: String,
    items: List<T>,
    dropDownSize: Dp = 120.dp,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit
) {

//    println("items: $items")

    var itemsFiltered by remember { mutableStateOf(items) }
    var isSearching by remember { mutableStateOf(false) }
    val view = LocalView.current

    val state = rememberScrollState()

    itemsFiltered = items.autoCompleteFilter(value)

    Column(
        modifier = Modifier
            .border(
                2.dp,
                MaterialTheme.colors.primary,
                RoundedCornerShape(Constants.CORNER_RADIUS)
            )
    ) {

        CustomTextField(
            value = value,
            label = label,
            onValueChange = { result ->
                onValueChange(result)
                itemsFiltered = if (result.isEmpty()) {
                    items
                } else {
                    items.autoCompleteFilter(value)
                }
            },
            onFocusChanged = { state ->
                isSearching = state.isFocused
            },
            onDoneActionClick = {
                onValueChange(itemsFiltered[0].toString())
                view.clearFocus()
            },
            onClearClick = {
                onClearClick()
//                onValueChange("")
                view.clearFocus()
            },
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
                        bottomEnd = Constants.CORNER_RADIUS
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
//                if (itemsFiltered.isNotEmpty()) {
//                    for (i in 0..(if (itemsFiltered.size-1 < 5) itemsFiltered.size-1 else 5) ) {
//                        Text(
//                            text = itemsFiltered[i].toString(),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable {
//                                    onValueChange(itemsFiltered[i].toString())
//                                    view.clearFocus()
//                                }
//                                .padding(Constants.PADDING_OUTER)
//                        )
//                        Divider()
//                    }
//                }
                itemsFiltered.forEach { content ->
                    Text(
                        text = content.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onValueChange(content.toString())
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

fun <T> List<T>.autoCompleteFilter(query: String): List<T> {
    return if (query.isBlank()) {
        emptyList()

    } else {
        this.filter { text: T ->

            text.toString().toLowerCase(Locale.current)
                .startsWith(query.toLowerCase(Locale.current))
        }
    }

}