package com.fer1592.k8s_android_console.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.repository.ClusterRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ClustersViewModelTest {
    private lateinit var clustersViewModel: ClustersViewModel
    // Initialize instantExecutorRule to run all in the same thread
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: ClusterRepository

    @Before
    fun setup() {
        Mockito.`when`(repository.getAllClusters()).thenReturn(
            MutableLiveData(
                listOf(
                    Cluster(0L, "Test Cluster 1", "192.168.1.2", 8443, "Bearer Token", "TestToken"),
                    Cluster(1L, "Test Cluster 2", "192.168.1.3", 8443, "Bearer Token", "TestToken2")
                )
            )
        )
        clustersViewModel = ClustersViewModel(repository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster navigates when edit clicked`() = runTest {
        clustersViewModel.onClusterEditClicked(clustersViewModel.clusters.value!![0].clusterId)

        val navigateOut = clustersViewModel.navigateToEditCluster.getOrAwaitValue()
        assertTrue("ViewModel hasn't navigated out", (navigateOut == 0L))
    }
}
