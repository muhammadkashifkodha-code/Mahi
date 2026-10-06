package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MahiBottomBar
import com.example.ui.components.MahiDrawerContent
import com.example.ui.components.MahiTopBar
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.MobileHelpScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.ResearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MahiViewModel
import com.example.viewmodel.ScreenDestination
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val mahiViewModel: MahiViewModel = viewModel()
            val isDarkThemePref by mahiViewModel.isDarkTheme.collectAsState()
            val useDarkTheme = isDarkThemePref ?: isSystemInDarkTheme()

            // Request Notification Permission on Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* Handled */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            MyApplicationTheme(darkTheme = useDarkTheme) {
                MahiMainApp(viewModel = mahiViewModel)
            }
        }
    }
}

@Composable
fun MahiMainApp(viewModel: MahiViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isSpeaking by viewModel.speechManager.isSpeaking.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle back button smoothly
    BackHandler(enabled = drawerState.isOpen || currentScreen != ScreenDestination.CHAT) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (currentScreen != ScreenDestination.CHAT) {
            viewModel.navigateTo(ScreenDestination.CHAT)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MahiDrawerContent(
                currentScreen = currentScreen,
                onNavigate = { destination ->
                    viewModel.navigateTo(destination)
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                MahiTopBar(
                    currentScreen = currentScreen,
                    isSpeaking = isSpeaking,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onSettingsClick = {
                        viewModel.navigateTo(ScreenDestination.SETTINGS)
                    },
                    onStopSpeech = {
                        viewModel.stopSpeaking()
                    }
                )
            },
            bottomBar = {
                MahiBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { destination ->
                        viewModel.navigateTo(destination)
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    ScreenDestination.CHAT -> ChatScreen(viewModel = viewModel)
                    ScreenDestination.TASKS -> TasksScreen(viewModel = viewModel)
                    ScreenDestination.REMINDERS -> RemindersScreen(viewModel = viewModel)
                    ScreenDestination.NOTES -> NotesScreen(viewModel = viewModel)
                    ScreenDestination.PLANNER -> PlannerScreen(viewModel = viewModel)
                    ScreenDestination.RESEARCH -> ResearchScreen(viewModel = viewModel)
                    ScreenDestination.MOBILE_HELP -> MobileHelpScreen(viewModel = viewModel)
                    ScreenDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
