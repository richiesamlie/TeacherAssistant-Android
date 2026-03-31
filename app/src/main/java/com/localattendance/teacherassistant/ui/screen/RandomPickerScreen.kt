package com.localattendance.teacherassistant.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localattendance.teacherassistant.ui.viewmodel.ClassViewModel
import com.localattendance.teacherassistant.ui.viewmodel.StudentViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomPickerScreen(
    onBack: () -> Unit,
    classViewModel: ClassViewModel = viewModel(),
    studentViewModel: StudentViewModel = viewModel()
) {
    val selectedClass by classViewModel.selectedClass.collectAsState()
    val students by studentViewModel.students.collectAsState()

    var isSpinning by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<String?>(null) }
    var currentDisplay by remember { mutableStateOf("") }

    val activeStudents = students.filter { !it.isArchived }

    LaunchedEffect(selectedClass) {
        selectedClass?.let {
            studentViewModel.loadStudents(it.id)
        }
    }

    LaunchedEffect(isSpinning) {
        if (isSpinning && activeStudents.isNotEmpty()) {
            val spinDuration = 2000L
            val interval = 50L
            val iterations = spinDuration / interval

            for (i in 0 until iterations) {
                currentDisplay = activeStudents.random().name
                delay(interval)
            }

            selectedStudent = activeStudents.random().name
            currentDisplay = selectedStudent ?: ""
            isSpinning = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Random Picker") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Class: ${selectedClass?.name ?: "No class"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Students: ${activeStudents.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSpinning) {
                        Text(
                            text = currentDisplay,
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else if (selectedStudent != null) {
                        Text(
                            text = selectedStudent ?: "",
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Text(
                            text = "Tap to pick a student",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (!isSpinning && activeStudents.isNotEmpty()) {
                        isSpinning = true
                        selectedStudent = null
                    }
                },
                enabled = !isSpinning && activeStudents.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isSpinning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        Icons.Default.Casino,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pick Random Student")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (selectedStudent != null && !isSpinning) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedStudent = null
                            currentDisplay = ""
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset")
                    }
                    Button(
                        onClick = {
                            isSpinning = true
                            selectedStudent = null
                        }
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pick Again")
                    }
                }
            }
        }
    }
}
