package com.muhammed.supabasejetpackcompose.data.repository

import android.content.Context
import android.net.Uri
import com.muhammed.supabasejetpackcompose.domain.repository.NotesRepository
import com.muhammed.supabasejetpackcompose.util.Resource
import com.muhammed.supabasejetpackcompose.BuildConfig
import com.muhammed.supabasejetpackcompose.domain.model.Note
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepositoryImpl @Inject constructor(
    private val client: SupabaseClient,
    @ApplicationContext private val context: Context
) : NotesRepository {
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    private val notesFlow = notes.asStateFlow()

    override fun observeNotes(userId: String): Flow<Resource<List<Note>>> {
        return notesFlow.map { list ->
            Resource.Success(list.filter { it.userId == userId }.sortedByDescending { it.updatedAt })
        }
    }

    override suspend fun refreshNotes(userId: String): Resource<Unit> {
        return runCatching {
            val result = client.from("notes").select {
                filter { eq("user_id", userId) }
            }.decodeList<Note>()
            notes.value = result
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Failed to fetch notes", it) }
        )
    }

    override suspend fun createNote(note: Note, imageUri: Uri?): Resource<Unit> {
        return runCatching {
            val imagePath = uploadImageIfNeeded(note.userId, note.id, imageUri)
            val payload = note.copy(imageUrl = imagePath)
            client.from("notes").insert(payload)
            notes.update { current -> (current + payload).distinctBy { it.id } }
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Failed to create note", it) }
        )
    }

    override suspend fun updateNote(note: Note, newImageUri: Uri?): Resource<Unit> {
        return runCatching {
            var imagePath = note.imageUrl
            if (newImageUri != null) {
                val isRemote = newImageUri.toString().startsWith("http") || newImageUri.scheme?.startsWith("http") == true
                if (!isRemote) {
                    imagePath?.let { deleteImageByPath(it) }
                    imagePath = uploadImageIfNeeded(note.userId, note.id, newImageUri)
                }
            } else {
                imagePath?.let { deleteImageByPath(it) }
                imagePath = null
            }
            val updated = note.copy(imageUrl = imagePath)
            
            // Supabase update typically requires explicit fields or a serializable object
            // Ensure we are sending the imageUrl as null in the database
            client.from("notes").update(
                buildJsonObject {
                    put("title", updated.title)
                    put("content", updated.content)
                    put("image_url", updated.imageUrl)
                    put("updated_at", updated.updatedAt)
                }
            ) {
                filter { eq("id", note.id) }
            }

            notes.update { list -> list.map { if (it.id == note.id) updated else it } }
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Failed to update note", it) }
        )
    }

    override suspend fun deleteNote(note: Note): Resource<Unit> {
        return runCatching {
            note.imageUrl?.let { deleteImageByPath(it) }
            client.from("notes").delete {
                filter { eq("id", note.id) }
            }
            notes.update { it.filterNot { n -> n.id == note.id } }
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Failed to delete note", it) }
        )
    }

    override suspend fun deleteAllUserData(userId: String): Resource<Unit> {
        return runCatching {
            val userNotes = client.from("notes").select {
                filter { eq("user_id", userId) }
            }.decodeList<Note>()
            userNotes.mapNotNull { it.imageUrl }.forEach { deleteImageByPath(it) }
            client.from("notes").delete {
                filter { eq("user_id", userId) }
            }
            notes.update { it.filterNot { n -> n.userId == userId } }
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Failed to delete user data", it) }
        )
    }

    private suspend fun uploadImageIfNeeded(userId: String, noteId: String, imageUri: Uri?): String? {
        if (imageUri == null) return null
        val bytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
            ?: return null
        val path = "$userId/$noteId/image.jpg"
        client.storage.from(BuildConfig.NOTES_BUCKET).upload(path, bytes) {
            upsert = true
        }
        return path
    }

    private suspend fun deleteImageByPath(path: String) {
        client.storage.from(BuildConfig.NOTES_BUCKET).delete(path)
    }
    override fun getImageUrl(path: String): String {
        return client.storage.from(BuildConfig.NOTES_BUCKET).publicUrl(path)
    }
}
