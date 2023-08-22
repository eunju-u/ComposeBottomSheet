package com.sheet.draggablebottomsheet

import android.content.res.Configuration
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class SwipeBottomSheetDirection {
    RIGHT, LEFT, BOTTOM
}

val MaxSheetWidth = 640.dp

/**
 *  * it is essential to specify the Modifier.fillMaxHeight() for the content.
 *
 * @param direction The direction of the sheet.
 * @param isMaxWidth determines the sheet's width will fill the entire screen
 * or will be set to a specified value for the width.
 * only used with the "SwipeBottomSheetDirection.BOTTOM" option.
 * @param useOffset in HalfExpanded, determines to use an offset when dragging downwards.
 * @param bottomSheetMaxWidth determines the sheet's width will be set to the specified width value
 * or the default maximum width of 640.dp
 * only used with the "SwipeBottomSheetDirection.BOTTOM" option and "isMaxWidth" is false
 * @param scrimColor The color of the scrim that is applied to the rest of the screen when the bottom sheet is visible
 * @param surfaceRoundedCornerShape The RoundedCornerShape of the sheet.
 * @param onDismiss event to dismiss the sheet
 * @param sheetBar The sheetBar of the bottom sheet.
 * if the sheetBar is not specified, the default bar is used.
 * @param content The content of the sheet.
 *
 * **/
@Composable
fun DraggableBottomSheet(
    direction: SwipeBottomSheetDirection = SwipeBottomSheetDirection.BOTTOM,
    isMaxWidth: Boolean = false,
    useOffset: Boolean = false,
    bottomSheetMaxWidth: Dp = MaxSheetWidth,
    scrimColor: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.32f),
    surfaceRoundedCornerShape: RoundedCornerShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp
    ),
    onDismiss: () -> Unit,
    sheetBar: (@Composable (modifier: Modifier) -> Unit)? = null,
    content: @Composable () -> Unit
) {

    // Common Variables
    var isDelete by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Back Scrim Visible
    var isVisible by remember { mutableStateOf(true) }
    val isLeftSheet = direction == SwipeBottomSheetDirection.LEFT
    val isRightSheet = direction == SwipeBottomSheetDirection.RIGHT
    val withModifier =
        if (isMaxWidth) Modifier.fillMaxWidth() else Modifier.widthIn(max = bottomSheetMaxWidth)

    // Bottom Sheet Variables
    val fullHeight = configuration.screenHeightDp
    val minRatio = 0.5f
    val maxRatio = 0.8f
    val landscapeMaxRatio = 0.88f

    var isOpen by remember { mutableStateOf(false) }
    val contentHeightDp =
        if (isLandscape) fullHeight * landscapeMaxRatio
        else fullHeight * (if (isOpen) maxRatio else minRatio)

    var yOffset by remember { mutableStateOf(0f) }
    var mYOffset = 0f
    var contentHeight by remember { mutableStateOf(contentHeightDp) }

    // Left or Right Sheet Variables
    val fullWidth = configuration.screenWidthDp
    val widthRatio = 0.58f

    val contentWidthDp = fullWidth * widthRatio

    var xOffset by remember { mutableStateOf(0f) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            Scrim(
                color = scrimColor, onDismiss = { onDismiss() }, visible = isVisible
            )
        }

        /** Left or Right Sheet **/
        if (isLeftSheet || isRightSheet) {
            Surface(
                modifier = Modifier
                    .width(contentWidthDp.dp)
                    .fillMaxHeight()
                    .focusable(true)
                    .align(if (isLeftSheet) Alignment.TopStart else Alignment.BottomEnd)
                    .offset { IntOffset(xOffset.roundToInt(), 0) }
                    .clip(surfaceRoundedCornerShape)
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val swipeModifier = Modifier
                        .width(40.dp)
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { _ ->
                                    // Reset on gesture start
                                    isDelete = false
                                },
                                onDrag = { change, dragAmount ->
                                    //Left Sheet
                                    if (isLeftSheet) {
                                        // Drag to the right
                                        if (xOffset > 0) {
                                            return@detectDragGestures
                                        }
                                        // Drag to the left
                                        else {
                                            xOffset += dragAmount.x
                                            isVisible = xOffset > -110.dp.toPx()
                                        }
                                    }

                                    //Right Sheet
                                    else {
                                        // Drag to the left
                                        if (xOffset < 0) {
                                            return@detectDragGestures
                                        }
                                        // Drag to the right
                                        else {
                                            xOffset += dragAmount.x
                                            isVisible = xOffset < 110.dp.toPx()
                                        }
                                    }
                                    change.consume()
                                },
                                onDragEnd = {
                                    if (isLeftSheet) {
                                        // Dismiss on significant leftward swipe
                                        if (xOffset < 0) {
                                            isDelete = xOffset <= -110.dp.toPx()
                                        }
                                    } else {
                                        // Dismiss on significant rightward swipe
                                        if (xOffset > 0) {
                                            isDelete = xOffset >= 110.dp.toPx()
                                        }
                                    }

                                    if (isDelete) {
                                        onDismiss()
                                    } else {
                                        xOffset = 0f
                                        isDelete = false
                                    }
                                },
                            )
                        }

                    // Left Sheet
                    if (isLeftSheet) {
                        Column(
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            content()
                        }

                        Box(
                            modifier = swipeModifier,
                            contentAlignment = Alignment.Center
                        ) {
                            if (sheetBar != null) {
                                sheetBar(swipeModifier)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 13.dp, end = 13.dp)
                                        .align(Alignment.CenterStart)
                                        .size(height = 50.dp, width = 4.dp)
                                        .background(
                                            color = Color(0xFFDDDDDD),
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                )
                            }
                        }
                    }
                    // Right Sheet
                    else {
                        Box(
                            modifier = swipeModifier,
                            contentAlignment = Alignment.Center
                        ) {
                            if (sheetBar != null) {
                                sheetBar(swipeModifier)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 13.dp, end = 13.dp)
                                        .size(height = 48.dp, width = 4.dp)
                                        .background(
                                            color = Color(0xFFDDDDDD),
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                )
                            }
                        }
                        content()
                    }
                }
            }
        }

        /** Bottom Sheet **/
        else {
            Surface(
                modifier = Modifier
                    .then(withModifier)
                    .height(contentHeight.dp)
                    .focusable(true)
                    .align(Alignment.BottomCenter)
                    .offset { IntOffset(0, yOffset.roundToInt()) }
                    .clip(surfaceRoundedCornerShape)
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val swipeModifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .focusable(true)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { _ ->
                                    // Reset on gesture start
                                    isDelete = false
                                },
                                onDrag = { change, dragAmount ->
                                    val height =
                                        if (isLandscape) fullHeight * landscapeMaxRatio
                                        else fullHeight * maxRatio

                                    mYOffset += dragAmount.y

                                    if (mYOffset > 0) {
                                        isVisible =
                                            if (isOpen) mYOffset < 300.dp.toPx() else mYOffset < 120.dp.toPx()
                                    }

                                    //In landscape mode
                                    if (isLandscape) {
                                        // Drag upward
                                        if (mYOffset < 0) {
                                            if (contentHeight >= height) {
                                                mYOffset = 0f
                                            } else {
                                                contentHeight -= (dragAmount.y / 3.2f)
                                            }
                                        }
                                        // Drag downward
                                        else {
                                            if (useOffset) {
                                                yOffset = mYOffset
                                            } else {
                                                contentHeight -= (dragAmount.y / 3.2f)
                                            }
                                        }
                                    }
                                    // In portrait mode
                                    else {
                                        // Expanded
                                        if (isOpen) {
                                            // Drag upward
                                            if (mYOffset < 0) {
                                                mYOffset = 0f
                                                return@detectDragGestures
                                            }
                                            // Drag downward
                                            else {
                                                if (contentHeight <= height / 1.7 && useOffset) {
                                                    yOffset = 0f
                                                    mYOffset = 0f
                                                    isOpen = false
                                                } else {
                                                    contentHeight -= (dragAmount.y / 3.2f)
                                                }
                                            }
                                        }
                                        // HalfExpanded
                                        else {
                                            // Drag upward
                                            if (mYOffset < 0) {
                                                //content height exceeds the maximum vertical value during dragging
                                                if (contentHeight >= height) {
                                                    isOpen = true
                                                } else {
                                                    contentHeight -= (dragAmount.y / 3.2f)
                                                }
                                            }
                                            // Drag downward
                                            else {
                                                isOpen = false
                                                if (useOffset) {
                                                    yOffset = mYOffset
                                                } else {
                                                    contentHeight -= (dragAmount.y / 3.2f)
                                                }
                                            }
                                        }
                                    }
                                    change.consume()
                                },
                                onDragEnd = {
                                    if (!isLandscape) {
                                        // Expanded
                                        if (isOpen) {
                                            // Drag downward
                                            if (mYOffset > 0) {
                                                //Set delete flag on significant downward swipe
                                                if (mYOffset > 300.dp.toPx()) {
                                                    isDelete = true
                                                } else {
                                                    // Resize to HalfExpanded
                                                    isOpen = false
                                                }
                                            }
                                        }
                                        // HalfExpanded
                                        else {
                                            isOpen = false
                                            // Drag downward
                                            if (mYOffset > 0) {
                                                //Set delete flag on significant downward swipe
                                                isDelete = mYOffset > 120.dp.toPx()
                                            } else {
                                                // Drag upward
                                                if (mYOffset < -100.dp.toPx()) {
                                                    isOpen = true
                                                }
                                            }
                                        }
                                    } else {
                                        if (mYOffset > 0) {
                                            //Set delete flag on significant downward swipe
                                            isDelete = mYOffset > 120.dp.toPx()
                                        } else {
                                            // Return to original size if slightly dragged down
                                            isOpen = false
                                        }
                                    }

                                    if (isDelete) {
                                        onDismiss()
                                    }
                                    // HalfExpanded
                                    else if (!isOpen) {
                                        yOffset = 0f
                                        mYOffset = 0f
                                        isDelete = false
                                        isOpen = false
                                    }
                                    // Expanded
                                    else {
                                        yOffset = 0f
                                        mYOffset = 0f
                                    }

                                    contentHeight =
                                        if (isLandscape) fullHeight * landscapeMaxRatio
                                        else fullHeight * (if (isOpen) maxRatio else minRatio)
                                }
                            )
                        }

                    Box(modifier = swipeModifier, contentAlignment = Alignment.Center) {
                        if (sheetBar != null) {
                            sheetBar(swipeModifier)
                        } else {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(top = 13.dp, bottom = 13.dp)
                                    .size(width = 48.dp, height = 4.dp)
                                    .background(
                                        color = Color(0xFFDDDDDD),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                    content()
                }
            }
        }
    }
}

@Composable
fun Scrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean
) {
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec(),
        )
        val dismissModifier = if (visible) {
            Modifier
                .pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
                .semantics(mergeDescendants = true) {
                    contentDescription = ""
                    onClick { onDismiss(); true }
                }
        } else {
            Modifier
        }

        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(
                color = color, alpha = alpha
            )
        }
    }
}