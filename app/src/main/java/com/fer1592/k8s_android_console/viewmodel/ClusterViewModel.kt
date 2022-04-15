package com.fer1592.k8s_android_console.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fer1592.k8s_android_console.data.db.ClusterDAO
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.net.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

class ClusterViewModel(private val dao: ClusterDAO, val clusterId: Long, val authMethods: List<String>) : ViewModel()  {
    // Variable that holds the cluster in case of Edition
    var cluster : LiveData<Cluster> = if (clusterId == -1L) MutableLiveData(Cluster())
    else dao.getCluster(clusterId)

    // Live data used to navigate once the cluster has been created/updated
    private val _navigateToClusterList = MutableLiveData(false)
    val navigateToClusterList: LiveData<Boolean>
        get() = _navigateToClusterList

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

    // Live data to indicate if the bearer token is not empty
    private val _clusterBearerTokenEmpty = MutableLiveData(false)
    val clusterBearerTokenEmpty: LiveData<Boolean>
        get() = _clusterBearerTokenEmpty

    // Live data to show if connection test was successful
    private val _connectionTestSuccessful = MutableLiveData<Boolean?>(null)
    val connectionTestSuccessful: LiveData<Boolean?>
        get() = _connectionTestSuccessful

    // Function that creates a new cluster
    fun addCluster() {
        if(validateInput()) {
            viewModelScope.launch {
                dao.insert(cluster.value!!)
                _navigateToClusterList.value = true
            }
        }
    }

    // Function that updates an existing cluster
    fun updateCluster(){
        if(validateInput()){
            viewModelScope.launch {
                dao.update(cluster.value!!)
                _navigateToClusterList.value = true
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

    // Function that validates if the cluster can be updated
    private fun validateInput() : Boolean{
        var valid = true
        cluster.value?.let {
            if (it.clusterName.isEmpty()) {
                valid = false
                _clusterNameEmpty.value = true
            } else _clusterNameEmpty.value = false

            // Validate Cluster Address
            val addressRegex = "(^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$)|(^[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b\$)".toRegex()
            if (!addressRegex.matches(it.clusterAddress)){
                valid = false
                _clusterAddressInvalid.value = true
            } else _clusterAddressInvalid.value = false

            // Validate Cluster Port
            if(it.clusterPort !in 1..49151) {
                valid = false
                _clusterPortInvalid.value = true
            } else _clusterPortInvalid.value = false

            // Validate Auth Methods
            when(it.clusterAuthenticationMethod){
                "Bearer Token" -> {
                    if(it.clusterBearerToken.isEmpty()){
                        valid = false
                        _clusterBearerTokenEmpty.value = true
                    } else _clusterBearerTokenEmpty.value = false
                }
            }
        }
        return valid
    }

    fun testConnection(){
        CoroutineScope(Dispatchers.IO).launch {
            cluster.value?.let {
                val map = HashMap<String, String>()
                when(it.clusterAuthenticationMethod){
                    "Bearer Token" -> {
                        try {
                            map["Authorization"] = "Bearer ${it.clusterBearerToken}"
                            val call = getRetrofit(it.clusterAddress, it.clusterPort).create(
                                APIService::class.java).testKubernetesApi(map)
                            _connectionTestSuccessful.postValue(call.isSuccessful)
                        } catch (e: Exception){
                            _connectionTestSuccessful.postValue(false)
                        }
                    }
                }
            }
        }
    }

    private fun getRetrofit(clusterAddress: String, clusterPort: Int) : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://$clusterAddress:$clusterPort/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(getUnsafeOkHttpClient())
            .build()
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOf()
                    }
                }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val trustManagerFactory: TrustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers: Array<TrustManager> =
                trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                "Unexpected default trust managers:" + trustManagers.contentToString()
            }

            val trustManager =
                trustManagers[0] as X509TrustManager

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustManager)
            builder.hostnameVerifier { _, _ -> true }
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}