package com.example.peertutoringsystem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
// import androidx.navigation.compose.composable // You will uncomment this as you add screens

object Routes {
    // Auth flows
    const val Role_Select = "role_select"
    const val Register = "register"
    const val Login = "login"

    // Student flows
    const val Student_Home = "student_home"
    const val Student_Session = "student_session"

    // Tutor flows
    const val Tutor_Home = "tutor_home"
    const val Tutor_Profile = "tutor_profile" // Changed hyphen to underscore
    // Profile
    const val Profile = "profile"
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Role_Select
    ) {
        // Composables will be added by each member here.
        // Example:
        // composable(Routes.Role_Select) { RoleSelectScreen(navController) }
    }
}