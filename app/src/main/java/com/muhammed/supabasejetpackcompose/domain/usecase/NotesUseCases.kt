package com.muhammed.supabasejetpackcompose.domain.usecase

import android.net.Uri
import com.muhammed.supabasejetpackcompose.domain.model.Note
import com.muhammed.supabasejetpackcompose.domain.repository.NotesRepository
import javax.inject.Inject

data class NotesUseCases @Inject constructor(
    val create: CreateNoteUseCase,
    val update: UpdateNoteUseCase,
    val delete: DeleteNoteUseCase
)

class CreateNoteUseCase @Inject constructor(private val repository: NotesRepository) {
    suspend operator fun invoke(note: Note, image: Uri?) = repository.createNote(note, image)
}

class UpdateNoteUseCase @Inject constructor(private val repository: NotesRepository) {
    suspend operator fun invoke(note: Note, image: Uri?) = repository.updateNote(note, image)
}

class DeleteNoteUseCase @Inject constructor(private val repository: NotesRepository) {
    suspend operator fun invoke(note: Note) = repository.deleteNote(note)
}
