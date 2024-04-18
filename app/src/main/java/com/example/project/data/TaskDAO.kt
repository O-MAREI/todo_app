package com.example.project.data

import com.example.project.entities.TaskEntity
import com.example.project.entities.SubtaskEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {
    @Insert
    suspend fun insert(key: TaskEntity): Long
    @Update
    suspend fun update(key: TaskEntity)
    @Delete
    suspend fun delete(key: TaskEntity)
    // TODO Note: non-suspend gets not using Flows for continual stream of data may not work
    @Query("SELECT * from " + TaskEntity.TABLE_NAME + " WHERE id = :id")
    fun getTask(id: Long): Flow<TaskEntity>
    @Query("SELECT * from " + SubtaskEntity.TABLE_NAME + " WHERE task_id = :task_id")
    fun getTaskSubtasks(task_id: Long): Flow<List<SubtaskEntity>>
    @Query("SELECT * from " + TaskEntity.TABLE_NAME)
    fun getAllTasks(): Flow<List<TaskEntity>>
    @Query("SELECT * from " + TaskEntity.TABLE_NAME + " WHERE trip_id IN (:activeIds)")
    fun getActiveTasks(activeIds: List<Long>): List<TaskEntity>
    @Query("UPDATE " + TaskEntity.TABLE_NAME + " SET completed = :completed WHERE id = :id")
    suspend fun updateTaskComplete(id: Long, completed: Boolean)
    @Query("DELETE FROM " + TaskEntity.TABLE_NAME + " WHERE id = :id")
    suspend fun deleteTask(id: Long)
    @Query("UPDATE " + TaskEntity.TABLE_NAME + " SET in_geofence = :value WHERE id = :id")
    fun updateInGeofence(id: Long, value: Boolean)
    @Query("UPDATE " + TaskEntity.TABLE_NAME + " SET date_reached = :value WHERE id = :id")
    fun updateDateReached(id: Long, value: Boolean)
}

// TODO order returned lists in SQL?
