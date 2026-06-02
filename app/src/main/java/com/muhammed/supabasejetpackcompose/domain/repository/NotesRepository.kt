package com.muhammed.supabasejetpackcompose.domain.repository

import android.net.Uri
import com.muhammed.supabasejetpackcompose.domain.model.Note
import com.muhammed.supabasejetpackcompose.util.Resource
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun observeNotes(userId: String): Flow<Resource<List<Note>>>
    suspend fun refreshNotes(userId: String): Resource<Unit>
    suspend fun createNote(note: Note, imageUri: Uri?): Resource<Unit>
    suspend fun updateNote(note: Note, newImageUri: Uri?): Resource<Unit>
    suspend fun deleteNote(note: Note): Resource<Unit>
    suspend fun deleteAllUserData(userId: String): Resource<Unit>
    fun getImageUrl(path: String): String
}
