package com.example.project.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.ToExploreDatabase
import com.example.project.entities.TaskEntity
import com.example.project.entities.TripEntity
import kotlinx.coroutines.launch

/**
 * ViewModel for manipulating Trip objects
 */
class TripViewModel(app: Application): AndroidViewModel(app) {
    private val tripInit: TripEntity? = null
    private val tasksInit: List<TaskEntity>? = null

    var trip by mutableStateOf(tripInit)
        private set
    var tasks by mutableStateOf(tasksInit)
        private set

    fun setCurrentTrip(tripLatest: TripEntity?) {
        Log.i("SCViewModelStuff", "updating trip with $tripLatest 2")
        trip = tripLatest
    }

    fun setCurrentTasks(tasksLatest: List<TaskEntity>) {
        Log.i("SCViewModelStuff", "updating tasks with $tasksLatest 2")
        tasks = tasksLatest
    }

    fun updateTripName(tripId: Long, name: String) {
        val context = getApplication<Application>().applicationContext
        val tripDAO = ToExploreDatabase.getDB(context).tripDAO()

        viewModelScope.launch {
            tripDAO.updateTripName(tripId, name)
        }
    }

    fun updateTripCompleted(tripId: Long, completed: Boolean) {
        val context = getApplication<Application>().applicationContext
        val tripDAO = ToExploreDatabase.getDB(context).tripDAO()

        viewModelScope.launch {
            tripDAO.updateTripCompleted(tripId, completed)
        }
    }

    fun updateTripIsActiveToFalse(tripId: Long) {
        val context = getApplication<Application>().applicationContext
        val tripDAO = ToExploreDatabase.getDB(context).tripDAO()

        viewModelScope.launch {
            tripDAO.updateTripIsActiveToFalse(tripId)
        }
    }

    fun updateTripIsActiveToTrue(tripId: Long) {
        val context = getApplication<Application>().applicationContext
        val tripDAO = ToExploreDatabase.getDB(context).tripDAO()

        viewModelScope.launch {
            tripDAO.updateAllTripsIsActiveToFalse()
            tripDAO.updateTripIsActiveToTrue(tripId)
        }
    }

    fun updateTripImage(tripId: Long, image: String) {
        val context = getApplication<Application>().applicationContext
        val tripDAO = ToExploreDatabase.getDB(context).tripDAO()

        viewModelScope.launch {
            tripDAO.updateTripImage(tripId, image)
        }
    }

    fun updateTaskComplete(id: Long, completed: Boolean) {
        val context = getApplication<Application>().applicationContext
        val taskDAO = ToExploreDatabase.getDB(context).taskDAO()

        viewModelScope.launch {
            taskDAO.updateTaskComplete(id, completed)
        }
    }

    fun deleteTrip(tripId: Long) {
        val context = getApplication<Application>().applicationContext
        val tripDAO = ToExploreDatabase.getDB(context).tripDAO()

        viewModelScope.launch {
            tripDAO.deleteTrip(tripId)
        }
    }
}
