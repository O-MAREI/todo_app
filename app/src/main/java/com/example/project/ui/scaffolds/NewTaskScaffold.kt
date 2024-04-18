package com.example.project.ui.scaffolds

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.Screen
import com.example.project.data.ToExploreDatabase
import com.example.project.ui.activities.NewTaskPageContent
import com.example.project.viewmodels.NewTaskViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun NewTaskScaffold(
    innerPadding: PaddingValues,
    currentTripId: Long,
    currentTaskID: Long,
    pageNavigation: (Screen) -> Unit,
    updateEditingTaskStatus: (Boolean) -> Unit,
    isEditing: Boolean
) {
    val newTaskViewModel = viewModel<NewTaskViewModel>()

    val taskDAO = ToExploreDatabase.getDB(LocalContext.current).taskDAO()

    Log.i(
        "SCViewModelStuff",
        "currentTaskID: $currentTaskID,"
    )

    if (isEditing) {
        LaunchedEffect("GET TODO $currentTaskID") {
            taskDAO.getTask(currentTaskID)
                .distinctUntilChanged()
                .collectLatest { task ->
                    if (task != null) {
                        newTaskViewModel.setCurrentTask(task)
                    }
                }
        }
    }

    NewTaskPageContent(
        innerPadding,
        currentTripId,
        currentTaskID,
        pageNavigation,
        newTaskViewModel,
        updateEditingTaskStatus,
        isEditing,
    )
}
