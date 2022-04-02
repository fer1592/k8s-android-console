package com.example.k8s_android_console.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.k8s_android_console.data.database.dao.ClusterDAO
import java.lang.IllegalArgumentException

class ClustersViewModelFactory(private val dao: ClusterDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClustersViewModel::class.java)){
            return ClustersViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}