package com.localattendance.teacherassistant.data.repository

import com.localattendance.teacherassistant.data.api.ApiService
import com.localattendance.teacherassistant.data.model.*

class EventRepository(private val apiService: ApiService) {

    suspend fun getEvents(
        classId: String,
        type: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Result<List<CalendarEvent>> {
        return try {
            val response = apiService.getEvents(classId, type, startDate, endDate)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get events"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(classId: String, request: CreateEventRequest): Result<ApiResponse> {
        return try {
            val response = apiService.createEvent(classId, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvent(eventId: String, request: UpdateEventRequest): Result<ApiResponse> {
        return try {
            val response = apiService.updateEvent(eventId, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(eventId: String): Result<ApiResponse> {
        return try {
            val response = apiService.deleteEvent(eventId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to delete event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
