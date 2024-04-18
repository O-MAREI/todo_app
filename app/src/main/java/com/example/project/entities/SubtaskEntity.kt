package com.example.project.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = SubtaskEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = ["id"],
        childColumns = ["task_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SubtaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val task_id: Long,
    val title: String,
    val completed: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "Subtasks"
    }
}
