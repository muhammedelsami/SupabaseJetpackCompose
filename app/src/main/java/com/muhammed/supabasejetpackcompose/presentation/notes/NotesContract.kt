package com.muhammed.supabasejetpackcompose.presentation.notes

import android.net.Uri
import com.muhammed.supabasejetpackcompose.domain.model.Note

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val query: String = "",
    val titleInput: String = "",
    val contentInput: String = "",
    val imageUri: Uri? = null,
    val selectedNote: Note? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

sealed interface NotesEvent {
    data class QueryChanged(val value: String) : NotesEvent
    data class TitleChanged(val value: String) : NotesEvent
    data class ContentChanged(val value: String) : NotesEvent
    data class SelectNote(val note: Note) : NotesEvent
    data object AddNote : NotesEvent
    data object UpdateNote : NotesEvent
    data class DeleteNote(val note: Note) : NotesEvent
    data class PickImage(val uri: Uri?) : NotesEvent
    data object ResetSaveState : NotesEvent
}
