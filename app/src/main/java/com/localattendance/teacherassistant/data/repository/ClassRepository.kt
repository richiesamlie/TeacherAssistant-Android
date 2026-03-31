package com.localattendance.teacherassistant.data.repository

import com.localattendance.teacherassistant.data.api.ApiService
import com.localattendance.teacherassistant.data.model.*

class ClassRepository(private val apiService: ApiService) {

    suspend fun getClasses(): Result<List<ClassItem>> {
        return try {
            val response = apiService.getClasses()
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get classes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createClass(id: String, name: String): Result<ClassItem> {
        return try {
            val response = apiService.createClass(CreateClassRequest(id, name))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create class"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateClass(classId: String, name: String): Result<ApiResponse> {
        return try {
            val response = apiService.updateClass(classId, UpdateClassRequest(name))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update class"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteClass(classId: String): Result<ApiResponse> {
        return try {
            val response = apiService.deleteClass(classId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to delete class"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClassTeachers(classId: String): Result<List<ClassTeacher>> {
        return try {
            val response = apiService.getClassTeachers(classId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get class teachers"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addTeacherToClass(classId: String, teacherId: String): Result<ApiResponse> {
        return try {
            val response = apiService.addTeacherToClass(classId, AddTeacherRequest(teacherId))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to add teacher"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeTeacherFromClass(classId: String, teacherId: String): Result<ApiResponse> {
        return try {
            val response = apiService.removeTeacherFromClass(classId, teacherId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to remove teacher"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
