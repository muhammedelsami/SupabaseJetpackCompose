package com.muhammed.supabasejetpackcompose.presentation.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.muhammed.supabasejetpackcompose.ui.theme.*

@Composable
fun AuthScreen(
    state: AuthUiState,
    onStateChange: (AuthUiState) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit,
    onLoggedIn: () -> Unit
) {
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) onLoggedIn()
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(BackgroundDark, Color(0xFF1E1B4B), BackgroundDark)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / Header Section
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = PrimaryLight
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Premium Notes",
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary
            )
            Text(
                text = if (state.isLoginMode) "Please sign in" else "Create a new account",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Auth Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceDark.copy(alpha = 0.6f),
                tonalElevation = 8.dp,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnimatedVisibility(!state.isLoginMode) {
                        ModernTextField(
                            value = state.name,
                            onValueChange = { onStateChange(state.copy(name = it)) },
                            label = "Full Name",
                            icon = Icons.Default.Person
                        )
                    }

                    ModernTextField(
                        value = state.email,
                        onValueChange = { onStateChange(state.copy(email = it)) },
                        label = "Email",
                        icon = Icons.Default.Email
                    )

                    var passwordVisible by remember { mutableStateOf(false) }
                    ModernTextField(
                        value = state.password,
                        onValueChange = { onStateChange(state.copy(password = it)) },
                        label = "Password",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible }
                    )

                    AnimatedVisibility(!state.isLoginMode) {
                        ModernTextField(
                            value = state.confirmPassword,
                            onValueChange = { onStateChange(state.copy(confirmPassword = it)) },
                            label = "Confirm Password",
                            icon = Icons.Default.LockReset,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onTogglePassword = { passwordVisible = !passwordVisible }
                        )
                    }

                    AnimatedVisibility(state.error != null) {
                        Text(
                            text = state.error.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = PrimaryLight
                        )
                    } else {
                        Button(
                            onClick = if (state.isLoginMode) onLogin else onRegister,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryMain,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                if (state.isLoginMode) "Sign In" else "Sign Up",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoginMode) {
                TextButton(onClick = onForgotPassword) {
                    Text("Forgot Password?", color = PrimaryLight)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (state.isLoginMode) "Don't have an account?" else "Already have an account?",
                    color = TextSecondary
                )
                TextButton(onClick = { onStateChange(state.copy(isLoginMode = !state.isLoginMode, error = null)) }) {
                    Text(
                        text = if (state.isLoginMode) "Sign Up" else "Sign In",
                        color = PrimaryLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = TextMuted) },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = TextMuted
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryMain,
            unfocusedBorderColor = SurfaceLighter,
            focusedLabelColor = PrimaryMain,
            unfocusedLabelColor = TextMuted,
            cursorColor = PrimaryMain,
            unfocusedTextColor = TextPrimary,
            focusedTextColor = TextPrimary
        ),
        singleLine = true
    )
}
