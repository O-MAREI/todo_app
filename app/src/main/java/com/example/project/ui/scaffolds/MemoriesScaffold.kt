package com.example.project.ui.scaffolds

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.data.ToExploreDatabase
import com.example.project.ui.activities.MemoriesPageContent
import com.example.project.viewmodels.MemoriesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MemoriesScaffold(innerPadding: PaddingValues) {
    var tripId by remember { mutableStateOf<Long?>(null) }
    val memoriesViewModel = viewModel<MemoriesViewModel>()
    val tripDAO = ToExploreDatabase.getDB(LocalContext.current).tripDAO()
    val taskDAO = ToExploreDatabase.getDB(LocalContext.current).taskDAO()

    LaunchedEffect("GET TRIP $tripId") {
        tripDAO.getAllTrips()
            .distinctUntilChanged().collectLatest { trips ->
                memoriesViewModel.setCurrentTrips(trips)
            }
    }

    if (tripId == null) {
        memoriesViewModel.setCurrentTrip(null)
        LaunchedEffect("GET TASKS of TRIP $tripId") {
            taskDAO.getAllTasks().distinctUntilChanged().collectLatest { tasks ->
                    memoriesViewModel.setCurrentTasks(tasks)
                }
        }
    } else {
        LaunchedEffect("GET TRIP $tripId") {
            tripDAO.getTrip(tripId!!).distinctUntilChanged().collectLatest { trip ->
                    memoriesViewModel.setCurrentTrip(trip)
                }
        }

        LaunchedEffect("GET TASKS of TRIP $tripId") {
            tripDAO.getTripTasks(tripId!!).distinctUntilChanged().collectLatest { tasks ->
                    memoriesViewModel.setCurrentTasks(tasks)
                }
        }
    }


    MemoriesPageContent(
        innerPadding,
        memoriesViewModel,
        tripId = {id ->
            tripId = id
        }
    )
}
