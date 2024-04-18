package com.example.project.ui.activities

import GeoLocationService
import UploadImageBox
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geolocation.viewmodels.LocationViewModel
import com.example.project.R
import com.example.project.Screen
import com.example.project.data.saveImage
import com.example.project.data.uriToBitmap
import com.example.project.entities.TaskEntity
import com.example.project.ui.components.DatePicker
import com.example.project.ui.components.DropDownMenu
import com.example.project.ui.components.useTemplateField
import com.example.project.ui.theme.Purple
import com.example.project.viewmodels.NewTaskViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

private const val DATE_FORMAT = "dd-MM-yyyy"
private const val TIME_FORMAT = "HH:mm"


/**
 * Converts a priority label (e.g., "High", "Medium", "Low") to its corresponding numerical value.
 */
fun getPriorityNumber(value: String): Int {
    return when (value.lowercase()) {
        "low" -> 1
        "medium" -> 2
        "high" -> 3
        else -> -1 // or any default value if the input doesn't match expected values
    }
}

/**
 * Converts a priority number to its corresponding string label.
 */
fun getPriorityString(priority: Int): String {
    return when (priority) {
        1 -> "low"
        2 -> "medium"
        3 -> "high"
        else -> "unknown" // or any default value if the input doesn't match expected values
    }
}

/**
 * Adjusts latitude and longitude format
 */
fun splitLatLong(input: String): Pair<String?, String?> {
    val parts = input.split(",")
    val lat = parts[0].trim()
    val long = parts[1].trim()
    return Pair(lat, long)
}

/**
 * Adjusts date and time format
 */
fun splitDateTime(dateTime: String): Triple<String?, String?, String?> {
    if (dateTime == null) {
        return Triple(null, null, null)
    }
    val parts = dateTime.split(" ")
    val date = parts[0]
    val time = parts[1]

    val dateParts = date.split("-")
    val taskDate = dateParts.joinToString(separator = "-") { it }

    val timeParts = time.split(":")
    val taskHour = timeParts[0]
    val taskMinute = timeParts[1]

    return Triple(taskDate, taskHour, taskMinute)
}

fun getCurrentDateTime(dateTime: LocalDateTime): String {
    val combinedFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("$DATE_FORMAT $TIME_FORMAT")
    return dateTime.format(combinedFormat)
}

/**
 * Additional adjustment to date and time format
 */
fun combineDateTime(date: String, hour: String, minute: String): String? {
    val dateTimeString = "$date $hour:$minute"
    val combinedFormat = DateTimeFormatter.ofPattern("$DATE_FORMAT $TIME_FORMAT")
    val combinedDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("$DATE_FORMAT $TIME_FORMAT"))
    println(combinedDateTime)
    return combinedDateTime.format(combinedFormat)
}

@Composable
fun NewTaskPageContent(
    innerPadding: PaddingValues,
    currentTripId: Long,
    currentTaskId: Long,
    pageNavigation: (Screen) -> Unit,
    newTaskViewModel: NewTaskViewModel,
    updateEditingTaskStatus: (Boolean) -> Unit,
    isEditing: Boolean
) {
    val locationViewModel = viewModel<LocationViewModel>()
    GeoLocationService.locationViewModel = locationViewModel

    var scrollState = rememberScrollState()

    var passedInTask : TaskEntity? = null

    if (isEditing) {
        passedInTask = newTaskViewModel.task
    }

    println("IS EDITING: $isEditing")
    println("passedInTask: $passedInTask")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var validationLabel = ""
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = validationLabel, Modifier.padding(horizontal = 3.dp))
        }

        var taskName by rememberSaveable { mutableStateOf("") }
        var taskDesc by rememberSaveable { mutableStateOf("") }
        var taskTemplate by rememberSaveable { mutableStateOf("") }
        var taskDueDate by rememberSaveable { mutableStateOf("01-01-2023") }
        var taskDueHour by rememberSaveable { mutableStateOf("12") }
        var taskDueMinute by rememberSaveable { mutableStateOf("00") }
        var currentLatitude = locationViewModel.latitude
        var currentLongitude = locationViewModel.longitude
        var taskImageUri by rememberSaveable { mutableStateOf(Uri.Builder().build()) }
        var taskLatitude by rememberSaveable { mutableStateOf("") }
        var taskLongitude by rememberSaveable { mutableStateOf("") }
        var taskRadius by rememberSaveable { mutableStateOf(100) }
        var taskPriority by rememberSaveable { mutableStateOf("High") }

        if (isEditing) {
            LaunchedEffect(newTaskViewModel.task) {
                newTaskViewModel.task?.let { task ->
                    // Update the mutable state values when task details are fetched
                    taskName = task.title.orEmpty()
                    taskDesc = task.description.orEmpty()
                    taskTemplate = task.is_template.orEmpty()
                    taskDueDate = task?.date_due?.let { splitDateTime(it)?.first } ?: "01-01-2023"
                    taskDueHour = task?.date_due?.let { splitDateTime(it)?.second } ?: "12"
                    taskDueMinute = task?.date_due?.let { splitDateTime(it)?.third } ?: "00"
                    passedInTask?.image?.let { imageUri -> taskImageUri = Uri.parse(imageUri) }
                    taskLatitude = task.location?.let { splitLatLong( it ).first } ?: ""
                    taskLongitude = task.location?.let { splitLatLong( it ).second } ?: ""
                    taskRadius = task?.radius ?: 100
                    taskPriority = task?.priority?.let { getPriorityString( it ) } ?: "High"
                }
            }
        }

        val checkUseTemplate = !taskTemplate.isNullOrEmpty()

        println("PASSED IN TASK RADIUS: ${passedInTask?.radius}")

        var showInvalidFormLabel by rememberSaveable { mutableStateOf(false) }

        var pictureUri: Uri? by remember { mutableStateOf(null) }
        val context = LocalContext.current
        var contentResolver = context.contentResolver

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = "Task Name",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Left,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Create,
                        contentDescription = "Localized description",
                        tint = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    placeholder = {
                        Text(
                            "Enter Task name here",
                            style = TextStyle(fontSize = 18.sp)
                        )
                    },
                    textStyle = TextStyle(fontSize = 18.sp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = if (showInvalidFormLabel && taskName.isBlank()) Color.Red else Color.Transparent),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Purple,
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 6.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = "Task Description",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Left,
                    )
                }
                OutlinedTextField(
                    value = taskDesc,
                    onValueChange = { taskDesc = it },
                    placeholder = {
                        Text(
                            "Enter Task description here",
                            style = TextStyle(fontSize = 18.sp)
                        )
                    },
                    textStyle = TextStyle(fontSize = 18.sp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = if (showInvalidFormLabel && taskDesc.isBlank()) Color.Red else Color.Transparent),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Purple,

                        )
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                useTemplateField(
                    isChecked = checkUseTemplate,
                    templateToUse = taskTemplate
                ) { selectedOption ->
                    taskTemplate = selectedOption
                }
            }
        }


        // format: 07-12-2023 13:15
        val dateTimeCreated = getCurrentDateTime(LocalDateTime.now())

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = "Due Date",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Left,
                    )
                }
                DatePicker(
                    preselectedDate = taskDueDate,
                ) { selectedDate ->
                    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
                    val dateString = selectedDate.format(dateFormat)

                    taskDueDate = dateString
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = "Due Time",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Left,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val hourOptions = List(24) { (it + 1).toString().padStart(2, '0') }
                    Box(modifier = Modifier.width(100.dp)) {

                        DropDownMenu(
                            options = hourOptions,
                            label = "Hour",
                            selectedItem = taskDueHour
                        ) { selectedOption ->
                            taskDueHour = selectedOption

                        }
                    }
                    val minuteOptions = (0 until 60).map { it.toString().padStart(2, '0') }
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        DropDownMenu(
                            options = minuteOptions,
                            label = "Minute",
                            selectedItem = taskDueMinute
                        ) { selectedOption ->
                            taskDueMinute = selectedOption

                        }
                    }
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Photo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_image),
                    contentDescription = "Localized description",
                    tint = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp)
                .fillMaxWidth()
        ) {
            UploadImageBox(pictureUri, 350.dp, 125.dp) { uri ->
                pictureUri = uri
                taskImageUri = pictureUri
            }
        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Location",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Left,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Localized description",
                    tint = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            val latLongRegex = Regex("[-\\d.]*")
            Row(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Column {
                    Text(
                        text = "Latitude",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                    )

                    OutlinedTextField(
                        value = taskLatitude,
                        onValueChange = {
                            //check input against regex
                            if (it.matches(latLongRegex)) {
                                taskLatitude = it
                            }
                        },
                        placeholder = {
                            Text(
                                "Enter latitude here",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        },
                        textStyle = TextStyle(fontSize = 18.sp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .background(color = if (showInvalidFormLabel && taskDesc.isBlank()) Color.Red else Color.Transparent),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Purple,
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Column {
                    Text(
                        text = "Longitude",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                    )

                    OutlinedTextField(
                        value = taskLongitude,
                        onValueChange = {
                            //check input against regex
                            if (it.matches(latLongRegex)) {
                                taskLongitude = it
                            }
                        },
                        placeholder = {
                            Text(
                                "Enter longitude here",
                                style = TextStyle(fontSize = 18.sp)
                            )
                        },
                        textStyle = TextStyle(fontSize = 18.sp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 2.dp)
                            .background(color = if (showInvalidFormLabel && taskDesc.isBlank()) Color.Red else Color.Transparent),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Purple,
                        )
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = "Radius",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Left,
                    )
                }

                Row {
                    var sliderPosition by remember { mutableStateOf(taskRadius.toFloat()) }
                    val formattedValue = "%.0f".format(sliderPosition)

                    Column(
                        modifier = Modifier.width(200.dp)
                    ) {
                        Slider(
                            value = sliderPosition,
                            onValueChange = { newPosition ->
                                sliderPosition = newPosition
                                taskRadius = sliderPosition.toInt()
                            },
                            valueRange = 100f..200f,
                            colors = SliderDefaults.colors(
                                thumbColor = Purple,
                                activeTrackColor = Purple,
                                inactiveTrackColor = Color.Gray
                            )
                        )
                        Text(
                            text = "$formattedValue m",
                            modifier = Modifier.padding(top = 8.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                        )
                    }

                    Column {
                        Canvas(
                            modifier = Modifier
                                .padding(top = 21.dp, start = 80.dp)
                                .width(200.dp)
                                .height(16.dp)
                        ) {
                            println("This is the slider position: ${sliderPosition}")
                            val circleSize = sliderPosition.dp.toPx() * 1.6f
                            println("This is the circle size: ${circleSize}")
                            drawCircle(
                                color = Purple,
                                center = Offset(0f, 0f),
                                radius = circleSize / 8,
                                alpha = 0.7f
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 6.dp, top = 12.dp)
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = "Priority",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Left,
                    )
                }
                val priorityOptions = listOf("Low", "Medium", "High")
                Box(modifier = Modifier.width(150.dp)) {
                    DropDownMenu(
                        selectedItem = taskPriority,
                        options = priorityOptions,
                        label = "Priority"
                    )
                    { selectedOption ->
                        taskPriority = selectedOption
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            if (showInvalidFormLabel) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Some fields are invalid (empty)", color = Color.Red)
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Button(
                onClick = {
                    val formIsValid = taskName.isNotBlank() && taskDesc.isNotBlank()
                    val priorityNumber = getPriorityNumber(taskPriority)
                    if (formIsValid) {
                            val currentLatLongValue = "$currentLatitude,$currentLongitude"
                            val latLongValue = "$taskLatitude,$taskLongitude"
                            val taskDueDateTime =
                                combineDateTime(taskDueDate, taskDueHour, taskDueMinute)

                            val uuid = UUID.randomUUID()
                            val uuidString = uuid.toString()

                            newTaskViewModel.insertOrUpdateTask(
                                currentTripId,
                                taskName,
                                currentLatLongValue,
                                latLongValue,
                                dateTimeCreated,
                                taskDueDateTime,
                                uuidString,
                                taskDesc,
                                taskRadius,
                                priorityNumber,
                                false,
                                taskTemplate
                            )

                            var bitmap = pictureUri?.let { uriToBitmap(contentResolver, it) }
                            if (bitmap != null) {
                                saveImage(context, uuidString, bitmap)
                            }
                            showInvalidFormLabel = false
                            pageNavigation(Screen.Trip)

                    } else {
                        showInvalidFormLabel = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
            ) {
                if (isEditing) {
                    Text("Apply Changes")
                } else {
                    Text("Add Task")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskPageTopAppBar(pageNavigation: (Screen) -> Unit, isEdit: Boolean) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { pageNavigation(Screen.Trip) }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Localized description",)
            }
        },
        title = { Text(text = if (isEdit) "Edit Task" else "New Task") },
        actions = {}
    )
}
