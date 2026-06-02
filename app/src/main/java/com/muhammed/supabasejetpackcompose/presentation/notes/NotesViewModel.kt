package com.muhammed.supabasejetpackcompose.presentation.notes

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.supabasejetpackcompose.domain.repository.AuthRepository
import com.muhammed.supabasejetpackcompose.domain.repository.NotesRepository
import com.muhammed.supabasejetpackcompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.muhammed.supabasejetpackcompose.domain.model.Note

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            val session = authRepository.session.first() ?: return@launch
            notesRepository.refreshNotes(session.userId)
            notesRepository.observeNotes(session.userId).collect { result ->
                when (result) {
                    is Resource.Success -> _uiState.update { it.copy(notes = result.data, isLoading = false) }
                    is Resource.Error -> _uiState.update { it.copy(error = result.message, isLoading = false) }
                    Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.QueryChanged -> _uiState.update { it.copy(query = event.value) }
            is NotesEvent.TitleChanged -> _uiState.update { it.copy(titleInput = event.value) }
            is NotesEvent.ContentChanged -> _uiState.update { it.copy(contentInput = event.value) }
            is NotesEvent.SelectNote -> _uiState.update {
                it.copy(
                    selectedNote = event.note,
                    titleInput = event.note.title,
                    contentInput = event.note.content,
                    imageUri = event.note.imageUrl?.let { path -> Uri.parse(getImageUrl(path)) }
                )
            }
            is NotesEvent.DeleteNote -> deleteNote(event.note)
            NotesEvent.AddNote -> addNote()
            NotesEvent.UpdateNote -> updateNote()
            is NotesEvent.PickImage -> _uiState.update { it.copy(imageUri = event.uri) }
            NotesEvent.ResetSaveState -> _uiState.update { it.copy(isSaved = false, successMessage = null, error = null) }
        }
    }

    private fun addNote() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.titleInput.isBlank() || state.contentInput.isBlank()) {
                _uiState.update { it.copy(error = "Title and content are required") }
                return@launch
            }
            val session = authRepository.session.first() ?: return@launch
            val note = Note(
                userId = session.userId,
                title = state.titleInput.trim(),
                content = state.contentInput.trim()
            )
            _uiState.update { it.copy(isSaving = true, error = null) }
            when (val result = notesRepository.createNote(note, state.imageUri)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isSaving = false, 
                            isSaved = true, 
                            successMessage = "Note added successfully!",
                            titleInput = "", 
                            contentInput = "", 
                            imageUri = null
                        ) 
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                Resource.Loading -> Unit
            }
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (val result = notesRepository.deleteNote(note)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSaving = false, successMessage = "Note deleted") }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                Resource.Loading -> Unit
            }
        }
    }

    private fun updateNote() {
        viewModelScope.launch {
            val selected = _uiState.value.selectedNote ?: return@launch
            val updated = selected.copy(
                title = _uiState.value.titleInput.trim(),
                content = _uiState.value.contentInput.trim()
            )
            _uiState.update { it.copy(isSaving = true, error = null) }
            when (val result = notesRepository.updateNote(updated, _uiState.value.imageUri)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isSaving = false, 
                            isSaved = true, 
                            successMessage = "Note updated successfully!",
                            selectedNote = null, 
                            titleInput = "", 
                            contentInput = "", 
                            imageUri = null
                        ) 
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                Resource.Loading -> Unit
            }
        }
    }

    fun getImageUrl(path: String) = notesRepository.getImageUrl(path)
}
