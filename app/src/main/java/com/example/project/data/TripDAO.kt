package com.example.project.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.project.entities.ProgressCount
import com.example.project.entities.TripEntity
import com.example.project.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDAO {
    @Insert
    suspend fun insert(key: TripEntity): Long
    @Update
    suspend fun update(key: TripEntity)
    @Delete
    suspend fun delete(key: TripEntity)
    // Note: non-suspend gets not using Flows for continual stream of data may not work
    @Query("SELECT * from " + TripEntity.TABLE_NAME + " WHERE id = :id")
    fun getTrip(id: Long): Flow<TripEntity>
    @Query("SELECT id from " + TripEntity.TABLE_NAME + " WHERE is_active = true")
    fun getActiveTripsID(): List<Long>
    @Query("SELECT * from " + TaskEntity.TABLE_NAME + " WHERE trip_id = :trip_id")
    fun getTripTasks(trip_id: Long): Flow<List<TaskEntity>>
    @Query("SELECT * from " + TripEntity.TABLE_NAME)
    fun getAllTrips(): Flow<List<TripEntity>>
    @Query("SELECT " +
            "trip_id, " +
            "COUNT(CASE WHEN completed = 1 THEN id END) AS completed_tasks, " +
            "COUNT(id) AS total_tasks " +
            "FROM " +
             TaskEntity.TABLE_NAME +
            " GROUP BY " +
            "trip_id")
    fun getTripsProgressCounts(): Flow<List<ProgressCount>>
    @Query("UPDATE " + TripEntity.TABLE_NAME + " SET name = :name WHERE id = :id")
    suspend fun updateTripName(id: Long, name: String)
    @Query("UPDATE " + TripEntity.TABLE_NAME + " SET completed = :completed WHERE id = :id")
    suspend fun updateTripCompleted(id: Long, completed: Boolean)
    @Query("UPDATE " + TripEntity.TABLE_NAME + " SET is_active = false WHERE id = :id")
    suspend fun updateTripIsActiveToFalse(id: Long)
    @Query("UPDATE " + TripEntity.TABLE_NAME + " SET is_active = false")
    suspend fun updateAllTripsIsActiveToFalse()
    @Query("UPDATE " + TripEntity.TABLE_NAME + " SET is_active = true WHERE id = :id")
    suspend fun updateTripIsActiveToTrue(id: Long)
    @Query("UPDATE " + TripEntity.TABLE_NAME + " SET image = :image WHERE id = :id")
    suspend fun updateTripImage(id: Long, image: String)
    @Query("DELETE FROM " + TripEntity.TABLE_NAME + " WHERE id = :id")
    suspend fun deleteTrip(id: Long)
}

// TODO Order List of Subtasks using "ORDER BY *field_name* ASC" in query?