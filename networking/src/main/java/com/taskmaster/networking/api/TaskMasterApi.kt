package com.taskmaster.networking.api

import com.taskmaster.core.domain.model.*
import retrofit2.http.*

interface TaskMasterApi {
    
    // Auth endpoints
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<User>
    
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<User>
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<String>
    
    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>
    
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: Map<String, String>): ApiResponse<Unit>
    
    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: Map<String, String>): ApiResponse<Unit>
    
    @POST("auth/change-password")
    suspend fun changePassword(@Body request: Map<String, String>): ApiResponse<Unit>
    
    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: Map<String, String>): ApiResponse<Unit>
    
    // User endpoints
    @GET("user/profile")
    suspend fun getCurrentUser(): ApiResponse<User>
    
    @PUT("user/profile")
    suspend fun updateUser(@Body user: User): ApiResponse<User>
    
    @DELETE("user/profile")
    suspend fun deleteUser(): ApiResponse<Unit>
    
    @PUT("user/profile/update")
    suspend fun updateUserProfile(@Body request: Map<String, String?>): ApiResponse<User>
    
    // Task endpoints
    @GET("tasks")
    suspend fun getTasks(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PaginatedResponse<Task>>
    
    @GET("tasks/{id}")
    suspend fun getTaskById(@Path("id") id: String): ApiResponse<Task>
    
    @POST("tasks")
    suspend fun createTask(@Body request: CreateTaskRequest): ApiResponse<Task>
    
    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body request: UpdateTaskRequest): ApiResponse<Task>
    
    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): ApiResponse<Unit>
    
    @GET("tasks/status/{status}")
    suspend fun getTasksByStatus(@Path("status") status: String): ApiResponse<List<Task>>
    
    @GET("tasks/category/{category}")
    suspend fun getTasksByCategory(@Path("category") category: String): ApiResponse<List<Task>>
    
    @GET("tasks/priority/{priority}")
    suspend fun getTasksByPriority(@Path("priority") priority: String): ApiResponse<List<Task>>
    
    @GET("tasks/search")
    suspend fun searchTasks(@Query("q") query: String): ApiResponse<List<Task>>
    
    @GET("tasks/date-range")
    suspend fun getTasksByDateRange(
        @Query("startDate") startDate: Long,
        @Query("endDate") endDate: Long
    ): ApiResponse<List<Task>>
    
    @GET("tasks/statistics")
    suspend fun getTaskStatistics(): ApiResponse<TaskStatistics>
    
    // Project endpoints
    @GET("projects")
    suspend fun getProjects(): ApiResponse<List<Project>>
    
    @GET("projects/{id}")
    suspend fun getProjectById(@Path("id") id: String): ApiResponse<Project>
    
    @POST("projects")
    suspend fun createProject(@Body project: Project): ApiResponse<Project>
    
    @PUT("projects/{id}")
    suspend fun updateProject(@Path("id") id: String, @Body project: Project): ApiResponse<Project>
    
    @DELETE("projects/{id}")
    suspend fun deleteProject(@Path("id") id: String): ApiResponse<Unit>
}