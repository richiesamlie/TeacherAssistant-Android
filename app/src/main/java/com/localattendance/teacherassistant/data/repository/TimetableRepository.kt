package com.localattendance.teacherassistant.data.repository

import com.localattendance.teacherassistant.data.api.ApiService
import com.localattendance.teacherassistant.data.model.*

class TimetableRepository(private val apiService: ApiService) {

    suspend fun getTimetable(classId: String): Result<List<TimetableSlot>> {
        return try {
            val response = apiService.getTimetable(classId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get timetable"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSlot(classId: String, request: CreateTimetableRequest): Result<ApiResponse> {
        return try {
            val response = apiService.createTimetableSlot(classId, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create timetable slot"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSlot(slotId: String, request: UpdateTimetableRequest): Result<ApiResponse> {
        return try {
            val response = apiService.updateTimetableSlot(slotId, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update timetable slot"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSlot(slotId: String): Result<ApiResponse> {
        return try {
            val response = apiService.deleteTimetableSlot(slotId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to delete timetable slot"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
