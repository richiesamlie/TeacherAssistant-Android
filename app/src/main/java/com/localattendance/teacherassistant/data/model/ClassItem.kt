package com.localattendance.teacherassistant.data.model

data class ClassItem(
    val id: String,
    val teacher_id: String,
    val name: String,
    val owner_name: String? = null
)

data class CreateClassRequest(
    val id: String,
    val name: String
)

data class UpdateClassRequest(
    val name: String
)

data class ClassTeacher(
    val teacher_id: String,
    val role: String,
    val username: String,
    val name: String
)

data class AddTeacherRequest(
    val teacherId: String
)
