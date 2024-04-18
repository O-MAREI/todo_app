package com.example.project.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    TaskEntity.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = TripEntity::class,
        parentColumns = ["id"],
        childColumns = ["trip_id"],
        onDelete = CASCADE
    )]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trip_id: Long,
    val title: String,
    val location_created: String? = null,
    val location: String? = null,
    val date_created: String? = null, // java.sql.Date gave errors
    val date_due: String? = null,
    val image: String? = null,
    val description: String? = null,
    val radius: Int? = null,
    val priority: Int,
    val completed: Boolean = false,
    val is_template: String = "",
    var in_geofence: Boolean? = false,
    var date_reached: Boolean? = false
) {
    companion object {
        const val TABLE_NAME = "Tasks"
    }
}
