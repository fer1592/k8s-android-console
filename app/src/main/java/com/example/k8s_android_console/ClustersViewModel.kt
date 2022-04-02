package com.example.k8s_android_console

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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
}