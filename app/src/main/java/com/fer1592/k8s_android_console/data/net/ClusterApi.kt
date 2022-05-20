package com.fer1592.k8s_android_console.data.net

import com.fer1592.k8s_android_console.data.model.ClusterResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap

interface ClusterApi {
    @GET("/api")
    fun testClusterApi(@HeaderMap headers: Map<String, String>): Call<ClusterResponse>
}
