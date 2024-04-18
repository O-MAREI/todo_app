package com.example.project.ui.activities.tripactivity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.Screen
import com.example.project.entities.TaskEntity
import com.example.project.ui.theme.Purple

@Composable
fun TripFilterBar(
    showCompleted: () -> Unit,
    descendingOrder: () -> Unit,
    completed: Boolean,
    pageNavigation: (Screen, Long?) -> Unit,
    updateEditingTaskStatus: (Boolean) -> Unit
)
{
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        updateEditingTaskStatus(false)
                        pageNavigation(Screen.NewTask, null)
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Purple
                    ),
                    modifier = Modifier
                        .fillMaxWidth(.3f)
                        .height(30.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Localized description",
                            tint = Color.White
                        )
                        Text(
                            text = "New Task",
                            modifier = Modifier.padding(end = 4.dp),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Column {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompletedSortButton(onClick = showCompleted, completed)
                PriorityOrderButton(onClick = descendingOrder)
            }
        }
    }
}

@Composable
fun CompletedSortButton(onClick: () -> Unit, completed: Boolean) {
    var containerColor = Color.Transparent
    var contentColor = Color(168, 144, 185)
    var text = "Uncompleted"
    if (completed){
        containerColor = Color(168, 144, 185)
        contentColor = Color.White
        text = "Completed"
    }

    OutlinedButton(
        onClick = {
            onClick.invoke()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor= containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(5.dp, 5.dp),
        modifier = Modifier
            .height(30.dp)
            .width(100.dp),
        border = BorderStroke(1.5.dp, Color(168, 144, 185))
    ) {
        Text(text, fontSize = 13.sp)
    }
}

@Composable
fun ToggleTaskCount(completed: Boolean, tasks: List<TaskEntity>) {
    if (completed){
        var a = 0
        for (task in tasks) {
            if (task.completed) {
                a++
            }
        }
        Text(text = "${a}/${tasks.size} ∙ Completed")
    } else {
        var a = 0
        for (task in tasks) {
            if (!task.completed) {
                a++
            }
        }
        Text(text = "${a}/${tasks.size} ∙ Uncompleted")
    }
}

@Composable
fun PriorityOrderButton(onClick: () -> Unit) {
    var descending by remember { mutableStateOf(true) }
    var iconRotation = -90f

    if (descending){
        iconRotation = 90f
    }

    IconButton(
        onClick = {
            descending = !descending
            onClick.invoke()
        },
        modifier = Modifier
            .padding(5.dp)
            .size(30.dp)
    ) {
        Icon(
            Icons.Filled.ArrowForward,
            modifier = Modifier.rotate(iconRotation),
            contentDescription = "Localized description",
        )
    }
}