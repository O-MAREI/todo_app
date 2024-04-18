package com.example.project.ui.activities.tripactivity

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.project.Screen
import com.example.project.data.loadImage
import com.example.project.entities.TripEntity
import com.example.project.ui.components.ProgressStatus
import com.example.project.ui.components.RandomGradientBackground
import com.example.project.viewmodels.TripViewModel


@Composable
fun TripPageContent(
    innerPadding: PaddingValues,
    pageNavigation: (Screen, Long?) -> Unit,
    tripViewModel: TripViewModel,
    changeCurrentTripID: (Long) -> Unit,
    updateEditingTaskStatus: (Boolean) -> Unit
) {
    var trip_ = tripViewModel.trip
    var tasks_ = tripViewModel.tasks

    val context = LocalContext.current

    if (trip_ != null) {
        var trip = trip_!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState(0))
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top
        ) {
            TripPageTopAppBar(
                tripViewModel,
                trip,
                pageNavigation = pageNavigation
            )

            TripImage(context, trip)

            var showCompleted by remember { mutableStateOf(false) }
            var descendingOrder by remember { mutableStateOf(true) }
            TripFilterBar(
                showCompleted = { showCompleted = !showCompleted },
                descendingOrder = { descendingOrder = !descendingOrder },
                showCompleted,
                pageNavigation = pageNavigation,
                updateEditingTaskStatus
            )

            if (tasks_ != null) {
                var tasks = tasks_!!

                tasks = if (descendingOrder) {
                    tasks.sortedWith(compareByDescending { it.priority })
                } else {
                    tasks.sortedWith(compareBy { it.priority })
                }

                for (task in tasks) {
                    if (task.completed == showCompleted) {
                        println("showCompleted = $showCompleted")
                        TaskCard(tripViewModel, task = task, pageNavigation = pageNavigation)
                    }
                }
            }
        }
    }
}

@Composable
fun TripImage(context: Context, trip: TripEntity){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        if (!trip.image.isNullOrEmpty()) {
            val imageBitmap = loadImage(context, trip.image!!)?.asImageBitmap()

            if (imageBitmap != null) {
                Image(
                    painter = BitmapPainter(imageBitmap!!),
                    contentDescription = "Image of ${trip.name}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                RandomGradientBackground(
                    trip.id,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.medium),
                )
            }
        }
        ProgressStatus(isActive = trip.is_active, modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 20.dp, bottom = 2.dp)
        )
    }
}