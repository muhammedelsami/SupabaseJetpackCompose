package com.muhammed.supabasejetpackcompose.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.muhammed.supabasejetpackcompose.domain.model.SessionInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.sessionStore by preferencesDataStore(name = "session_store")

class SessionDataStore(private val context: Context) {
    private object Keys {
        val accessToken = stringPreferencesKey("access_token")
        val refreshToken = stringPreferencesKey("refresh_token")
        val userId = stringPreferencesKey("user_id")
        val email = stringPreferencesKey("email")
    }

    val session: Flow<SessionInfo?> = context.sessionStore.data
        .catch {
            if (it is IOException) emit(emptyPreferences()) else throw it
        }
        .map { prefs: Preferences ->
            val accessToken = prefs[Keys.accessToken] ?: return@map null
            SessionInfo(
                accessToken = accessToken,
                refreshToken = prefs[Keys.refreshToken],
                userId = prefs[Keys.userId].orEmpty(),
                email = prefs[Keys.email].orEmpty()
            )
        }

    suspend fun save(sessionInfo: SessionInfo) {
        context.sessionStore.edit { prefs ->
            prefs[Keys.accessToken] = sessionInfo.accessToken
            prefs[Keys.refreshToken] = sessionInfo.refreshToken.orEmpty()
            prefs[Keys.userId] = sessionInfo.userId
            prefs[Keys.email] = sessionInfo.email
        }
    }

    suspend fun clear() {
        context.sessionStore.edit { it.clear() }
    }
}
