package com.example.project.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.project.entities.TripEntity
import com.example.project.entities.TaskEntity
import com.example.project.entities.SubtaskEntity

@Database(
    entities = [TripEntity::class, TaskEntity::class, SubtaskEntity::class],
    version = 10, exportSchema = false
)
abstract class ToExploreDatabase: RoomDatabase() {
    abstract fun tripDAO(): TripDAO
    abstract fun taskDAO(): TaskDAO
    abstract fun subtaskDAO(): SubtaskDAO

    companion object {
        private const val DB_NAME = "to_explore_db"
        @Volatile private var thisDB: ToExploreDatabase? = null
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                ToExploreDatabase::class.java,
                DB_NAME
            ).fallbackToDestructiveMigration().build()


        fun getDB(context: Context): ToExploreDatabase =
            thisDB?: synchronized(this) {
                thisDB?:buildDatabase(context).also{ thisDB = it }
            }
    }
}

// TODO Settings Table to persist user settings on app close?
