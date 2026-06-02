package com.muhammed.supabasejetpackcompose

import android.app.Application
import com.muhammed.supabasejetpackcompose.data.local.SupabaseSessionObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NotesApplication : Application() {
    @Inject
    lateinit var sessionObserver: SupabaseSessionObserver

    override fun onCreate() {
        super.onCreate()
        sessionObserver.start()
    }
}
