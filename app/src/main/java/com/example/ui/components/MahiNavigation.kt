package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.viewmodel.ScreenDestination

data class NavItem(
    val destination: ScreenDestination,
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

val bottomNavItems = listOf(
    NavItem(ScreenDestination.CHAT, "Chat", Icons.Filled.Forum, "nav_bottom_chat"),
    NavItem(ScreenDestination.TASKS, "Tasks", Icons.Filled.CheckCircle, "nav_bottom_tasks"),
    NavItem(ScreenDestination.PLANNER, "Planner", Icons.Filled.CalendarMonth, "nav_bottom_planner"),
    NavItem(ScreenDestination.NOTES, "Notes", Icons.Filled.Description, "nav_bottom_notes")
)

val drawerNavItems = listOf(
    NavItem(ScreenDestination.CHAT, "Mahi AI Chat", Icons.Filled.Forum, "nav_drawer_chat"),
    NavItem(ScreenDestination.TASKS, "Tasks & To-Do", Icons.Filled.CheckCircle, "nav_drawer_tasks"),
    NavItem(ScreenDestination.REMINDERS, "Smart Reminders", Icons.Filled.Alarm, "nav_drawer_reminders"),
    NavItem(ScreenDestination.NOTES, "Notes & Voice Memos", Icons.Filled.Description, "nav_drawer_notes"),
    NavItem(ScreenDestination.PLANNER, "Daily Planner", Icons.Filled.CalendarMonth, "nav_drawer_planner"),
    NavItem(ScreenDestination.RESEARCH, "Research Assistant", Icons.Filled.Psychology, "nav_drawer_research"),
    NavItem(ScreenDestination.MOBILE_HELP, "Mobile Help & Tips", Icons.Filled.PhoneAndroid, "nav_drawer_mobile_help"),
    NavItem(ScreenDestination.SETTINGS, "Settings & Persona", Icons.Filled.Settings, "nav_drawer_settings")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MahiTopBar(
    currentScreen: ScreenDestination,
    isSpeaking: Boolean,
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onStopSpeech: () -> Unit
) {
    val title = when (currentScreen) {
        ScreenDestination.CHAT -> "Mahi AI Sathi"
        ScreenDestination.TASKS -> "Tasks & To-Dos"
        ScreenDestination.REMINDERS -> "Reminders"
        ScreenDestination.NOTES -> "Notes"
        ScreenDestination.PLANNER -> "Daily Planner"
        ScreenDestination.RESEARCH -> "Research Help"
        ScreenDestination.MOBILE_HELP -> "Mobile Help"
        ScreenDestination.SETTINGS -> "Settings"
    }

    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_mahi_launcher),
                        contentDescription = "Mahi Mascot",
                        modifier = Modifier.size(36.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = "Roman Urdu Companion",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.testTag("menu_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Open Navigation Menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            if (isSpeaking) {
                IconButton(
                    onClick = onStopSpeech,
                    modifier = Modifier.testTag("stop_speech_button")
                ) {
                    BadgedBox(
                        badge = { Badge { Text("Playing") } }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Stop Speech",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag("top_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun MahiBottomBar(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentScreen == item.destination
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.destination) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

@Composable
fun MahiDrawerContent(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.width(320.dp)
    ) {
        // Drawer Header with Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_mahi_launcher),
                        contentDescription = "Mahi AI Sathi",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mahi AI Sathi",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "Aapka Zahanat-Bahar Dost",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Roman Urdu • Tasks • Planner • Reminders",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        drawerNavItems.forEach { item ->
            val selected = currentScreen == item.destination
            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                selected = selected,
                onClick = {
                    onNavigate(item.destination)
                    onCloseDrawer()
                },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .testTag(item.testTag),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                )
            )
        }
    }
}
