package com.example.testapplication.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("api/v1/entities/getAllIds")
    fun getItems(): Call<ApiResponse<List<Data>>>

    @GET("api/v1/object/{id}")
    fun getObject(@Path("id") id: Int):Call<DataX>
}