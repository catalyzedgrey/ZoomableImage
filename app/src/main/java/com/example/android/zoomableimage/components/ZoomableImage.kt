package com.example.android.zoomableimage.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastAny
import com.example.android.zoomableimage.ChangeSystemBarColor

@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    painter: Painter,
    maxScale: Float = .30f,
    minScale: Float = 2f,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    isRotation: Boolean = false,
    isZoomable: Boolean = true,
    onZoomGestureDetected: (isZoomIn: Boolean) -> Unit
) {
    val scale = remember { mutableStateOf(1f) }
    val rotationState = remember { mutableStateOf(1f) }
    val offsetX = remember { mutableStateOf(1f) }
    val offsetY = remember { mutableStateOf(1f) }
    var imgSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(key1 = scale.value) {
        onZoomGestureDetected.invoke(scale.value != 1f)
    }

    fun canPanX(offset: Offset): Boolean {
        return (offsetX.value < imgSize.width / 2 && offsetX.value > -(imgSize.width / 2))
                || (offsetX.value < 0 && offset.x >= 0)
                || (offsetX.value >= 0 && offset.x < 0)
    }

    fun canPanY(offset: Offset): Boolean {
        return (offsetY.value < imgSize.height / 2 && offsetY.value > -(imgSize.height / 2))
                || (offsetY.value < 0 && offset.y >= 0)
                || (offsetY.value >= 0 && offset.y < 0)
    }

    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale.value >= minScale) {
                            scale.value = 1f
                            offsetX.value = 1f
                            offsetY.value = 1f
                        } else scale.value = minScale
                    }
                )
            }
            .pointerInput(Unit) {
                if (isZoomable) {
                    awaitEachGesture {
                        awaitFirstDown()
                        do {
                            val event = awaitPointerEvent()
                            val canceled = event.changes.fastAny { it.isConsumed }
                            if (!canceled) {
                                val scaleDelta = event.calculateZoom()
                                scale.value *= scaleDelta

                                if (scale.value > 1) { //zoom in
                                    val offset = event.calculatePan()

                                    if (canPanX(offset))
                                        offsetX.value += offset.x
                                    else event.changes
                                        .last()
                                        .consume()

                                    if (canPanY(offset))
                                        offsetY.value += offset.y
                                    else event.changes
                                        .last()
                                        .consume()

                                    onZoomGestureDetected(true)

                                } else {//zoom out
                                    scale.value = 1f
                                    offsetX.value = 1f
                                    offsetY.value = 1f
                                    onZoomGestureDetected(false)
                                }
                            }

                        } while (!canceled && event.changes.fastAny { it.pressed })
                    }
                }
            }

    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = contentScale,
            alignment = alignment,
            modifier =
            modifier
                .align(Alignment.Center)
                .onPlaced {
                    imgSize = it.size
                }
                .graphicsLayer {
                    if (isZoomable) {
                        scaleX = maxOf(maxScale, minOf(minScale, scale.value))
                        scaleY = maxOf(maxScale, minOf(minScale, scale.value))
                        if (isRotation) {
                            rotationZ = rotationState.value
                        }
                        translationX = offsetX.value
                        translationY = offsetY.value
                    }
                }
                .conditional(scale.value > 1f) {
                    fillMaxSize()
                }
        )
    }
}

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}
