package com.example.project.ui.scaffolds

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.Screen
import com.example.project.data.ToExploreDatabase
import com.example.project.ui.activities.TaskPageContent
import com.example.project.viewmodels.TaskViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun TaskScaffold(
    innerPadding: PaddingValues,
    pageNavigation: (Screen, Long?) -> Unit,
    currentTaskID: Long,
    updateCurrentTaskID: (Long) -> Unit,
    updateEditingTaskStatus: (Boolean) -> Unit
) {
    val taskViewModel = viewModel<TaskViewModel>()

    val taskDAO = ToExploreDatabase.getDB(LocalContext.current).taskDAO()

    if (currentTaskID >= 0) {
        Log.i(
            "SCViewModelStuff",
            "getting task and its subtasks for task_id $currentTaskID"
        )

        LaunchedEffect("GET TASK $currentTaskID AND ITS TASKS") {
            taskDAO.getTask(currentTaskID)
                .distinctUntilChanged().collectLatest { task ->
                    taskViewModel.setCurrentTask(task)
                    Log.i(
                        "SCViewModelStuff",
                        "updating task with $task 1"
                    )
                }
        }

        LaunchedEffect("GET TASKS OF $currentTaskID") {
            taskDAO.getTaskSubtasks(currentTaskID)
                .distinctUntilChanged().collectLatest { subtasks ->
                    taskViewModel.setCurrentSubtasks(subtasks)
                    Log.i(
                        "SCViewModelStuff",
                        "updating subtasks with $subtasks 1"
                    )
                }
        }

    }

    TaskPageContent(
        innerPadding,
        pageNavigation,
        taskViewModel,
        updateEditingTaskStatus,
    )
}