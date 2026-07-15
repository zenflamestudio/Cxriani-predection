package com.example

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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.AdminHomeScreen
import com.example.ui.AdminUserDetailScreen
import com.example.ui.AppViewModel
import com.example.ui.AppViewModelFactory
import com.example.ui.LoginScreen
import com.example.ui.UserHomeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PredictionApp()
                }
            }
        }
    }
}

@Composable
fun PredictionApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = AppRepository(database.appDao())
    val factory = AppViewModelFactory(repository)
    val viewModel: AppViewModel = viewModel(factory = factory)

    // Initialize matches if needed
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.initializeMatches()
    }

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(viewModel, navController)
        }
        composable("admin_home") {
            AdminHomeScreen(viewModel, navController)
        }
        composable(
            route = "admin_user/{userId}/{userEmail}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("userEmail") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            AdminUserDetailScreen(viewModel, navController, userId, userEmail)
        }
        composable("user_home") {
            UserHomeScreen(viewModel, navController)
        }
    }
}
