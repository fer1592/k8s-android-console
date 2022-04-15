package com.fer1592.k8s_android_console.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cluster")
data class Cluster (
    @PrimaryKey(autoGenerate = true)
    var clusterId: Long = 0L,
    var clusterName: String = "",
    var clusterAddress: String = "",
    var clusterPort: Int = 8443,
    var clusterAuthenticationMethod: String = "",
    var clusterBearerToken: String = ""
)