package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatMessage
import com.example.data.model.HabitItem
import com.example.data.model.NoteItem
import com.example.data.model.PlannerItem
import com.example.data.model.ReminderItem
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MahiDao {

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages WHERE category = :category ORDER BY timestamp ASC")
    fun getChatMessages(category: String = "general"): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages WHERE category = :category")
    suspend fun clearChatMessages(category: String = "general")

    // --- Tasks ---
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, priority DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    // --- Reminders ---
    @Query("SELECT * FROM reminders ORDER BY isCompleted ASC, dateTime ASC")
    fun getAllReminders(): Flow<List<ReminderItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderItem): Long

    @Update
    suspend fun updateReminder(reminder: ReminderItem)

    @Delete
    suspend fun deleteReminder(reminder: ReminderItem)

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun getReminderById(id: Long): ReminderItem?

    // --- Notes ---
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteItem>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY isPinned DESC, updatedAt DESC")
    fun searchNotes(query: String): Flow<List<NoteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteItem): Long

    @Update
    suspend fun updateNote(note: NoteItem)

    @Delete
    suspend fun deleteNote(note: NoteItem)

    // --- Planner Items ---
    @Query("SELECT * FROM planner_items WHERE dateStr = :dateStr ORDER BY id ASC")
    fun getPlannerItems(dateStr: String): Flow<List<PlannerItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlannerItem(item: PlannerItem): Long

    @Update
    suspend fun updatePlannerItem(item: PlannerItem)

    @Delete
    suspend fun deletePlannerItem(item: PlannerItem)

    @Query("DELETE FROM planner_items WHERE dateStr = :dateStr")
    suspend fun clearPlannerForDate(dateStr: String)

    // --- Habits ---
    @Query("SELECT * FROM habits WHERE dateStr = :dateStr ORDER BY id ASC")
    fun getHabitsForDate(dateStr: String): Flow<List<HabitItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitItem): Long

    @Update
    suspend fun updateHabit(habit: HabitItem)

    @Delete
    suspend fun deleteHabit(habit: HabitItem)
}
