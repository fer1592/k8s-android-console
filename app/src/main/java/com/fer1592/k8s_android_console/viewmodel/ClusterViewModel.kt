package com.fer1592.k8s_android_console.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fer1592.k8s_android_console.R
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.repositoryimplementation.ClusterRepositoryImplementation
import com.fer1592.k8s_android_console.repository.ClusterRepository
import com.fer1592.k8s_android_console.util.EspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClusterViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {
    // Variable that holds the cluster in case of Edition
    var cluster: LiveData<Cluster>? = null
    // Variable holding the clusterRepository
    private var clusterRepository: ClusterRepository? = null
    var authMethods: List<String>? = null
    var clusterId: Long? = null

    // Live data used to navigate once the cluster has been created/updated
    private val _navigateToClusterList = MutableLiveData(false)
    val navigateToClusterList: LiveData<Boolean>
        get() = _navigateToClusterList

    // Live data that Indicates if the Bearer token field should be displayed
    private val _requestBearerToken = MutableLiveData(false)
    val requestBearerToken: LiveData<Boolean>
        get() = _requestBearerToken

    // Live data to show if connection test was successful
    private val _displayMessage = MutableLiveData<Int?>(null)
    val displayMessage: LiveData<Int?>
        get() = _displayMessage

    // Live data to show input data errors
    private val _isInputValid = MutableLiveData<Boolean?>(null)
    val isInputValid: LiveData<Boolean?>
        get() = _isInputValid

    // Live data to disable add/update button
    private val _processingData = MutableLiveData(false)
    val processingData: LiveData<Boolean>
        get() = _processingData

    // Function that init the viewModel
    fun getCluster(clusterId: Long, authMethods: List<String>, clusterRepository: ClusterRepository = ClusterRepositoryImplementation()) {
        this.clusterId = clusterId
        this.clusterRepository = clusterRepository
        this.authMethods = authMethods
        EspressoIdlingResource.increment()
        viewModelScope.launch(dispatcher) {
            _processingData.postValue(true)
            cluster = clusterRepository.getCluster(clusterId)
            _processingData.postValue(false)
            EspressoIdlingResource.decrement()
        }
    }

    // Function that creates a new cluster
    fun addCluster() {
        EspressoIdlingResource.increment()
        viewModelScope.launch(dispatcher) {
            _processingData.postValue(true)
            cluster?.value?.let {
                if (clusterRepository?.addCluster(it) == true) {
                    _isInputValid.postValue(true)
                    _navigateToClusterList.postValue(true)
                } else {
                    if (clusterRepository?.clusterIsValid(it) == true) _displayMessage.postValue(R.string.error_add_cluster)
                    else _isInputValid.postValue(false)
                }
            }
            _processingData.postValue(false)
            EspressoIdlingResource.decrement()
        }
    }

    // Function that updates an existing cluster
    fun updateCluster() {
        EspressoIdlingResource.increment()
        viewModelScope.launch(dispatcher) {
            _processingData.postValue(true)
            cluster?.value?.let {
                _processingData.postValue(true)
                if (clusterRepository?.updateCluster(it) == true) {
                    _isInputValid.postValue(true)
                    _navigateToClusterList.postValue(true)
                } else {
                    if (clusterRepository?.clusterIsValid(it) == true) _displayMessage.postValue(R.string.error_update_cluster)
                    else _isInputValid.postValue(false)
                }
            }
            _processingData.postValue(false)
            EspressoIdlingResource.decrement()
        }
    }

    // Function that indicates that fragment needs to navigate back to main screen
    fun onNavigatedToClusterList() {
        _navigateToClusterList.value = false
    }

    // Function that updates the authentication method based on the position received from the spinner
    fun setAuthMethod(authMethodPosition: Int) {
        when (authMethodPosition) {
            0 -> {
                _requestBearerToken.value = true
            }
        }
        cluster?.value?.let {
            it.clusterAuthenticationMethod = authMethods?.get(authMethodPosition) ?: "Bearer Token"
        }
    }

    fun testConnection() {
        EspressoIdlingResource.increment()
        viewModelScope.launch(dispatcher) {
            _processingData.postValue(true)
            cluster?.value?.let {
                val connectionResult = clusterRepository?.testClusterConnection(it)
                if (connectionResult == true) _displayMessage.postValue(R.string.connection_succeeded)
                else if (connectionResult == false) _displayMessage.postValue(R.string.connection_failed)
            }
            _processingData.postValue(false)
            EspressoIdlingResource.decrement()
        }
    }

    fun clearMessages() {
        _displayMessage.value = null
    }
}
