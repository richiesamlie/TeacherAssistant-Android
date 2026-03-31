package com.localattendance.teacherassistant.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.teacherassistant.data.api.ApiClient
import com.localattendance.teacherassistant.data.model.*
import com.localattendance.teacherassistant.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.getInstance(application)
    private val repository = EventRepository(apiService)

    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadEvents(classId: String, type: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEvents(classId, type)
                .onSuccess { eventList ->
                    _events.value = eventList
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun loadEventsByDateRange(classId: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEvents(classId, startDate = startDate, endDate = endDate)
                .onSuccess { eventList ->
                    _events.value = eventList
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun createEvent(classId: String, title: String, date: String, type: String, description: String? = null) {
        viewModelScope.launch {
            val id = "event_${System.currentTimeMillis()}"
            val request = CreateEventRequest(id, date, title, type, description)
            repository.createEvent(classId, request)
                .onSuccess {
                    loadEvents(classId)
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun updateEvent(eventId: String, title: String, date: String, type: String, description: String? = null) {
        viewModelScope.launch {
            val request = UpdateEventRequest(date, title, type, description)
            repository.updateEvent(eventId, request)
                .onSuccess {
                    _events.value = _events.value.map { event ->
                        if (event.id == eventId) {
                            event.copy(title = title, date = date, type = type, description = description)
                        } else event
                    }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
                .onSuccess {
                    _events.value = _events.value.filter { it.id != eventId }
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
