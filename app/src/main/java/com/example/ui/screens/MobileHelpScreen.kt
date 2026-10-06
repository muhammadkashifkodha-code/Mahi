package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MahiViewModel

data class HelpGuide(
    val title: String,
    val icon: ImageVector,
    val summary: String,
    val steps: List<String>,
    val settingsAction: String? = null
)

@Composable
fun MobileHelpScreen(viewModel: MahiViewModel) {
    val context = LocalContext.current
    val messages by viewModel.mobileHelpMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    var customQuestion by remember { mutableStateOf("") }

    val guides = remember {
        listOf(
            HelpGuide(
                title = "Battery Jaldi Khatam Hoti Hai?",
                icon = Icons.Filled.BatteryAlert,
                summary = "Fast battery drain rokne ke 4 asan tareeqe",
                steps = listOf(
                    "Dark Mode on karein (Display Settings mein)",
                    "Location aur Bluetooth jab zaroorat na ho band rakhein",
                    "Settings > Battery mein 'Battery Saver' on karein",
                    "Aisi apps check karein jo background mein battery khati hain"
                ),
                settingsAction = Settings.ACTION_BATTERY_SAVER_SETTINGS
            ),
            HelpGuide(
                title = "Mobile Storage Full Ho Gayi?",
                icon = Icons.Filled.Folder,
                summary = "Phone ki memory khali karein bina zaroori data gawaye",
                steps = listOf(
                    "WhatsApp Settings > Storage and Data > Manage Storage mein jakar barhi videos delete karein",
                    "Settings > Apps mein Chrome aur YouTube ka 'Clear Cache' karein",
                    "Google Photos mein 'Free up space' ka option use karein",
                    "Files by Google app se duplicate files delete karein"
                ),
                settingsAction = Settings.ACTION_INTERNAL_STORAGE_SETTINGS
            ),
            HelpGuide(
                title = "Phone Slow Ya Garam Ho Raha Hai?",
                icon = Icons.Filled.Speed,
                summary = "Phone ki speed tez karein aur heating issue rokein",
                steps = listOf(
                    "Hafte mein kam az kam ek martaba phone ko Restart karein",
                    "Charging ke dauran phone par heavy games na khelein",
                    "Faltu apps ko uninstall karein jin ki zaroorat nahi",
                    "Settings > System update mein ja kar latest version update karein"
                ),
                settingsAction = Settings.ACTION_APPLICATION_SETTINGS
            ),
            HelpGuide(
                title = "Wi-Fi Ya Internet Masla?",
                icon = Icons.Filled.Wifi,
                summary = "Slow data aur disconnected Wi-Fi theek karein",
                steps = listOf(
                    "Phone ko 10 second ke liye Airplane Mode par lagayein phir off karein",
                    "Wi-Fi router ko 1 minute ke liye band kar ke dubara on karein",
                    "Settings > Network & internet mein Wi-Fi network ko Forget kar ke reconnect karein",
                    "SIM card ki APN settings verify karein"
                ),
                settingsAction = Settings.ACTION_WIFI_SETTINGS
            ),
            HelpGuide(
                title = "Phone Security & Privacy",
                icon = Icons.Filled.Security,
                summary = "Apna data aur apps safe rakhein",
                steps = listOf(
                    "Settings > Privacy > Permission manager mein check karein kon si app Camera/Mic use kar rahi hai",
                    "Google Play Protect ko scan karne dein",
                    "Anjaan links aur APK files install na karein",
                    "Screen Lock (Fingerprint/PIN) hamesha active rakhein"
                ),
                settingsAction = Settings.ACTION_SECURITY_SETTINGS
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.PhoneAndroid,
                    contentDescription = "Mobile Help",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Mobile Phone Rahnumai (Help)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Android problems ka fori hal aur shortcuts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            item {
                Text(
                    text = "🔧 Ahem Problems & Hal (Guides)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(guides) { guide ->
                HelpGuideCard(guide = guide, onOpenSetting = { action ->
                    try {
                        context.startActivity(Intent(action))
                    } catch (e: Exception) {
                        context.startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                })
            }

            if (messages.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💬 Mahi Ke Sath Phone Sawalat:",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(messages) { msg ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (msg.isUser) "Aap: ${msg.text}" else "Mahi Hal:\n${msg.text}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            if (isGenerating) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mahi mobile guide dhoond raha hai...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Custom phone question input
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customQuestion,
                    onValueChange = { customQuestion = it },
                    placeholder = { Text("Koi aur mobile problem poochni hai?") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mobile_help_input"),
                    maxLines = 2
                )

                IconButton(
                    onClick = {
                        if (customQuestion.isNotBlank()) {
                            val q = customQuestion
                            customQuestion = ""
                            viewModel.sendMessage(q, "mobile_help")
                        }
                    },
                    modifier = Modifier.testTag("mobile_help_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun HelpGuideCard(
    guide: HelpGuide,
    onOpenSetting: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = guide.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = guide.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = guide.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    guide.steps.forEachIndexed { index, step ->
                        Text(
                            text = "${index + 1}. $step",
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    if (guide.settingsAction != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onOpenSetting(guide.settingsAction) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("open_setting_${guide.title.take(6)}")
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Phone Settings Kholein", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
