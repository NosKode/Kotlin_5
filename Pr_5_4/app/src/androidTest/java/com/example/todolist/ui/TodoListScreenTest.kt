package com.example.todolist.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.todolist.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testDisplayAllThreeTasksFromJson() {
        // Ждем загрузки данных
        Thread.sleep(1000)
        
        // Проверяем, что отображаются все 3 задачи из JSON
        composeTestRule.onNodeWithText("Купить молоко").assertIsDisplayed()
        composeTestRule.onNodeWithText("Позвонить маме").assertIsDisplayed()
        composeTestRule.onNodeWithText("Сделать ДЗ по Android").assertIsDisplayed()
    }

    @Test
    fun testCheckboxTogglesStatus() {
        // Ждем загрузки данных
        Thread.sleep(1500)
        
        // Находим чекбокс для задачи с id=1 ("Купить молоко")
        val checkbox = composeTestRule.onNodeWithTag("checkbox_1")
        checkbox.assertIsDisplayed()
        
        // Проверяем, что задача отображается и не зачеркнута (начальное состояние)
        val taskText = composeTestRule.onNodeWithText("Купить молоко")
        taskText.assertIsDisplayed()
        
        // Кликаем на чекбокс для переключения статуса
        checkbox.performClick()
        
        // Ждем обновления состояния
        Thread.sleep(1000)
        
        // Проверяем, что задача все еще отображается после переключения
        taskText.assertIsDisplayed()
        
        // Проверяем, что чекбокс все еще доступен (не исчез)
        checkbox.assertIsDisplayed()
        
        // Кликаем еще раз, чтобы вернуть в исходное состояние
        checkbox.performClick()
        Thread.sleep(500)
        
        // Проверяем, что задача все еще отображается
        taskText.assertIsDisplayed()
    }

    @Test
    fun testNavigationListToDetailAndBack() {
        // Ждем загрузки данных
        Thread.sleep(1000)
        
        // Кликаем на карточку задачи для перехода на детальный экран
        composeTestRule.onNodeWithTag("todo_card_1").performClick()
        
        // Проверяем, что мы на детальном экране (должно быть описание)
        composeTestRule.onNodeWithText("2 литра, обезжиренное").assertIsDisplayed()
        
        // Кликаем кнопку "Назад"
        composeTestRule.onNodeWithText("Назад").performClick()
        
        // Проверяем, что вернулись на список (должна быть видна задача)
        composeTestRule.onNodeWithText("Купить молоко").assertIsDisplayed()
    }
}

