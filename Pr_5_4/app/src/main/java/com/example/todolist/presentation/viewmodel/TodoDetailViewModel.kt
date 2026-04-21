package com.example.todolist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.model.TodoItem
import com.example.todolist.domain.usecase.DeleteTodoUseCase
import com.example.todolist.domain.usecase.GetTodoByIdUseCase
import com.example.todolist.domain.usecase.UpdateTodoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoDetailViewModel(
    private val getTodoByIdUseCase: GetTodoByIdUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase
) : ViewModel() {

    private val _todo = MutableStateFlow<TodoItem?>(null)
    val todo: StateFlow<TodoItem?> = _todo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadTodo(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _todo.value = getTodoByIdUseCase(id)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTodo(id: Int, title: String, description: String, isCompleted: Boolean) {
        viewModelScope.launch {
            updateTodoUseCase(id, title, description, isCompleted)
            loadTodo(id)
        }
    }

    fun deleteTodo(id: Int, onFinished: () -> Unit) {
        viewModelScope.launch {
            deleteTodoUseCase(id)
            _todo.value = null
            onFinished()
        }
    }
}

