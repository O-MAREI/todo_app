package com.example.project.ui.activities

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.data.loadImage
import com.example.project.viewmodels.MemoriesViewModel
import java.math.BigDecimal
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MemoriesPageContent(innerPadding: PaddingValues, memoriesViewModel: MemoriesViewModel, tripId: (Long?) -> Unit) {
    val context = LocalContext.current
    val images = collectImages(context, memoriesViewModel)

    if (images.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(.15f),
            ) {
                MemoriesPageTopAppBar(
                    memoriesViewModel,
                    tripId = tripId,
                    modifier = Modifier.padding(bottom = 48.dp)
                )
            }

            val firstPagerState = rememberPagerState()
            val secondPagerState = rememberPagerState()

            val scrollingFollowingPair by remember {
                derivedStateOf {
                    if (firstPagerState.isScrollInProgress) {
                        firstPagerState to secondPagerState
                    } else if (secondPagerState.isScrollInProgress) {
                        secondPagerState to firstPagerState
                    } else null
                }
            }
            LaunchedEffect(scrollingFollowingPair) {
                val (scrollingState, followingState) = scrollingFollowingPair
                    ?: return@LaunchedEffect
                snapshotFlow { scrollingState.currentPage + scrollingState.currentPageOffsetFraction }
                    .collect { pagePart ->
                        val divideAndRemainder = BigDecimal
                            .valueOf(pagePart.toDouble())
                            .divideAndRemainder(BigDecimal.ONE)

                        var pageOffsetFraction = divideAndRemainder[1].toFloat()
                        if (divideAndRemainder[1].toFloat() < -0.5) {
                            pageOffsetFraction = -0.5f
                        } else if (divideAndRemainder[1].toFloat() > 0.5) {
                            pageOffsetFraction = 0.5f
                        }

                        followingState.scrollToPage(
                            divideAndRemainder[0].toInt(),
                            pageOffsetFraction,
                        )
                    }
            }

            HorizontalPager(
                pageCount = images.size,
                state = firstPagerState,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()

            ) { page ->
                Card(
                    modifier = Modifier
                        .fillMaxHeight(0.88f)
                        .graphicsLayer {
                            val pageOffset = (
                                    (firstPagerState.currentPage - page) + firstPagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue

                            // We animate the alpha, between 50% and 100%
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            // We animate the scale, between 80% and 100%
                            val scale = lerp(
                                start = 0.8f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleX = scale
                            scaleY = scale
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    val image = images[page]
                    Image(
                        painter = BitmapPainter(image),
                        contentDescription = "Image of null",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium),
                    )
                }
            }

            val density = LocalDensity.current.density
            val paddingPercentage = 0.4f
            val screenWidth = context.resources.displayMetrics.widthPixels / density
            val horizontalPadding = (screenWidth * paddingPercentage).dp

            HorizontalPager(
                pageCount = images.size,
                state = secondPagerState,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                pageSize = pageSize
            ) { page ->
                var height = 50.dp
                if (page == firstPagerState.currentPage) {
                    height = 80.dp
                }

                Card(
                    Modifier
                        .height(height)
                        .clickable { }
                ) {
                    val image = images[page]
                    Image(
                        painter = BitmapPainter(image),
                        contentDescription = "Image of null",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    } else {
        NoTripsMessage(
            "Add some pictures first to see some memories",
            Color.Gray,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            verticalAlignment = Alignment.Top
        )
    }
}

@ExperimentalFoundationApi
private val pageSize = object : PageSize {
    override fun Density.calculateMainAxisPageSize(
        availableSpace: Int,
        pageSpacing: Int
    ): Int {
        return (availableSpace - 2 * pageSpacing) / 1
    }
}

/**
 * Performs linear interpolation between two float values.
 */
fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}

/**
 * Collects images associated with memories from past trips
 */
fun collectImages(context: Context, memoriesViewModel: MemoriesViewModel):List<ImageBitmap> {
    val images = mutableListOf<ImageBitmap>()
    val trip = memoriesViewModel.trip
    val trips = memoriesViewModel.trips
    if (trip != null) {
        val tripImageBitmap = loadImage(context, trip.image!!)?.asImageBitmap()
        if (tripImageBitmap != null) {
            images.add(tripImageBitmap)
        }

        val tasks = memoriesViewModel.tasks
        if (tasks != null) {
            for (task in tasks) {
                val taskImageBitmap = loadImage(context, task.image!!)?.asImageBitmap()
                if (taskImageBitmap != null) {
                    images.add(taskImageBitmap)
                }
            }
        }
    } else if (trips != null) {
        val tasks = memoriesViewModel.tasks
        for (t in trips) {
            val tripImageBitmap = loadImage(context, t.image!!)?.asImageBitmap()
            if (tripImageBitmap != null) {
                images.add(tripImageBitmap)
            }

            if (tasks != null) {
                for (task in tasks) {
                    if (task.trip_id == t.id) {
                        val taskImageBitmap = loadImage(context, task.image!!)?.asImageBitmap()
                        if (taskImageBitmap != null) {
                            images.add(taskImageBitmap)
                        }
                    }
                }
            }
        }
    }
    return images
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesPageTopAppBar(memoriesViewModel: MemoriesViewModel, tripId: (Long?) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("All Trips") }
    val trips = memoriesViewModel.trips

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 12.dp)
    ){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = title,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                textStyle = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
//                modifier = Modifier.verticalScroll(rememberScrollState()) //TODO get scrollable working
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "All Trips",
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                        )
                    },
                    onClick = {
                        title = "All Trips"
                        expanded = false
                        tripId(null)
                    },
                )
                if (trips != null) {
                    for (trip in trips) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = trip.name,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                )
                            },
                            onClick = {
                                title = trip.name
                                expanded = false
                                tripId(trip.id)
                            },
                        )
                    }
                }
            }
        }
    }

}