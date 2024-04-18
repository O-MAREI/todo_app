package com.example.project.ui.activities.tripactivity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.Screen
import com.example.project.entities.Priorities
import com.example.project.entities.TaskEntity
import com.example.project.ui.theme.*
import com.example.project.viewmodels.TripViewModel

@Composable
fun TaskCard(tripViewModel: TripViewModel, task: TaskEntity, pageNavigation: (Screen, Long?) -> Unit) {
    ElevatedCard(
        colors = CardDefaults.cardColors(contentColor = LighterPurple),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { pageNavigation(Screen.Task, task.id) }
    ) {
        TaskCardContent(tripViewModel, task = task)
    }
}


@Composable
fun TaskCardContent(tripViewModel: TripViewModel, task: TaskEntity) {
    val context = LocalContext.current

    val listItemColours = ListItemDefaults.colors(
        containerColor = LighterPurple,
        headlineColor = Color.Black,
        leadingIconColor = Purple,
        overlineColor = Color.Transparent,
        trailingIconColor = Purple,
        disabledHeadlineColor = Color.Transparent ,
        disabledLeadingIconColor = Color.Transparent,
        disabledTrailingIconColor = Color.Transparent,
    )

    Column {
        ListItem(
            headlineContent = {
                val maxLength = 25
                val taskTitle = task.title
                val displayTitle = if (taskTitle.length > maxLength) "${taskTitle.take(maxLength)}..." else taskTitle
                Text(
                    text = displayTitle,
                    modifier = Modifier.padding(start = 9.dp, top = 6.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            },
            supportingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val latAndLon: List<String>? = task.location?.split(",")
                    latAndLon?.let { GeoLocation(openMaps = ::openMaps, lat = it[0].toString(), lon = it[1].toString(), context) }
                }
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     PriorityPill(task)

                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = "Localized description",
                    )
                }
            },
            leadingContent = {
                TaskCheckbox(tripViewModel, task = task)
            },
            colors = listItemColours
        )
    }
}

private fun openMaps(lat: String, lon: String, context: Context) {
    val geoUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
    val intent = Intent(Intent.ACTION_VIEW, geoUri)
    intent.setPackage("com.google.android.apps.maps")
    context.startActivity(intent)
}

@Composable
fun GeoLocation(openMaps: (String, String, Context) -> Unit, lat: String, lon: String, context: Context) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(28.dp)
                .alpha(0.75f)
        ) {
            drawCircle(color = Purple, radius = size.minDimension / 2, alpha = 1f)
        }
        IconButton(
            onClick = {
                openMaps(lat, lon, context)
            },
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp).padding(bottom = 1.dp, end = 1.dp),
                    tint = Color.White
                )
            }
        }
    }
    Text(
        text = "Open in Maps",
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        color = Color.Black
    )
}

@Composable
fun PriorityPill(task: TaskEntity) {
    var text = "I'm Broken"
    var color = Color(255, 255, 255, 255)
    var textColour =  Color(255, 255, 255, 255)

    when (task.priority) {
        Priorities.HIGH.value -> {
            text = "High"
            textColour = RedPillText
            color = RedPill
        }
        Priorities.MEDIUM.value -> {
            text = "Medium"
            textColour = YellowPillText
            color = YellowPill
        }
        Priorities.LOW.value -> {
            text = "Low"
            textColour = GreenPillText
            color = GreenPill
        }
    }

    Button(
        onClick = {},
        enabled = false,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = color,
            disabledContentColor = Color.Black
        ),
        contentPadding = PaddingValues(10.dp, 5.dp),
        modifier = Modifier
            .height(30.dp)
            .padding(end = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = textColour
        )
    }
}

@Composable
fun TaskCheckbox(tripViewModel: TripViewModel, task: TaskEntity) {
    val (checkedState, onStateChange) = remember { mutableStateOf(task.completed) }
    Checkbox(
        modifier = Modifier.toggleable(
            value = checkedState,
            onValueChange = {
                onStateChange(!checkedState)
                tripViewModel.updateTaskComplete(task.id, !checkedState)
            },
            role = Role.Checkbox
        ),
        checked = checkedState, onCheckedChange = null
    )
}