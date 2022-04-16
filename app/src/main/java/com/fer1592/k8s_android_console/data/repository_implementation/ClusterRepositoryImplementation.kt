package com.fer1592.k8s_android_console.data.repository_implementation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fer1592.k8s_android_console.data.db.ClusterDAO
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.net.RetrofitClient
import com.fer1592.k8s_android_console.data.repository.ClusterRepository
import com.fer1592.k8s_android_console.db
import retrofit2.awaitResponse
import java.lang.Exception
import kotlin.concurrent.thread


class ClusterRepositoryImplementation : ClusterRepository {
    private val clusterDao: ClusterDAO = db.clusterDao()

    override fun getCluster(clusterId: Long): LiveData<Cluster> {
        return if (clusterId == -1L) MutableLiveData(Cluster())
        else clusterDao.getCluster(clusterId)
    }

    override fun addCluster(cluster: Cluster): Boolean {
        return if (cluster.isValid()){
            thread {
                clusterDao.insert(cluster)
            }
            true
        } else false
    }

    override fun updateCluster(cluster: Cluster): Boolean {
        return if (cluster.isValid()){
            thread {
                clusterDao.update(cluster)
            }
            true
        } else false
    }

    override fun deleteCluster(cluster: Cluster) {
        thread {
            clusterDao.delete(cluster)
        }
    }

    override suspend fun testClusterConnection(cluster: Cluster): Boolean {
        val retrofitClient = RetrofitClient(cluster.clusterAddress, cluster.clusterPort)
        val map = HashMap<String, String>()

        when(cluster.clusterAuthenticationMethod){
            "Bearer Token" -> map["Authorization"] = "Bearer ${cluster.clusterBearerToken}"
        }
        return try {
            retrofitClient.testCluster(map).awaitResponse().isSuccessful
        } catch (e: Exception){
            false
        }
    }

    override fun getAllClusters(): LiveData<List<Cluster>> {
        return clusterDao.getAllClusters()
    }


}