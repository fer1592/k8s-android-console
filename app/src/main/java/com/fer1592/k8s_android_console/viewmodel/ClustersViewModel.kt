package com.fer1592.k8s_android_console.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fer1592.k8s_android_console.data.db.ClusterDAO
import com.fer1592.k8s_android_console.data.model.Cluster
import kotlinx.coroutines.launch

class ClustersViewModel(val dao: ClusterDAO) : ViewModel() {
    val clusters = dao.getAllClusters()
    private val _navigateToCluster = MutableLiveData<Long?>()
    val navigateToCluster: LiveData<Long?>
        get() = _navigateToCluster

    fun onClusterClicked(clusterId: Long){
        _navigateToCluster.value = clusterId
    }

    fun onClusterNavigated() {
        _navigateToCluster.value = null
    }

    // Function that deletes a cluster
    fun deleteCluster(cluster: Cluster){
        viewModelScope.launch {
            dao.delete(cluster)
        }
    }
}