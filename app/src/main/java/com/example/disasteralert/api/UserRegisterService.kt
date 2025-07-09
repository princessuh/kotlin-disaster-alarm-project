package com.example.disasteralert.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserRegisterService {
    @POST("devices/register")
    fun registerUser(@Body request: UserRegisterRequest): Call<Void>
}
