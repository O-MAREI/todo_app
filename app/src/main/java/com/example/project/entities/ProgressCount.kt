package com.example.project.entities

import androidx.room.ColumnInfo

class ProgressCount {
    @ColumnInfo(name = "trip_id")
    var tripID: Long? = null
    @ColumnInfo(name = "completed_tasks")
    var countCompleted: Int? = null
    @ColumnInfo(name = "total_tasks")
    var countTotal: Int? = null

    operator fun component1(): Long? {
        return tripID
    }
    operator fun component2(): Int? {
        return countCompleted
    }
    operator fun component3(): Int? {
        return countTotal
    }
}
