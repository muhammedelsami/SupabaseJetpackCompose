package com.muhammed.supabasejetpackcompose.di

import android.content.Context
import com.muhammed.supabasejetpackcompose.data.local.SupabaseSessionObserver
import com.muhammed.supabasejetpackcompose.data.local.SessionDataStore
import com.muhammed.supabasejetpackcompose.data.repository.AuthRepositoryImpl
import com.muhammed.supabasejetpackcompose.data.repository.NotesRepositoryImpl
import com.muhammed.supabasejetpackcompose.domain.repository.AuthRepository
import com.muhammed.supabasejetpackcompose.domain.repository.NotesRepository
import com.muhammed.supabasejetpackcompose.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    }

    @Provides
    @Singleton
    fun provideSessionDataStore(@ApplicationContext context: Context): SessionDataStore =
        SessionDataStore(context)

    @Provides
    @Singleton
    fun provideSupabaseSessionObserver(
        client: SupabaseClient,
        dataStore: SessionDataStore
    ): SupabaseSessionObserver = SupabaseSessionObserver(client, dataStore)

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideNotesRepository(impl: NotesRepositoryImpl): NotesRepository = impl
}
