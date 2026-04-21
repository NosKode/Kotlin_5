package com.example.todolist.data.repository

import com.example.todolist.data.local.TodoDao
import com.example.todolist.data.local.TodoEntity
import com.example.todolist.data.local.TodoJsonDataSource
import com.example.todolist.data.model.TodoItemDto
import com.example.todolist.domain.model.TodoItem
import com.example.todolist.domain.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val jsonDataSource: TodoJsonDataSource
) : TodoRepository {

    override fun getTodosFlow(): Flow<List<TodoItem>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTodos(): List<TodoItem> = withContext(Dispatchers.IO) {
        todoDao.getAllTodos().first().map { it.toDomain() }
    }

    override suspend fun getTodoById(id: Int): TodoItem? = withContext(Dispatchers.IO) {
        todoDao.getTodoById(id)?.toDomain()
    }

    override suspend fun addTodo(title: String, description: String) {
        withContext(Dispatchers.IO) {
            val entity = TodoEntity(
                title = title,
                description = description,
                isCompleted = false
            )
            todoDao.insertTodo(entity)
        }
    }

    override suspend fun updateTodo(
        id: Int,
        title: String,
        description: String,
        isCompleted: Boolean
    ) {
        withContext(Dispatchers.IO) {
            val entity = TodoEntity(
                id = id,
                title = title,
                description = description,
                isCompleted = isCompleted
            )
            todoDao.updateTodo(entity)
        }
    }

    override suspend fun deleteTodo(id: Int) {
        withContext(Dispatchers.IO) {
            todoDao.deleteTodoById(id)
        }
    }

    override suspend fun toggleTodo(id: Int) {
        withContext(Dispatchers.IO) {
            val todo = todoDao.getTodoById(id)
            if (todo != null) {
                val updated = todo.copy(isCompleted = !todo.isCompleted)
                todoDao.updateTodo(updated)
            }
        }
    }

    override suspend fun importTodosFromJson() {
        withContext(Dispatchers.IO) {
            val count = todoDao.getTodosCount()
            if (count == 0) {
                val dtos = jsonDataSource.getTodos()
                val entities = dtos.map { it.toEntity() }
                todoDao.insertTodos(entities)
            }
        }
    }

    private fun TodoEntity.toDomain(): TodoItem {
        return TodoItem(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted
        )
    }

    private fun TodoItemDto.toEntity(): TodoEntity {
        return TodoEntity(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted
        )
    }
}

