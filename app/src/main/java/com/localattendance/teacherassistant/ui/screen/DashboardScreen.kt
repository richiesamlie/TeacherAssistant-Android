package com.localattendance.teacherassistant.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localattendance.teacherassistant.ui.viewmodel.AuthViewModel
import com.localattendance.teacherassistant.ui.viewmodel.ClassViewModel
import com.localattendance.teacherassistant.ui.viewmodel.StudentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAttendance: () -> Unit,
    onNavigateToRoster: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToSeating: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToPicker: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    classViewModel: ClassViewModel = viewModel(),
    studentViewModel: StudentViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val classes by classViewModel.classes.collectAsState()
    val selectedClass by classViewModel.selectedClass.collectAsState()
    val students by studentViewModel.students.collectAsState()

    LaunchedEffect(Unit) {
        classViewModel.loadClasses()
    }

    LaunchedEffect(selectedClass) {
        selectedClass?.let {
            studentViewModel.loadStudents(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teacher Assistant") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = {
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Welcome, ${currentUser?.name ?: "Teacher"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (classes.isNotEmpty()) {
                Text(
                    text = "Select Class",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedClass?.name ?: "Select a class",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        classes.forEach { classItem ->
                            DropdownMenuItem(
                                text = { Text(classItem.name) },
                                onClick = {
                                    classViewModel.selectClass(classItem)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(
                        title = "Students",
                        value = students.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatCard(
                        title = "Present Today",
                        value = "${students.count { !it.isArchived }}",
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No classes yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Create a class to get started",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    DashboardCard(
                        title = "Take Attendance",
                        icon = Icons.Default.CheckCircle,
                        onClick = onNavigateToAttendance
                    )
                }
                item {
                    DashboardCard(
                        title = "Student Roster",
                        icon = Icons.Default.People,
                        onClick = onNavigateToRoster
                    )
                }
                item {
                    DashboardCard(
                        title = "Timetable",
                        icon = Icons.Default.Schedule,
                        onClick = onNavigateToTimetable
                    )
                }
                item {
                    DashboardCard(
                        title = "Events",
                        icon = Icons.Default.Event,
                        onClick = onNavigateToEvents
                    )
                }
                item {
                    DashboardCard(
                        title = "Seating Chart",
                        icon = Icons.Default.GridOn,
                        onClick = onNavigateToSeating
                    )
                }
                item {
                    DashboardCard(
                        title = "Reports",
                        icon = Icons.Default.Assessment,
                        onClick = onNavigateToReports
                    )
                }
                item {
                    DashboardCard(
                        title = "Random Picker",
                        icon = Icons.Default.Casino,
                        onClick = onNavigateToPicker
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
