package com.example.todolist.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.preferences.PreferencesManager
import com.example.todolist.domain.model.TodoItem
import com.example.todolist.domain.repository.TodoRepository
import com.example.todolist.domain.usecase.AddTodoUseCase
import com.example.todolist.domain.usecase.DeleteTodoUseCase
import com.example.todolist.domain.usecase.ToggleTodoUseCase
import com.example.todolist.domain.usecase.UpdateTodoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoListViewModel(
    private val repository: TodoRepository,
    private val toggleTodoUseCase: ToggleTodoUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    val todos: StateFlow<List<TodoItem>> = _todos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _completedTodoColor =
        MutableStateFlow(Color(PreferencesManager.DEFAULT_COMPLETED_COLOR_ARGB))
    val completedTodoColor: StateFlow<Color> = _completedTodoColor.asStateFlow()

    private val _useCustomCompletedTodoColor = MutableStateFlow(true)
    val useCustomCompletedTodoColor: StateFlow<Boolean> = _useCustomCompletedTodoColor.asStateFlow()

    init {
        importTodosAndObserve()
        observeCompletedColor()
        observeUseCustomCompletedColor()
    }

    private fun importTodosAndObserve() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.importTodosFromJson()
            } finally {
                _isLoading.value = false
            }
        }
        viewModelScope.launch {
            repository.getTodosFlow().collect { todoList ->
                _todos.value = todoList
            }
        }
    }

    private fun observeCompletedColor() {
        viewModelScope.launch {
            preferencesManager.completedTodoColorArgb.collect { argb ->
                _completedTodoColor.value = Color(argb)
            }
        }
    }

    private fun observeUseCustomCompletedColor() {
        viewModelScope.launch {
            preferencesManager.useCustomCompletedTodoColor.collect { enabled ->
                _useCustomCompletedTodoColor.value = enabled
            }
        }
    }

    fun toggleTodo(id: Int) {
        viewModelScope.launch {
            toggleTodoUseCase(id)
        }
    }

    fun addTodo(title: String, description: String) {
        viewModelScope.launch {
            addTodoUseCase(title, description)
        }
    }

    fun updateTodo(id: Int, title: String, description: String, isCompleted: Boolean) {
        viewModelScope.launch {
            updateTodoUseCase(id, title, description, isCompleted)
        }
    }

    fun deleteTodo(id: Int) {
        viewModelScope.launch {
            deleteTodoUseCase(id)
        }
    }

    fun updateCompletedTodoColor(color: Color) {
        viewModelScope.launch {
            preferencesManager.saveCompletedTodoColorArgb(color.toArgb())
        }
    }

    fun setUseCustomCompletedTodoColor(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.saveUseCustomCompletedTodoColor(enabled)
        }
    }
}

