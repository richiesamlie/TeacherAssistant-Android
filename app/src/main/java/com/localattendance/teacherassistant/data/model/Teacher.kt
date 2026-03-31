package com.localattendance.teacherassistant.data.model

data class Teacher(
    val id: String,
    val username: String,
    val name: String,
    val created_at: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val teacherId: String? = null,
    val username: String? = null,
    val name: String? = null
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val name: String
)

data class AuthResponse(
    val authenticated: Boolean,
    val teacherId: String? = null
)

data class TeacherInfo(
    val id: String,
    val username: String,
    val name: String
)
