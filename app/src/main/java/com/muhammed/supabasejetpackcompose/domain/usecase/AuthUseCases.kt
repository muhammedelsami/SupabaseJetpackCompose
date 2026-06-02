package com.muhammed.supabasejetpackcompose.domain.usecase

import com.muhammed.supabasejetpackcompose.domain.repository.AuthRepository
import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val login: LoginUseCase,
    val register: RegisterUseCase,
    val logout: LogoutUseCase
)

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.login(email, password)
}

class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String) =
        repository.register(name, email, password)
}

class LogoutUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}
