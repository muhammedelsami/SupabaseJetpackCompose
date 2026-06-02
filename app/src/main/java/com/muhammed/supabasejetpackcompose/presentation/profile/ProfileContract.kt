package com.muhammed.supabasejetpackcompose.presentation.profile

import com.muhammed.supabasejetpackcompose.domain.model.UserProfile

data class ProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
