package com.muhammed.supabasejetpackcompose.presentation.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.muhammed.supabasejetpackcompose.domain.model.Note
import com.muhammed.supabasejetpackcompose.presentation.components.ModernAlertDialog
import com.muhammed.supabasejetpackcompose.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    onResolveImageUrl: (String) -> String
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        ModernAlertDialog(
            title = "Delete Note",
            text = "Are you sure you want to delete this note?",
            confirmText = "Delete",
            confirmColor = ErrorMain,
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = SurfaceDark.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = onEdit,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = PrimaryMain.copy(alpha = 0.2f))
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryLight)
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = ErrorMain.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorMain)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                if (note.imageUrl != null) {
                    AsyncImage(
                        model = onResolveImageUrl(note.imageUrl),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(PrimaryDark, SurfaceDark)
                                )
                            )
                    )
                }
                
                // Content Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, BackgroundDark.copy(alpha = 0.8f), BackgroundDark)
                            )
                        )
                )
                
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Surface(
                        color = PrimaryMain,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Note",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Last updated: ${note.updatedAt.take(16).replace("T", " ")}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                    color = TextPrimary
                )
            }
        }
    }
}
