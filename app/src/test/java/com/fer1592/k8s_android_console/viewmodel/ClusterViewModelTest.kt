package com.fer1592.k8s_android_console.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.repository.ClusterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ClusterViewModelTest{
    private lateinit var clusterViewModel : ClusterViewModel
    // Initialize instantExecutorRule to run all in the same thread
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    lateinit var repository: ClusterRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setup(){
        Dispatchers.setMain(testDispatcher)
        `when`(repository.getCluster(anyLong())).thenReturn(MutableLiveData(Cluster()))
        clusterViewModel = ClusterViewModel(testDispatcher)
        clusterViewModel.getCluster(-1L, listOf("Bearer Token"),repository)
        clusterViewModel.cluster!!.value?.clusterName = "Test Cluster"
        clusterViewModel.cluster!!.value?.clusterAddress = "test.com"
        clusterViewModel.cluster!!.value?.clusterAuthenticationMethod = "Bearer Token"
        clusterViewModel.cluster!!.value?.clusterBearerToken = "TestToken"
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can be added (Using IP as cluster address)`() = runTest {
        clusterViewModel.cluster!!.value?.clusterAddress = "192.168.1.1"
        `when`(repository.addCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.addCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        assertTrue("Record was not inserted into the database", ((isValid == true) and navigateOut))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can be added (Using Url as cluster address)`() = runTest {
        clusterViewModel.cluster!!.value?.clusterAddress = "test.com"
        `when`(repository.addCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.addCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        assertTrue("Record was not inserted into the database", ((isValid == true) and navigateOut))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can be updated (Using IP as cluster address)`() = runTest {
        clusterViewModel.cluster!!.value?.clusterAddress = "192.168.1.1"
        clusterViewModel.cluster!!.value?.clusterId = 0
        `when`(repository.updateCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.updateCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        assertTrue("Record was not updated into the database", ((isValid == true) and navigateOut))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can be update (Using Url as cluster address)`() = runTest {
        clusterViewModel.cluster!!.value?.clusterAddress = "test.com"
        clusterViewModel.cluster!!.value?.clusterId = 0
        `when`(repository.updateCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.updateCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        assertTrue("Record was not updated into the database", ((isValid == true) and navigateOut))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure live data value is updated when test connection is executed`() = runTest {
        `when`(repository.testClusterConnection(clusterViewModel.cluster?.value!!)).thenReturn(true)

        clusterViewModel.testConnection()

        val testConnectionResult = clusterViewModel.connectionTestSuccessful.getOrAwaitValue()

        assertTrue("Connection was not successful", testConnectionResult == true)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can't be added if an Empty name is set`() = runTest {
        clusterViewModel.cluster!!.value?.clusterName = ""
        `when`(repository.addCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.addCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val validName = clusterViewModel.cluster!!.value?.validName ?: true
        assertTrue("Record was not inserted into the database", ((isValid == false) and !navigateOut and !validName))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can't be updated if an Empty name is set`() = runTest {
        clusterViewModel.cluster!!.value?.clusterName = ""
        `when`(repository.updateCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.updateCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val validName = clusterViewModel.cluster!!.value?.validName ?: true
        assertTrue("Record was not updated into the database", ((isValid == false) and !navigateOut and !validName))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can't be added if an Empty Address is set`() = runTest {
        clusterViewModel.cluster!!.value?.clusterAddress = ""
        `when`(repository.addCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.addCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val validAddress = clusterViewModel.cluster!!.value?.validClusterAddress ?: true
        assertTrue("Record was not inserted into the database", ((isValid == false) and !navigateOut and !validAddress))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can't be updated if an Empty Address is set`() = runTest {
        clusterViewModel.cluster!!.value?.clusterAddress = ""
        `when`(repository.updateCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.updateCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val validAddress = clusterViewModel.cluster!!.value?.validClusterAddress ?: true
        assertTrue("Record was not updated into the database", ((isValid == false) and !navigateOut and !validAddress))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can't be added if a Port out of range is set`() = runTest {
        clusterViewModel.cluster!!.value?.clusterPort = 0
        `when`(repository.addCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.addCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val validPort = clusterViewModel.cluster!!.value?.validClusterPort ?: true
        assertTrue("Record was not inserted into the database", ((isValid == false) and !navigateOut and !validPort))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can't be updated if a Port out of range is set`() = runTest {
        clusterViewModel.cluster!!.value?.clusterPort = 49152
        `when`(repository.updateCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.updateCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val validPort = clusterViewModel.cluster!!.value?.validClusterPort ?: true
        assertTrue("Record was not updated into the database", ((isValid == false) and !navigateOut and !validPort))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can't be added if an Empty BearerToken is set`() = runTest {
        clusterViewModel.cluster!!.value?.clusterBearerToken = ""
        `when`(repository.addCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.addCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val validBearerToken = clusterViewModel.cluster!!.value?.validClusterBearerToken ?: true
        assertTrue("Record was not inserted into the database", ((isValid == false) and !navigateOut and !validBearerToken))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `ensure cluster can't be updated if an Empty BearerToken is set`() = runTest {
        clusterViewModel.cluster!!.value?.clusterBearerToken = ""
        `when`(repository.updateCluster(clusterViewModel.cluster?.value!!)).thenReturn(clusterViewModel.cluster?.value!!.isValid())
        clusterViewModel.updateCluster()

        val isValid = clusterViewModel.isInputValid.getOrAwaitValue()
        val navigateOut = clusterViewModel.navigateToClusterList.getOrAwaitValue()
        val validBearerToken = clusterViewModel.cluster!!.value?.validClusterBearerToken ?: true
        assertTrue("Record was not inserted into the database", ((isValid == false) and !navigateOut and !validBearerToken))
    }
}
