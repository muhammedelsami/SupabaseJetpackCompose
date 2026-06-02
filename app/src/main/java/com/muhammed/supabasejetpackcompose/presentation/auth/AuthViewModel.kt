package com.muhammed.supabasejetpackcompose.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammed.supabasejetpackcompose.domain.repository.AuthRepository
import com.muhammed.supabasejetpackcompose.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun update(state: AuthUiState) {
        _uiState.value = state
    }

    fun login() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.login(state.email.trim(), state.password)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                Resource.Loading -> Unit
            }
        }
    }

    fun register() {
        viewModelScope.launch {
            val s = _uiState.value
            if (s.password != s.confirmPassword) {
                _uiState.update { it.copy(error = "Passwords do not match") }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.register(s.name.trim(), s.email.trim(), s.password)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                Resource.Loading -> Unit
            }
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            val email = _uiState.value.email.trim()
            when (val result = repository.sendResetPasswordLink(email)) {
                is Resource.Error -> _uiState.update { it.copy(error = result.message) }
                else -> _uiState.update { it.copy(error = "Password reset link sent") }
            }
        }
    }
}
