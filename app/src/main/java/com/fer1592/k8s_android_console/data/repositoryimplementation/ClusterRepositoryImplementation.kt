package com.fer1592.k8s_android_console.data.repositoryimplementation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fer1592.k8s_android_console.data.db.ClusterDAO
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.net.RetrofitClient
import com.fer1592.k8s_android_console.db
import com.fer1592.k8s_android_console.repository.ClusterRepository
import retrofit2.awaitResponse
import kotlin.Exception

class ClusterRepositoryImplementation(private val clusterDao: ClusterDAO = db.clusterDao()) : ClusterRepository {

    override suspend fun getCluster(clusterId: Long): LiveData<Cluster> {
        return if (clusterId == -1L) MutableLiveData(Cluster())
        else clusterDao.getCluster(clusterId)
    }

    override fun clusterIsValid(cluster: Cluster): Boolean {
        return cluster.isValid()
    }

    override suspend fun addCluster(cluster: Cluster): Boolean {
        return if (cluster.isValid()) {
            try {
                clusterDao.insert(cluster)
                true
            } catch (e: Exception) {
                false
            }
        } else false
    }

    override suspend fun updateCluster(cluster: Cluster): Boolean {
        return if (cluster.isValid()) {
            try {
                clusterDao.update(cluster)
                true
            } catch (e: Exception) {
                false
            }
        } else false
    }

    override suspend fun deleteCluster(cluster: Cluster): Boolean {
        return try {
            (clusterDao.delete(cluster) != 0)
        } catch (e: Exception) {
            false
        }
    }

    // function to be used only on UI tests
    override suspend fun cleanUpClusters() {
        clusterDao.deleteAllClusters()
    }

    override suspend fun testClusterConnection(cluster: Cluster): Boolean {
        return if (cluster.clusterAddress.isNotEmpty() and (cluster.clusterPort in 1..49151)) {
            val retrofitClient = RetrofitClient(cluster.clusterAddress, cluster.clusterPort)
            val map = HashMap<String, String>()

            when (cluster.clusterAuthenticationMethod) {
                "Bearer Token" -> map["Authorization"] = "Bearer ${cluster.clusterBearerToken}"
            }
            try {
                retrofitClient.testCluster(map).awaitResponse().isSuccessful
            } catch (e: Exception) {
                false
            }
        } else false
    }

    override fun getAllClusters(): LiveData<List<Cluster>> {
        return clusterDao.getAllClusters()
    }
}
