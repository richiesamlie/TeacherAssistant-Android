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
import com.localattendance.teacherassistant.ui.viewmodel.AttendanceViewModel
import com.localattendance.teacherassistant.ui.viewmodel.ClassViewModel
import com.localattendance.teacherassistant.ui.viewmodel.StudentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    classViewModel: ClassViewModel = viewModel(),
    studentViewModel: StudentViewModel = viewModel(),
    attendanceViewModel: AttendanceViewModel = viewModel()
) {
    val selectedClass by classViewModel.selectedClass.collectAsState()
    val students by studentViewModel.students.collectAsState()
    val attendanceRecords by attendanceViewModel.attendanceRecords.collectAsState()
    val isLoading by attendanceViewModel.isLoading.collectAsState()

    var selectedMonth by remember { mutableIntStateOf(LocalDate.now().monthValue) }
    var selectedYear by remember { mutableIntStateOf(LocalDate.now().year) }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    LaunchedEffect(selectedClass) {
        selectedClass?.let {
            studentViewModel.loadStudents(it.id)
        }
    }

    LaunchedEffect(selectedClass, selectedMonth, selectedYear) {
        selectedClass?.let {
            val startDate = LocalDate.of(selectedYear, selectedMonth, 1)
                .format(DateTimeFormatter.ISO_DATE)
            val endDate = LocalDate.of(selectedYear, selectedMonth, 1)
                .withDayOfMonth(LocalDate.of(selectedYear, selectedMonth, 1).lengthOfMonth())
                .format(DateTimeFormatter.ISO_DATE)
            attendanceViewModel.loadAttendanceRange(it.id, startDate, endDate)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Reports") },
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
                        text = "Select Period",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        var monthExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = monthExpanded,
                            onExpandedChange = { monthExpanded = !monthExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = months[selectedMonth - 1],
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Month") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = monthExpanded,
                                onDismissRequest = { monthExpanded = false }
                            ) {
                                months.forEachIndexed { index, month ->
                                    DropdownMenuItem(
                                        text = { Text(month) },
                                        onClick = {
                                            selectedMonth = index + 1
                                            monthExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = selectedYear.toString(),
                            onValueChange = {
                                it.toIntOrNull()?.let { year -> selectedYear = year }
                            },
                            label = { Text("Year") },
                            modifier = Modifier.weight(1f)
                        )
                    }
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
                val activeStudents = students.filter { !it.isArchived }
                val studentStats = activeStudents.map { student ->
                    val records = attendanceRecords.filter { it.studentId == student.id }
                    val present = records.count { it.status == "Present" }
                    val absent = records.count { it.status == "Absent" }
                    val sick = records.count { it.status == "Sick" }
                    val late = records.count { it.status == "Late" }
                    val total = records.size
                    Triple(student.name, total, mapOf(
                        "Present" to present,
                        "Absent" to absent,
                        "Sick" to sick,
                        "Late" to late
                    ))
                }

                LazyColumn {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Summary",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Total Students: ${activeStudents.size}")
                                Text("Total Records: ${attendanceRecords.size}")
                                Text("Period: ${months[selectedMonth - 1]} $selectedYear")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(studentStats) { (name, total, stats) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    StatChip("P", stats["Present"] ?: 0, MaterialTheme.colorScheme.primary)
                                    StatChip("A", stats["Absent"] ?: 0, MaterialTheme.colorScheme.error)
                                    StatChip("S", stats["Sick"] ?: 0, MaterialTheme.colorScheme.tertiary)
                                    StatChip("L", stats["Late"] ?: 0, MaterialTheme.colorScheme.secondary)
                                }
                                if (total > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val rate = ((stats["Present"] ?: 0) + (stats["Late"] ?: 0)) * 100 / total
                                    LinearProgressIndicator(
                                        progress = { rate / 100f },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = "Attendance Rate: $rate%",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
