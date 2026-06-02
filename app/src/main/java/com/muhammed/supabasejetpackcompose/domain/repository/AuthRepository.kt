package com.muhammed.supabasejetpackcompose.domain.repository

import com.muhammed.supabasejetpackcompose.domain.model.UserProfile
import com.muhammed.supabasejetpackcompose.domain.model.SessionInfo
import com.muhammed.supabasejetpackcompose.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<SessionInfo?>
    suspend fun register(name: String, email: String, password: String): Resource<Unit>
    suspend fun login(email: String, password: String): Resource<Unit>
    suspend fun sendResetPasswordLink(email: String): Resource<Unit>
    suspend fun logout(): Resource<Unit>
    suspend fun deleteAccount(): Resource<Unit>
    suspend fun getProfile(): Resource<UserProfile>
}
