package com.fer1592.k8s_android_console.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.repository.ClusterRepository
import com.fer1592.k8s_android_console.util.EspressoIdlingResource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ClustersViewModelTest {
    private lateinit var clustersViewModel: ClustersViewModel
    private lateinit var cluster: Cluster
    // Initialize instantExecutorRule to run all in the same thread
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var repository: ClusterRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        cluster = mockk()
        every { repository.getAllClusters() } returns MutableLiveData(listOf(cluster))
        clustersViewModel = ClustersViewModel(repository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster navigates when edit clicked`() = runTest {
        clustersViewModel.onClusterEditClicked(0L)

        val navigateOut = clustersViewModel.navigateToEditCluster.getOrAwaitValue()
        assertTrue("ViewModel hasn't navigated out", (navigateOut == 0L))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster deleted`() = runTest {
        mockkObject(EspressoIdlingResource)
        coEvery { repository.deleteCluster(cluster) } returns true
        clustersViewModel.deleteCluster(cluster)
        coVerify(exactly = 1) { repository.deleteCluster(cluster) }
        val processingData = clustersViewModel.processingData.getOrAwaitValue()
        assertTrue("Process didn't finish", !processingData)
    }
}
