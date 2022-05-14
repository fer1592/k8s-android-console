package com.fer1592.k8s_android_console.repository

import androidx.lifecycle.LiveData
import com.fer1592.k8s_android_console.data.model.Cluster

interface ClusterRepository {

    // Function to get all saved clusters
    fun getAllClusters(): LiveData<List<Cluster>>

    // Function to get a single cluster
    fun getCluster(clusterId: Long): LiveData<Cluster>

    // Function to add a new cluster
    suspend fun addCluster(cluster: Cluster): Boolean

    // Function to update a cluster
    suspend fun updateCluster(cluster: Cluster): Boolean

    // Function to delete a cluster
    suspend fun deleteCluster(cluster: Cluster): Boolean

    // Function to test cluster connection
    suspend fun testClusterConnection(cluster: Cluster): Boolean
}
