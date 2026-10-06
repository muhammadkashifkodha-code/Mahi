package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_PROMPT = """
You are Mahi AI Sathi (ماہی اے آئی ساتھی), a warm, intelligent, respectful, and enthusiastic personal AI companion.
Primary Language: Roman Urdu (Urdu written in Roman/English alphabet, e.g. "Main theek hoon, aap sunayein?"), with natural English terms where common in Pakistan/South Asia.
Tone: Warm, friendly ("Dostana"), empathetic, and very helpful.
You assist with:
1. Daily schedule planning and motivation
2. Task breakdown and productivity
3. Smart reminders and notes organization
4. Research and learning topics explained simply
5. Android smartphone troubleshooting and tips
Keep your answers mobile-friendly: clean, well-formatted, with emojis and short bullet points when explaining steps.
"""

    suspend fun generateResponse(
        userPrompt: String,
        customApiKey: String? = null,
        languagePreference: String = "Roman Urdu",
        tonePreference: String = "Dostana",
        conversationHistory: List<Pair<String, Boolean>> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        val apiKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey.trim()
            try { BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" } catch (e: Throwable) { false } -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (apiKey.isBlank()) {
            return@withContext getSmartLocalResponse(userPrompt, languagePreference, tonePreference)
        }

        try {
            val root = JSONObject()

            // System instruction
            val sysInstructionObj = JSONObject()
            val sysPartsArr = JSONArray()
            val fullSystemPrompt = "$SYSTEM_PROMPT\nUser Language Preference: $languagePreference\nPersona Tone: $tonePreference"
            sysPartsArr.put(JSONObject().put("text", fullSystemPrompt))
            sysInstructionObj.put("parts", sysPartsArr)
            root.put("systemInstruction", sysInstructionObj)

            // Contents (History + Latest userPrompt)
            val contentsArr = JSONArray()

            // Recent history up to last 6 messages
            val recentHistory = conversationHistory.takeLast(6)
            for ((text, isUser) in recentHistory) {
                val contentObj = JSONObject()
                contentObj.put("role", if (isUser) "user" else "model")
                val parts = JSONArray().put(JSONObject().put("text", text))
                contentObj.put("parts", parts)
                contentsArr.put(contentObj)
            }

            // Current user message
            val currentContent = JSONObject()
            currentContent.put("role", "user")
            currentContent.put("parts", JSONArray().put(JSONObject().put("text", userPrompt)))
            contentsArr.put(currentContent)

            root.put("contents", contentsArr)

            // Generation config
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.7)
            genConfig.put("topP", 0.95)
            root.put("generationConfig", genConfig)

            val requestBody = root.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val url = "$BASE_URL?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "API Error: ${response.code} $responseBody")
                return@withContext getSmartLocalResponse(userPrompt, languagePreference, tonePreference)
            }

            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text")
                    if (text.isNotBlank()) {
                        return@withContext text
                    }
                }
            }

            getSmartLocalResponse(userPrompt, languagePreference, tonePreference)
        } catch (e: Exception) {
            Log.e(TAG, "Request exception", e)
            getSmartLocalResponse(userPrompt, languagePreference, tonePreference)
        }
    }

    /**
     * Intelligent Roman Urdu companion offline responses when internet or API key is absent.
     */
    private fun getSmartLocalResponse(
        query: String,
        lang: String,
        tone: String
    ): String {
        val q = query.lowercase().trim()

        return when {
            q.contains("salam") || q.contains("hello") || q.contains("hi") || q.contains("kaise ho") || q.contains("kese ho") ->
                "Walaikum Assalam! Main theek hoon aur bilkul fit! 😊 Aap ka din kaisa guzar raha hai? Aaj Tasks, Planner ya Mobile Help mein kya madad karoon?"

            q.contains("naam") || q.contains("who are you") || q.contains("kon ho") || q.contains("kaun ho") ->
                "Mera naam **Mahi** hai! 🌟 Main aapka apna AI Sathi hoon. Main Roman Urdu mein baat kar sakta hoon, aapke tasks aur daily planner manage kar sakta hoon, reminders set kar sakta hoon aur research mein madad kar sakta hoon!"

            q.contains("battery") || q.contains("charge") || q.contains("drain") ->
                "📱 **Battery Bachane Ke Asan Tips:**\n1. **Dark Mode on karein:** OLED screen par 30% battery bachti hai.\n2. **Location & Bluetooth:** Jab zaroorat na ho to band rakhein.\n3. **Background Apps:** Settings > Battery > Battery Saver enable karein.\n4. **Auto-Brightness:** Screen brightness hamesha 50% ya auto par rakhein."

            q.contains("storage") || q.contains("space") || q.contains("memory full") ->
                "💾 **Mobile Storage Khali Karne Ke Tareeqe:**\n1. **WhatsApp Media:** WhatsApp Settings > Storage and Data > Manage Storage mein jakar barhi videos delete karein.\n2. **Cached Data:** Settings > Apps mein ja kar Chrome aur YouTube ka Cache clear karein.\n3. **Google Files App:** 'Clean' tab use karein duplicate files hatane ke liye."

            q.contains("task") || q.contains("kam") || q.contains("todo") ->
                "✅ Bilkul! Aap 'Tasks' tab mein naya task add kar sakte hain ya mujhe batayein, maslan:\n- 'Aaj sham 5 baje grocery leni hai'\n- 'Assignment complete karna hai'\nMain aapko priority ke sath organize kar ke doonga!"

            q.contains("plan") || q.contains("routine") || q.contains("din") ->
                "📅 **Daily Productive Routine (Mahi Special):**\n- **Subah:** Namaz/Walk + Sab se ahem kaam (Deep Work)\n- **Dopehar:** Important meetings & pending emails\n- **Sham:** Break, exercise aur family time\n- **Raat:** Agle din ka schedule check karna\n\n'Planner' tab mein ja kar aap apna schedule save kar sakte hain!"

            q.contains("shayari") || q.contains("poetry") || q.contains("sher") ->
                "Hazir hai shandaar sher! ✨\n\n*\"Manzil se aage barh kar manzil talash kar,\nMil jaye tujhko darya to samundar talash kar!\"*\n\nMehnat jari rakhein, kamyabi aap ke qadam choome gi!"

            q.contains("motivat") || q.contains("paigham") || q.contains("quote") ->
                "💡 **Aaj Ka Paigham:**\n*\"Har nayi subah ek naya moqa hoti hai khud ko behtar banane ka. Agar kal kuch ghalat hua tha, to aaj ka din naye azam ke sath shuru karein!\"* ✨"

            q.contains("research") || q.contains("study") || q.contains("parhai") || q.contains("exam") ->
                "📚 **Study & Research Tip (Pomodoro Technique):**\n25 minute poori tawajjoh se parhein, phir 5 minute ka break lein. 4 cycles ke baad 20 minute ka lamba break lein. 'Research' tab mein ja kar kisi bhi topic ki tafseelat jan sakte hain!"

            q.contains("wifi") || q.contains("internet") || q.contains("slow") ->
                "📶 **Slow Internet Solution:**\n1. Mobile ko 10 seconds ke liye **Airplane Mode** par lagayein aur phir off karein.\n2. Wi-Fi Router ko restart karein.\n3. Settings > Network mein ja kar APN settings check karein."

            else ->
                "Zabardast! Main ne aapki baat note kar li hai: *\"$query\"*.\n\nAap isey Tasks, Planner ya Notes mein bhi save kar sakte hain! Agar aap detailed AI analysis chahte hain to Settings mein apna Gemini API key configure kar lein taake open-ended answers mazeed shandaar mil sakein. 😊"
        }
    }
}
