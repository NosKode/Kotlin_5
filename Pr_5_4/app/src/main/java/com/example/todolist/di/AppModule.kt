package com.example.todolist.di

import android.content.Context
import com.example.todolist.data.local.TodoDatabase
import com.example.todolist.data.local.TodoJsonDataSource
import com.example.todolist.data.preferences.PreferencesManager
import com.example.todolist.data.repository.TodoRepositoryImpl
import com.example.todolist.domain.repository.TodoRepository
import com.example.todolist.domain.usecase.AddTodoUseCase
import com.example.todolist.domain.usecase.DeleteTodoUseCase
import com.example.todolist.domain.usecase.GetTodoByIdUseCase
import com.example.todolist.domain.usecase.GetTodosUseCase
import com.example.todolist.domain.usecase.ToggleTodoUseCase
import com.example.todolist.domain.usecase.UpdateTodoUseCase

object AppModule {
    @Volatile
    private var repositoryInstance: TodoRepository? = null

    @Volatile
    private var preferencesManagerInstance: PreferencesManager? = null

    fun provideTodoRepository(context: Context): TodoRepository {
        return repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: TodoRepositoryImpl(
                todoDao = TodoDatabase.getDatabase(context).todoDao(),
                jsonDataSource = TodoJsonDataSource(context)
            ).also {
                repositoryInstance = it
            }
        }
    }

    fun providePreferencesManager(context: Context): PreferencesManager {
        return preferencesManagerInstance ?: synchronized(this) {
            preferencesManagerInstance ?: PreferencesManager(context).also {
                preferencesManagerInstance = it
            }
        }
    }

    fun provideGetTodosUseCase(repository: TodoRepository): GetTodosUseCase {
        return GetTodosUseCase(repository)
    }

    fun provideGetTodoByIdUseCase(repository: TodoRepository): GetTodoByIdUseCase {
        return GetTodoByIdUseCase(repository)
    }

    fun provideToggleTodoUseCase(repository: TodoRepository): ToggleTodoUseCase {
        return ToggleTodoUseCase(repository)
    }

    fun provideAddTodoUseCase(repository: TodoRepository): AddTodoUseCase {
        return AddTodoUseCase(repository)
    }

    fun provideUpdateTodoUseCase(repository: TodoRepository): UpdateTodoUseCase {
        return UpdateTodoUseCase(repository)
    }

    fun provideDeleteTodoUseCase(repository: TodoRepository): DeleteTodoUseCase {
        return DeleteTodoUseCase(repository)
    }
}

