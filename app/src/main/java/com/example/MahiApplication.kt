package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.data.db.MahiDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.HabitItem
import com.example.data.model.NoteItem
import com.example.data.model.PlannerItem
import com.example.data.model.TaskItem
import com.example.data.repository.MahiRepository
import com.example.reminder.ReminderReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MahiApplication : Application() {

    lateinit var repository: MahiRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = MahiDatabase.getDatabase(this)
        repository = MahiRepository(database.mahiDao())

        createNotificationChannel()
        seedInitialDataIfNeeded()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReminderReceiver.CHANNEL_ID,
                "Mahi AI Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for Mahi AI reminders and task alerts"
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun seedInitialDataIfNeeded() {
        CoroutineScope(Dispatchers.IO).launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Seed chat welcome
            val existingChat = repository.getChatMessages("general").first()
            if (existingChat.isEmpty()) {
                repository.insertChatMessage(
                    ChatMessage(
                        text = "Assalam-o-Alaikum! Main hoon aapka Mahi AI Sathi! 🌟\n\nMain aapki Roman Urdu mein madad karne ke liye tayyar hoon. Aap mujhse tasks, daily schedule, reminders, notes, research aur mobile phone troubleshooting ke baare mein kuch bhi pooch sakte hain!",
                        isUser = false,
                        category = "general"
                    )
                )
            }

            // Seed sample tasks
            val existingTasks = repository.allTasks.first()
            if (existingTasks.isEmpty()) {
                repository.insertTask(
                    TaskItem(
                        title = "Mahi AI Sathi ke features explore karein",
                        description = "Chat, Daily Planner, Notes aur Reminders check karein",
                        category = "Personal",
                        priority = "HIGH"
                    )
                )
                repository.insertTask(
                    TaskItem(
                        title = "Aaj ka schedule plan karein",
                        description = "Planner tab mein ja kar time blocks set karein",
                        category = "Work",
                        priority = "MEDIUM"
                    )
                )
            }

            // Seed planner items
            val existingPlanner = repository.getPlannerItems(todayStr).first()
            if (existingPlanner.isEmpty()) {
                repository.insertPlannerItem(PlannerItem(timeSlot = "07:30 AM", title = "Subah ki walk aur tazza hawa", period = "MORNING", dateStr = todayStr))
                repository.insertPlannerItem(PlannerItem(timeSlot = "09:00 AM", title = "Ahem kaam (Deep Focus Work)", period = "MORNING", dateStr = todayStr))
                repository.insertPlannerItem(PlannerItem(timeSlot = "02:00 PM", title = "Lunch & short break", period = "AFTERNOON", dateStr = todayStr))
                repository.insertPlannerItem(PlannerItem(timeSlot = "05:00 PM", title = "Pending emails & task review", period = "EVENING", dateStr = todayStr))
                repository.insertPlannerItem(PlannerItem(timeSlot = "10:30 PM", title = "Agle din ki planning & araam", period = "NIGHT", dateStr = todayStr))
            }

            // Seed habits
            val existingHabits = repository.getHabitsForDate(todayStr).first()
            if (existingHabits.isEmpty()) {
                repository.insertHabit(HabitItem(name = "8 Glass Pani Peena", target = 8, completedCount = 3, dateStr = todayStr))
                repository.insertHabit(HabitItem(name = "20 Min Walk / Exercise", target = 1, completedCount = 0, dateStr = todayStr))
                repository.insertHabit(HabitItem(name = "Kitab Parhna (15 mins)", target = 1, completedCount = 0, dateStr = todayStr))
                repository.insertHabit(HabitItem(name = "Namaz / Prayer", target = 5, completedCount = 2, dateStr = todayStr))
            }

            // Seed sample note
            val existingNotes = repository.allNotes.first()
            if (existingNotes.isEmpty()) {
                repository.insertNote(
                    NoteItem(
                        title = "Khush-Aamdeed Note - Mahi Sathi",
                        content = "Mahi AI Sathi mein aap voice ke zariye bhi notes record kar sakte hain aur 'Summarize' button daba kar AI se organize karwa sakte hain!",
                        category = "Idea",
                        colorHex = "#E0E7FF",
                        isPinned = true
                    )
                )
            }
        }
    }
}
