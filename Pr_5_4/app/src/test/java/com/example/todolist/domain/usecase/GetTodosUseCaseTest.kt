package com.example.todolist.domain.usecase

import com.example.todolist.domain.model.TodoItem
import com.example.todolist.domain.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetTodosUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var getTodosUseCase: GetTodosUseCase

    @Before
    fun setup() {
        repository = mockk()
        getTodosUseCase = GetTodosUseCase(repository)
    }

    @Test
    fun `GetTodosUseCase returns 3 tasks`() = runTest {
        // Given
        val expectedTodos = listOf(
            TodoItem(1, "Купить молоко", "2 литра, обезжиренное", false),
            TodoItem(2, "Позвонить маме", "Спросить про выходные", true),
            TodoItem(3, "Сделать ДЗ по Android", "Clean Architecture + Compose", false)
        )
        coEvery { repository.getTodos() } returns expectedTodos

        // When
        val result = getTodosUseCase()

        // Then
        assertEquals(3, result.size)
        assertEquals(expectedTodos, result)
    }
}

