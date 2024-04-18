package com.example.project.data

import com.example.project.entities.SubtaskEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubtaskDAO {
    @Insert
    suspend fun insert(key: SubtaskEntity): Long
    @Update
    suspend fun update(key: SubtaskEntity)
    @Delete
    suspend fun delete(key: SubtaskEntity)
    // TODO Note: non-suspend gets not using Flows for continual stream of data may not work
    @Query("SELECT * from " + SubtaskEntity.TABLE_NAME + " WHERE id = :id")
    suspend fun getSubtask(id: Long): SubtaskEntity
    @Query("SELECT * from " + SubtaskEntity.TABLE_NAME + " ORDER BY completed ASC")
    fun getAllSubtasks(): List<SubtaskEntity>
}