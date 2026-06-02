package com.muhammed.supabasejetpackcompose

import com.example.notesapp.data.model.SessionInfo
import com.example.notesapp.data.model.UserProfile
import com.example.notesapp.domain.repository.AuthRepository
import com.muhammed.supabasejetpackcompose.presentation.auth.AuthUiState
import com.muhammed.supabasejetpackcompose.presentation.auth.AuthViewModel
import com.example.notesapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthViewModelTest {

    @Test
    fun `register returns error when passwords mismatch`() = runTest {
        val vm = AuthViewModel(FakeAuthRepository())
        vm.update(
            AuthUiState(
                isLoginMode = false,
                name = "Muhammed",
                email = "test@mail.com",
                password = "123456",
                confirmPassword = "000000"
            )
        )

        vm.register()

        assertTrue(vm.uiState.value.error?.contains("uyusmuyor") == true)
    }
}

private class FakeAuthRepository : AuthRepository {
    private val sessionFlow = MutableStateFlow<SessionInfo?>(null)
    override val session: Flow<SessionInfo?> = sessionFlow

    override suspend fun register(name: String, email: String, password: String): Resource<Unit> =
        Resource.Success(Unit)

    override suspend fun login(email: String, password: String): Resource<Unit> = Resource.Success(Unit)

    override suspend fun sendResetPasswordLink(email: String): Resource<Unit> = Resource.Success(Unit)

    override suspend fun logout(): Resource<Unit> = Resource.Success(Unit)

    override suspend fun deleteAccount(): Resource<Unit> = Resource.Success(Unit)

    override suspend fun getProfile(): Resource<UserProfile> =
        Resource.Success(UserProfile(id = "1", email = "test@mail.com"))
}
