package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "general" // general, research, mobile_help
)

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General", // Work, Personal, Study, Health
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val dueDate: Long = 0L,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dateTime: Long,
    val repeatType: String = "ONCE", // ONCE, DAILY, WEEKLY
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Idea", // Idea, Work, Study, Diary
    val colorHex: String = "#E0E7FF",
    val isPinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "planner_items")
data class PlannerItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timeSlot: String,
    val title: String,
    val period: String = "MORNING", // MORNING, AFTERNOON, EVENING, NIGHT
    val isDone: Boolean = false,
    val dateStr: String = "" // YYYY-MM-DD
)

@Entity(tableName = "habits")
data class HabitItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String = "check",
    val target: Int = 1,
    val completedCount: Int = 0,
    val dateStr: String = ""
)
