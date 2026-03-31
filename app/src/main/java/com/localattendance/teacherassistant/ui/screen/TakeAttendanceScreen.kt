package com.localattendance.teacherassistant.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localattendance.teacherassistant.data.model.AttendanceStatus
import com.localattendance.teacherassistant.ui.viewmodel.AttendanceViewModel
import com.localattendance.teacherassistant.ui.viewmodel.ClassViewModel
import com.localattendance.teacherassistant.ui.viewmodel.StudentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeAttendanceScreen(
    onBack: () -> Unit,
    classViewModel: ClassViewModel = viewModel(),
    studentViewModel: StudentViewModel = viewModel(),
    attendanceViewModel: AttendanceViewModel = viewModel()
) {
    val selectedClass by classViewModel.selectedClass.collectAsState()
    val students by studentViewModel.students.collectAsState()
    val attendanceMap by attendanceViewModel.attendanceMap.collectAsState()
    val selectedDate by attendanceViewModel.selectedDate.collectAsState()
    val isLoading by attendanceViewModel.isLoading.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(selectedClass) {
        selectedClass?.let {
            studentViewModel.loadStudents(it.id)
            attendanceViewModel.loadAttendance(it.id, selectedDate)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take Attendance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedClass?.let { attendanceViewModel.saveAttendance(it.id) }
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Date: $selectedDate",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Class: ${selectedClass?.name ?: "No class selected"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(students.filter { !it.isArchived }) { student ->
                        AttendanceRow(
                            studentName = student.name,
                            rollNumber = student.rollNumber,
                            currentStatus = attendanceMap[student.id] ?: AttendanceStatus.PRESENT.value,
                            onStatusChange = { status ->
                                attendanceViewModel.updateAttendanceLocally(student.id, status)
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = LocalDate.ofEpochDay(millis / 86400000)
                            val formatted = date.format(DateTimeFormatter.ISO_DATE)
                            attendanceViewModel.setSelectedDate(formatted)
                            selectedClass?.let { attendanceViewModel.loadAttendance(it.id, formatted) }
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun AttendanceRow(
    studentName: String,
    rollNumber: String,
    currentStatus: String,
    onStatusChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = studentName, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Roll: $rollNumber", style = MaterialTheme.typography.bodySmall)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            AttendanceStatus.entries.forEach { status ->
                FilterChip(
                    selected = currentStatus == status.value,
                    onClick = { onStatusChange(status.value) },
                    label = {
                        Text(
                            text = when (status) {
                                AttendanceStatus.PRESENT -> "P"
                                AttendanceStatus.ABSENT -> "A"
                                AttendanceStatus.SICK -> "S"
                                AttendanceStatus.LATE -> "L"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (status) {
                            AttendanceStatus.PRESENT -> MaterialTheme.colorScheme.primaryContainer
                            AttendanceStatus.ABSENT -> MaterialTheme.colorScheme.errorContainer
                            AttendanceStatus.SICK -> MaterialTheme.colorScheme.tertiaryContainer
                            AttendanceStatus.LATE -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    )
                )
            }
        }
    }
}
