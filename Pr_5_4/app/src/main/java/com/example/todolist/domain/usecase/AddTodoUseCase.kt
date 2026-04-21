package com.example.todolist.domain.usecase

import com.example.todolist.domain.repository.TodoRepository

class AddTodoUseCase(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(title: String, description: String) {
        repository.addTodo(title, description)
    }
}
