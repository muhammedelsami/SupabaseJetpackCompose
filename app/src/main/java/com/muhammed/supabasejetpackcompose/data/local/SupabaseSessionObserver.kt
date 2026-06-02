package com.muhammed.supabasejetpackcompose.data.local

import com.muhammed.supabasejetpackcompose.domain.model.SessionInfo
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseSessionObserver @Inject constructor(
    private val client: SupabaseClient,
    private val dataStore: SessionDataStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        scope.launch {
            client.auth.sessionStatus.collectLatest { status ->
                when (status) {
                    is SessionStatus.NotAuthenticated -> dataStore.clear()
                    is SessionStatus.Authenticated -> {
                        val user = status.session.user
                        dataStore.save(
                            SessionInfo(
                                accessToken = status.session.accessToken,
                                refreshToken = status.session.refreshToken,
                                userId = user?.id.orEmpty(),
                                email = user?.email.orEmpty()
                            )
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}
