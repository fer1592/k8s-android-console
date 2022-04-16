package com.fer1592.k8s_android_console.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.repository.ClusterRepository
import com.fer1592.k8s_android_console.data.repository_implementation.ClusterRepositoryImplementation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClusterViewModel(val clusterId: Long, val authMethods: List<String>, private val clusterRepository: ClusterRepository = ClusterRepositoryImplementation()) : ViewModel()  {
    // Variable that holds the cluster in case of Edition
    var cluster : LiveData<Cluster> = clusterRepository.getCluster(clusterId)

    // Live data used to navigate once the cluster has been created/updated
    private val _navigateToClusterList = MutableLiveData(false)
    val navigateToClusterList: LiveData<Boolean>
        get() = _navigateToClusterList

    // Live data that Indicates if the Bearer token field should be displayed
    private val _requestBearerToken = MutableLiveData(false)
    val requestBearerToken: LiveData<Boolean>
        get() = _requestBearerToken

    // Live data to show if connection test was successful
    private val _connectionTestSuccessful = MutableLiveData<Boolean?>(null)
    val connectionTestSuccessful: LiveData<Boolean?>
        get() = _connectionTestSuccessful

    // Live data to show input data errors
    private val _isInputValid = MutableLiveData<Boolean?>(null)
    val isInputValid: LiveData<Boolean?>
        get() = _isInputValid

    // Function that creates a new cluster
    fun addCluster() {
        viewModelScope.launch(Dispatchers.IO) {
            cluster.value?.let {
                if (clusterRepository.addCluster(it)) {
                    _isInputValid.postValue(true)
                    _navigateToClusterList.postValue(true)
                } else _isInputValid.postValue(false)
            }
        }
    }

    // Function that updates an existing cluster
    fun updateCluster(){
        viewModelScope.launch(Dispatchers.IO) {
            cluster.value?.let {
                if (clusterRepository.updateCluster(it)) {
                    _isInputValid.postValue(true)
                    _navigateToClusterList.postValue(true)
                } else _isInputValid.postValue(false)
            }
        }
    }

    // Function that indicates that fragment needs to navigate back to main screen
    fun onNavigatedToClusterList(){
        _navigateToClusterList.value = false
    }

    // Function that updates the authentication method based on the position received from the spinner
    fun setAuthMethod(authMethodPosition: Int){
        when(authMethodPosition){
            0 -> {
                _requestBearerToken.value = true
            }
        }
        cluster.value?.let{
            it.clusterAuthenticationMethod = authMethods[authMethodPosition]
        }
    }

    fun testConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            cluster.value?.let {
                _connectionTestSuccessful.postValue(clusterRepository.testClusterConnection(it))
            }
        }
    }
}