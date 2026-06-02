package com.muhammed.supabasejetpackcompose.presentation.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.muhammed.supabasejetpackcompose.domain.model.Note
import com.muhammed.supabasejetpackcompose.presentation.components.ModernAlertDialog
import com.muhammed.supabasejetpackcompose.ui.theme.*

@Composable
fun NotesScreen(
    padding: PaddingValues,
    state: NotesUiState,
    onEvent: (NotesEvent) -> Unit,
    onOpenAdd: () -> Unit,
    onOpenDetail: (Note) -> Unit,
    onResolveImageUrl: (String) -> String
) {
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    if (noteToDelete != null) {
        ModernAlertDialog(
            title = "Delete Note",
            text = "Are you sure you want to delete this note?",
            confirmText = "Delete",
            confirmColor = ErrorMain,
            onConfirm = {
                noteToDelete?.let { onEvent(NotesEvent.DeleteNote(it)) }
                noteToDelete = null
            },
            onDismiss = { noteToDelete = null }
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Bar
            OutlinedTextField(
                value = state.query,
                onValueChange = { onEvent(NotesEvent.QueryChanged(it)) },
                placeholder = { Text("Search your notes...", color = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryLight) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryMain,
                    unfocusedBorderColor = SurfaceLighter,
                    focusedContainerColor = SurfaceDark.copy(alpha = 0.5f),
                    unfocusedContainerColor = SurfaceDark.copy(alpha = 0.5f),
                    unfocusedTextColor = TextPrimary,
                    focusedTextColor = TextPrimary
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            val filtered = state.notes.filter {
                it.title.contains(state.query, true) || it.content.contains(state.query, true)
            }

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No notes yet", style = MaterialTheme.typography.headlineMedium, color = TextMuted)
                        Text("Tap the + button to add your first note", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(filtered, key = Note::id) { note ->
                        NoteItem(
                            note = note,
                            onDelete = { noteToDelete = note },
                            onClick = { onOpenDetail(note) },
                            onResolveImageUrl = onResolveImageUrl
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onOpenAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp),
            containerColor = PrimaryMain,
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun NoteItem(
    note: Note,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onResolveImageUrl: (String) -> String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            if (note.imageUrl != null) {
                AsyncImage(
                    model = onResolveImageUrl(note.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = ErrorMain.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = note.updatedAt.take(10), // Simple date format
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
            }
        }
    }
}
