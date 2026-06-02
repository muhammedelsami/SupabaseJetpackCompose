package com.muhammed.supabasejetpackcompose.presentation.notes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.muhammed.supabasejetpackcompose.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    title: String,
    content: String,
    imageUri: Uri?,
    isEdit: Boolean,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onImagePicked: (Uri?) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagePicked(uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Note" else "New Note", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        floatingActionButton = {
            if (title.isNotBlank()) {
                FloatingActionButton(
                    onClick = onSave,
                    containerColor = PrimaryLight,
                    contentColor = TextPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Done, contentDescription = "Save")
                }
            }
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(SurfaceDark)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Semi-transparent overlay to show it's clickable
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        TextButton(
                            onClick = { onImagePicked(null) },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text("Remove")
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = TextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Add Image", color = TextMuted)
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // Title Input
                TextField(
                    value = title,
                    onValueChange = onTitleChanged,
                    placeholder = {
                        Text(
                            "Title",
                            style = MaterialTheme.typography.displayLarge,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.displayLarge.copy(color = TextPrimary),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryLight
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Content Input
                TextField(
                    value = content,
                    onValueChange = onContentChanged,
                    placeholder = {
                        Text(
                            "Write your note here...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryLight
                    ),
                    minLines = 10
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditNoteScreenPreview() {
    SupabaseJetpackComposeTheme {
        AddEditNoteScreen(
            title = "My Note",
            content = "This is a sample note content",
            imageUri = null,
            isEdit = false,
            onTitleChanged = {},
            onContentChanged = {},
            onImagePicked = {},
            onSave = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditNoteScreenEditPreview() {
    SupabaseJetpackComposeTheme {
        AddEditNoteScreen(
            title = "Existing Note",
            content = "This is a note that is being edited with some longer content to see how it looks in the editor.",
            imageUri = Uri.parse("https://placehold.net/800x600.png"),
            isEdit = true,
            onTitleChanged = {},
            onContentChanged = {},
            onImagePicked = {},
            onSave = {},
            onBack = {}
        )
    }
}
