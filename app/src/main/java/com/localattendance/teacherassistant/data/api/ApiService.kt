package com.localattendance.teacherassistant.data.api

import com.localattendance.teacherassistant.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==================== AUTH ENDPOINTS ====================

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse>

    @GET("auth/verify")
    suspend fun verifyAuth(): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getCurrentTeacher(): Response<TeacherInfo>

    // ==================== TEACHER ENDPOINTS ====================

    @POST("teachers/register")
    suspend fun registerTeacher(@Body request: RegisterRequest): Response<Teacher>

    @GET("teachers")
    suspend fun getAllTeachers(): Response<List<Teacher>>

    // ==================== CLASS ENDPOINTS ====================

    @GET("classes")
    suspend fun getClasses(): Response<List<ClassItem>>

    @POST("classes")
    suspend fun createClass(@Body request: CreateClassRequest): Response<ClassItem>

    @PUT("classes/{classId}")
    suspend fun updateClass(
        @Path("classId") classId: String,
        @Body request: UpdateClassRequest
    ): Response<ApiResponse>

    @DELETE("classes/{classId}")
    suspend fun deleteClass(@Path("classId") classId: String): Response<ApiResponse>

    // ==================== CLASS TEACHERS ENDPOINTS ====================

    @GET("classes/{classId}/teachers")
    suspend fun getClassTeachers(@Path("classId") classId: String): Response<List<ClassTeacher>>

    @POST("classes/{classId}/teachers")
    suspend fun addTeacherToClass(
        @Path("classId") classId: String,
        @Body request: AddTeacherRequest
    ): Response<ApiResponse>

    @DELETE("classes/{classId}/teachers/{teacherId}")
    suspend fun removeTeacherFromClass(
        @Path("classId") classId: String,
        @Path("teacherId") teacherId: String
    ): Response<ApiResponse>

    // ==================== STUDENT ENDPOINTS ====================

    @GET("classes/{classId}/students")
    suspend fun getStudents(
        @Path("classId") classId: String,
        @Query("includeArchived") includeArchived: Boolean = false
    ): Response<List<Student>>

    @POST("classes/{classId}/students")
    suspend fun addStudent(
        @Path("classId") classId: String,
        @Body request: CreateStudentRequest
    ): Response<ApiResponse>

    @PUT("students/{studentId}")
    suspend fun updateStudent(
        @Path("studentId") studentId: String,
        @Body request: UpdateStudentRequest
    ): Response<ApiResponse>

    @DELETE("students/{studentId}")
    suspend fun deleteStudent(@Path("studentId") studentId: String): Response<ApiResponse>

    @POST("classes/{classId}/students/sync")
    suspend fun syncStudents(
        @Path("classId") classId: String,
        @Body students: List<Student>
    ): Response<ApiResponse>

    // ==================== ATTENDANCE ENDPOINTS ====================

    @GET("classes/{classId}/records")
    suspend fun getAttendanceRecords(
        @Path("classId") classId: String,
        @Query("date") date: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<List<AttendanceRecord>>

    @POST("records")
    suspend fun saveAttendanceRecords(
        @Body records: List<CreateAttendanceRequest>
    ): Response<ApiResponse>

    // ==================== DAILY NOTES ENDPOINTS ====================

    @GET("classes/{classId}/daily-notes")
    suspend fun getDailyNotes(
        @Path("classId") classId: String
    ): Response<Map<String, String>>

    @POST("classes/{classId}/daily-notes")
    suspend fun saveDailyNote(
        @Path("classId") classId: String,
        @Body request: CreateNoteRequest
    ): Response<ApiResponse>

    // ==================== EVENTS ENDPOINTS ====================

    @GET("classes/{classId}/events")
    suspend fun getEvents(
        @Path("classId") classId: String,
        @Query("type") type: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<List<CalendarEvent>>

    @POST("classes/{classId}/events")
    suspend fun createEvent(
        @Path("classId") classId: String,
        @Body request: CreateEventRequest
    ): Response<ApiResponse>

    @PUT("events/{eventId}")
    suspend fun updateEvent(
        @Path("eventId") eventId: String,
        @Body request: UpdateEventRequest
    ): Response<ApiResponse>

    @DELETE("events/{eventId}")
    suspend fun deleteEvent(@Path("eventId") eventId: String): Response<ApiResponse>

    // ==================== TIMETABLE ENDPOINTS ====================

    @GET("classes/{classId}/timetable")
    suspend fun getTimetable(@Path("classId") classId: String): Response<List<TimetableSlot>>

    @POST("classes/{classId}/timetable")
    suspend fun createTimetableSlot(
        @Path("classId") classId: String,
        @Body request: CreateTimetableRequest
    ): Response<ApiResponse>

    @PUT("timetable/{slotId}")
    suspend fun updateTimetableSlot(
        @Path("slotId") slotId: String,
        @Body request: UpdateTimetableRequest
    ): Response<ApiResponse>

    @DELETE("timetable/{slotId}")
    suspend fun deleteTimetableSlot(@Path("slotId") slotId: String): Response<ApiResponse>

    // ==================== SEATING ENDPOINTS ====================

    @GET("classes/{classId}/seating")
    suspend fun getSeatingLayout(@Path("classId") classId: String): Response<Map<String, String>>

    @POST("classes/{classId}/seating")
    suspend fun updateSeat(
        @Path("classId") classId: String,
        @Body request: UpdateSeatRequest
    ): Response<ApiResponse>

    @PUT("classes/{classId}/seating")
    suspend fun replaceSeatingLayout(
        @Path("classId") classId: String,
        @Body layout: Map<String, String>
    ): Response<ApiResponse>

    @DELETE("classes/{classId}/seating")
    suspend fun clearSeating(@Path("classId") classId: String): Response<ApiResponse>

    // ==================== SETTINGS ENDPOINTS ====================

    @GET("settings")
    suspend fun getSettings(): Response<Map<String, String>>

    @POST("settings")
    suspend fun saveSetting(@Body setting: Settings): Response<ApiResponse>
}
