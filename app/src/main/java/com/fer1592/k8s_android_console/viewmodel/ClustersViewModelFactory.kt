package com.fer1592.k8s_android_console.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fer1592.k8s_android_console.data.db.ClusterDAO
import java.lang.IllegalArgumentException

class ClustersViewModelFactory(private val dao: ClusterDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClustersViewModel::class.java)){
            return ClustersViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}