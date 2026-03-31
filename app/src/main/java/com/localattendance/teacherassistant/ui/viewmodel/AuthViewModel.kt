package com.localattendance.teacherassistant.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.localattendance.teacherassistant.data.api.ApiClient
import com.localattendance.teacherassistant.data.model.LoginResponse
import com.localattendance.teacherassistant.data.model.TeacherInfo
import com.localattendance.teacherassistant.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiClient.getInstance(application)
    private val repository = AuthRepository(apiService)

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _currentUser = MutableStateFlow<TeacherInfo?>(null)
    val currentUser: StateFlow<TeacherInfo?> = _currentUser

    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> = _loginResponse

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            repository.verifyAuth()
                .onSuccess { response ->
                    _isLoggedIn.value = response.authenticated
                    if (response.authenticated) {
                        loadCurrentUser()
                    }
                }
                .onFailure {
                    _isLoggedIn.value = false
                }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.login(username, password)
                .onSuccess { response ->
                    if (response.success) {
                        _isLoggedIn.value = true
                        _loginResponse.value = response
                        loadCurrentUser()
                    } else {
                        _error.value = "Login failed"
                    }
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Login failed"
                }

            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
                .onSuccess {
                    _isLoggedIn.value = false
                    _currentUser.value = null
                }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            repository.getCurrentTeacher()
                .onSuccess { teacher ->
                    _currentUser.value = teacher
                }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
