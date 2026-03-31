package com.localattendance.teacherassistant.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localattendance.teacherassistant.data.model.EventType
import com.localattendance.teacherassistant.ui.viewmodel.ClassViewModel
import com.localattendance.teacherassistant.ui.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onBack: () -> Unit,
    classViewModel: ClassViewModel = viewModel(),
    eventViewModel: EventViewModel = viewModel()
) {
    val selectedClass by classViewModel.selectedClass.collectAsState()
    val events by eventViewModel.events.collectAsState()
    val isLoading by eventViewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Classwork") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(selectedClass) {
        selectedClass?.let {
            eventViewModel.loadEvents(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Events") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                title = ""
                date = ""
                type = "Classwork"
                description = ""
                showAddDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(events) { event ->
                        EventCard(
                            title = event.title,
                            date = event.date,
                            type = event.type,
                            description = event.description,
                            onDelete = {
                                eventViewModel.deleteEvent(event.id)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Event") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    var typeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded }
                    ) {
                        OutlinedTextField(
                            value = type,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            EventType.entries.forEach { eventType ->
                                DropdownMenuItem(
                                    text = { Text(eventType.value) },
                                    onClick = {
                                        type = eventType.value
                                        typeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedClass?.let {
                            eventViewModel.createEvent(it.id, title, date, type, description)
                        }
                        showAddDialog = false
                    },
                    enabled = title.isNotBlank() && date.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EventCard(
    title: String,
    date: String,
    type: String,
    description: String?,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Text(text = date, style = MaterialTheme.typography.bodySmall)
                AssistChip(
                    onClick = {},
                    label = { Text(type, style = MaterialTheme.typography.bodySmall) }
                )
                if (!description.isNullOrBlank()) {
                    Text(text = description, style = MaterialTheme.typography.bodySmall)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
