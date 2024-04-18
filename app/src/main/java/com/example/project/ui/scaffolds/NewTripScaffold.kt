package com.example.project.ui.scaffolds

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.Screen
import com.example.project.entities.TripEntity
import com.example.project.ui.activities.NewTripPageContent
import com.example.project.viewmodels.NewTripViewModel

@Composable
fun NewTripScaffold(innerPadding: PaddingValues, pageNavigation: (Screen, Long?) -> Unit) {
    val newTripViewModel = viewModel<NewTripViewModel>()

    NewTripPageContent(innerPadding, pageNavigation, newTripViewModel)
}
