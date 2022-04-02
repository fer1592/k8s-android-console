package com.example.k8s_android_console.ui.viewmodel

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.k8s_android_console.data.database.dao.ClusterDAO
import com.example.k8s_android_console.data.database.entities.ClusterEntity
import kotlinx.coroutines.launch
import java.lang.Exception

class ClusterViewModel(private val dao: ClusterDAO, val clusterId: Long, val authMethods: List<String>) : ViewModel()  {
    // Variable that holds the cluster in case of Edition
    val cluster : LiveData<ClusterEntity> = dao.getCluster(clusterId)

    // Live data used to navigate once the cluster has been created/updated
    private val _navigateToClusterList = MutableLiveData(false)
    val navigateToClusterList: LiveData<Boolean>
        get() = _navigateToClusterList

    // Live data that Indicates if the Client Cert fields should be displayed
    private val _requestClusterClient = MutableLiveData(true)
    val requestClusterClient: LiveData<Boolean>
        get() = _requestClusterClient

    // Live data that Indicates if the Bearer token field should be displayed
    private val _requestBearerToken = MutableLiveData(false)
    val requestBearerToken: LiveData<Boolean>
        get() = _requestBearerToken

    // Live data to indicate if the cluster name is empty
    private val _clusterNameEmpty = MutableLiveData(false)
    val clusterNameEmpty: LiveData<Boolean>
        get() = _clusterNameEmpty

    // Live data to indicate if the cluster address is valid
    private val _clusterAddressInvalid = MutableLiveData(false)
    val clusterAddressInvalid: LiveData<Boolean>
        get() = _clusterAddressInvalid

    // Live data to indicate if the port is valid
    private val _clusterPortInvalid = MutableLiveData(false)
    val clusterPortInvalid: LiveData<Boolean>
        get() = _clusterPortInvalid

    // Live data to indicate if the Client Ca is not empty
    private val _clusterClientCaInvalid = MutableLiveData(false)
    val clusterClientCaInvalid: LiveData<Boolean>
        get() = _clusterClientCaInvalid

    // Live data to indicate if the Client Key is not empty
    private val _clusterClientKeyInvalid = MutableLiveData(false)
    val clusterClientKeyInvalid: LiveData<Boolean>
        get() = _clusterClientKeyInvalid

    // Live data to indicate if the bearer token is not empty
    private val _clusterBearerTokenEmpty = MutableLiveData(false)
    val clusterBearerTokenEmpty: LiveData<Boolean>
        get() = _clusterBearerTokenEmpty

    // Function that creates a new cluster
    fun addCluster(clusterName: String, clusterAddress: String, clusterPort: String, clusterAuthMethodIndex: Int, clusterClientCa: String,
                   clusterClientKey: String, clusterBearerToken: String) {
        if(validateInput(clusterName, clusterAddress, clusterPort.toInt(), authMethods[clusterAuthMethodIndex], clusterClientCa, clusterClientKey, clusterBearerToken)) {
            viewModelScope.launch {
                val newCluster = ClusterEntity(
                    clusterName = clusterName,
                    clusterAddress = clusterAddress,
                    clusterPort = clusterPort.toInt(),
                    clusterAuthenticationMethod = authMethods[clusterAuthMethodIndex],
                    clusterClientCa = clusterClientCa,
                    clusterClientKey = clusterClientKey,
                    clusterBearerToken = clusterBearerToken
                )
                dao.insert(newCluster)
                _navigateToClusterList.value = true
            }
        }
    }

    // Function that updates an existing cluster
    fun updateCluster(){
        if(validateInput(cluster.value!!.clusterName, cluster.value!!.clusterAddress, cluster.value!!.clusterPort, cluster.value!!.clusterAuthenticationMethod, cluster.value!!.clusterClientCa, cluster.value!!.clusterClientKey, cluster.value!!.clusterBearerToken)){
            viewModelScope.launch {
                dao.update(cluster.value!!)
                _navigateToClusterList.value = true
            }
        }
    }

    // Function that deletes a cluster
    fun deleteCluster(){
        viewModelScope.launch {
            dao.delete(cluster.value!!)
            _navigateToClusterList.value = true
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
                _requestClusterClient.value = true
                _requestBearerToken.value = false
            }
            1 -> {
                _requestClusterClient.value = false
                _requestBearerToken.value = true
            }
        }
        if(cluster.value != null){
            cluster.value!!.clusterAuthenticationMethod = authMethods[authMethodPosition]
        }
    }

    // Function that validates if the cluster can be updated
    private fun validateInput(clusterName: String, clusterAddress: String, clusterPort: Int, clusterAuthMethod: String, clusterClientCa: String, clusterClientKey: String, clusterBearerToken: String) : Boolean{
        var valid = true

        // Validate Cluster Name
        if (clusterName.isEmpty()) {
            valid = false
            _clusterNameEmpty.value = true
        } else _clusterNameEmpty.value = false

        // Validate Cluster Address
        val addressRegex = "(^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$)|(^[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)$)".toRegex()
        if (!addressRegex.matches(clusterAddress)){
            valid = false
            _clusterAddressInvalid.value = true
        } else _clusterAddressInvalid.value = false

        // Validate Cluster Port
        if(clusterPort !in 1..49151) {
            valid = false
            _clusterPortInvalid.value = true
        } else _clusterPortInvalid.value = false

        // Validate Auth Methods
        when(clusterAuthMethod){
            "Client Cert Auth" -> {
                _clusterBearerTokenEmpty.value = false
                val certRegex = "(?m)^-{3,}BEGIN CERTIFICATE-{3,}.*?-{3,}END CERTIFICATE-{3,}\$".toRegex()
                try {
                    val clientCaDecoded = String(Base64.decode(clusterClientCa, Base64.NO_WRAP), Charsets.UTF_8).replace("\n","")
                    if (!certRegex.matches(clientCaDecoded)) {
                        valid = false
                        _clusterClientCaInvalid.value = true
                    } else _clusterClientCaInvalid.value = false
                } catch (e: Exception) {
                    _clusterClientCaInvalid.value = true
                }

                val keyRegex = "(?m)^-{3,}BEGIN RSA PRIVATE KEY-{3,}.*?-{3,}END RSA PRIVATE KEY-{3,}\$".toRegex()
                try {
                    val clientKeyDecoded = String(Base64.decode(clusterClientKey, Base64.DEFAULT), Charsets.UTF_8).replace("\n","")
                    Log.i("Client Key","$clusterClientKey\n$clientKeyDecoded")
                    if(!keyRegex.matches(clientKeyDecoded)){
                        valid = false
                        _clusterClientKeyInvalid.value = true
                    } else _clusterClientKeyInvalid.value = false
                } catch (e: Exception) {
                    _clusterClientKeyInvalid.value = true
                }
            }
            "Bearer Token" -> {
                _clusterClientCaInvalid.value = false
                _clusterClientKeyInvalid.value = false
                if(clusterBearerToken.isEmpty()){
                    valid = false
                    _clusterBearerTokenEmpty.value = true
                } else _clusterBearerTokenEmpty.value = false
            }
        }

        return valid
    }
}