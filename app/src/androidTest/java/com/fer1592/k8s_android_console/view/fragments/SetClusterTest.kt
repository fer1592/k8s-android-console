package com.fer1592.k8s_android_console.view.fragments

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fer1592.k8s_android_console.R
import com.fer1592.k8s_android_console.data.repository_implementation.ClusterRepositoryImplementation
import com.fer1592.k8s_android_console.util.EspressoIdlingResource
import com.fer1592.k8s_android_console.view.activities.MainActivity
import com.fer1592.k8s_android_console.view.atPosition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SetClusterTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private lateinit var context: Context
    private lateinit var clusterAddress: String
    private lateinit var clusterPort: String
    private lateinit var clusterBearerToken: String

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        clusterAddress = System.getProperty("address")?.toString() ?: "192.168.100.114"
        clusterPort = System.getProperty("port")?.toString() ?: "16443"
        clusterBearerToken = System.getProperty("token")?.toString() ?: "VFVqYldCT0V6NFFEK3NBOGdVQVFoYXRualgrNnhrUWFmbUE4ZW52d1lvaz0K"
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    @ExperimentalCoroutinesApi
    @Throws(IOException::class)
    fun clearDatabase() = runTest {
        val clusterRepository = ClusterRepositoryImplementation()
        clusterRepository.cleanUpClusters()
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun addNewClusterWithIp() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("16443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("testToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.clusters_list)).check(matches(atPosition(0, hasDescendant(withText("Test Cluster")))))
    }

    @Test
    fun addNewClusterWithUrl() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("test.cluster.com"))
        onView(withId(R.id.cluster_port)).perform(replaceText("16443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("testToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.clusters_list)).check(matches(atPosition(0, hasDescendant(withText("Test Cluster")))))
    }

    @Test
    fun addNewClusterDisplayEmptyNameError() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText(""))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("16443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("TestToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_name_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_empty_cluster_name)))))
    }

    @Test
    fun addNewClusterDisplayWrongAddressError() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("16443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("TestToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_address_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_invalid_cluster_address)))))
        onView(withId(R.id.cluster_address)).perform(replaceText("https://invalid.address"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_address_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_invalid_cluster_address)))))
        onView(withId(R.id.cluster_address)).perform(replaceText(""))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_address_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_invalid_cluster_address)))))
    }

    @Test
    fun addNewClusterDisplayWrongPortError() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("0"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("TestToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_port_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_invalid_port_address)))))
        onView(withId(R.id.cluster_port)).perform(replaceText(""))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_port_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_invalid_port_address)))))
    }

    @Test
    fun addNewClusterDisplayEmptyBearerError() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("8443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText(""))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_bearer_token_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_empty_cluster_bearer_token)))))
    }

    @Test
    fun updateCluster() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("16443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("testToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withText(context.getString(R.string.edit))).perform(click())
        onView(withId(R.id.cluster_name)).perform(replaceText("Test Cluster2"))
        onView(withId(R.id.cluster_address)).perform(replaceText("192.168.1.2"))
        onView(withId(R.id.cluster_port)).perform(replaceText("8443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(replaceText("testToken2"))
        onView(withId(R.id.update_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.clusters_list)).check(matches(atPosition(0, hasDescendant(withText("Test Cluster2")))))
        onView(withText(context.getString(R.string.edit))).perform(click())
        onView(withId(R.id.cluster_address)).perform(replaceText("test.cluster.com"))
        onView(withId(R.id.update_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.clusters_list)).check(matches(atPosition(0, hasDescendant(withText("Test Cluster2")))))
    }

    @Test
    fun updateClusterDisplayEmptyNameError() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("16443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("TestToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withText(context.getString(R.string.edit))).perform(click())
        onView(withId(R.id.cluster_name)).perform(replaceText(""))
        onView(withId(R.id.update_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_name_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_empty_cluster_name)))))
    }

    @Test
    fun updateClusterDisplayWrongAddressError() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("16443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("TestToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withText(context.getString(R.string.edit))).perform(click())
        onView(withId(R.id.cluster_address)).perform(replaceText(""))
        onView(withId(R.id.update_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_address_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_invalid_cluster_address)))))
        onView(withId(R.id.cluster_address)).perform(replaceText("192.168.1"))
        onView(withId(R.id.update_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_address_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_invalid_cluster_address)))))
        onView(withId(R.id.cluster_address)).perform(replaceText("https://test.cluster.com"))
        onView(withId(R.id.update_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_address_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_invalid_cluster_address)))))
    }

    @Test
    fun updateClusterDisplayWrongPortError() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("16443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("TestToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withText(context.getString(R.string.edit))).perform(click())
        onView(withId(R.id.cluster_port)).perform(replaceText("0"))
        onView(withId(R.id.update_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_port_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_invalid_port_address)))))
        onView(withId(R.id.cluster_port)).perform(replaceText(""))
        onView(withId(R.id.update_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_port_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_invalid_port_address)))))
    }

    @Test
    fun updateClusterDisplayEmptyBearerError() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("8443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("TestToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withText(context.getString(R.string.edit))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(replaceText(""))
        onView(withId(R.id.update_cluster)).perform(scrollTo(), click())
        onView(withId(R.id.cluster_bearer_token_input_layout)).check(matches(hasDescendant(withText(context.getString(R.string.validation_empty_cluster_bearer_token)))))
    }

    @Test
    fun testConnectionShowToast() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText(clusterAddress))
        onView(withId(R.id.cluster_port)).perform(replaceText(clusterPort))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText(clusterBearerToken))
        onView(withId(R.id.test_connection)).perform(scrollTo(), click())
        onView(withText(R.string.connection_succeeded)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.cluster_address)).perform(replaceText("test.com"))
        onView(withId(R.id.test_connection)).perform(scrollTo(), click())
        onView(withText(R.string.connection_failed)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}
