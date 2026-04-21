package com.example.todolist.domain.usecase

import com.example.todolist.domain.model.TodoItem
import com.example.todolist.domain.repository.TodoRepository

class GetTodoByIdUseCase(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(id: Int): TodoItem? {
        return repository.getTodoById(id)
    }
}
