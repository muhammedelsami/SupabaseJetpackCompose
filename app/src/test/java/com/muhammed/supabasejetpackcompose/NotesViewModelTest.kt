package com.muhammed.supabasejetpackcompose

import android.net.Uri
import com.muhammed.supabasejetpackcompose.domain.model.Note
import com.muhammed.supabasejetpackcompose.domain.model.SessionInfo
import com.muhammed.supabasejetpackcompose.domain.model.UserProfile
import com.muhammed.supabasejetpackcompose.domain.repository.AuthRepository
import com.muhammed.supabasejetpackcompose.domain.repository.NotesRepository
import com.muhammed.supabasejetpackcompose.presentation.notes.NotesEvent
import com.muhammed.supabasejetpackcompose.presentation.notes.NotesViewModel
import com.muhammed.supabasejetpackcompose.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class NotesViewModelTest {

    @Test
    fun `add note shows error when title or content empty`() = runTest {
        val vm = NotesViewModel(FakeNotesRepository(), FakeAuthRepository())
        vm.onEvent(NotesEvent.TitleChanged(""))
        vm.onEvent(NotesEvent.ContentChanged("icerik"))
        vm.onEvent(NotesEvent.AddNote)
        assertTrue(vm.uiState.value.error?.contains("zorunludur") == true)
    }
}

private class FakeNotesRepository : NotesRepository {
    override fun observeNotes(userId: String): Flow<Resource<List<Note>>> =
        flowOf(Resource.Success(emptyList()))

    override suspend fun refreshNotes(userId: String): Resource<Unit> = Resource.Success(Unit)
    override suspend fun createNote(note: Note, imageUri: Uri?): Resource<Unit> = Resource.Success(Unit)
    override suspend fun updateNote(note: Note, newImageUri: Uri?): Resource<Unit> = Resource.Success(Unit)
    override suspend fun deleteNote(note: Note): Resource<Unit> = Resource.Success(Unit)
    override suspend fun deleteAllUserData(userId: String): Resource<Unit> = Resource.Success(Unit)
    override fun getImageUrl(path: String): String {
        TODO("Not yet implemented")
    }
}

private class FakeAuthRepository : AuthRepository {
    override val session: Flow<SessionInfo?> =
        MutableStateFlow(SessionInfo("a", "b", "user-1", "test@mail.com"))

    override suspend fun register(name: String, email: String, password: String): Resource<Unit> =
        Resource.Success(Unit)

    override suspend fun login(email: String, password: String): Resource<Unit> = Resource.Success(Unit)
    override suspend fun sendResetPasswordLink(email: String): Resource<Unit> = Resource.Success(Unit)
    override suspend fun logout(): Resource<Unit> = Resource.Success(Unit)
    override suspend fun deleteAccount(): Resource<Unit> = Resource.Success(Unit)
    override suspend fun getProfile(): Resource<UserProfile> =
        Resource.Success(UserProfile(id = "1", email = "test@mail.com"))
}
