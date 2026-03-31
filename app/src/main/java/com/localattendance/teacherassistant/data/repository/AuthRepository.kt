package com.localattendance.teacherassistant.data.repository

import com.localattendance.teacherassistant.data.api.ApiService
import com.localattendance.teacherassistant.data.model.*

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<ApiResponse> {
        return try {
            val response = apiService.logout()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Logout failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyAuth(): Result<AuthResponse> {
        return try {
            val response = apiService.verifyAuth()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Auth verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentTeacher(): Result<TeacherInfo> {
        return try {
            val response = apiService.getCurrentTeacher()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get teacher info"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String, name: String): Result<Teacher> {
        return try {
            val response = apiService.registerTeacher(RegisterRequest(username, password, name))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
