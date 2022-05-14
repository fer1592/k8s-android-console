package com.fer1592.k8s_android_console.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fer1592.k8s_android_console.data.db.ClusterDatabase
import com.fer1592.k8s_android_console.data.model.Cluster
import com.fer1592.k8s_android_console.data.repository_implementation.ClusterRepositoryImplementation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ClusterRepositoryImplementationTest {
    private lateinit var clusterRepository: ClusterRepositoryImplementation
    private lateinit var db: ClusterDatabase
    private lateinit var cluster: Cluster

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ClusterDatabase::class.java).build()
        clusterRepository = ClusterRepositoryImplementation()
        cluster = Cluster(0, "Test Cluster", "192.168.1.1", 8443, "Bearer Token", "TestToken")
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addClusterWithIP() = runTest {
        val result = clusterRepository.addCluster(cluster)

        Assert.assertTrue("Record was not inserted into the database", result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addClusterWithUrl() = runTest {
        cluster.clusterAddress = "testAddress.com"
        val result = clusterRepository.addCluster(cluster)

        Assert.assertTrue("Record was not inserted into the database", result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateClusterWithIP() = runTest {
        clusterRepository.addCluster(cluster)
        cluster.clusterAddress = "192.168.10.20"
        val result = clusterRepository.updateCluster(cluster)

        Assert.assertTrue("Record was not inserted into the database", result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateClusterWithUrl() = runTest {
        clusterRepository.addCluster(cluster)
        cluster.clusterAddress = "testAddress.com"
        val result = clusterRepository.updateCluster(cluster)

        Assert.assertTrue("Record was not inserted into the database", result)
    }
}
