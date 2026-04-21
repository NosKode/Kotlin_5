package com.example.todolist.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        /** ARGB (sRGB), same as [android.graphics.Color] packed int — not Compose [Color.value]. */
        private val COMPLETED_TODO_COLOR_ARGB_KEY = intPreferencesKey("completed_todo_argb")
        private val USE_CUSTOM_COMPLETED_COLOR_KEY = booleanPreferencesKey("use_custom_completed_color")
        val DEFAULT_COMPLETED_COLOR_ARGB: Int = 0xFFE8F5E9.toInt()
    }

    val useCustomCompletedTodoColor: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[USE_CUSTOM_COMPLETED_COLOR_KEY] ?: true
    }

    val completedTodoColorArgb: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[COMPLETED_TODO_COLOR_ARGB_KEY] ?: DEFAULT_COMPLETED_COLOR_ARGB
    }

    suspend fun saveCompletedTodoColorArgb(argb: Int) {
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_TODO_COLOR_ARGB_KEY] = argb
        }
    }

    suspend fun saveUseCustomCompletedTodoColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_CUSTOM_COMPLETED_COLOR_KEY] = enabled
        }
    }
}
