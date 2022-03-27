package com.example.k8s_android_console

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClusterViewModel(val dao: ClusterDAO, val clusterId: Long) : ViewModel()  {
    val cluster : LiveData<Cluster> = dao.getCluster(clusterId)
    private val _navigateToClusterList = MutableLiveData<Boolean>(false)
    val navigateToClusterList: LiveData<Boolean>
        get() = _navigateToClusterList

    fun addCluster(clusterName: String, clusterAddress: String, clusterUsername: String, clusterPassword: String) {
        Log.i("Fruta", "$clusterName $clusterAddress $clusterUsername $clusterPassword")
        viewModelScope.launch {
            val newCluster = Cluster(
                clusterName = clusterName,
                clusterAddress = clusterAddress,
                clusterUsername = clusterUsername,
                clusterPassword = clusterPassword
            )
            dao.insert(newCluster)
            _navigateToClusterList.value = true
        }
    }

    fun updateCluster(){
        viewModelScope.launch {
            dao.update(cluster.value!!)
            _navigateToClusterList.value = true
        }
    }

    fun deleteCluster(){
        viewModelScope.launch {
            dao.delete(cluster.value!!)
            _navigateToClusterList.value = true
        }
    }

    fun onNavigatedToClusterList(){
        _navigateToClusterList.value = false
    }
}