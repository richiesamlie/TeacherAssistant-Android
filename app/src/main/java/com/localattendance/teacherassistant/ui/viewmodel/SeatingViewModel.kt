package com.localattendance.teacherassistant.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.teacherassistant.data.api.ApiClient
import com.localattendance.teacherassistant.data.repository.SeatingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SeatingViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.getInstance(application)
    private val repository = SeatingRepository(apiService)

    private val _seatingLayout = MutableStateFlow<Map<String, String>>(emptyMap())
    val seatingLayout: StateFlow<Map<String, String>> = _seatingLayout

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadSeating(classId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getSeatingLayout(classId)
                .onSuccess { layout ->
                    _seatingLayout.value = layout
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun updateSeat(classId: String, seatId: String, studentId: String?) {
        viewModelScope.launch {
            repository.updateSeat(classId, seatId, studentId)
                .onSuccess {
                    _seatingLayout.value = _seatingLayout.value.toMutableMap().apply {
                        if (studentId != null) {
                            put(seatId, studentId)
                        } else {
                            remove(seatId)
                        }
                    }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun replaceLayout(classId: String, layout: Map<String, String>) {
        viewModelScope.launch {
            repository.replaceLayout(classId, layout)
                .onSuccess {
                    _seatingLayout.value = layout
                }
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun clearSeating(classId: String) {
        viewModelScope.launch {
            repository.clearSeating(classId)
                .onSuccess {
                    _seatingLayout.value = emptyMap()
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
