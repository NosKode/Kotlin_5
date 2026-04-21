package com.example.todolist.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolist.di.AppModule
import com.example.todolist.presentation.ui.component.TodoItemComponent
import com.example.todolist.presentation.viewmodel.TodoListViewModel
import com.example.todolist.presentation.viewmodel.TodoListViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onTodoClick: (Int) -> Unit,
    viewModel: TodoListViewModel = viewModel(
        factory = TodoListViewModelFactory(
            repository = AppModule.provideTodoRepository(LocalContext.current),
            toggleTodoUseCase = AppModule.provideToggleTodoUseCase(
                AppModule.provideTodoRepository(LocalContext.current)
            ),
            addTodoUseCase = AppModule.provideAddTodoUseCase(
                AppModule.provideTodoRepository(LocalContext.current)
            ),
            updateTodoUseCase = AppModule.provideUpdateTodoUseCase(
                AppModule.provideTodoRepository(LocalContext.current)
            ),
            deleteTodoUseCase = AppModule.provideDeleteTodoUseCase(
                AppModule.provideTodoRepository(LocalContext.current)
            ),
            preferencesManager = AppModule.providePreferencesManager(LocalContext.current)
        )
    )
) {
    val todos by viewModel.todos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val completedColor by viewModel.completedTodoColor.collectAsState()
    val useCustomCompletedColor by viewModel.useCustomCompletedTodoColor.collectAsState()

    var showColorPickerDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val completedCardColor =
        if (useCustomCompletedColor) completedColor else MaterialTheme.colorScheme.surface

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список задач") },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Цвет завершенных", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = useCustomCompletedColor,
                            onCheckedChange = { viewModel.setUseCustomCompletedTodoColor(it) }
                        )
                        if (useCustomCompletedColor) {
                            Spacer(modifier = Modifier.width(4.dp))
                            TextButton(
                                onClick = { showColorPickerDialog = true },
                                modifier = Modifier.padding(0.dp)
                            ) {
                                Text("Цвет", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить задачу")
            }
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
            } else if (todos.isEmpty()) {
                Text(
                    text = "Нет задач",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(todos) { todo ->
                        TodoItemComponent(
                            todo = todo,
                            onCheckedChange = { checked ->
                                viewModel.toggleTodo(todo.id)
                            },
                            onClick = { onTodoClick(todo.id) },
                            completedColor = completedCardColor
                        )
                    }
                }
            }
        }

        // Color picker dialog
        if (showColorPickerDialog) {
            ColorPickerDialog(
                currentColor = completedColor,
                onColorSelected = { color ->
                    viewModel.updateCompletedTodoColor(color)
                },
                onDismiss = { showColorPickerDialog = false }
            )
        }

        // Add todo dialog
        if (showAddDialog) {
            AddTodoDialog(
                onConfirm = { title, description ->
                    viewModel.addTodo(title, description)
                    showAddDialog = false
                },
                onDismiss = { showAddDialog = false }
            )
        }
    }
}

@Composable
fun ColorPickerDialog(
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val predefinedColors = listOf(
        Color(0xFFE8F5E9) to "Светло-зеленый",
        Color(0xFFFFF9C4) to "Светло-желтый",
        Color(0xFFE1F5FE) to "Светло-голубой",
        Color(0xFFF3E5F5) to "Светло-фиолетовый",
        Color(0xFFFFE0B2) to "Светло-оранжевый",
        Color(0xFFFFCDD2) to "Светло-розовый"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите цвет для завершенных задач") },
        text = {
            Column {
                predefinedColors.forEach { (color, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onColorSelected(color)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
fun AddTodoDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая задача") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description)
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

