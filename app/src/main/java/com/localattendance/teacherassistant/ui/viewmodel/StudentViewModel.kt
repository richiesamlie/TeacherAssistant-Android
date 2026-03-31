package com.localattendance.teacherassistant.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.teacherassistant.data.api.ApiClient
import com.localattendance.teacherassistant.data.model.CreateStudentRequest
import com.localattendance.teacherassistant.data.model.Student
import com.localattendance.teacherassistant.data.model.UpdateStudentRequest
import com.localattendance.teacherassistant.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.getInstance(application)
    private val repository = StudentRepository(apiService)

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadStudents(classId: String, includeArchived: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getStudents(classId, includeArchived)
                .onSuccess { studentList ->
                    _students.value = studentList
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun addStudent(classId: String, name: String, rollNumber: String, parentName: String? = null, parentPhone: String? = null) {
        viewModelScope.launch {
            val id = "student_${System.currentTimeMillis()}"
            val request = CreateStudentRequest(id, name, rollNumber, parentName, parentPhone)
            repository.addStudent(classId, request)
                .onSuccess {
                    loadStudents(classId)
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun updateStudent(studentId: String, request: UpdateStudentRequest) {
        viewModelScope.launch {
            repository.updateStudent(studentId, request)
                .onSuccess {
                    _students.value = _students.value.map { student ->
                        if (student.id == studentId) {
                            student.copy(
                                name = request.name ?: student.name,
                                rollNumber = request.rollNumber ?: student.rollNumber,
                                parentName = request.parentName ?: student.parentName,
                                parentPhone = request.parentPhone ?: student.parentPhone,
                                isFlagged = request.isFlagged ?: student.isFlagged,
                                isArchived = request.isArchived ?: student.isArchived
                            )
                        } else student
                    }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            repository.deleteStudent(studentId)
                .onSuccess {
                    _students.value = _students.value.filter { it.id != studentId }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun toggleFlag(studentId: String, isFlagged: Boolean) {
        viewModelScope.launch {
            repository.updateStudent(studentId, UpdateStudentRequest(isFlagged = isFlagged))
                .onSuccess {
                    _students.value = _students.value.map { student ->
                        if (student.id == studentId) student.copy(isFlagged = isFlagged) else student
                    }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
