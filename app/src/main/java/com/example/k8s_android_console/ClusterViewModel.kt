package com.example.k8s_android_console

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.security.KeyFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import javax.net.ssl.*

class ClusterViewModel(private val dao: ClusterDAO, val clusterId: Long, val authMethods: List<String>) : ViewModel()  {
    // Variable that holds the cluster in case of Edition
    val cluster : LiveData<Cluster> = dao.getCluster(clusterId)

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

    // Live data to show if connection test was successful
    private val _connectionTestSuccessful = MutableLiveData<Boolean?>(null)
    val connectionTestSuccessful: LiveData<Boolean?>
        get() = _connectionTestSuccessful

    // Function that creates a new cluster
    fun addCluster(clusterName: String, clusterAddress: String, clusterPort: String, clusterAuthMethodIndex: Int, clusterClientCa: String,
                   clusterClientKey: String, clusterBearerToken: String) {
        if(validateInput(clusterName, clusterAddress, clusterPort.toInt(), authMethods[clusterAuthMethodIndex], clusterClientCa, clusterClientKey, clusterBearerToken)) {
            viewModelScope.launch {
                val newCluster = Cluster(
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
        val addressRegex = "(^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$)|(^[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b\$)".toRegex()
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

    fun testConnection(clusterAddress: String, clusterPort: String, clusterAuthMethodIndex: Int, clusterClientCa: String, clusterClientKey: String, clusterBearerToken: String){
        CoroutineScope(Dispatchers.IO).launch {
            val map = HashMap<String, String>()
            when(authMethods[clusterAuthMethodIndex]){
                "Client Cert Auth" -> {
                    try {
                        val certificateFactory = CertificateFactory.getInstance("X.509")
                        val certificateInputStream: InputStream = String(Base64.decode(clusterClientCa, Base64.NO_WRAP), Charsets.UTF_8).byteInputStream()
                        val trustedCertificate = certificateFactory.generateCertificate(certificateInputStream)
                        //val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                        //keyStore.load(null,"secret".toCharArray())
                        //keyStore.setCertificateEntry(clusterAddress,trustedCertificate)

                        val privateKeyContent = String(Base64.decode(clusterClientKey, Base64.DEFAULT), Charsets.UTF_8).replace("\n","")
                            .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                            .replace("\n","")
                            .replace("-----END RSA PRIVATE KEY-----", "")

                        val privateKeyAsBytes = Base64.decode(privateKeyContent, Base64.DEFAULT)
                        val keyFactory = KeyFactory.getInstance("RSA")
                        val keySpec = PKCS8EncodedKeySpec(privateKeyAsBytes)

                        val identityStore = KeyStore.getInstance(KeyStore.getDefaultType())
                        identityStore.load(null, "secret".toCharArray())
                        identityStore.setKeyEntry("client", keyFactory.generatePrivate(keySpec),"".toCharArray(),arrayOf(trustedCertificate))

                        val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                        trustManagerFactory.init(null as KeyStore?)
                        val trustManagers = trustManagerFactory.trustManagers

                        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
                        keyManagerFactory.init(identityStore,"secret".toCharArray())
                        val keyManagers = keyManagerFactory.keyManagers

                        val sslContext = SSLContext.getInstance("TLS")
                        sslContext.init(keyManagers, trustManagers, null)
                        val sslSocketFactory = sslContext.socketFactory

                        val okHttpClient = OkHttpClient.Builder()
                            .sslSocketFactory(sslSocketFactory, trustManagers[0] as X509TrustManager)
                            .build()

                        val json = GsonBuilder().setLenient().create()

                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://$clusterAddress:$clusterPort/")
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create(json))
                            .build()
                        val call = retrofit.create(APIService::class.java).testKubernetesApi(map)

                        Log.i("API call", "$call")
                    } catch (e: Exception) {
                        Log.i("API call", "$e")
                        _connectionTestSuccessful.postValue(false)
                    }
                }
                "Bearer Token" -> {
                    try {
                        map["Authorization"] = "Bearer $clusterBearerToken"
                        val call = getRetrofit(clusterAddress, clusterPort).create(APIService::class.java).testKubernetesApi(map)
                        Log.i("API call", "$call")
                        _connectionTestSuccessful.postValue(call.isSuccessful)
                    } catch (e: Exception){
                        _connectionTestSuccessful.postValue(false)
                    }
                }
            }
        }
    }

    private fun getRetrofit(clusterAddress: String, clusterPort: String) : Retrofit {
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
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?>? {
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
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })
            builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}