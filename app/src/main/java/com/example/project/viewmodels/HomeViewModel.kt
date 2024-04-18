package com.example.project.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.project.entities.ProgressCount
import com.example.project.entities.TripEntity

/**
 * ViewModel to control paramaters in the Home page
 */
class HomeViewModel(app: Application): AndroidViewModel(app) {
    private val tripsInit: List<TripEntity>? = null
    private val tripsProgressCountsInit: Pair<Long?, Pair<Int?, Int?>> =
        Pair(null, Pair(null, null))

    var trips by mutableStateOf(tripsInit)
        private set
    var tripsProgressCounts by mutableStateOf(mapOf(tripsProgressCountsInit))
        private set

    fun setCurrentTrips(tripsLatest: List<TripEntity>) {
        Log.i("SCViewModelStuff", "updating trips with $tripsLatest 2")
        trips = tripsLatest
    }

    fun setCurrentTripsProgressCounts(tripsProgressCountsNew: List<ProgressCount>) {
        tripsProgressCountsNew.forEach() { counts ->
            val (tripID, countCompleted, countTotal) = counts

            val countPair: Pair<Long?, Pair<Int?, Int?>> =
                Pair(tripID, Pair(countCompleted, countTotal))

            tripsProgressCounts += countPair
        }
    }
}
