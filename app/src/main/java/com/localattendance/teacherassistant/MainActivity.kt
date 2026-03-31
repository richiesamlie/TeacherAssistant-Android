package com.localattendance.teacherassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.localattendance.teacherassistant.ui.navigation.AppNavigation
import com.localattendance.teacherassistant.ui.theme.LocalAttendanceTheme
import com.localattendance.teacherassistant.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocalAttendanceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

                    AppNavigation(
                        navController = navController,
                        isLoggedIn = isLoggedIn
                    )
                }
            }
        }
    }
}
