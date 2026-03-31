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
import com.localattendance.teacherassistant.ui.viewmodel.ClassViewModel
import com.localattendance.teacherassistant.ui.viewmodel.TimetableViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    onBack: () -> Unit,
    classViewModel: ClassViewModel = viewModel(),
    timetableViewModel: TimetableViewModel = viewModel()
) {
    val selectedClass by classViewModel.selectedClass.collectAsState()
    val timetable by timetableViewModel.timetable.collectAsState()
    val isLoading by timetableViewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableIntStateOf(1) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("09:00") }
    var subject by remember { mutableStateOf("") }
    var lesson by remember { mutableStateOf("") }

    val daysOfWeek = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    LaunchedEffect(selectedClass) {
        selectedClass?.let {
            timetableViewModel.loadTimetable(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timetable") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedDay = 1
                startTime = "08:00"
                endTime = "09:00"
                subject = ""
                lesson = ""
                showAddDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Slot")
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
                    daysOfWeek.forEachIndexed { index, day ->
                        if (index > 0) {
                            item {
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                HorizontalDivider()
                            }
                            val daySlots = timetable.filter { it.dayOfWeek == index }
                            items(daySlots) { slot ->
                                TimetableSlotCard(
                                    subject = slot.subject,
                                    lesson = slot.lesson,
                                    startTime = slot.startTime,
                                    endTime = slot.endTime,
                                    onDelete = {
                                        timetableViewModel.deleteSlot(slot.id)
                                    }
                                )
                            }
                            if (daySlots.isEmpty()) {
                                item {
                                    Text(
                                        text = "No classes scheduled",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Timetable Slot") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    var dayExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = dayExpanded,
                        onExpandedChange = { dayExpanded = !dayExpanded }
                    ) {
                        OutlinedTextField(
                            value = daysOfWeek[selectedDay],
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Day") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = dayExpanded,
                            onDismissRequest = { dayExpanded = false }
                        ) {
                            daysOfWeek.drop(1).forEachIndexed { index, day ->
                                DropdownMenuItem(
                                    text = { Text(day) },
                                    onClick = {
                                        selectedDay = index + 1
                                        dayExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = lesson,
                        onValueChange = { lesson = it },
                        label = { Text("Lesson") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time (HH:MM)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time (HH:MM)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedClass?.let {
                            timetableViewModel.createSlot(
                                it.id, selectedDay, startTime, endTime, subject, lesson
                            )
                        }
                        showAddDialog = false
                    },
                    enabled = subject.isNotBlank() && lesson.isNotBlank()
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
fun TimetableSlotCard(
    subject: String,
    lesson: String,
    startTime: String,
    endTime: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = subject, style = MaterialTheme.typography.titleSmall)
                Text(text = lesson, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "$startTime - $endTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
