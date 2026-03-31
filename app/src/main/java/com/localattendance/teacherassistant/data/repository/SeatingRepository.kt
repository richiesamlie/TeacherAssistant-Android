package com.localattendance.teacherassistant.data.repository

import com.localattendance.teacherassistant.data.api.ApiService
import com.localattendance.teacherassistant.data.model.*

class SeatingRepository(private val apiService: ApiService) {

    suspend fun getSeatingLayout(classId: String): Result<Map<String, String>> {
        return try {
            val response = apiService.getSeatingLayout(classId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get seating layout"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSeat(classId: String, seatId: String, studentId: String?): Result<ApiResponse> {
        return try {
            val response = apiService.updateSeat(classId, UpdateSeatRequest(seatId, studentId))
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update seat"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun replaceLayout(classId: String, layout: Map<String, String>): Result<ApiResponse> {
        return try {
            val response = apiService.replaceSeatingLayout(classId, layout)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to replace seating layout"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearSeating(classId: String): Result<ApiResponse> {
        return try {
            val response = apiService.clearSeating(classId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to clear seating"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
