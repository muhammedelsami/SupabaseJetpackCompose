package com.muhammed.supabasejetpackcompose.presentation.auth

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
)
