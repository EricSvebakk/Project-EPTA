package no.tepohi.projectepta.ui.screens

//package com.skyyo.draggable.cards

import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import no.tepohi.projectepta.ui.theme.Constants
//import com.skyyo.draggable.CardModel
//import com.skyyo.draggable.theme.cardCollapsedBackgroundColor
//import com.skyyo.draggable.theme.cardExpandedBackgroundColor
import kotlin.math.roundToInt

const val ANIMATION_DURATION = 500
const val MIN_DRAG_AMOUNT = 3

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableCardComplex(
    card: CardModel,
    cardHeight: Dp,
    isRevealed: Boolean,
    cardOffset: Float,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
) {
    val offsetX = remember { mutableStateOf(0f) }
    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }
    val transition = updateTransition(transitionState, "cardTransition")
    val cardBgColor by transition.animateColor(
        label = "cardBgColorTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = {
            if (isRevealed)
                Color(158, 140, 16, 255)
            else
                Color(121, 38, 156, 255)
        }
    )
    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isRevealed) cardOffset - offsetX.value else -offsetX.value },

        )
    val cardElevation by transition.animateDp(
        label = "cardElevation",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isRevealed) 40.dp else 2.dp }
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(cardHeight)
            .offset { IntOffset((offsetX.value + offsetTransition).roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    val original = Offset(offsetX.value, 0f)
                    val summed = original + Offset(x = dragAmount, y = 0f)
                    val newValue = Offset(x = summed.x.coerceIn(0f, cardOffset), y = 0f)
                    if (newValue.x >= 10) {
                        onExpand()
                        return@detectHorizontalDragGestures
                    } else if (newValue.x <= 0) {
                        onCollapse()
                        return@detectHorizontalDragGestures
                    }
                    if (change.positionChange() != Offset.Zero) change.consume()
                    offsetX.value = newValue.x
                }
            },
        backgroundColor = cardBgColor,
        shape = remember {
            RoundedCornerShape(0.dp)
        },
        elevation = cardElevation,
        content = { Text(card.title) }
    )
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableCard(
    card: CardModel,
    cardHeight: Dp,
    isRevealed: Boolean,
    cardOffset: Float,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
) {
    val transitionState = remember {
        MutableTransitionState(isRevealed).apply {
            targetState = !isRevealed
        }
    }
    val transition = updateTransition(transitionState, "cardTransition")
    val cardBgColor by transition.animateColor(
        label = "cardBgColorTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = {
            if (isRevealed)
                MaterialTheme.colors.surface
            else
                MaterialTheme.colors.primary
        }
    )
    val cardTextColor by transition.animateColor(
        label = "cardBgColorTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = {
            if (isRevealed)
                MaterialTheme.colors.onSurface
            else
                MaterialTheme.colors.onPrimary
        }
    )
    val offsetTransition by transition.animateFloat(
        label = "cardOffsetTransition",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isRevealed) cardOffset else 0f },

        )
    val cardElevation by transition.animateDp(
        label = "cardElevation",
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isRevealed) 400.dp else 2.dp }
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(cardHeight)
            .offset { IntOffset(offsetTransition.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    when {
                        dragAmount < 1 -> onExpand()
                        dragAmount >= -1 -> onCollapse()
                    }
                }
            },
        backgroundColor = cardBgColor,
//        shape = remember {
//            RoundedCornerShape(Constants.CORNER_RADIUS)
//        },
        elevation = cardElevation,
        content = {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Constants.PADDING_INNER)
            ) {
                Text(
                    modifier = Modifier.offset(-offsetTransition.dp, 0.dp),
                    text = card.title,
                    color = cardTextColor
                )
            }
        }
    )
}

@Immutable
data class CardModel(val id: String, val title: String)


@Composable
fun ActionsRow(
    actionIconSize: Dp,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onFavorite: () -> Unit,
) {
    Row(
        Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        IconButton(
            modifier = Modifier.size(actionIconSize),
            onClick = onDelete,
            content = {
                Icon(
                    imageVector = Icons.Filled.Delete,
//                    painter = painterResource(id = R.drawable.ic_bin),
                    tint = Color.Gray,
                    contentDescription = "delete action",
                )
            }
        )
//        IconButton(
//            modifier = Modifier.size(actionIconSize),
//            onClick = onEdit,
//            content = {
//                Icon(
//                    imageVector = Icons.Filled.Edit,
//                    tint = Color.Gray,
//                    contentDescription = "edit action",
//                )
//            },
//        )
//        IconButton(
//            modifier = Modifier.size(actionIconSize),
//            onClick = onFavorite,
//            content = {
//                Icon(
//                    imageVector = Icons.Filled.Favorite,
//                    tint = Color.Red,
//                    contentDescription = "Expandable Arrow",
//                )
//            }
//        )
    }
}