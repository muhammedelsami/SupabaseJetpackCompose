package com.muhammed.supabasejetpackcompose.data.repository

import com.muhammed.supabasejetpackcompose.data.local.SessionDataStore
import com.muhammed.supabasejetpackcompose.domain.repository.AuthRepository
import com.muhammed.supabasejetpackcompose.domain.repository.NotesRepository
import com.muhammed.supabasejetpackcompose.util.Resource
import com.muhammed.supabasejetpackcompose.BuildConfig
import com.muhammed.supabasejetpackcompose.domain.model.SessionInfo
import com.muhammed.supabasejetpackcompose.domain.model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserUpdateBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val client: SupabaseClient,
    private val sessionDataStore: SessionDataStore,
    private val notesRepository: NotesRepository
) : AuthRepository {

    override val session: Flow<SessionInfo?> = sessionDataStore.session

    override suspend fun register(name: String, email: String, password: String): Resource<Unit> {
        if (email.isBlank() || password.length < 6 || name.isBlank()) {
            return Resource.Error("Invalid registration info")
        }
        return runCatching {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("name", name)
                }
            }
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "An error occurred during registration", it) }
        )
    }

    override suspend fun login(email: String, password: String): Resource<Unit> {
        if (email.isBlank() || password.length < 6) return Resource.Error("Invalid login info")
        return runCatching {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val session = client.auth.currentSessionOrNull()
            val user = client.auth.currentUserOrNull()
            if (session != null && user != null) {
                sessionDataStore.save(
                    SessionInfo(
                        accessToken = session.accessToken,
                        refreshToken = session.refreshToken,
                        userId = user.id,
                        email = user.email.orEmpty()
                    )
                )
            }
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "An error occurred during login", it) }
        )
    }

    override suspend fun sendResetPasswordLink(email: String): Resource<Unit> {
        if (!email.contains("@")) return Resource.Error("Invalid email")
        return runCatching {
            client.auth.resetPasswordForEmail(
                email = email,
                redirectUrl = "${BuildConfig.SUPABASE_REDIRECT_SCHEME}://reset-password"
            )
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Failed to send reset link", it) }
        )
    }

    override suspend fun logout(): Resource<Unit> {
        return runCatching {
            client.auth.signOut()
            sessionDataStore.clear()
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Failed to sign out", it) }
        )
    }

    override suspend fun deleteAccount(): Resource<Unit> {
        return runCatching {
            val current = sessionDataStore.session.first() ?: error("No active session")
            notesRepository.deleteAllUserData(current.userId)
            client.functions.invoke("delete-user-account")
            sessionDataStore.clear()
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = {
                Resource.Error(
                    it.message ?: "Edge Function 'delete-user-account' required for account deletion",
                    it
                )
            }
        )
    }

    override suspend fun getProfile(): Resource<UserProfile> {
        return runCatching {
            val user = client.auth.currentUserOrNull() ?: error("No active session")
            UserProfile(
                id = user.id,
                email = user.email.orEmpty(),
                name = user.userMetadata?.get("name")?.toString()?.removeSurrounding("\"")
            )
        }.fold(
            onSuccess = { Resource.Success(it) },
            onFailure = { Resource.Error(it.message ?: "Failed to fetch profile", it) }
        )
    }

    override suspend fun updateProfile(name: String): Resource<Unit> {
        if (name.isBlank()) return Resource.Error("Name cannot be empty")
        return runCatching {
            client.auth.updateUser {
                data = buildJsonObject {
                    put("name", name)
                }
            }
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Failed to update profile", it) }
        )
    }
}
