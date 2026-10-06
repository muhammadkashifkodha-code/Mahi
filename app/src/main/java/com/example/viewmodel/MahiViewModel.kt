package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MahiApplication
import com.example.data.api.GeminiClient
import com.example.data.model.ChatMessage
import com.example.data.model.HabitItem
import com.example.data.model.NoteItem
import com.example.data.model.PlannerItem
import com.example.data.model.ReminderItem
import com.example.data.model.TaskItem
import com.example.data.repository.MahiRepository
import com.example.reminder.ReminderScheduler
import com.example.service.SpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ScreenDestination {
    CHAT,
    TASKS,
    REMINDERS,
    NOTES,
    PLANNER,
    RESEARCH,
    MOBILE_HELP,
    SETTINGS
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MahiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MahiRepository = (application as MahiApplication).repository
    val speechManager = SpeechManager(application)

    private val _currentScreen = MutableStateFlow(ScreenDestination.CHAT)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    // --- Date String ---
    private val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // --- Flows ---
    val chatMessages: StateFlow<List<ChatMessage>> = repository.getChatMessages("general")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val researchMessages: StateFlow<List<ChatMessage>> = repository.getChatMessages("research")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mobileHelpMessages: StateFlow<List<ChatMessage>> = repository.getChatMessages("mobile_help")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskItem>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<ReminderItem>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val notes: StateFlow<List<NoteItem>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allNotes else repository.searchNotes(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plannerItems: StateFlow<List<PlannerItem>> = repository.getPlannerItems(todayDateStr)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitItem>> = repository.getHabitsForDate(todayDateStr)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Loading & State ---
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // --- Settings Preferences ---
    val languagePref = MutableStateFlow("Roman Urdu")
    val tonePref = MutableStateFlow("Dostana")
    val ttsSpeed = MutableStateFlow(0.95f)
    val ttsPitch = MutableStateFlow(1.05f)
    val isDarkTheme = MutableStateFlow<Boolean?>(null) // null for system default
    val customApiKey = MutableStateFlow("")

    // --- Chat Functions ---
    fun sendMessage(prompt: String, category: String = "general") {
        if (prompt.isBlank()) return
        val trimmed = prompt.trim()

        viewModelScope.launch {
            // Save user message
            repository.insertChatMessage(ChatMessage(text = trimmed, isUser = true, category = category))

            _isGenerating.value = true

            // Gather past messages for context
            val history = when (category) {
                "research" -> researchMessages.value
                "mobile_help" -> mobileHelpMessages.value
                else -> chatMessages.value
            }.map { it.text to it.isUser }

            val response = GeminiClient.generateResponse(
                userPrompt = trimmed,
                customApiKey = customApiKey.value,
                languagePreference = languagePref.value,
                tonePreference = tonePref.value,
                conversationHistory = history
            )

            repository.insertChatMessage(ChatMessage(text = response, isUser = false, category = category))
            _isGenerating.value = false
        }
    }

    fun clearChat(category: String = "general") {
        viewModelScope.launch {
            repository.clearChatMessages(category)
        }
    }

    // --- Speech TTS / STT ---
    fun speakText(text: String) {
        speechManager.speak(text, rate = ttsSpeed.value, pitch = ttsPitch.value)
    }

    fun stopSpeaking() {
        speechManager.stopSpeaking()
    }

    fun startListening(onResult: (String) -> Unit) {
        speechManager.startListening(onResult)
    }

    fun stopListening() {
        speechManager.stopListening()
    }

    // --- Task Functions ---
    fun addTask(
        title: String,
        description: String = "",
        category: String = "Personal",
        priority: String = "MEDIUM",
        dueDate: Long = 0L
    ) {
        viewModelScope.launch {
            repository.insertTask(
                TaskItem(
                    title = title.trim(),
                    description = description.trim(),
                    category = category,
                    priority = priority,
                    dueDate = dueDate
                )
            )
        }
    }

    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun generateTasksWithAi(goalPrompt: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            val prompt = "Create a list of 3 concise, practical actionable tasks in Roman Urdu to achieve this goal: '$goalPrompt'. Format each task on a new line starting with '-'."
            val response = GeminiClient.generateResponse(
                userPrompt = prompt,
                customApiKey = customApiKey.value,
                languagePreference = languagePref.value,
                tonePreference = tonePref.value
            )

            val lines = response.lines()
                .map { it.trim().removePrefix("-").removePrefix("•").removePrefix("*").trim() }
                .filter { it.length > 3 }
                .take(4)

            for (taskTitle in lines) {
                repository.insertTask(
                    TaskItem(
                        title = taskTitle,
                        description = "Mahi AI Task breakdown for: $goalPrompt",
                        category = "Personal",
                        priority = "HIGH"
                    )
                )
            }
            _isGenerating.value = false
        }
    }

    // --- Reminder Functions ---
    fun addReminder(title: String, dateTime: Long, repeatType: String = "ONCE") {
        viewModelScope.launch {
            val id = repository.insertReminder(
                ReminderItem(
                    title = title.trim(),
                    dateTime = dateTime,
                    repeatType = repeatType
                )
            )
            ReminderScheduler.scheduleReminder(
                getApplication(),
                id = id,
                title = title.trim(),
                timeMillis = dateTime
            )
        }
    }

    fun toggleReminder(reminder: ReminderItem) {
        viewModelScope.launch {
            val updated = reminder.copy(isCompleted = !reminder.isCompleted)
            repository.updateReminder(updated)
            if (updated.isCompleted) {
                ReminderScheduler.cancelReminder(getApplication(), reminder.id)
            } else {
                ReminderScheduler.scheduleReminder(
                    getApplication(),
                    reminder.id,
                    reminder.title,
                    reminder.dateTime
                )
            }
        }
    }

    fun deleteReminder(reminder: ReminderItem) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(getApplication(), reminder.id)
            repository.deleteReminder(reminder)
        }
    }

    // --- Notes Functions ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addNote(
        title: String,
        content: String,
        category: String = "Idea",
        colorHex: String = "#E0E7FF",
        isPinned: Boolean = false
    ) {
        viewModelScope.launch {
            repository.insertNote(
                NoteItem(
                    title = title.trim(),
                    content = content.trim(),
                    category = category,
                    colorHex = colorHex,
                    isPinned = isPinned
                )
            )
        }
    }

    fun updateNote(note: NoteItem) {
        viewModelScope.launch {
            repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(note: NoteItem) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun summarizeNoteWithAi(note: NoteItem) {
        viewModelScope.launch {
            _isGenerating.value = true
            val prompt = "Please summarize and organize this note cleanly in Roman Urdu with key bullet points and action items:\n\nTitle: ${note.title}\nContent: ${note.content}"
            val summary = GeminiClient.generateResponse(
                userPrompt = prompt,
                customApiKey = customApiKey.value,
                languagePreference = languagePref.value,
                tonePreference = tonePref.value
            )
            repository.updateNote(
                note.copy(
                    content = "${note.content}\n\n📌 **Mahi AI Khulasa (Summary):**\n$summary",
                    updatedAt = System.currentTimeMillis()
                )
            )
            _isGenerating.value = false
        }
    }

    // --- Planner Functions ---
    fun addPlannerItem(timeSlot: String, title: String, period: String = "MORNING") {
        viewModelScope.launch {
            repository.insertPlannerItem(
                PlannerItem(
                    timeSlot = timeSlot.trim(),
                    title = title.trim(),
                    period = period,
                    dateStr = todayDateStr
                )
            )
        }
    }

    fun togglePlannerItem(item: PlannerItem) {
        viewModelScope.launch {
            repository.updatePlannerItem(item.copy(isDone = !item.isDone))
        }
    }

    fun deletePlannerItem(item: PlannerItem) {
        viewModelScope.launch {
            repository.deletePlannerItem(item)
        }
    }

    fun incrementHabit(habit: HabitItem) {
        viewModelScope.launch {
            val newCount = if (habit.completedCount >= habit.target) 0 else habit.completedCount + 1
            repository.updateHabit(habit.copy(completedCount = newCount))
        }
    }

    fun generateDayScheduleWithAi(focusGoal: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            val prompt = "Create a 4-slot balanced day routine in Roman Urdu for someone focusing on '$focusGoal'. Return exactly 4 lines formatted as: Time | Title | Morning/Afternoon/Evening/Night."
            val response = GeminiClient.generateResponse(
                userPrompt = prompt,
                customApiKey = customApiKey.value,
                languagePreference = languagePref.value,
                tonePreference = tonePref.value
            )

            val lines = response.lines().filter { it.contains("|") }
            if (lines.isNotEmpty()) {
                repository.clearPlannerForDate(todayDateStr)
                for (line in lines) {
                    val parts = line.split("|").map { it.trim() }
                    if (parts.size >= 2) {
                        val time = parts[0]
                        val title = parts[1]
                        val period = if (parts.size >= 3) {
                            when {
                                parts[2].contains("afternoon", true) || parts[2].contains("dopehar", true) -> "AFTERNOON"
                                parts[2].contains("evening", true) || parts[2].contains("sham", true) -> "EVENING"
                                parts[2].contains("night", true) || parts[2].contains("raat", true) -> "NIGHT"
                                else -> "MORNING"
                            }
                        } else "MORNING"
                        repository.insertPlannerItem(
                            PlannerItem(timeSlot = time, title = title, period = period, dateStr = todayDateStr)
                        )
                    }
                }
            }
            _isGenerating.value = false
        }
    }

    fun saveResearchAsNote(title: String, content: String) {
        viewModelScope.launch {
            repository.insertNote(
                NoteItem(
                    title = "Research: $title",
                    content = content,
                    category = "Study",
                    colorHex = "#DCFCE7",
                    isPinned = false
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
    }
}
