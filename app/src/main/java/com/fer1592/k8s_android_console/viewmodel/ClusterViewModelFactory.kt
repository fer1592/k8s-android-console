package com.fer1592.k8s_android_console.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fer1592.k8s_android_console.data.db.ClusterDAO
import java.lang.IllegalArgumentException

class ClusterViewModelFactory(private val dao: ClusterDAO, private val clusterId: Long, private val authMethods: List<String>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClusterViewModel::class.java)){
            return ClusterViewModel(dao, clusterId, authMethods) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}