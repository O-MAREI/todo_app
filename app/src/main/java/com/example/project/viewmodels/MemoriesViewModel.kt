package com.example.project.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.project.entities.TaskEntity
import com.example.project.entities.TripEntity

/**
 * ViewModel for setting
 */
class MemoriesViewModel(app: Application): AndroidViewModel(app) {
    private val tripsInit: List<TripEntity>? = null
    private val tripInit: TripEntity? = null
    private val tasksInit: List<TaskEntity>? = null

    var trips by mutableStateOf(tripsInit)
        private set
    var trip by mutableStateOf(tripInit)
        private set
    var tasks by mutableStateOf(tasksInit)
        private set

    fun setCurrentTrips(tripsLatest: List<TripEntity>?) {
        trips = tripsLatest
    }

    fun setCurrentTrip(tripLatest: TripEntity?) {
        trip = tripLatest
    }

    fun setCurrentTasks(tasksLatest: List<TaskEntity>) {
        tasks = tasksLatest
    }
}
