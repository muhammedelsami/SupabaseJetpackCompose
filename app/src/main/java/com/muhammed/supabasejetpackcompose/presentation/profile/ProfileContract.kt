package com.muhammed.supabasejetpackcompose.presentation.profile

import com.muhammed.supabasejetpackcompose.domain.model.UserProfile

data class ProfileUiState(
    val profile: UserProfile? = null,
    val nameInput: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isEditing: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

sealed interface ProfileEvent {
    data class NameChanged(val name: String) : ProfileEvent
    data object ToggleEdit : ProfileEvent
    data object SaveProfile : ProfileEvent
    data object ResetState : ProfileEvent
}
