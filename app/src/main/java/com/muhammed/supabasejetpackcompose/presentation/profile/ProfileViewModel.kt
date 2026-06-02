package com.muhammed.supabasejetpackcompose.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.supabasejetpackcompose.domain.repository.AuthRepository
import com.muhammed.supabasejetpackcompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.NameChanged -> _uiState.update { it.copy(nameInput = event.name) }
            ProfileEvent.ToggleEdit -> {
                val current = _uiState.value
                _uiState.update { 
                    it.copy(
                        isEditing = !current.isEditing,
                        nameInput = current.profile?.name.orEmpty()
                    ) 
                }
            }
            ProfileEvent.SaveProfile -> updateProfile()
            ProfileEvent.ResetState -> _uiState.update { it.copy(successMessage = null, error = null) }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            when (val result = authRepository.getProfile()) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            profile = result.data, 
                            nameInput = result.data.name.orEmpty(),
                            isLoading = false 
                        ) 
                    }
                }
                is Resource.Error -> _uiState.update { it.copy(error = result.message, isLoading = false) }
                Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    private fun updateProfile() {
        viewModelScope.launch {
            val name = _uiState.value.nameInput
            _uiState.update { it.copy(isSaving = true) }
            when (val result = authRepository.updateProfile(name)) {
                is Resource.Success -> {
                    _uiState.update { 
                        it.copy(
                            isSaving = false, 
                            isEditing = false,
                            successMessage = "Profile updated successfully"
                        ) 
                    }
                    loadProfile()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                Resource.Loading -> Unit
            }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }

    fun deleteAccount() {
        viewModelScope.launch { authRepository.deleteAccount() }
    }
}
