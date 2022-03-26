package com.example.k8s_android_console

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClustersViewModel(val dao: ClusterDAO) : ViewModel() {
    val clusters = dao.getAllClusters()
}