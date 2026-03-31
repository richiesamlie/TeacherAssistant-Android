package com.localattendance.teacherassistant.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object TakeAttendance : Screen("take_attendance")
    object StudentRoster : Screen("student_roster")
    object Timetable : Screen("timetable")
    object Events : Screen("events")
    object SeatingChart : Screen("seating_chart")
    object Reports : Screen("reports")
    object RandomPicker : Screen("random_picker")
    object Settings : Screen("settings")
}
