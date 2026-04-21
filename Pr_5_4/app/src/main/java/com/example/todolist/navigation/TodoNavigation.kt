package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todolist.presentation.ui.screen.TodoDetailScreen
import com.example.todolist.presentation.ui.screen.TodoListScreen

sealed class Screen(val route: String) {
    object TodoList : Screen("todo_list")
    object TodoDetail : Screen("todo_detail/{todoId}") {
        fun createRoute(todoId: Int) = "todo_detail/$todoId"
    }
}

@Composable
fun TodoNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.TodoList.route
    ) {
        composable(Screen.TodoList.route) {
            TodoListScreen(
                onTodoClick = { todoId ->
                    navController.navigate(Screen.TodoDetail.createRoute(todoId))
                }
            )
        }
        composable(
            route = Screen.TodoDetail.route,
            arguments = listOf(
                navArgument("todoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getInt("todoId") ?: 0
            TodoDetailScreen(
                todoId = todoId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

