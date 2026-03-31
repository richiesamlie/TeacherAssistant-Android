package com.localattendance.teacherassistant.data.model

data class DailyNote(
    val classId: String,
    val date: String,
    val note: String
)

data class CreateNoteRequest(
    val date: String,
    val note: String
)

data class SeatingSeat(
    val seatId: String,
    val studentId: String? = null
)

data class UpdateSeatRequest(
    val seatId: String,
    val studentId: String? = null
)

data class Settings(
    val key: String,
    val value: String
)

data class ApiResponse(
    val success: Boolean,
    val message: String? = null
)
