package com.example.android.zoomableimage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.android.zoomableimage.ui.theme.darkNeutral
import com.example.android.zoomableimage.ui.theme.darkNeutral100
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImagePagerScreen(
    index: Int = 0,
    imgList: List<String>,
) {
    var isZoomedIn by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        coroutineScope.launch {
            pagerState.scrollToPage(index)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        isZoomedIn = false
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(darkNeutral)
    ) {
        HorizontalPager(
            count = imgList.size,
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            userScrollEnabled = !isZoomedIn
        ) { page ->
            Card(
                modifier = Modifier
                    .graphicsLayer {
                        if (!isZoomedIn) {
                            // Calculate the absolute offset for the current page from the
                            // scroll position. We use the absolute value which allows us to mirror
                            // any effects for both directions
                            val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                            // We animate the scaleX + scaleY, between 85% and 100%
                            lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }

                            // We animate the alpha, between 50% and 100%
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                    }
                    .fillMaxWidth()
                    .wrapContentHeight(),
                backgroundColor = (darkNeutral),
                elevation = 0.dp
            ) {
                ZoomableImage(
                    painter = rememberAsyncImagePainter(model = imgList[page]),
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth(),
                    onZoomGestureDetected = {
                        isZoomedIn = it
                    }
                )
            }
        }

        if (imgList.size > 1) {
            HorizontalPagerIndicator(
                activeColor = Color.White,
                inactiveColor = darkNeutral100,
                indicatorHeight = 6.dp,
                indicatorWidth = 6.dp,
                indicatorShape = CircleShape,
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
            )
        }

        // Later, scroll to page 2
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    (1 - fraction) * start + fraction * stop
