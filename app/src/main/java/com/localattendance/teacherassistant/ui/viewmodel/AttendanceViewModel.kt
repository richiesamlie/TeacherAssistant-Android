package com.localattendance.teacherassistant.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.teacherassistant.data.api.ApiClient
import com.localattendance.teacherassistant.data.model.*
import com.localattendance.teacherassistant.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.getInstance(application)
    private val repository = AttendanceRepository(apiService)

    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords

    private val _selectedDate = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
    val selectedDate: StateFlow<String> = _selectedDate

    private val _dailyNotes = MutableStateFlow<Map<String, String>>(emptyMap())
    val dailyNotes: StateFlow<Map<String, String>> = _dailyNotes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _attendanceMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val attendanceMap: StateFlow<Map<String, String>> = _attendanceMap

    fun loadAttendance(classId: String, date: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val targetDate = date ?: _selectedDate.value
            repository.getAttendanceRecords(classId, date = targetDate)
                .onSuccess { records ->
                    _attendanceRecords.value = records
                    _attendanceMap.value = records.associate { it.studentId to it.status }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun loadAttendanceRange(classId: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAttendanceRecords(classId, startDate = startDate, endDate = endDate)
                .onSuccess { records ->
                    _attendanceRecords.value = records
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun updateAttendanceLocally(studentId: String, status: String) {
        _attendanceMap.value = _attendanceMap.value.toMutableMap().apply {
            put(studentId, status)
        }
    }

    fun saveAttendance(classId: String) {
        viewModelScope.launch {
            val records = _attendanceMap.value.map { (studentId, status) ->
                CreateAttendanceRequest(
                    studentId = studentId,
                    classId = classId,
                    date = _selectedDate.value,
                    status = status
                )
            }
            repository.saveAttendanceRecords(records)
                .onSuccess {
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun loadDailyNotes(classId: String) {
        viewModelScope.launch {
            repository.getDailyNotes(classId)
                .onSuccess { notes ->
                    _dailyNotes.value = notes
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun saveDailyNote(classId: String, date: String, note: String) {
        viewModelScope.launch {
            repository.saveDailyNote(classId, date, note)
                .onSuccess {
                    _dailyNotes.value = _dailyNotes.value.toMutableMap().apply {
                        put(date, note)
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
