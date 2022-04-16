package com.fer1592.k8s_android_console.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fer1592.k8s_android_console.data.model.Cluster

@Dao
interface ClusterDAO {
    @Insert
    suspend fun insert(cluster: Cluster)

    @Update
    suspend fun update(cluster: Cluster)

    @Delete
    suspend fun delete(cluster: Cluster)

    @Query("SELECT * FROM Cluster WHERE clusterId = :clusterId")
    fun getCluster(clusterId: Long): LiveData<Cluster>

    @Query("SELECT * FROM Cluster ORDER BY clusterId ASC")
    fun getAllClusters(): LiveData<List<Cluster>>
}