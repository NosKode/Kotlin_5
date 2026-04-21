package com.example.pr_5_1

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditorScreen(
    viewModel: DiaryViewModel,
    fileName: String?,
    onNavigateBack: () -> Unit
) {
    val existingEntry = fileName?.let { viewModel.getEntry(it) }
    var title by remember { mutableStateOf(existingEntry?.title ?: "") }
    var text by remember { mutableStateOf(existingEntry?.fullText ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (existingEntry != null) "Редактировать запись" else "Новая запись")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заголовок (опционально)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Текст заметки") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 10
            )

            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        if (existingEntry != null) {
                            viewModel.updateEntry(existingEntry.fileName, title, text)
                        } else {
                            viewModel.saveEntry(title, text)
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = text.isNotBlank()
            ) {
                Text(
                    text = "Сохранить запись",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
