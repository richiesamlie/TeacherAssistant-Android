package com.localattendance.teacherassistant.data.repository

import com.localattendance.teacherassistant.data.api.ApiService
import com.localattendance.teacherassistant.data.model.*

class AttendanceRepository(private val apiService: ApiService) {

    suspend fun getAttendanceRecords(
        classId: String,
        date: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Result<List<AttendanceRecord>> {
        return try {
            val response = apiService.getAttendanceRecords(classId, date, startDate, endDate)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get attendance records"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveAttendanceRecords(records: List<CreateAttendanceRequest>): Result<ApiResponse> {
        return try {
            val response = apiService.saveAttendanceRecords(records)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to save attendance"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDailyNotes(classId: String): Result<Map<String, String>> {
        return try {
            val response = apiService.getDailyNotes(classId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get daily notes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveDailyNote(classId: String, date: String, note: String): Result<ApiResponse> {
        return try {
            val response = apiService.saveDailyNote(classId, CreateNoteRequest(date, note))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to save daily note"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
