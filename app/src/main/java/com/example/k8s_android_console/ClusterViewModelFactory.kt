package com.example.k8s_android_console

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ClusterViewModelFactory(private val dao: ClusterDAO, private val clusterId: Long) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClusterViewModel::class.java)){
            return ClusterViewModel(dao,clusterId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}