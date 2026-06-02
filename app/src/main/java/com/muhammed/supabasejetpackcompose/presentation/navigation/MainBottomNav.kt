package com.muhammed.supabasejetpackcompose.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammed.supabasejetpackcompose.domain.model.Note
import com.muhammed.supabasejetpackcompose.presentation.notes.NotesScreen
import com.muhammed.supabasejetpackcompose.presentation.notes.NotesViewModel
import com.muhammed.supabasejetpackcompose.presentation.profile.ProfileScreen
import com.muhammed.supabasejetpackcompose.presentation.profile.ProfileViewModel
import com.muhammed.supabasejetpackcompose.ui.theme.*

@Composable
fun MainBottomNav(
    notesVm: NotesViewModel,
    profileVm: ProfileViewModel,
    onOpenAdd: () -> Unit,
    onOpenDetail: (Note) -> Unit,
    onLogout: () -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = TextPrimary,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 },
                    icon = { Icon(Icons.Outlined.Notes, contentDescription = "Notes") },
                    label = { Text("Notes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryLight,
                        selectedTextColor = PrimaryLight,
                        indicatorColor = PrimaryMain.copy(alpha = 0.1f),
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                    icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryLight,
                        selectedTextColor = PrimaryLight,
                        indicatorColor = PrimaryMain.copy(alpha = 0.1f),
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
            }
        },
        containerColor = BackgroundDark
    ) { padding ->
        when (selectedIndex) {
            0 -> NotesScreen(
                padding = padding,
                state = notesVm.uiState.collectAsStateWithLifecycle().value,
                onEvent = notesVm::onEvent,
                onOpenAdd = onOpenAdd,
                onOpenDetail = onOpenDetail,
                onResolveImageUrl = notesVm::getImageUrl
            )
            else -> ProfileScreen(
                padding = padding,
                state = profileVm.uiState.collectAsStateWithLifecycle().value,
                onLogout = {
                    profileVm.logout()
                    onLogout()
                },
                onDeleteAccount = {
                    profileVm.deleteAccount()
                    onLogout()
                }
            )
        }
    }
}
