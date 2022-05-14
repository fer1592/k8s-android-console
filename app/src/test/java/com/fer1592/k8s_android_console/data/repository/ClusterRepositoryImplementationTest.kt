package com.fer1592.k8s_android_console.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fer1592.k8s_android_console.data.db.ClusterDAO
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.repository_implementation.ClusterRepositoryImplementation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ClusterRepositoryImplementationTest {
    private lateinit var clusterRepository: ClusterRepositoryImplementation
    private lateinit var cluster: Cluster

    @Mock
    lateinit var clusterDao: ClusterDAO

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = UnconfinedTestDispatcher()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        clusterDao = Mockito.mock(ClusterDAO::class.java)
        clusterRepository = ClusterRepositoryImplementation(clusterDao)

        val commandResult = Runtime.getRuntime().exec("microk8s config").inputStream.bufferedReader().readLines()
        val address = commandResult.find { item ->
            item.contains(".*server: https://.*".toRegex())
        }?.replace(" ", "")?.replace("server:", "")?.replace("https://", "")?.split(":")
        val token = commandResult.find { item ->
            item.contains(".*token:.*".toRegex())
        }?.replace(" ", "")?.replace("token:", "")
        cluster = Cluster(0, "Test Cluster", address?.get(0) ?: "192.168.1.1", address?.get(1)?.toInt() ?: 8443, "Bearer Token", token ?: "TestToken")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test connection to kubernetes`() = runTest {
        val result = clusterRepository.testClusterConnection(cluster)
        Assert.assertTrue("Connection with kubernetes failed", result)
    }
}
