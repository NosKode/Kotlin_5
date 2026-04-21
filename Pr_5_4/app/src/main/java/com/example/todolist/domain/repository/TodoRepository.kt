package com.example.todolist.domain.repository

import com.example.todolist.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodosFlow(): Flow<List<TodoItem>>
    suspend fun getTodos(): List<TodoItem>
    suspend fun getTodoById(id: Int): TodoItem?
    suspend fun addTodo(title: String, description: String)
    suspend fun updateTodo(id: Int, title: String, description: String, isCompleted: Boolean)
    suspend fun deleteTodo(id: Int)
    suspend fun toggleTodo(id: Int)
    suspend fun importTodosFromJson()
}

