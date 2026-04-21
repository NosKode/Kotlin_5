package com.example.todolist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.data.preferences.PreferencesManager
import com.example.todolist.domain.repository.TodoRepository
import com.example.todolist.domain.usecase.AddTodoUseCase
import com.example.todolist.domain.usecase.DeleteTodoUseCase
import com.example.todolist.domain.usecase.GetTodoByIdUseCase
import com.example.todolist.domain.usecase.ToggleTodoUseCase
import com.example.todolist.domain.usecase.UpdateTodoUseCase

class TodoListViewModelFactory(
    private val repository: TodoRepository,
    private val toggleTodoUseCase: ToggleTodoUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoListViewModel::class.java)) {
            return TodoListViewModel(
                repository,
                toggleTodoUseCase,
                addTodoUseCase,
                updateTodoUseCase,
                deleteTodoUseCase,
                preferencesManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TodoDetailViewModelFactory(
    private val getTodoByIdUseCase: GetTodoByIdUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoDetailViewModel::class.java)) {
            return TodoDetailViewModel(
                getTodoByIdUseCase,
                updateTodoUseCase,
                deleteTodoUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

