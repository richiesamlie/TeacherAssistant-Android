package com.localattendance.teacherassistant.data.model

data class TimetableSlot(
    val id: String,
    val class_id: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val lesson: String
)

data class CreateTimetableRequest(
    val id: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val lesson: String
)

data class UpdateTimetableRequest(
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val lesson: String
)
