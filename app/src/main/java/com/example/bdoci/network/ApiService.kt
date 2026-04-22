package com.example.bdoci.network

import com.example.bdoci.models.Doc
import retrofit2.http.GET

interface ApiService {
    @GET("api/data")
    suspend fun getAllDocs(): List<Doc>
}