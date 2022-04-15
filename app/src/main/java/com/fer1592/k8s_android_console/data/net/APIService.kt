package com.fer1592.k8s_android_console.data.net

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap

interface APIService {
    @GET("/api")
    suspend fun testKubernetesApi(@HeaderMap headers: Map<String, String>) : Response<KubernetesApiResponse>
}