package com.example.k8s_android_console.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cluster")
data class ClusterEntity (
    @PrimaryKey(autoGenerate = true)
    var clusterId: Long = 0L,
    var clusterName: String = "",
    var clusterAddress: String = "",
    var clusterPort: Int = 8443,
    var clusterAuthenticationMethod: String = "",
    var clusterClientCa: String = "",
    var clusterClientKey: String = "",
    var clusterBearerToken: String = ""
)