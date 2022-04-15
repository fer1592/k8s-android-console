package com.fer1592.k8s_android_console.data.net

import com.google.gson.annotations.SerializedName

data class KubernetesApiResponse (
    @SerializedName("kind") var kind : String,
    @SerializedName("versions") var versions : List<String>
)