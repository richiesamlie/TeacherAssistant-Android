package com.localattendance.teacherassistant.data.repository

import com.localattendance.teacherassistant.data.api.ApiService
import com.localattendance.teacherassistant.data.model.*

class StudentRepository(private val apiService: ApiService) {

    suspend fun getStudents(classId: String, includeArchived: Boolean = false): Result<List<Student>> {
        return try {
            val response = apiService.getStudents(classId, includeArchived)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get students"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addStudent(classId: String, request: CreateStudentRequest): Result<ApiResponse> {
        return try {
            val response = apiService.addStudent(classId, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to add student"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudent(studentId: String, request: UpdateStudentRequest): Result<ApiResponse> {
        return try {
            val response = apiService.updateStudent(studentId, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update student"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(studentId: String): Result<ApiResponse> {
        return try {
            val response = apiService.deleteStudent(studentId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to delete student"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncStudents(classId: String, students: List<Student>): Result<ApiResponse> {
        return try {
            val response = apiService.syncStudents(classId, students)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to sync students"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
