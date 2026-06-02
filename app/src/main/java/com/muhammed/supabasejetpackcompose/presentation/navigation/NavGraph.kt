package com.muhammed.supabasejetpackcompose.presentation.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.muhammed.supabasejetpackcompose.presentation.auth.AuthScreen
import com.muhammed.supabasejetpackcompose.presentation.auth.AuthViewModel
import com.muhammed.supabasejetpackcompose.presentation.auth.ForgotPasswordScreen
import com.muhammed.supabasejetpackcompose.presentation.components.LoadingDialog
import com.muhammed.supabasejetpackcompose.presentation.notes.AddEditNoteScreen
import com.muhammed.supabasejetpackcompose.presentation.notes.NoteDetailScreen
import com.muhammed.supabasejetpackcompose.presentation.notes.NotesEvent
import com.muhammed.supabasejetpackcompose.presentation.notes.NotesViewModel
import com.muhammed.supabasejetpackcompose.presentation.profile.ProfileEvent
import com.muhammed.supabasejetpackcompose.presentation.profile.ProfileViewModel
import com.muhammed.supabasejetpackcompose.presentation.splash.SplashScreen
import com.muhammed.supabasejetpackcompose.presentation.splash.SplashViewModel

object Routes {
    const val Splash = "splash"
    const val Auth = "auth"
    const val Forgot = "forgot"
    const val Main = "main"
    const val AddNote = "add_note"
    const val EditNote = "edit_note"
    const val NoteDetail = "note_detail"
}

@Composable
fun NotesAppRoot() {
    val navController = rememberNavController()
    val notesVm: NotesViewModel = hiltViewModel()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Routes.Splash) {
        composable(Routes.Splash) {
            val vm: SplashViewModel = hiltViewModel()
            val isLoggedIn = vm.isLoggedIn.collectAsStateWithLifecycle().value
            SplashScreen()
            LaunchedEffect(isLoggedIn) {
                when (isLoggedIn) {
                    true -> navController.navigate(Routes.Main) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                    false -> navController.navigate(Routes.Auth) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                    null -> Unit
                }
            }
        }
        composable(Routes.Auth) {
            val vm: AuthViewModel = hiltViewModel()
            val state = vm.uiState.collectAsStateWithLifecycle().value
            AuthScreen(
                state = state,
                onStateChange = vm::update,
                onLogin = vm::login,
                onRegister = vm::register,
                onForgotPassword = { navController.navigate(Routes.Forgot) },
                onLoggedIn = {
                    navController.navigate(Routes.Main) {
                        popUpTo(Routes.Auth) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Forgot) {
            val vm: AuthViewModel = hiltViewModel()
            val state = vm.uiState.collectAsStateWithLifecycle().value
            ForgotPasswordScreen(
                email = state.email,
                onEmailChange = { vm.update(state.copy(email = it)) },
                onSendLink = vm::resetPassword
            )
        }
        composable(Routes.Main) {
            val profileVm: ProfileViewModel = hiltViewModel()
            val state = notesVm.uiState.collectAsStateWithLifecycle().value
            val profileState = profileVm.uiState.collectAsStateWithLifecycle().value
            
            LaunchedEffect(state.successMessage) {
                state.successMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    notesVm.onEvent(NotesEvent.ResetSaveState)
                }
            }

            LaunchedEffect(state.error) {
                state.error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    notesVm.onEvent(NotesEvent.ResetSaveState)
                }
            }

            LaunchedEffect(profileState.successMessage) {
                profileState.successMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    profileVm.onEvent(ProfileEvent.ResetState)
                }
            }

            LaunchedEffect(profileState.error) {
                profileState.error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    profileVm.onEvent(ProfileEvent.ResetState)
                }
            }

            if (state.isSaving || profileState.isSaving) {
                LoadingDialog()
            }

            MainBottomNav(
                notesVm = notesVm,
                profileVm = profileVm,
                onOpenAdd = { navController.navigate(Routes.AddNote) },
                onOpenDetail = { note ->
                    notesVm.onEvent(NotesEvent.SelectNote(note))
                    navController.navigate(Routes.NoteDetail)
                },
                onLogout = {
                    navController.navigate(Routes.Auth) {
                        popUpTo(Routes.Main) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.AddNote) {
            val state = notesVm.uiState.collectAsStateWithLifecycle().value
            
            LaunchedEffect(state.successMessage) {
                state.successMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                    notesVm.onEvent(NotesEvent.ResetSaveState)
                }
            }

            LaunchedEffect(state.error) {
                state.error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    notesVm.onEvent(NotesEvent.ResetSaveState)
                }
            }

            if (state.isSaving) {
                LoadingDialog(message = "Saving note...")
            }

            AddEditNoteScreen(
                title = state.titleInput,
                content = state.contentInput,
                imageUri = state.imageUri,
                isEdit = false,
                onTitleChanged = { notesVm.onEvent(NotesEvent.TitleChanged(it)) },
                onContentChanged = { notesVm.onEvent(NotesEvent.ContentChanged(it)) },
                onImagePicked = { notesVm.onEvent(NotesEvent.PickImage(it)) },
                onSave = {
                    notesVm.onEvent(NotesEvent.AddNote)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.NoteDetail) {
            val state = notesVm.uiState.collectAsStateWithLifecycle().value
            val note = state.selectedNote ?: return@composable
            
            LaunchedEffect(state.successMessage) {
                state.successMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    navController.popBackStack(Routes.Main, false)
                    notesVm.onEvent(NotesEvent.ResetSaveState)
                }
            }

            if (state.isSaving) {
                LoadingDialog(message = "Deleting note...")
            }

            NoteDetailScreen(
                note = note,
                onEdit = { navController.navigate(Routes.EditNote) },
                onDelete = {
                    notesVm.onEvent(NotesEvent.DeleteNote(note))
                },
                onBack = { navController.popBackStack() },
                onResolveImageUrl = notesVm::getImageUrl
            )
        }
        composable(Routes.EditNote) {
            val state = notesVm.uiState.collectAsStateWithLifecycle().value
            
            LaunchedEffect(state.successMessage) {
                state.successMessage?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    navController.popBackStack(Routes.Main, false)
                    notesVm.onEvent(NotesEvent.ResetSaveState)
                }
            }

            LaunchedEffect(state.error) {
                state.error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    notesVm.onEvent(NotesEvent.ResetSaveState)
                }
            }

            if (state.isSaving) {
                LoadingDialog(message = "Updating note...")
            }

            AddEditNoteScreen(
                title = state.titleInput,
                content = state.contentInput,
                imageUri = state.imageUri,
                isEdit = true,
                onTitleChanged = { notesVm.onEvent(NotesEvent.TitleChanged(it)) },
                onContentChanged = { notesVm.onEvent(NotesEvent.ContentChanged(it)) },
                onImagePicked = { notesVm.onEvent(NotesEvent.PickImage(it)) },
                onSave = {
                    notesVm.onEvent(NotesEvent.UpdateNote)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
