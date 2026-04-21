package com.example.todolist.domain.usecase

import com.example.todolist.domain.model.TodoItem
import com.example.todolist.domain.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ToggleTodoUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var toggleTodoUseCase: ToggleTodoUseCase

    @Before
    fun setup() {
        repository = mockk()
        toggleTodoUseCase = ToggleTodoUseCase(repository)
    }

    @Test
    fun `toggleTodo changes isCompleted`() = runTest {
        // Given
        val todoId = 1
        coEvery { repository.toggleTodo(todoId) } returns Unit

        // When
        toggleTodoUseCase(todoId)

        // Then
        coVerify(exactly = 1) { repository.toggleTodo(todoId) }
    }
}

