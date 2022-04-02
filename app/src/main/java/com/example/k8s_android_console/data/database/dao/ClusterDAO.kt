package com.example.k8s_android_console.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.k8s_android_console.data.database.entities.ClusterEntity

@Dao
interface ClusterDAO {
    @Insert
    suspend fun insert(cluster: ClusterEntity)

    @Update
    suspend fun update(cluster: ClusterEntity)

    @Delete
    suspend fun delete(cluster: ClusterEntity)

    @Query("SELECT * FROM Cluster WHERE clusterId = :clusterId")
    fun getCluster(clusterId: Long): LiveData<ClusterEntity>

    @Query("SELECT * FROM Cluster ORDER BY clusterId ASC")
    fun getAllClusters(): LiveData<List<ClusterEntity>>
}