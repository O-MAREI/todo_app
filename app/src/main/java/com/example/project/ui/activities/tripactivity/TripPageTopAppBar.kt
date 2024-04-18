package com.example.project.ui.activities.tripactivity

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.project.R
import com.example.project.Screen
import com.example.project.data.saveImage
import com.example.project.data.uriToBitmap
import com.example.project.entities.TripEntity
import com.example.project.viewmodels.TripViewModel
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPageTopAppBar(
    tripViewModel: TripViewModel,
    trip: TripEntity,
    pageNavigation: (Screen, Long?) -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var title by rememberSaveable { mutableStateOf(trip.name) }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { pageNavigation(Screen.Home, null) }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Localized description")
            }
        },
        title = {
            if (editing) {
                OutlinedTextField( //TODO this is being weird and is getting cut off
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                )

            } else {
                Text(trip.name)
            }
        },
        actions = {
            if (editing) {
                IconButton(
                    onClick = {
                        editing = !editing
                        tripViewModel.updateTripName(trip.id, title)
                    }
                ) {
                    Icon(Icons.Filled.Done, contentDescription = "Localized description")
                }
            } else {
                IconButton(onClick = {
                    title = trip.name
                    editing = !editing
                }
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Localized description")
                }
            }
            if (trip.completed) {
                TripDropDownCompleted(
                    tripViewModel,
                    trip.id,
                    pageNavigation = pageNavigation
                )
            } else {
                TripDropDownNotCompleted(
                    tripViewModel,
                    trip,
                    pageNavigation = pageNavigation
                )
            }
        }
    )
}

@Composable
fun TripDropDownCompleted(
    tripViewModel: TripViewModel,
    id: Long,
    pageNavigation: (Screen, Long?) -> Unit
){
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Restore") },
            onClick = {
                tripViewModel.updateTripCompleted(id, false)
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = null
                )
            }
        )
        DeleteConfirmationDialog(tripViewModel, id, pageNavigation = pageNavigation)
    }
}

@Composable
fun DeleteConfirmationDialog(
    tripViewModel: TripViewModel,
    id: Long,
    pageNavigation: (Screen, Long?) -> Unit
) {
    var visable by remember { mutableStateOf(false) }

    DropdownMenuItem(
        text = { Text("Delete") },
        onClick = { visable = true },
        leadingIcon = {
            Icon(
                Icons.Filled.Delete,
                contentDescription = null
            )
        }
    )

    if (visable) {
        AlertDialog(
            onDismissRequest = { visable = false },
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
                        visable = false
                        tripViewModel.setCurrentTrip(null)
                        pageNavigation.invoke(Screen.Home, null)
                        tripViewModel.deleteTrip(id)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        visable = false
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
fun TripDropDownNotCompleted(
    tripViewModel: TripViewModel,
    trip: TripEntity,
    pageNavigation: (Screen, Long?) -> Unit
){
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Complete Trip") },
            onClick = {
                tripViewModel.updateTripCompleted(trip.id, true)
                tripViewModel.updateTripIsActiveToFalse(trip.id)
                pageNavigation(Screen.Home, null)
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Done,
                    contentDescription = null
                )
            }
        )
        TripDropDownActive(tripViewModel, trip) { expanded = false }
        ChangePictureDropDown(tripViewModel, trip) { expanded = false }
        DeleteConfirmationDialog(tripViewModel, trip.id, pageNavigation = pageNavigation)
    }
}

@Composable
fun TripDropDownActive(
    tripViewModel: TripViewModel,
    trip: TripEntity,
    expanded: () -> Unit
){
    if (trip.is_active) {
        DropdownMenuItem(
            text = { Text("Set as Inactive") },
            onClick = {
                expanded.invoke()
                tripViewModel.updateTripIsActiveToFalse(trip.id)
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null
                )
            }
        )
    } else {
        DropdownMenuItem(
            text = { Text("Set as Active") },
            onClick = {
                expanded.invoke()
                tripViewModel.updateTripIsActiveToTrue(trip.id)
            },
            leadingIcon = {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null
                )
            }
        )
    }

}

@Composable
fun ChangePictureDropDown(
    tripViewModel: TripViewModel,
    trip: TripEntity,
    expanded: () -> Unit
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val visualMediaPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        expanded.invoke()
        val bitmap = uri?.let { uriToBitmap(contentResolver, it) }
        if (bitmap != null) {
            val uuid = UUID.randomUUID()
            val uuidString = uuid.toString()
            if (saveImage(context, uuidString, bitmap)) {
                tripViewModel.updateTripImage(trip.id, uuidString)
            }
        }
    }

    DropdownMenuItem(
        text = { Text("Change Picture") },
        onClick = {
            visualMediaPicker.launch(
                PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_image),
                contentDescription = null
            )
        }
    )
}