package com.fer1592.k8s_android_console.data.repositoryimplementation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.fer1592.k8s_android_console.data.db.ClusterDAO
import com.fer1592.k8s_android_console.data.model.Cluster
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

class ClusterRepositoryImplementationTest {
    private lateinit var clusterRepository: ClusterRepositoryImplementation
    private lateinit var cluster: Cluster

    @MockK
    lateinit var clusterDao: ClusterDAO

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = UnconfinedTestDispatcher()

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(testDispatcher)
        clusterRepository = ClusterRepositoryImplementation(clusterDao)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test get new cluster`() = runTest {
        val result = clusterRepository.getCluster(-1L)
        verify(exactly = 0) { clusterDao.getCluster(-1L) }
        Assert.assertTrue(result.value is Cluster)
        Assert.assertTrue(result.value?.clusterId == 0L)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test get existing cluster`() = runTest {
        every { clusterDao.getCluster(2L) } returns MutableLiveData(Cluster(clusterId = 2L))
        val result = clusterRepository.getCluster(2L)
        verify { clusterDao.getCluster(2L) }
        Assert.assertTrue(result.value is Cluster)
        Assert.assertTrue(result.value?.clusterId == 2L)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test valid cluster`() {
        cluster = mockk()
        every { cluster.isValid() } returns true
        clusterRepository.clusterIsValid(cluster)
        verify { cluster.isValid() }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster added when valid`() = runTest {
        cluster = mockk()
        every { cluster.isValid() } returns true
        val result = clusterRepository.addCluster(cluster)
        verify { cluster.isValid() }
        coVerify(exactly = 1) { clusterDao.insert(cluster) }
        Assert.assertTrue(result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster not added when invalid`() = runTest {
        cluster = mockk()
        every { cluster.isValid() } returns false
        val result = clusterRepository.addCluster(cluster)
        verify { cluster.isValid() }
        coVerify(exactly = 0) { clusterDao.insert(cluster) }
        Assert.assertTrue(!result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster add on Exception return false`() = runTest {
        cluster = mockk()
        every { cluster.isValid() } returns true
        coEvery { clusterDao.insert(cluster) }.throws(Exception())
        val result = clusterRepository.addCluster(cluster)
        coVerify(exactly = 1) { clusterDao.insert(cluster) }
        Assert.assertTrue(!result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster updated when valid`() = runTest {
        cluster = mockk()
        every { cluster.isValid() } returns true
        val result = clusterRepository.updateCluster(cluster)
        verify { cluster.isValid() }
        coVerify(exactly = 1) { clusterDao.update(cluster) }
        Assert.assertTrue(result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster not updated when invalid`() = runTest {
        cluster = mockk()
        every { cluster.isValid() } returns false
        val result = clusterRepository.updateCluster(cluster)
        verify { cluster.isValid() }
        coVerify(exactly = 0) { clusterDao.update(cluster) }
        Assert.assertTrue(!result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster update on Exception return false`() = runTest {
        cluster = mockk()
        every { cluster.isValid() } returns true
        coEvery { clusterDao.update(cluster) }.throws(Exception())
        val result = clusterRepository.updateCluster(cluster)
        coVerify(exactly = 1) { clusterDao.update(cluster) }
        Assert.assertTrue(!result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster deleted`() = runTest {
        cluster = mockk()
        coEvery { clusterDao.delete(cluster) } returns 1
        val result = clusterRepository.deleteCluster(cluster)
        coVerify(exactly = 1) { clusterDao.delete(cluster) }
        Assert.assertTrue(result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster not deleted`() = runTest {
        cluster = mockk()
        coEvery { clusterDao.delete(cluster) } returns 0
        val result = clusterRepository.deleteCluster(cluster)
        coVerify(exactly = 1) { clusterDao.delete(cluster) }
        Assert.assertTrue(!result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test cluster deletion on Exception return false`() = runTest {
        cluster = mockk()
        coEvery { clusterDao.delete(cluster) }.throws(Exception())
        val result = clusterRepository.deleteCluster(cluster)
        coVerify(exactly = 1) { clusterDao.delete(cluster) }
        Assert.assertTrue(!result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test get all clusters`() = runTest {
        every { clusterDao.getAllClusters() } returns MutableLiveData(listOf(mockk()))
        clusterRepository.getAllClusters()
        coVerify(exactly = 1) { clusterDao.getAllClusters() }
    }

    // Connection test is done with a real api, which is a locally deployed instance of microk8s
    // @ExperimentalCoroutinesApi
    // @Test
    // fun `test connection to kubernetes`() = runTest {
    //     val pb = ProcessBuilder("sg", "microk8s", "-c", "microk8s config")
    //     pb.redirectErrorStream(true)
    //     val proc = pb.start()
    //     val inputStream = proc.inputStream
//
    //     val commandResult = inputStream.bufferedReader().readLines()
    //     val address = commandResult.find { item ->
    //         item.contains(".*server: https://.*".toRegex())
    //     }?.replace(" ", "")?.replace("server:", "")?.replace("https://", "")?.split(":")
    //     val token = commandResult.find { item ->
    //         item.contains(".*token:.*".toRegex())
    //     }?.replace(" ", "")?.replace("token:", "")
    //     cluster = Cluster(0, "Test Cluster", address?.get(0) ?: "192.168.1.1", address?.get(1)?.toInt() ?: 8443, "Bearer Token", token ?: "TestToken")
//
    //     val result = clusterRepository.testClusterConnection(cluster)
    //     Assert.assertTrue("Connection with kubernetes failed", result)
    // }
}
