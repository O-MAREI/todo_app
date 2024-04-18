package com.example.project.ui.scaffolds

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.Screen
import com.example.project.data.ToExploreDatabase
import com.example.project.ui.activities.tripactivity.TripPageContent
import com.example.project.viewmodels.TripViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun TripScaffold(
    innerPadding: PaddingValues,
    pageNavigation: (Screen, Long?) -> Unit,
    currentTripID: Long,
    updateCurrentTripID: (Long) -> Unit,
    updateEditingTaskStatus: (Boolean) -> Unit
) {
    val tripViewModel = viewModel<TripViewModel>()

    val tripDAO = ToExploreDatabase.getDB(LocalContext.current).tripDAO()



    if (currentTripID >= 0 ) {
        Log.i(
            "SCViewModelStuff",
            "getting trip and its tasks for trip_id $currentTripID"
        )

        LaunchedEffect("GET TRIP $currentTripID") {
            tripDAO.getTrip(currentTripID)
                .distinctUntilChanged().collectLatest { trip ->
                    tripViewModel.setCurrentTrip(trip)
                    Log.i(
                        "SCViewModelStuff",
                        "updating trip with $trip 1"
                    )
                }
        }

        LaunchedEffect("GET TASKS of TRIP $currentTripID") {
            tripDAO.getTripTasks(currentTripID)
                .distinctUntilChanged().collectLatest { tasks ->
                    tripViewModel.setCurrentTasks(tasks)
                    Log.i(
                        "SCViewModelStuff",
                        "updating tasks with $tasks 1"
                    )
                }
        }
    }

    TripPageContent(
        innerPadding,
        pageNavigation,
        tripViewModel,
        updateCurrentTripID,
        updateEditingTaskStatus
    )

}
