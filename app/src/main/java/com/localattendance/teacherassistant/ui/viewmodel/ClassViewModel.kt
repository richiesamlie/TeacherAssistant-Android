package com.localattendance.teacherassistant.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.teacherassistant.data.api.ApiClient
import com.localattendance.teacherassistant.data.model.ClassItem
import com.localattendance.teacherassistant.data.model.ClassTeacher
import com.localattendance.teacherassistant.data.model.Teacher
import com.localattendance.teacherassistant.data.repository.ClassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClassViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.getInstance(application)
    private val repository = ClassRepository(apiService)

    private val _classes = MutableStateFlow<List<ClassItem>>(emptyList())
    val classes: StateFlow<List<ClassItem>> = _classes

    private val _selectedClass = MutableStateFlow<ClassItem?>(null)
    val selectedClass: StateFlow<ClassItem?> = _selectedClass

    private val _classTeachers = MutableStateFlow<List<ClassTeacher>>(emptyList())
    val classTeachers: StateFlow<List<ClassTeacher>> = _classTeachers

    private val _allTeachers = MutableStateFlow<List<Teacher>>(emptyList())
    val allTeachers: StateFlow<List<Teacher>> = _allTeachers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadClasses() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getClasses()
                .onSuccess { classList ->
                    _classes.value = classList
                    if (_selectedClass.value == null && classList.isNotEmpty()) {
                        _selectedClass.value = classList.first()
                    }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun selectClass(classItem: ClassItem) {
        _selectedClass.value = classItem
    }

    fun createClass(name: String) {
        viewModelScope.launch {
            val id = "class_${System.currentTimeMillis()}"
            repository.createClass(id, name)
                .onSuccess {
                    loadClasses()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun updateClass(classId: String, name: String) {
        viewModelScope.launch {
            repository.updateClass(classId, name)
                .onSuccess {
                    loadClasses()
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun deleteClass(classId: String) {
        viewModelScope.launch {
            repository.deleteClass(classId)
                .onSuccess {
                    _classes.value = _classes.value.filter { it.id != classId }
                    if (_selectedClass.value?.id == classId) {
                        _selectedClass.value = _classes.value.firstOrNull()
                    }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun loadClassTeachers(classId: String) {
        viewModelScope.launch {
            repository.getClassTeachers(classId)
                .onSuccess { teachers ->
                    _classTeachers.value = teachers
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun addTeacherToClass(classId: String, teacherId: String) {
        viewModelScope.launch {
            repository.addTeacherToClass(classId, teacherId)
                .onSuccess {
                    loadClassTeachers(classId)
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun removeTeacherFromClass(classId: String, teacherId: String) {
        viewModelScope.launch {
            repository.removeTeacherFromClass(classId, teacherId)
                .onSuccess {
                    loadClassTeachers(classId)
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
