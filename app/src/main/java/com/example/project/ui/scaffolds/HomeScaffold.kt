package com.example.project.ui.scaffolds

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.Screen
import com.example.project.data.ToExploreDatabase
import com.example.project.ui.activities.HomePageContent
import com.example.project.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun HomeScaffold(innerPadding: PaddingValues, pageNavigation: (Screen, Long?) -> Unit) {
    val homeViewModel = viewModel<HomeViewModel>()

    val tripDAO = ToExploreDatabase.getDB(LocalContext.current).tripDAO()

    LaunchedEffect("GET ALL TRIPS") {
        tripDAO.getAllTrips().distinctUntilChanged().collectLatest { trips ->
            homeViewModel.setCurrentTrips(trips)
            Log.i(
                "SCViewModelStuff",
                "updating trips with $trips 1"
            )
        }
    }

    LaunchedEffect("GET TRIP'S PROGRESS COUNTS") {
        tripDAO.getTripsProgressCounts().distinctUntilChanged().collectLatest { counts ->
            Log.i("SCViewModelStuff", "updating tripsProgressCounts")
            homeViewModel.setCurrentTripsProgressCounts(counts)
        }
    }

    HomePageContent(
        innerPadding,
        pageNavigation = pageNavigation,
        homeViewModel
    )
}