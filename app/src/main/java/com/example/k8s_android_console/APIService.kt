package com.example.k8s_android_console

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun testKubernetesApi(@Url url: String) : Response<KubernetesApiResponse>
}