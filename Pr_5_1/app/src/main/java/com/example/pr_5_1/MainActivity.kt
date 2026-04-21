package com.example.pr_5_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiaryApp()
        }
    }
}

@Composable
fun DiaryApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val viewModel: DiaryViewModel = viewModel()

            DiaryNavHost(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun DiaryNavHost(
    navController: NavHostController,
    viewModel: DiaryViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            val context = androidx.compose.ui.platform.LocalContext.current
            androidx.compose.runtime.LaunchedEffect(Unit) {
                viewModel.initialize(context)
            }

            DiaryListScreen(
                viewModel = viewModel,
                onAddEntry = {
                    navController.navigate("editor")
                },
                onEditEntry = { fileName ->
                    navController.navigate("editor/$fileName")
                }
            )
        }

        composable("editor") {
            DiaryEditorScreen(
                viewModel = viewModel,
                fileName = null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "editor/{fileName}",
            arguments = listOf(
                navArgument("fileName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName")
            DiaryEditorScreen(
                viewModel = viewModel,
                fileName = fileName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}