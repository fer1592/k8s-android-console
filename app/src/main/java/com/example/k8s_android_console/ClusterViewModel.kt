package com.example.k8s_android_console

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClusterViewModel(private val dao: ClusterDAO, val clusterId: Long, val authMethods: List<String>) : ViewModel()  {
    val cluster : LiveData<Cluster> = dao.getCluster(clusterId)
    private val _navigateToClusterList = MutableLiveData<Boolean>(false)
    val navigateToClusterList: LiveData<Boolean>
        get() = _navigateToClusterList
    private val _requestUsernamePassword = MutableLiveData<Boolean>(true)
    val requestUsernamePassword: LiveData<Boolean>
        get() = _requestUsernamePassword
    private val _requestBearerToken = MutableLiveData<Boolean>(false)
    val requestBearerToken: LiveData<Boolean>
        get() = _requestBearerToken
    fun addCluster(clusterName: String, clusterAddress: String, clusterPort: String, clusterAuthMethodIndex: Int, clusterUsername: String,
                   clusterPassword: String, clusterBearerToken: String) {
        viewModelScope.launch {
            val newCluster = Cluster(
                clusterName = clusterName,
                clusterAddress = clusterAddress,
                clusterPort = clusterPort.toInt(),
                clusterAuthenticationMethod = authMethods[clusterAuthMethodIndex],
                clusterUsername = clusterUsername,
                clusterPassword = clusterPassword,
                clusterBearerToken = clusterBearerToken
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

    fun setAuthMethod(authMethodPosition: Int){
        when(authMethodPosition){
            0 -> {
                _requestUsernamePassword.value = true
                _requestBearerToken.value = false
            }
            1 -> {
                _requestUsernamePassword.value = false
                _requestBearerToken.value = true
            }
        }
        Log.i("Fruta", "AuthMethodPos: ${authMethodPosition} usernamePass: ${requestUsernamePassword} Bearer: ${requestBearerToken}")
        if(cluster.value != null){
            cluster.value!!.clusterAuthenticationMethod = authMethods[authMethodPosition]
        }
    }
}