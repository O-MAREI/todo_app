package com.example.project.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.ToExploreDatabase
import com.example.project.entities.SubtaskEntity
import com.example.project.entities.TaskEntity
import kotlinx.coroutines.launch

/**
 * ViewModel for manipulating Task objects
 */
class TaskViewModel(app: Application) : AndroidViewModel(app) {
    private val taskInit: TaskEntity? = null
    private val subtasksInit: List<SubtaskEntity>? = null

    var task by mutableStateOf(taskInit)
        private set
    var subtasks by mutableStateOf(subtasksInit)
        private set

    fun setCurrentTask(taskLatest: TaskEntity?) {
        task = taskLatest
    }

    fun setCurrentSubtasks(subtasksLatest: List<SubtaskEntity>) {
        subtasks = subtasksLatest
    }

    fun insertSubtask(
        taskID: Long,
        title: String,
        completed: Boolean = false
    ) {
        val context = getApplication<Application>().applicationContext
        val subtaskDAO = ToExploreDatabase.getDB(context).subtaskDAO()

        val subtask = SubtaskEntity(
            task_id = taskID,
            title = title,
            completed = completed
        )

        viewModelScope.launch {
            subtaskDAO.insert(subtask)
        }
    }

    // delete a subtask
     fun deleteSubtask(subtaskID: Long) {
        val context = getApplication<Application>().applicationContext
        val subtaskDAO = ToExploreDatabase.getDB(context).subtaskDAO()

        viewModelScope.launch {
            val subtaskToDelete = subtaskDAO.getSubtask(subtaskID)
            subtaskDAO.delete(subtaskToDelete)
        }
    }

    // mark a subtask as complete/incomplete
    fun updateSubtaskCompletion(subtask: SubtaskEntity) {
        val context = getApplication<Application>().applicationContext
        val subtaskDAO = ToExploreDatabase.getDB(context).subtaskDAO()

        viewModelScope.launch {
            subtaskDAO.update(subtask)
        }
    }

    // edit a subtask's title
    fun updateSubtaskTitle(subtask: SubtaskEntity) {
        val context = getApplication<Application>().applicationContext
        val subtaskDAO = ToExploreDatabase.getDB(context).subtaskDAO()

        viewModelScope.launch {
            subtaskDAO.update(subtask)
        }
    }

    fun deleteTask(tripId: Long) {
        val context = getApplication<Application>().applicationContext
        val taskDAO = ToExploreDatabase.getDB(context).taskDAO()

        viewModelScope.launch {
            taskDAO.deleteTask(tripId)
        }
    }
}
