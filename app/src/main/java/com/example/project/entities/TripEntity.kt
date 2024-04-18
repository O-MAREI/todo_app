package com.example.project.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(TripEntity.TABLE_NAME)
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    val start_date: String? = null,
    val end_date: String? = null,
    val image: String? = null,
    val completed: Boolean = false,
    val is_active: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "Trips"
    }
}
