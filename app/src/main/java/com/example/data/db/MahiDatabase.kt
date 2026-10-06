package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.MahiDao
import com.example.data.model.ChatMessage
import com.example.data.model.HabitItem
import com.example.data.model.NoteItem
import com.example.data.model.PlannerItem
import com.example.data.model.ReminderItem
import com.example.data.model.TaskItem

@Database(
    entities = [
        ChatMessage::class,
        TaskItem::class,
        ReminderItem::class,
        NoteItem::class,
        PlannerItem::class,
        HabitItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MahiDatabase : RoomDatabase() {
    abstract fun mahiDao(): MahiDao

    companion object {
        @Volatile
        private var INSTANCE: MahiDatabase? = null

        fun getDatabase(context: Context): MahiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MahiDatabase::class.java,
                    "mahi_sathi_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
