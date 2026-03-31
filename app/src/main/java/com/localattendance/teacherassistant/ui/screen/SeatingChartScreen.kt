package com.localattendance.teacherassistant.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.localattendance.teacherassistant.data.model.Student
import com.localattendance.teacherassistant.ui.viewmodel.ClassViewModel
import com.localattendance.teacherassistant.ui.viewmodel.SeatingViewModel
import com.localattendance.teacherassistant.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatingChartScreen(
    onBack: () -> Unit,
    classViewModel: ClassViewModel = viewModel(),
    studentViewModel: StudentViewModel = viewModel(),
    seatingViewModel: SeatingViewModel = viewModel()
) {
    val selectedClass by classViewModel.selectedClass.collectAsState()
    val students by studentViewModel.students.collectAsState()
    val seatingLayout by seatingViewModel.seatingLayout.collectAsState()
    val isLoading by seatingViewModel.isLoading.collectAsState()

    val rows = 5
    val cols = 6
    var selectedSeat by remember { mutableStateOf<String?>(null) }
    var showAssignDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedClass) {
        selectedClass?.let {
            studentViewModel.loadStudents(it.id)
            seatingViewModel.loadSeating(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seating Chart") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        selectedClass?.let { seatingViewModel.clearSeating(it.id) }
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear All")
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
            Text(
                text = "Front of Class",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(cols),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (row in 0 until rows) {
                        for (col in 0 until cols) {
                            val seatId = "seat_${row}_$col"
                            val studentId = seatingLayout[seatId]
                            val student = students.find { it.id == studentId }

                            item {
                                SeatCard(
                                    seatId = seatId,
                                    student = student,
                                    onClick = {
                                        selectedSeat = seatId
                                        showAssignDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Legend:",
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Empty", style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(1.dp, MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Occupied", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    if (showAssignDialog && selectedSeat != null) {
        val assignedStudentId = seatingLayout[selectedSeat]
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Assign Seat") },
            text = {
                Column {
                    Text("Seat: $selectedSeat")
                    Spacer(modifier = Modifier.height(8.dp))

                    if (assignedStudentId != null) {
                        val assignedStudent = students.find { it.id == assignedStudentId }
                        Text("Currently assigned: ${assignedStudent?.name ?: "Unknown"}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                selectedClass?.let {
                                    seatingViewModel.updateSeat(it, selectedSeat!!, null)
                                }
                                showAssignDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Remove Student")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text("Assign a student:")
                    Spacer(modifier = Modifier.height(8.dp))

                    val unassignedStudents = students.filter { student ->
                        !student.isArchived && seatingLayout.values.none { it == student.id }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        unassignedStudents.forEach { student ->
                            item {
                                Button(
                                    onClick = {
                                        selectedClass?.let {
                                            seatingViewModel.updateSeat(it, selectedSeat!!, student.id)
                                        }
                                        showAssignDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = student.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAssignDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SeatCard(
    seatId: String,
    student: Student?,
    onClick: () -> Unit
) {
    val isOccupied = student != null
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isOccupied)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isOccupied)
            CardDefaults.outlinedCardBorder()
        else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (student != null) {
                Text(
                    text = student.name.take(3),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Icon(
                    Icons.Default.Chair,
                    contentDescription = "Empty Seat",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
