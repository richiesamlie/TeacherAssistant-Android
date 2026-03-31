package com.localattendance.teacherassistant.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.teacherassistant.data.api.ApiClient
import com.localattendance.teacherassistant.data.model.*
import com.localattendance.teacherassistant.data.repository.TimetableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimetableViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.getInstance(application)
    private val repository = TimetableRepository(apiService)

    private val _timetable = MutableStateFlow<List<TimetableSlot>>(emptyList())
    val timetable: StateFlow<List<TimetableSlot>> = _timetable

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadTimetable(classId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getTimetable(classId)
                .onSuccess { slots ->
                    _timetable.value = slots.sortedWith(compareBy({ it.dayOfWeek }, { it.startTime }))
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun createSlot(
        classId: String,
        dayOfWeek: Int,
        startTime: String,
        endTime: String,
        subject: String,
        lesson: String
    ) {
        viewModelScope.launch {
            val id = "slot_${System.currentTimeMillis()}"
            val request = CreateTimetableRequest(id, dayOfWeek, startTime, endTime, subject, lesson)
            repository.createSlot(classId, request)
                .onSuccess {
                    loadTimetable(classId)
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun updateSlot(
        slotId: String,
        dayOfWeek: Int,
        startTime: String,
        endTime: String,
        subject: String,
        lesson: String
    ) {
        viewModelScope.launch {
            val request = UpdateTimetableRequest(dayOfWeek, startTime, endTime, subject, lesson)
            repository.updateSlot(slotId, request)
                .onSuccess {
                    _timetable.value = _timetable.value.map { slot ->
                        if (slot.id == slotId) {
                            slot.copy(
                                dayOfWeek = dayOfWeek,
                                startTime = startTime,
                                endTime = endTime,
                                subject = subject,
                                lesson = lesson
                            )
                        } else slot
                    }.sortedWith(compareBy({ it.dayOfWeek }, { it.startTime }))
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun deleteSlot(slotId: String) {
        viewModelScope.launch {
            repository.deleteSlot(slotId)
                .onSuccess {
                    _timetable.value = _timetable.value.filter { it.id != slotId }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun getSlotsByDay(dayOfWeek: Int): List<TimetableSlot> {
        return _timetable.value.filter { it.dayOfWeek == dayOfWeek }
    }

    fun clearError() {
        _error.value = null
    }
}
