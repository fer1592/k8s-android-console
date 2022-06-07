package com.fer1592.k8s_android_console.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.repository_implementation.ClusterRepositoryImplementation
import com.fer1592.k8s_android_console.repository.ClusterRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClustersViewModel(private val clusterRepository: ClusterRepository = ClusterRepositoryImplementation(), private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {
    val clusters = clusterRepository.getAllClusters()

    // Live data that indicates navigation to setClusterFragment
    private val _navigateToEditCluster = MutableLiveData<Long?>()
    val navigateToEditCluster: LiveData<Long?>
        get() = _navigateToEditCluster

    // Live data to disable UI when deleting a cluster
    private val _processingData = MutableLiveData(false)
    val processingData: LiveData<Boolean>
        get() = _processingData

    fun onClusterEditClicked(clusterId: Long) {
        _navigateToEditCluster.value = clusterId
    }

    fun onClusterEditNavigated() {
        _navigateToEditCluster.value = null
    }

    // Function that deletes a cluster
    fun deleteCluster(cluster: Cluster) {
        viewModelScope.launch(dispatcher) {
            _processingData.postValue(true)
            clusterRepository.deleteCluster(cluster)
            _processingData.postValue(false)
        }
    }
}
