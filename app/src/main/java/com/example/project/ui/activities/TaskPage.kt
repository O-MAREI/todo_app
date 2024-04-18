package com.example.project.ui.activities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geolocation.viewmodels.LocationViewModel
import com.example.project.Screen
import com.example.project.data.loadImage
import com.example.project.entities.SubtaskEntity
import com.example.project.entities.TaskEntity
import com.example.project.ui.components.AddButton
import com.example.project.ui.theme.DesignVariables
import com.example.project.ui.theme.LighterPurple
import com.example.project.ui.theme.Purple
import com.example.project.viewmodels.NewTaskViewModel
import com.example.project.viewmodels.TaskViewModel

@Composable
fun TaskPageContent(
    innerPadding: PaddingValues,
    pageNavigation: (Screen, Long?) -> Unit,
    taskViewModel: TaskViewModel,
    updateEditingTaskStatus: (Boolean) -> Unit
) {
    var subtasksDB = taskViewModel.subtasks
    val task = taskViewModel.task
    var newSubtaskText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    if (task != null) {
        val taskEntity = taskViewModel.task!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            TaskPageTopAppBar(
                taskViewModel,
                task,
                pageNavigation = pageNavigation,
                updateEditingTaskStatus
            )
            // display the trip's image
            if (!task.image.isNullOrEmpty()) {
                val imageBitmap = loadImage(context, task.image!!)?.asImageBitmap()
                imageBitmap?.let {
                    Image(
                        painter = BitmapPainter(imageBitmap!!),
                        contentDescription = "Image of ${task.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                            .padding(vertical = 4.dp)
                            .clip(shape = RoundedCornerShape(
                                topStart = DesignVariables.cornerRadiusLarge,
                                topEnd = DesignVariables.cornerRadiusLarge,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // the trip's title, description, time and location
            Text(
                text = taskEntity.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Start)
            )

            Text(
                text =
                if (taskEntity.description != null) {
                    taskEntity.description.toString()
                }
                else "No description.",
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )

            val creationDateString = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append("Created on: ")
                }
                append(taskEntity.date_created ?: "Unknown creation date.")
            }
            Text(
                text = creationDateString,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )

            val locationString = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append("Location: ")
                }
                append(taskEntity.location ?: "No location.")
            }
            Text(
                text = locationString,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Subtasks",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Start)
            )
            Row(
                modifier = Modifier.padding(bottom = 18.dp)
            ) {

                val buttonAlpha by rememberUpdatedState(if (newSubtaskText.isNotEmpty()) 1f else null) // will get set to 0.7f anyway

                // the new subtask text field
                OutlinedTextField(
                    value = newSubtaskText,
                    onValueChange = { newSubtaskText = it },
                    placeholder = { Text("New subtask...", style = TextStyle(fontSize = 18.sp)) },
                    textStyle = TextStyle(fontSize = 18.sp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(.85f).padding(top = 12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Purple,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                AddButton(
                    modifier = Modifier.padding(top = 8.dp),
                    onClickAction = {
                        if (newSubtaskText.isNotEmpty()) {
                            taskViewModel.insertSubtask(
                                taskID = task.id,
                                title = newSubtaskText,
                            )
                            newSubtaskText = ""
                            focusManager.clearFocus()
                        }
                    },
                    alpha = buttonAlpha
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // display the list of subtasks
                if (subtasksDB != null){
                    items(subtasksDB!!.sortedBy { it.completed }) { subtask ->
                        SubtaskItem(
                            subtask = subtask,
                            onCheckboxClicked = { modifiedSubtask ->
                                taskViewModel.updateSubtaskCompletion(
                                    subtask = modifiedSubtask
                                )
                                subtasksDB = subtasksDB!!.map {
                                    if (it.id == modifiedSubtask.id) {
                                        it.copy(completed = modifiedSubtask.completed)
                                    } else {
                                        it
                                    }
                                }.sortedBy { it.completed }
                            },
                            onDeleteClicked = {
                                taskViewModel.deleteSubtask(
                                    subtaskID = subtask.id
                                )
                                focusManager.clearFocus()
                            },
                            onEditClicked = { modifiedSubtask ->
                                taskViewModel.updateSubtaskTitle(
                                    subtask = modifiedSubtask
                                )
                                focusManager.clearFocus()
                            }
                        )
                    }
                }

            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPageTopAppBar(
    taskViewModel: TaskViewModel,
    taskEntity: TaskEntity,
    pageNavigation: (Screen, Long?) -> Unit,
    updateEditingTaskStatus: (Boolean) -> Unit
)
{
    var displayConfirmationDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    TopAppBar(
        title = {
            // the title of the trip
            Text(
                text = taskEntity.title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        modifier = Modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.primary),
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { pageNavigation(Screen.Trip, null) }
                    .padding(8.dp)
            )
        },
        actions = {
            IconButton(
                onClick = {
                    updateEditingTaskStatus(true)
                    pageNavigation(Screen.NewTask, null)
                    focusManager.clearFocus()
                })
            {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit icon",
                    tint = Color.DarkGray
                )
            }
            IconButton(
                onClick = {
                    displayConfirmationDialog = true
                    focusManager.clearFocus()
                })
            {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete icon",
                    tint = Color.DarkGray
                )
            }
        }
    )
    // ask for confirmation on deletion
    if (displayConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { displayConfirmationDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(text = "Delete Item")
                }
            },
            text = {
                Text("Are you sure you want to delete this item?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // delete and return to the trip page
                        displayConfirmationDialog = false
                        taskViewModel.setCurrentTask(null)
                        pageNavigation.invoke(Screen.Trip, null)
                        taskViewModel.deleteTask(taskEntity.id)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        displayConfirmationDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            },
            modifier = Modifier
                .padding(16.dp)
        )
    }

}

@Composable
fun SubtaskItem(subtask: SubtaskEntity, onCheckboxClicked: (SubtaskEntity) -> Unit, onDeleteClicked: () -> Unit,
             onEditClicked: (SubtaskEntity) -> Unit) {

    var editingTitle by remember { mutableStateOf(false) }
    var updatedTitle by remember { mutableStateOf(subtask.title) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = DesignVariables.elevationExtraSmall,
                shape = RoundedCornerShape(12.dp),
                clip = true
            )
            .background(LighterPurple)
            .clip(shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

        ) {
            // display the editing interface
            if (editingTitle) {
                BasicTextField(
                    value = updatedTitle,
                    onValueChange = {
                        updatedTitle = it
                    },
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (subtask.completed) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (subtask.completed) Color.Gray else Color.Black
                    ),
                    modifier = Modifier
                        .clickable {}
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Save icon",
                        tint = Color.Black,

                        modifier = Modifier.clickable {
                            onEditClicked(subtask.copy(title = updatedTitle))
                            editingTitle = false
                        }
                    )

                }

            } else {
                // display the title, checkbox and button
                Text(
                    fontWeight = FontWeight.Bold,
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                textDecoration = if (subtask.completed) TextDecoration.LineThrough
                                else TextDecoration.None,
                                color = if (subtask.completed) Color.Gray else Color.Black
                            )
                        ) {
                            append(subtask.title)
                        }
                    }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Checkbox(
                        checked = subtask.completed,
                        onCheckedChange = { isChecked ->
                            onCheckboxClicked(subtask.copy(completed = isChecked))
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Purple
                        )
                    )

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit icon",
                        tint = if (subtask.completed) Color.Gray else Color.Blue,
                        modifier = Modifier.clickable(enabled = !subtask.completed) {
                            editingTitle = true
                        }
                    )

                    IconButton(
                        onClick = onDeleteClicked
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete icon",
                            tint = if (subtask.completed) Color.Red else Color.Gray
                        )
                    }
                }

            }
        }
    }
}
