package com.muhammed.supabasejetpackcompose.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class UserProfile(
    val id: String,
    val name: String? = null,
    val email: String
)

@Serializable
data class Note(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("user_id") val userId: String,
    val title: String,
    val content: String,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("created_at") val createdAt: String = Instant.now().toString(),
    @SerialName("updated_at") val updatedAt: String = Instant.now().toString()
)

data class SessionInfo(
    val accessToken: String,
    val refreshToken: String?,
    val userId: String,
    val email: String
)
