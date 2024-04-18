package com.example.project.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.TemplateSubTasks
import com.example.project.data.ToExploreDatabase
import com.example.project.entities.SubtaskEntity
import com.example.project.entities.TaskEntity
import kotlinx.coroutines.launch

/**
 * ViewModel for creating a new Task object
 */
class NewTaskViewModel(app: Application): AndroidViewModel(app) {
    private val taskInit: TaskEntity? = null

    private val insertedTaskIDInit: Long? = null

    var task by mutableStateOf(taskInit)
        private set

    var insertedTaskID by mutableStateOf(insertedTaskIDInit)
        private set

    private var insertedTask by mutableStateOf<TaskEntity?>(null)

    fun setCurrentTask(taskLatest: TaskEntity?) {
        task = taskLatest
    }

    fun insertOrUpdateTask(
        tripID: Long,
        title: String,
        locationCreated: String? = null,
        location: String? = null,
        dateCreated: String? = null,
        dateDue: String? = null,
        image: String? = null,
        description: String? = null,
        radius: Int? = null,
        priority: Int,
        completed: Boolean = false,
        isTemplate: String = ""
    ) {
        val context = getApplication<Application>().applicationContext
        val taskDAO = ToExploreDatabase.getDB(context).taskDAO()

        if (task != null) {
            // Update existing task
            val newTask = TaskEntity(
                id = task!!.id,
                trip_id = tripID,
                title = title,
                location_created =  locationCreated,
                location = location,
                date_created = dateCreated,
                date_due = dateDue,
                image = image,
                description = description,
                radius = radius,
                priority = priority,
                completed = completed,
                is_template = isTemplate
            )

            viewModelScope.launch {
                taskDAO.update(newTask)
                insertedTaskID = task!!.id

                Log.i("SCViewModelStuff", "task updated with id $insertedTaskID")
            }
        } else {
            // Insert new task
            val newTask = TaskEntity(
                trip_id = tripID,
                title = title,
                location_created =  locationCreated,
                location = location,
                date_created = dateCreated,
                date_due = dateDue,
                image = image,
                description = description,
                radius = radius,
                priority = priority,
                completed = completed,
                is_template = isTemplate
            )

            insertedTask = task

            viewModelScope.launch {
                insertedTaskID = taskDAO.insert(newTask)

                isTemplate.let { template ->
                    TemplateSubTasks.subTaskMap[template]?.forEach { subtask ->
                       insertTemplateSubTask(insertedTaskID!!, subtask, false)
                    }
                }

                Log.i("SCViewModelStuff", "task inserted with id $insertedTaskID")
            }
        }
    }

    private fun insertTemplateSubTask(
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

}
