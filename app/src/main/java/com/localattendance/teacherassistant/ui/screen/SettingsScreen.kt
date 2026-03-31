package com.localattendance.teacherassistant.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localattendance.teacherassistant.ui.viewmodel.AuthViewModel
import com.localattendance.teacherassistant.ui.viewmodel.ClassViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    classViewModel: ClassViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val classes by classViewModel.classes.collectAsState()

    var showCreateClassDialog by remember { mutableStateOf(false) }
    var newClassName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        classViewModel.loadClasses()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Account",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Name: ${currentUser?.name ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Username: ${currentUser?.username ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Classes",
                            style = MaterialTheme.typography.titleMedium
                        )
                        FilledTonalButton(onClick = { showCreateClassDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Class")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    classes.forEach { classItem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    classViewModel.selectClass(classItem)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Class_,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = classItem.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (!classItem.owner_name.isNullOrBlank()) {
                                    Text(
                                        text = "Owner: ${classItem.owner_name}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    classViewModel.deleteClass(classItem.id)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        HorizontalDivider()
                    }

                    if (classes.isEmpty()) {
                        Text(
                            text = "No classes yet. Create one to get started!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Server Configuration",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "To connect to a different server, update the BASE_URL in the app settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Current: http://10.0.2.2:3000/api/",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    authViewModel.logout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }

    if (showCreateClassDialog) {
        AlertDialog(
            onDismissRequest = { showCreateClassDialog = false },
            title = { Text("Create New Class") },
            text = {
                OutlinedTextField(
                    value = newClassName,
                    onValueChange = { newClassName = it },
                    label = { Text("Class Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newClassName.isNotBlank()) {
                            classViewModel.createClass(newClassName)
                            newClassName = ""
                            showCreateClassDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateClassDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
