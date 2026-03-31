package com.localattendance.teacherassistant.data.model

enum class AttendanceStatus(val value: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    SICK("Sick"),
    LATE("Late");

    companion object {
        fun fromString(value: String): AttendanceStatus {
            return entries.find { it.value == value } ?: PRESENT
        }
    }
}

data class AttendanceRecord(
    val studentId: String,
    val date: String,
    val status: String,
    val reason: String? = null
)

data class CreateAttendanceRequest(
    val studentId: String,
    val classId: String,
    val date: String,
    val status: String,
    val reason: String? = null
)

data class AttendanceQuery(
    val date: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val limit: Int? = null,
    val offset: Int? = null
)
