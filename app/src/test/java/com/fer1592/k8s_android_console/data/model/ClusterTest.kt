package com.fer1592.k8s_android_console.data.model

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ClusterTest {
    private lateinit var cluster: Cluster

    @Before
    fun setUp() {
        cluster = Cluster(0L, "Test Cluster", "testcluster.com", 16443, "Bearer Token", "TestToken")
    }

    @Test
    fun `test valid cluster with dns address returns true`() {
        Assert.assertTrue(cluster.isValid())
    }

    @Test
    fun `test valid cluster with ip address returns true`() {
        cluster.clusterAddress = "192.168.1.1"
        Assert.assertTrue(cluster.isValid())
    }

    @Test
    fun `test empty cluster returns false`() {
        cluster = Cluster()
        Assert.assertTrue(!cluster.isValid())
    }

    @Test
    fun `test empty name returns false`() {
        cluster.clusterName = ""
        Assert.assertTrue(!cluster.isValid())
        Assert.assertTrue(!cluster.validName)
        Assert.assertTrue(cluster.validClusterAddress and cluster.validClusterPort and cluster.validClusterBearerToken)
    }

    @Test
    fun `test invalid address returns false`() {
        cluster.clusterAddress = ""
        Assert.assertTrue(!cluster.isValid())
        Assert.assertTrue(!cluster.validClusterAddress)
        Assert.assertTrue(cluster.validName and cluster.validClusterPort and cluster.validClusterBearerToken)
        cluster.clusterAddress = "https://invalid.address.com"
        Assert.assertTrue(!cluster.isValid())
        Assert.assertTrue(!cluster.validClusterAddress)
        Assert.assertTrue(cluster.validName and cluster.validClusterPort and cluster.validClusterBearerToken)
        cluster.clusterAddress = "192.168.1"
        Assert.assertTrue(!cluster.isValid())
        Assert.assertTrue(!cluster.validClusterAddress)
        Assert.assertTrue(cluster.validName and cluster.validClusterPort and cluster.validClusterBearerToken)
    }

    @Test
    fun `test invalid port returns false`() {
        cluster.clusterPort = 0
        Assert.assertTrue(!cluster.isValid())
        Assert.assertTrue(!cluster.validClusterPort)
        Assert.assertTrue(cluster.validName and cluster.validClusterAddress and cluster.validClusterBearerToken)
        cluster.clusterPort = 49152
        Assert.assertTrue(!cluster.isValid())
        Assert.assertTrue(!cluster.validClusterPort)
        Assert.assertTrue(cluster.validName and cluster.validClusterAddress and cluster.validClusterBearerToken)
    }

    @Test
    fun `test empty Bearer returns false`() {
        cluster.clusterBearerToken = ""
        Assert.assertTrue(!cluster.isValid())
        Assert.assertTrue(!cluster.validClusterBearerToken)
        Assert.assertTrue(cluster.validName and cluster.validClusterAddress and cluster.validClusterPort)
    }

    @Test
    fun `test empty Auth Method returns false`() {
        cluster.clusterAuthenticationMethod = ""
        Assert.assertTrue(!cluster.isValid())
    }
}
