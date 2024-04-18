package com.example.project.ui.activities

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.R
import com.example.project.Screen
import com.example.project.data.loadImage
import com.example.project.entities.TripEntity
import com.example.project.ui.components.AddButton
import com.example.project.ui.components.ColouredLinearProgressIndicator
import com.example.project.ui.components.ProgressStatus
import com.example.project.ui.components.RandomGradientBackground
import com.example.project.ui.theme.DesignVariables
import com.example.project.ui.theme.Green
import com.example.project.ui.theme.Purple
import com.example.project.viewmodels.HomeViewModel
import kotlin.random.Random


@Composable
fun HomePageContent(
    innerPadding: PaddingValues,
    pageNavigation: (Screen, Long?) -> Unit,
    homeViewModel: HomeViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var checked by remember { mutableStateOf(false) }

                TopContent(checked, onCheckedChange = { checked = it })

                //TempInsertButton()

                Spacer(modifier = Modifier.height(12.dp))

                var trips = homeViewModel.trips

                val completedTrips = trips?.filter { it.completed }
                val incompleteTrips = trips?.filter { !it.completed }

                var tripsProgressCounts = homeViewModel.tripsProgressCounts

                Row(
                    modifier = Modifier
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = "My Trips",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Left,
                    )

                    Spacer(modifier = Modifier.weight(1f)) // align to right
                }

                if (!incompleteTrips.isNullOrEmpty()) {
                    TripsCarousel(incompleteTrips, tripsProgressCounts, isCompleted = false, pageNavigation)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                NewTripButton(pageNavigation = pageNavigation)

                Row(
                    modifier = Modifier
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(start = 8.dp, top = 12.dp, bottom = 3.dp),
                        text = "Completed Trips",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Left,
                    )

                    Spacer(modifier = Modifier.weight(1f)) // align to right
                }

                if (!completedTrips.isNullOrEmpty()) {
                    TripsCarousel(completedTrips, tripsProgressCounts, isCompleted = true, pageNavigation)
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    NoTripsMessage("You haven't completed any trips yet")
                }
            }
        }
    }
}

@Composable
fun NewTripButton(pageNavigation: (Screen, Long?) -> Unit) {
    Box() {
        IconButton(
            onClick = {pageNavigation(Screen.NewTrip, null)},
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(top = 6.dp)
                .height(50.dp)
                .background(color = Purple, shape = RoundedCornerShape(16.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Localized description",
                    tint = Color.White
                )
                Text(
                    text = "New Trip",
                    modifier = Modifier.padding(start = 2.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun TopContent(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp)
            .height(50.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.app_icon),
            contentDescription = "App logo",
            modifier = Modifier
                .size(50.dp)
                .clip(shape = RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            text = "ToExplore",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Left,
        )
    }
}
@Composable
fun TripsCarousel(trips: List<TripEntity>, taskCounts: Map<Long?, Pair<Int?, Int?>>, isCompleted: Boolean, pageNavigation: (Screen, Long?) -> Unit) {

    // Sort the trips so active trip is always in first position
    val (activeTrips, inactiveTrips) = trips.partition { it.is_active }
    val sortedTrips = activeTrips.toMutableList()
    sortedTrips.addAll(inactiveTrips)

    val lazyListState = rememberLazyListState(0)
    val flingBehavior = ScrollableDefaults.flingBehavior()

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        state = lazyListState,
        flingBehavior = flingBehavior,
        content = {
            items(count = sortedTrips.size + 1) { index ->
                if (index < sortedTrips.size) {
                    val (completedTaskCount, taskCount) = taskCounts[sortedTrips[index].id] ?: Pair(null, null)

                    TripsCard(
                        currentTrip = sortedTrips[index],
                        completedTaskCount = completedTaskCount ?: 0,
                        taskCount = taskCount ?: 0,
                        pageNavigation
                    )
                } else {
                    if (!isCompleted) {
                        AddTripCard(pageNavigation)
                    }
                }
            }
        }
    )
}

@Composable
fun TripsCard(currentTrip : TripEntity, completedTaskCount : Int, taskCount : Int, pageNavigation: (Screen, Long) -> Unit) {

    val context = LocalContext.current

    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignVariables.elevationSmall),
        modifier = Modifier
            .size(width = 230.dp, height = 225.dp)
            .clickable {
                pageNavigation(
                    Screen.Trip,
                    currentTrip.id
                )
            }
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(DesignVariables.cornerRadiusSmall),
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val cornerShape = RoundedCornerShape(
                    topStart = DesignVariables.cornerRadiusSmall,
                    topEnd = DesignVariables.cornerRadiusSmall,
                    bottomEnd = 0.dp,
                    bottomStart = 0.dp
                )
                val borderModifier = if (currentTrip.is_active) {
                    Modifier
                        .border(2.dp, Green, cornerShape)
                        .clip(cornerShape)
                } else {
                    Modifier
                }

                if (!currentTrip.image.isNullOrEmpty()) {

                    val imageBitmap = loadImage(context, currentTrip.image!!)?.asImageBitmap()

                    // Display the image if loaded successfully
                    if (imageBitmap != null) {
                        Image(
                            painter = BitmapPainter(imageBitmap!!),
                            contentDescription = "Image of ${currentTrip.name}",
                            modifier = Modifier
                                .fillMaxSize()
                                .then(borderModifier),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        RandomGradientBackground(currentTrip.id, borderModifier)
                    }
                }
                ProgressStatus(isActive = currentTrip.is_active, modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp)
            ) {
                val maxLength = 15
                val displayName = if (currentTrip.name.length > maxLength) "${currentTrip.name.take(maxLength)}..." else currentTrip.name
                Text(
                    text = displayName,
                    textAlign = TextAlign.Left,
                )
                Spacer(modifier = Modifier.weight(1f)) // push text to right
                Text(
                    text = completedTaskCount.toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = "/$taskCount",
                    fontWeight = FontWeight.Light,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(end = 12.dp) // Add padding between the texts if needed
                )
            }

                ColouredLinearProgressIndicator((completedTaskCount.toFloat() / taskCount.toFloat()))

        }
    }

}

@Composable
fun AddTripCard(pageNavigation: (Screen, Long?) -> Unit) {
    Box(
        modifier = Modifier
                .size(width = 160.dp, height = 225.dp),
        contentAlignment = Alignment.Center
    ) {
        AddButton(onClickAction = { pageNavigation(Screen.NewTrip, null) })
    }
}

/**
 * Displays a no trips message with optional styling parameters.
*/
@Composable
fun NoTripsMessage(text: String, colour: Color = Color.LightGray, modifier: Modifier = Modifier,
                   verticalAlignment: Alignment.Vertical = Alignment.CenterVertically) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .then(modifier),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Left,
            color = colour
        )
    }
}
