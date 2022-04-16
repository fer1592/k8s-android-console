package com.fer1592.k8s_android_console.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.repository.ClusterRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ClusterViewModelTest{
        // Initialize instantExecutorRule to run all in the same thread
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: ClusterRepository

    @ExperimentalCoroutinesApi
    @Test
    fun addCluster() = runTest {
        `when`(repository.getCluster(-1L)).thenReturn(MutableLiveData(Cluster()))
        val clusterViewModel = ClusterViewModel(-1L, listOf("Bearer Token"),repository)
        clusterViewModel.cluster.value?.clusterName = "Test Cluster"
        clusterViewModel.cluster.value?.clusterAddress = "192.168.1.1"
        clusterViewModel.cluster.value?.clusterPort = 16443
        clusterViewModel.cluster.value?.clusterAuthenticationMethod = "Bearer Token"
        clusterViewModel.cluster.value?.clusterBearerToken = "testBearerToken"
        `when`(repository.addCluster(clusterViewModel.cluster.value!!)).thenReturn(clusterViewModel.cluster.value!!.isValid())

        clusterViewModel.addCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        assertTrue("Record was not inserted into the database", isValid == true and navigateOut)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun editCluster() = runTest {
        `when`(repository.getCluster(0L)).thenReturn(MutableLiveData(Cluster(0L,"Test Cluster",
            "192.168.1.1", 16443,"Bearer Token","testBearerToken")))
        val clusterViewModel = ClusterViewModel(0L, listOf("Bearer Token"),repository)
        clusterViewModel.cluster.value?.clusterName = "Test Cluster Updated"
        clusterViewModel.cluster.value?.clusterAddress = "192.168.1.2"
        clusterViewModel.cluster.value?.clusterPort = 16444
        clusterViewModel.cluster.value?.clusterAuthenticationMethod = "Bearer Token"
        clusterViewModel.cluster.value?.clusterBearerToken = "testBearerTokenV2"
        `when`(repository.updateCluster(clusterViewModel.cluster.value!!)).thenReturn(clusterViewModel.cluster.value!!.isValid())

        clusterViewModel.updateCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        assertTrue("Record was not inserted into the database", isValid == true and navigateOut)
    }
}