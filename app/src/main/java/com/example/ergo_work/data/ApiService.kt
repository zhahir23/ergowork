package com.example.ergo_work.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    fun login(@Body request: Map<String, String>): Call<ApiResponse<AuthResponse>>

    @POST("api/auth/register")
    fun register(@Body request: Map<String, String>): Call<ApiResponse<AuthResponse>>

    @POST("api/posture/telemetry")
    fun saveTelemetry(@Body request: PostureRequest): Call<ApiResponse<Any>>

    @GET("api/posture/history/{userId}")
    fun getHistory(@Path("userId") userId: Long): Call<ApiResponse<List<ErgoTelemetry>>>
}

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val username: String,
    val email: String,
    val fullName: String?
)

data class PostureRequest(
    val userId: Long,
    val ergoScore: Int,
    val neckAngle: Float,
    val shoulderSymmetry: Float,
    val isPostureSafe: Boolean,
    val errorDescription: String?
)
