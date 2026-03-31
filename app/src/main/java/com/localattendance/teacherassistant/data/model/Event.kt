package com.localattendance.teacherassistant.data.model

enum class EventType(val value: String) {
    CLASSWORK("Classwork"),
    TEST("Test"),
    EXAM("Exam"),
    HOLIDAY("Holiday"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): EventType {
            return entries.find { it.value == value } ?: OTHER
        }
    }
}

data class CalendarEvent(
    val id: String,
    val class_id: String,
    val date: String,
    val title: String,
    val type: String,
    val description: String? = null
)

data class CreateEventRequest(
    val id: String,
    val date: String,
    val title: String,
    val type: String,
    val description: String? = null
)

data class UpdateEventRequest(
    val date: String,
    val title: String,
    val type: String,
    val description: String? = null
)
