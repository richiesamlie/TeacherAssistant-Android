package com.localattendance.teacherassistant.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.localattendance.teacherassistant.ui.screen.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    isLoggedIn: Boolean,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAttendance = { navController.navigate(Screen.TakeAttendance.route) },
                onNavigateToRoster = { navController.navigate(Screen.StudentRoster.route) },
                onNavigateToTimetable = { navController.navigate(Screen.Timetable.route) },
                onNavigateToEvents = { navController.navigate(Screen.Events.route) },
                onNavigateToSeating = { navController.navigate(Screen.SeatingChart.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                onNavigateToPicker = { navController.navigate(Screen.RandomPicker.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.TakeAttendance.route) {
            TakeAttendanceScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.StudentRoster.route) {
            StudentRosterScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Timetable.route) {
            TimetableScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Events.route) {
            EventsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SeatingChart.route) {
            SeatingChartScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Reports.route) {
            ReportsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RandomPicker.route) {
            RandomPickerScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
