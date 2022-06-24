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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ClusterViewModelTest {
    private lateinit var clusterViewModel: ClusterViewModel
    // Initialize instantExecutorRule to run all in the same thread
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = UnconfinedTestDispatcher()

    @MockK
    lateinit var repository: ClusterRepository

    @MockK
    lateinit var cluster: MutableLiveData<Cluster>

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() = runTest {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)
        mockkObject(EspressoIdlingResource)
        repository = mockk()
        cluster = mockk()
        clusterViewModel = ClusterViewModel(testDispatcher)
        coEvery { repository.getCluster(0L) } returns cluster
        clusterViewModel.getCluster(0L, listOf("Bearer Token"), repository)
        every { cluster.value } returns mockk()
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test get cluster`() = runTest {
        coEvery { repository.getCluster(5L) } returns mockk()
        clusterViewModel.getCluster(5L, listOf("Bearer Token"), repository)
        coVerify(exactly = 1) { repository.getCluster(5L) }
        val processingData = clusterViewModel.processingData.getOrAwaitValue()
        assertTrue("Process didn't finish", !processingData)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test add cluster`() = runTest {
        coEvery { repository.addCluster(any()) } returns true
        clusterViewModel.addCluster()
        coVerify(exactly = 1) { repository.addCluster(any()) }
        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val processingData = clusterViewModel.processingData.getOrAwaitValue()
        assertTrue("Record was not inserted into the database", ((isValid == true) and navigateOut and !processingData))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test add cluster failed because invalid`() = runTest {
        coEvery { repository.addCluster(any()) } returns false
        coEvery { repository.clusterIsValid(any()) } returns false
        clusterViewModel.addCluster()
        coVerify(exactly = 1) { repository.addCluster(any()) }
        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val processingData = clusterViewModel.processingData.getOrAwaitValue()
        assertTrue("Record was not inserted into the database", ((isValid == false) and !navigateOut and !processingData))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test add cluster failed because unexpected problem`() = runTest {
        coEvery { repository.addCluster(any()) } returns false
        coEvery { repository.clusterIsValid(any()) } returns true
        clusterViewModel.addCluster()
        coVerify(exactly = 1) { repository.addCluster(any()) }
        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val processingData = clusterViewModel.processingData.getOrAwaitValue()
        assertTrue("Record was not inserted into the database", ((isValid != false) and !navigateOut and !processingData))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test update cluster`() = runTest {
        coEvery { repository.updateCluster(any()) } returns true
        clusterViewModel.updateCluster()
        coVerify(exactly = 1) { repository.updateCluster(any()) }
        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val processingData = clusterViewModel.processingData.getOrAwaitValue()
        assertTrue("Record was not inserted into the database", ((isValid == true) and navigateOut and !processingData))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test update cluster failed because invalid`() = runTest {
        coEvery { repository.updateCluster(any()) } returns false
        coEvery { repository.clusterIsValid(any()) } returns false
        clusterViewModel.updateCluster()
        coVerify(exactly = 1) { repository.updateCluster(any()) }
        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val processingData = clusterViewModel.processingData.getOrAwaitValue()
        assertTrue("Record was not inserted into the database", ((isValid == false) and !navigateOut and !processingData))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test update cluster failed because unexpected problem`() = runTest {
        coEvery { repository.updateCluster(any()) } returns false
        coEvery { repository.clusterIsValid(any()) } returns true
        clusterViewModel.updateCluster()
        coVerify(exactly = 1) { repository.updateCluster(any()) }
        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val processingData = clusterViewModel.processingData.getOrAwaitValue()
        assertTrue("Record was not inserted into the database", ((isValid != false) and !navigateOut and !processingData))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test connection testing`() = runTest {
        coEvery { repository.testClusterConnection(any()) } returns true
        clusterViewModel.testConnection()
        val testConnectionResult = clusterViewModel.displayMessage.getOrAwaitValue()
        val processingData = clusterViewModel.processingData.getOrAwaitValue()
        assertTrue("Connection was not successful", (testConnectionResult != null) and !processingData)
    }
}
