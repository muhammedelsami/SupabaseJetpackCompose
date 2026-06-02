package com.muhammed.supabasejetpackcompose.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.muhammed.supabasejetpackcompose.presentation.components.ModernAlertDialog
import com.muhammed.supabasejetpackcompose.ui.theme.*

@Composable
fun ProfileScreen(
    padding: PaddingValues,
    state: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        ModernAlertDialog(
            title = "Sign Out",
            text = "Are you sure you want to sign out from your account?",
            confirmText = "Sign Out",
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showDeleteDialog) {
        ModernAlertDialog(
            title = "Delete Account",
            text = "Are you sure you want to delete your account and all data permanently? This action cannot be undone.",
            confirmText = "Delete",
            confirmColor = ErrorMain,
            onConfirm = {
                showDeleteDialog = false
                onDeleteAccount()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Avatar Section
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(PrimaryMain.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = PrimaryLight
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (state.isEditing) {
                OutlinedTextField(
                    value = state.nameInput,
                    onValueChange = { onEvent(ProfileEvent.NameChanged(it)) },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { onEvent(ProfileEvent.SaveProfile) }) {
                            Icon(Icons.Default.Check, contentDescription = "Save", tint = PrimaryLight)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryMain,
                        unfocusedBorderColor = SurfaceLighter,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedLabelColor = PrimaryMain,
                        unfocusedLabelColor = TextMuted
                    )
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.profile?.name ?: "User",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    IconButton(onClick = { onEvent(ProfileEvent.ToggleEdit) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryLight, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Text(
                text = state.profile?.email.orEmpty(),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Info Sections
            ProfileInfoItem(label = "User ID", value = state.profile?.id.orEmpty())
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Actions Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceDark
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileActionButton(
                        label = "Sign Out",
                        icon = Icons.Default.Logout,
                        color = TextPrimary,
                        onClick = { showLogoutDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = SurfaceLighter)
                    ProfileActionButton(
                        label = "Delete Account",
                        icon = Icons.Default.Delete,
                        color = ErrorMain,
                        onClick = { showDeleteDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextMuted)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ProfileActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = color)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = null)
            Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}
