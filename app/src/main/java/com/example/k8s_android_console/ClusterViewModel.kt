package com.example.k8s_android_console

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClusterViewModel(private val dao: ClusterDAO, val clusterId: Long, val authMethods: List<String>) : ViewModel()  {
    // Variable that holds the cluster in case of Edition
    val cluster : LiveData<Cluster> = dao.getCluster(clusterId)

    // Live data used to navigate once the cluster has been created/updated
    private val _navigateToClusterList = MutableLiveData(false)
    val navigateToClusterList: LiveData<Boolean>
        get() = _navigateToClusterList

    // Live data that Indicates if the user/password fields should be displayed
    private val _requestUsernamePassword = MutableLiveData(true)
    val requestUsernamePassword: LiveData<Boolean>
        get() = _requestUsernamePassword

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

    // Live data to indicate if the username is not empty
    private val _clusterUsernameEmpty = MutableLiveData(false)
    val clusterUsernameEmpty: LiveData<Boolean>
        get() = _clusterUsernameEmpty

    // Live data to indicate if the password is not empty
    private val _clusterPasswordEmpty = MutableLiveData(false)
    val clusterPasswordEmpty: LiveData<Boolean>
        get() = _clusterPasswordEmpty

    // Live data to indicate if the bearer token is not empty
    private val _clusterBearerTokenEmpty = MutableLiveData(false)
    val clusterBearerTokenEmpty: LiveData<Boolean>
        get() = _clusterBearerTokenEmpty

    //private val _clusterValidations = MutableLiveData(mutableMapOf(
    //     "clusterAddressFormat" to false,
    //    "clusterPortEmpty" to false, "clusterPortMinMax" to false, "clusterUsernameEmpty" to false, "clusterUsernameLength" to false,
    //    "clusterPasswordEmpty" to false, "clusterPasswordLength" to false, "clusterBearerTokenEmpty" to false, "clusterBearerTokenLength" to false,)
    //)
    //val clusterValidations: LiveData<MutableMap<String, Boolean>>
    //    get() = _clusterValidations

    // Function that creates a new cluster
    fun addCluster(clusterName: String, clusterAddress: String, clusterPort: String, clusterAuthMethodIndex: Int, clusterUsername: String,
                   clusterPassword: String, clusterBearerToken: String) {
        if(validateInput(clusterName, clusterAddress, clusterPort.toInt(), authMethods[clusterAuthMethodIndex], clusterUsername, clusterPassword, clusterBearerToken)) {
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
    }

    // Function that updates an existing cluster
    fun updateCluster(){
        if(validateInput(cluster.value!!.clusterName, cluster.value!!.clusterAddress, cluster.value!!.clusterPort, cluster.value!!.clusterAuthenticationMethod, cluster.value!!.clusterUsername, cluster.value!!.clusterPassword, cluster.value!!.clusterBearerToken)){
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
                _requestUsernamePassword.value = true
                _requestBearerToken.value = false
            }
            1 -> {
                _requestUsernamePassword.value = false
                _requestBearerToken.value = true
            }
        }
        if(cluster.value != null){
            cluster.value!!.clusterAuthenticationMethod = authMethods[authMethodPosition]
        }
    }

    // Function that validates if the cluster can be updated
    private fun validateInput(clusterName: String, clusterAddress: String, clusterPort: Int, clusterAuthMethod: String, clusterUsername: String, clusterPassword: String, clusterBearerToken: String) : Boolean{
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
            "Basic Auth" -> {
                _clusterBearerTokenEmpty.value = false
                if(clusterUsername.isEmpty()){
                    valid = false
                    _clusterUsernameEmpty.value = true
                } else _clusterUsernameEmpty.value = false
                if(clusterPassword.isEmpty()){
                    valid = false
                    _clusterPasswordEmpty.value = true
                } else _clusterPasswordEmpty.value = false
            }
            "Bearer Token" -> {
                _clusterUsernameEmpty.value = false
                _clusterPasswordEmpty.value = false
                if(clusterBearerToken.isEmpty()){
                    valid = false
                    _clusterBearerTokenEmpty.value = true
                } else _clusterBearerTokenEmpty.value = false
            }
        }

        return valid
    }
}