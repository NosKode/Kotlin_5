package com.example.todolist.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.di.AppModule
import com.example.todolist.presentation.viewmodel.TodoDetailViewModel
import com.example.todolist.presentation.viewmodel.TodoDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    todoId: Int,
    onBackClick: () -> Unit,
    viewModel: TodoDetailViewModel = viewModel(
        factory = TodoDetailViewModelFactory(
            getTodoByIdUseCase = AppModule.provideGetTodoByIdUseCase(
                AppModule.provideTodoRepository(LocalContext.current)
            ),
            updateTodoUseCase = AppModule.provideUpdateTodoUseCase(
                AppModule.provideTodoRepository(LocalContext.current)
            ),
            deleteTodoUseCase = AppModule.provideDeleteTodoUseCase(
                AppModule.provideTodoRepository(LocalContext.current)
            )
        )
    )
) {
    val todo by viewModel.todo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var editCompleted by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(todoId) {
        viewModel.loadTodo(todoId)
    }

    LaunchedEffect(todo) {
        todo?.let {
            editTitle = it.title
            editDescription = it.description
            editCompleted = it.isCompleted
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали задачи") },
                actions = {
                    if (todo != null) {
                        IconButton(
                            onClick = { showDeleteConfirm = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить задачу"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (todo == null) {
                Text(
                    text = "Задача не найдена",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        if (isEditing) {
                            OutlinedTextField(
                                value = editTitle,
                                onValueChange = { editTitle = it },
                                label = { Text("Название") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = editDescription,
                                onValueChange = { editDescription = it },
                                label = { Text("Описание") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                maxLines = 5
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 24.dp)
                            ) {
                                Checkbox(
                                    checked = editCompleted,
                                    onCheckedChange = { editCompleted = it }
                                )
                                Text(
                                    text = "Выполнено",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
                            Text(
                                text = todo!!.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = "Описание:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = todo!!.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )
                        }

                        Text(
                            text = "Статус: ${if (todo!!.isCompleted) "Выполнено" else "Не выполнено"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (todo!!.isCompleted)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        if (isEditing) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.updateTodo(
                                            todoId,
                                            editTitle,
                                            editDescription,
                                            editCompleted
                                        )
                                        isEditing = false
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Сохранить")
                                }
                                OutlinedButton(
                                    onClick = {
                                        editTitle = todo!!.title
                                        editDescription = todo!!.description
                                        editCompleted = todo!!.isCompleted
                                        isEditing = false
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Отмена")
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { isEditing = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Изменить")
                                }
                                OutlinedButton(
                                    onClick = onBackClick,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Назад")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteConfirm && todo != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Удалить задачу?") },
                text = { Text("Действие нельзя отменить.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirm = false
                            viewModel.deleteTodo(todoId, onBackClick)
                        }
                    ) {
                        Text("Удалить", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

