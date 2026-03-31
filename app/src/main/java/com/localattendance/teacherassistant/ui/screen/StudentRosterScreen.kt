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
import com.localattendance.teacherassistant.data.model.Student
import com.localattendance.teacherassistant.ui.viewmodel.ClassViewModel
import com.localattendance.teacherassistant.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRosterScreen(
    onBack: () -> Unit,
    classViewModel: ClassViewModel = viewModel(),
    studentViewModel: StudentViewModel = viewModel()
) {
    val selectedClass by classViewModel.selectedClass.collectAsState()
    val students by studentViewModel.students.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    var name by remember { mutableStateOf("") }
    var rollNumber by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }

    LaunchedEffect(selectedClass) {
        selectedClass?.let {
            studentViewModel.loadStudents(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Roster") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                name = ""
                rollNumber = ""
                parentName = ""
                parentPhone = ""
                showAddDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Student")
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
                        text = "Class: ${selectedClass?.name ?: "No class selected"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Total Students: ${students.count { !it.isArchived }}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(students.filter { !it.isArchived }) { student ->
                    StudentCard(
                        student = student,
                        onEdit = {
                            selectedStudent = student
                            name = student.name
                            rollNumber = student.rollNumber
                            parentName = student.parentName ?: ""
                            parentPhone = student.parentPhone ?: ""
                            showEditDialog = true
                        },
                        onToggleFlag = {
                            studentViewModel.toggleFlag(student.id, !student.isFlagged)
                        },
                        onDelete = {
                            studentViewModel.deleteStudent(student.id)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        StudentDialog(
            title = "Add Student",
            name = name,
            rollNumber = rollNumber,
            parentName = parentName,
            parentPhone = parentPhone,
            onNameChange = { name = it },
            onRollNumberChange = { rollNumber = it },
            onParentNameChange = { parentName = it },
            onParentPhoneChange = { parentPhone = it },
            onConfirm = {
                selectedClass?.let {
                    studentViewModel.addStudent(it.id, name, rollNumber, parentName, parentPhone)
                }
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    if (showEditDialog) {
        StudentDialog(
            title = "Edit Student",
            name = name,
            rollNumber = rollNumber,
            parentName = parentName,
            parentPhone = parentPhone,
            onNameChange = { name = it },
            onRollNumberChange = { rollNumber = it },
            onParentNameChange = { parentName = it },
            onParentPhoneChange = { parentPhone = it },
            onConfirm = {
                selectedStudent?.let { student ->
                    studentViewModel.updateStudent(
                        student.id,
                        com.localattendance.teacherassistant.data.model.UpdateStudentRequest(
                            name = name,
                            rollNumber = rollNumber,
                            parentName = parentName,
                            parentPhone = parentPhone
                        )
                    )
                }
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
fun StudentCard(
    student: Student,
    onEdit: () -> Unit,
    onToggleFlag: () -> Unit,
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
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    if (student.isFlagged) {
                        Icon(
                            Icons.Default.Flag,
                            contentDescription = "Flagged",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(text = student.name, style = MaterialTheme.typography.titleSmall)
                }
                Text(text = "Roll: ${student.rollNumber}", style = MaterialTheme.typography.bodySmall)
                if (!student.parentName.isNullOrBlank()) {
                    Text(text = "Parent: ${student.parentName}", style = MaterialTheme.typography.bodySmall)
                }
            }

            IconButton(onClick = onToggleFlag) {
                Icon(
                    if (student.isFlagged) Icons.Default.Flag else Icons.Default.OutlinedFlag,
                    contentDescription = "Toggle Flag",
                    tint = if (student.isFlagged) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun StudentDialog(
    title: String,
    name: String,
    rollNumber: String,
    parentName: String,
    parentPhone: String,
    onNameChange: (String) -> Unit,
    onRollNumberChange: (String) -> Unit,
    onParentNameChange: (String) -> Unit,
    onParentPhoneChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Student Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rollNumber,
                    onValueChange = onRollNumberChange,
                    label = { Text("Roll Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = parentName,
                    onValueChange = onParentNameChange,
                    label = { Text("Parent Name (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = parentPhone,
                    onValueChange = onParentPhoneChange,
                    label = { Text("Parent Phone (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = name.isNotBlank() && rollNumber.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
