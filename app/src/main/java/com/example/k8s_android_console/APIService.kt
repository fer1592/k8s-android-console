package com.example.k8s_android_console

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Url

interface APIService {
    @GET("/api")
    suspend fun testKubernetesApi(@HeaderMap headers: Map<String, String>) : Response<KubernetesApiResponse>
}