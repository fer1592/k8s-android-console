package com.example.k8s_android_console

import com.google.gson.annotations.SerializedName

data class KubernetesApiResponse (
    @SerializedName("kind") var kind : String,
    @SerializedName("versions") var versions : List<String>
)