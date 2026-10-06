package com.example.data.repository

import com.example.data.dao.MahiDao
import com.example.data.model.ChatMessage
import com.example.data.model.HabitItem
import com.example.data.model.NoteItem
import com.example.data.model.PlannerItem
import com.example.data.model.ReminderItem
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

class MahiRepository(private val dao: MahiDao) {

    // Chat
    fun getChatMessages(category: String = "general"): Flow<List<ChatMessage>> =
        dao.getChatMessages(category)

    suspend fun insertChatMessage(message: ChatMessage): Long =
        dao.insertChatMessage(message)

    suspend fun clearChatMessages(category: String = "general") =
        dao.clearChatMessages(category)

    // Tasks
    val allTasks: Flow<List<TaskItem>> = dao.getAllTasks()

    suspend fun insertTask(task: TaskItem): Long = dao.insertTask(task)

    suspend fun updateTask(task: TaskItem) = dao.updateTask(task)

    suspend fun deleteTask(task: TaskItem) = dao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = dao.deleteTaskById(id)

    // Reminders
    val allReminders: Flow<List<ReminderItem>> = dao.getAllReminders()

    suspend fun insertReminder(reminder: ReminderItem): Long = dao.insertReminder(reminder)

    suspend fun updateReminder(reminder: ReminderItem) = dao.updateReminder(reminder)

    suspend fun deleteReminder(reminder: ReminderItem) = dao.deleteReminder(reminder)

    suspend fun getReminderById(id: Long): ReminderItem? = dao.getReminderById(id)

    // Notes
    val allNotes: Flow<List<NoteItem>> = dao.getAllNotes()

    fun searchNotes(query: String): Flow<List<NoteItem>> = dao.searchNotes(query)

    suspend fun insertNote(note: NoteItem): Long = dao.insertNote(note)

    suspend fun updateNote(note: NoteItem) = dao.updateNote(note)

    suspend fun deleteNote(note: NoteItem) = dao.deleteNote(note)

    // Planner
    fun getPlannerItems(dateStr: String): Flow<List<PlannerItem>> = dao.getPlannerItems(dateStr)

    suspend fun insertPlannerItem(item: PlannerItem): Long = dao.insertPlannerItem(item)

    suspend fun updatePlannerItem(item: PlannerItem) = dao.updatePlannerItem(item)

    suspend fun deletePlannerItem(item: PlannerItem) = dao.deletePlannerItem(item)

    suspend fun clearPlannerForDate(dateStr: String) = dao.clearPlannerForDate(dateStr)

    // Habits
    fun getHabitsForDate(dateStr: String): Flow<List<HabitItem>> = dao.getHabitsForDate(dateStr)

    suspend fun insertHabit(habit: HabitItem): Long = dao.insertHabit(habit)

    suspend fun updateHabit(habit: HabitItem) = dao.updateHabit(habit)

    suspend fun deleteHabit(habit: HabitItem) = dao.deleteHabit(habit)
}
