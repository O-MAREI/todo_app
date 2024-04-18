package com.example.project

import GeoLocationService
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.project.data.ToExploreDatabase
import com.example.project.data.TaskDAO
import com.example.project.entities.TaskEntity
import com.example.project.ui.activities.NewTaskPageTopAppBar
import com.example.project.ui.activities.NewTripPageTopAppBar
import com.example.project.ui.scaffolds.HomeScaffold
import com.example.project.ui.scaffolds.MemoriesScaffold
import com.example.project.ui.scaffolds.NewTaskScaffold
import com.example.project.ui.scaffolds.NewTripScaffold
import com.example.project.ui.scaffolds.TaskScaffold
import com.example.project.ui.scaffolds.TripScaffold
import com.example.project.ui.theme.ProjectTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasPermission()) {
            requestFineLocationPermission()
        }

        setContent {
            ProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // application-wide state variables
                    var currentScreen by remember { mutableStateOf(Screen.Home) }

                    val initTripID: Long = -1
                    var currentTripID by remember { mutableStateOf(initTripID) }

                    val initTaskID: Long = -1
                    var currentTaskID by remember { mutableStateOf(initTaskID) }

                    var isEditingTask by remember { mutableStateOf(false) }

                    // functions to change state
                    val updateScreen = { screen: Screen ->
                        currentScreen = screen
                    }

                    val updateCurrentTripID = { newTripID: Long ->
                        currentTripID = newTripID
                    }
                    val updateCurrentTaskID = { newTaskID: Long ->
                        currentTaskID = newTaskID
                    }

                    val updateScreenAndCurrentTripID = { screen: Screen, newTripID: Long? ->
                        if (newTripID != null) {
                            currentTripID = newTripID
                        }
                        currentScreen = screen
                    }
                    val updateScreenAndCurrentTaskID = { screen: Screen, taskID: Long? ->
                        if (taskID != null) {
                            currentTaskID = taskID
                        }
                        currentScreen = screen
                    }

                    val updateEditingTaskStatus = { isEditing: Boolean ->
                        isEditingTask = isEditing
                    }

                    Log.i("SCViewModelStuff", "composing Surface")

                    Scaffold(
                        topBar = {
                            when (currentScreen) {
                                Screen.Home -> {}
                                Screen.Trip -> {}
                                Screen.Task -> {}
                                Screen.NewTrip -> NewTripPageTopAppBar(
                                    pageNavigation = updateScreen
                                )
                                Screen.NewTask -> NewTaskPageTopAppBar(
                                    pageNavigation = updateScreen,
                                    isEdit = isEditingTask
                                )
                                Screen.Memories -> {}
                            }
                        },
                        bottomBar = {
                            NavigationBar {
                                listOf(Screen.Home, Screen.NewTrip, Screen.Memories)
                                    .forEach { screen ->
                                        NavigationBarItem(
                                            selected = currentScreen == screen,
                                            onClick = { currentScreen = screen },
                                            icon = { Icon(
                                                imageVector = screen.icon,
                                                contentDescription = null
                                            ) },
                                            label = { Text(text = screen.title) }
                                        )
                                    }
                            }
                        }
                    ) { innerPadding ->
                        when (currentScreen) {
                            Screen.Home -> HomeScaffold(
                                innerPadding,
                                pageNavigation = updateScreenAndCurrentTripID
                            )
                            Screen.Trip -> TripScaffold(
                                innerPadding,
                                pageNavigation = updateScreenAndCurrentTaskID,
                                currentTripID,
                                updateCurrentTripID,
                                updateEditingTaskStatus
                            )
                            Screen.Task -> TaskScaffold(
                                innerPadding,
                                pageNavigation = updateScreenAndCurrentTaskID,
                                currentTaskID,
                                updateCurrentTaskID,
                                updateEditingTaskStatus
                            )
                            Screen.NewTrip -> NewTripScaffold(
                                innerPadding,
                                pageNavigation = updateScreenAndCurrentTripID
                            )
                            Screen.NewTask -> NewTaskScaffold(
                                innerPadding,
                                currentTripID,
                                currentTaskID,
                                pageNavigation = updateScreen,
                                updateEditingTaskStatus,
                                isEditing = isEditingTask
                            )
                            Screen.Memories -> MemoriesScaffold(innerPadding)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val context = this
        val chosen_provider = checkProvider(this)

        @SuppressLint("MissingPermission")
        if (hasPermission()) {
            // Updating location
            locationManager.requestLocationUpdates(chosen_provider, 10000, 0.0f, GeoLocationService)
        }

        // Creating seperate to access database outside of main
        lifecycleScope.launch(Dispatchers.IO) {
            val taskDAO = ToExploreDatabase.getDB(context).taskDAO()
            val tripDAO = ToExploreDatabase.getDB(context).tripDAO()

            // Checking current location against geofences, and current time against due date
            while (true) {
                val activeIds = tripDAO.getActiveTripsID()
                val tasks = taskDAO.getActiveTasks(activeIds)
                checkDateTime(tasks, context, taskDAO)

                @SuppressLint("MissingPermission")
                if (hasPermission()) {
                    val location = locationManager.getLastKnownLocation(chosen_provider)

                    if (location != null) {
                        checkGeofences(
                            tasks,
                            location.latitude,
                            location.longitude,
                            context,
                            taskDAO
                        )
                        delay(10000)
                    }
                }
            }
        }
    }



    override fun onPause() {
        super.onPause()
        val locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(GeoLocationService)
    }

    private val GPS_LOCATION_PERMISSION_REQUEST = 1

    /**
     * Requests permission for location tracking
     */
    private fun requestFineLocationPermission() {
        // This triggers the window that request permission to track your location
        // and you are given the option of precise and general location tracking
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION ),
            GPS_LOCATION_PERMISSION_REQUEST
        )
    }

    /**
     * Checks if permission has been granted for location tracking
     */
    private fun hasPermission(): Boolean {
        return (PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(
                    applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                || PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(
                    applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    /**
     * Checks whether the user has chosen general or precise location tracking
     */
    private fun checkProvider(context: Context): String {
        var chosen = ""
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            chosen = LocationManager.GPS_PROVIDER
        } else if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            chosen = LocationManager.NETWORK_PROVIDER
        }
        return (chosen)
    }

    /**
     * Helper method for continuously checking the date and time for reminders
     */
    private suspend fun checkDateTime(tasks: List<TaskEntity>, context: Context, taskDAO: TaskDAO) {
        for (task in tasks) {
            // Check reminder date and time
            if (!task.completed && task.date_due != null) {
                val combinedFormat: DateTimeFormatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                val currentTime = LocalDateTime.now().format(combinedFormat)
                val reminderTime = task.date_due
                val compareTimes = currentTime.compareTo(reminderTime)

                if (task.date_reached == false && (compareTimes == 0 || compareTimes > 0)) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Reminder: \"${task.title}\"",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    taskDAO.updateDateReached(task.id, true)
                }
            }
        }
    }

    /**
     * Helper method for continuously checking the user's geo-location
     */
    private suspend fun checkGeofences(tasks: List<TaskEntity>, lat: Double, lon: Double, context: Context, taskDAO: TaskDAO) {
        for (task in tasks) {
            // Check if user is inside one of the geofences
            val latAndLon: List<String>? = task.location?.split(",")

            if (!task.completed && latAndLon != null && latAndLon[0] != "" && latAndLon[0] != "") {
                val difference =
                    latAndLon.let { getDistance(it[0].toDouble(), it[1].toDouble(), lat, lon) }
                if (task.radius != null) {

                    if (task.in_geofence == false) {

                        // If the user's current location is inside the radius
                        if (difference <= task.radius) {

                            // Set in_geofence to true
                            taskDAO.updateInGeofence(task.id, true)

                            // Return to main thread to show Toast notification
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "\"${task.title}\" location reached!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    } else if (task.in_geofence == true) {

                        if (difference <= task.radius) {
                            // Keep in_geofence as true
                        } else {
                            // If a user has exited the radius, set it to false
                            taskDAO.updateInGeofence(task.id, false)
                        }
                    }

                }

            }
        }
    }
}

enum class Screen(val title: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Trip("Active Trip", Icons.Default.Place),
    Task("Task", Icons.Default.Place),
    NewTrip("New Trip", Icons.Default.Add),
    NewTask("New Task", Icons.Default.Place),
    Memories("Memories", Icons.Default.Favorite)
}


/**
 * Calculates the user's distance from the task's given goe-location
 */
fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    // Radius of the earth in km needed for Haversine formula
    val radius = 6371
    // Degrees to Radians conversion
    val dLat = deg2rad(lat2 - lat1)
    val dLon = deg2rad(lon2 - lon1)
    val a =
        kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(deg2rad(lat1)) * kotlin.math.cos(deg2rad(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)

    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return radius * c
}

/**
 * Converts from degrees to radians
 */
fun deg2rad(deg: Double): Double {
    return deg * (Math.PI/180)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjectTheme {

    }
}