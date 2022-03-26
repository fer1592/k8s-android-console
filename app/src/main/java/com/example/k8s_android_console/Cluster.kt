package com.example.k8s_android_console

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cluster")
data class Cluster (
    @PrimaryKey(autoGenerate = true)
    var clusterId: Long = 0L,
    var clusterName: String = "",
    var clusterAddress: String = "",
    var clusterAuthenticationMethod: String = "",
    var clusterUsername: String = "",
    var clusterPassword: String = ""
)