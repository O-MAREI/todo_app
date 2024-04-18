package com.example.project.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.ToExploreDatabase
import com.example.project.entities.TripEntity
import kotlinx.coroutines.launch

/**
 * ViewModel for creating a new Trip object
 */
class NewTripViewModel(app: Application): AndroidViewModel(app) {
    private val insertedTripIDInit: Long? = null

    var insertedTripID by mutableStateOf(insertedTripIDInit)
        private set

    fun insertTrip(
        name: String,
        startDate: String? = null,
        endDate: String? = null,
        image: String? = null,
        completed: Boolean = false,
        isActive: Boolean = false
    ) {
        val context = getApplication<Application>().applicationContext
        val tripDAO = ToExploreDatabase.getDB(context).tripDAO()

        val trip = TripEntity(
            name = name,
            start_date = startDate,
            end_date = endDate,
            image = image,
            completed = completed,
            is_active = isActive
        )

        viewModelScope.launch {
            insertedTripID = tripDAO.insert(trip)
            Log.i("SCViewModelStuff", "trip inserted with id $insertedTripID")
        }
    }

}
