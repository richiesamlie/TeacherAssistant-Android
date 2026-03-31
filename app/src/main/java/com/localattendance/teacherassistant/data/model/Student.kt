package com.localattendance.teacherassistant.data.model

data class Student(
    val id: String,
    val name: String,
    val rollNumber: String,
    val parentName: String? = null,
    val parentPhone: String? = null,
    val isFlagged: Boolean = false,
    val isArchived: Boolean = false
)

data class CreateStudentRequest(
    val id: String,
    val name: String,
    val rollNumber: String,
    val parentName: String? = null,
    val parentPhone: String? = null,
    val isFlagged: Boolean = false
)

data class UpdateStudentRequest(
    val name: String? = null,
    val rollNumber: String? = null,
    val parentName: String? = null,
    val parentPhone: String? = null,
    val isFlagged: Boolean? = null,
    val isArchived: Boolean? = null
)
