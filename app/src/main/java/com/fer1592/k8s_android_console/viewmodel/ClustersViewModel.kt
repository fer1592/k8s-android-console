package com.fer1592.k8s_android_console.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.repository.ClusterRepository
import com.fer1592.k8s_android_console.data.repository_implementation.ClusterRepositoryImplementation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClustersViewModel(private val clusterRepository: ClusterRepository = ClusterRepositoryImplementation()) : ViewModel() {
    val clusters = clusterRepository.getAllClusters()
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
        viewModelScope.launch(Dispatchers.IO) {
            clusterRepository.deleteCluster(cluster)
        }
    }
}