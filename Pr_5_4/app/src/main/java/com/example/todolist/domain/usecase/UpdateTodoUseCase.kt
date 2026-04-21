package com.example.todolist.domain.usecase

import com.example.todolist.domain.repository.TodoRepository

class UpdateTodoUseCase(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(id: Int, title: String, description: String, isCompleted: Boolean) {
        repository.updateTodo(id, title, description, isCompleted)
    }
}
