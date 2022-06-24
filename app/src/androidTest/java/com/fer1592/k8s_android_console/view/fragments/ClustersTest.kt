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
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fer1592.k8s_android_console.R
import com.fer1592.k8s_android_console.data.repository_implementation.ClusterRepositoryImplementation
import com.fer1592.k8s_android_console.util.EspressoIdlingResource
import com.fer1592.k8s_android_console.view.activities.MainActivity
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
class ClustersTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
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
    fun deleteCluster() {
        onView(withId(R.id.add_cluster)).perform(click())
        onView(withId(R.id.cluster_name)).perform(typeText("Test Cluster"))
        onView(withId(R.id.cluster_address)).perform(typeText("192.168.1.1"))
        onView(withId(R.id.cluster_port)).perform(replaceText("8443"))
        onView(withId(R.id.cluster_authentication_method)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Bearer Token"))).perform(click())
        onView(withId(R.id.cluster_bearer_token)).perform(typeText("TestToken"))
        onView(withId(R.id.add_cluster)).perform(scrollTo(), click())
        onView(withText(context.getString(R.string.delete))).perform(click())
        onView(withText(context.getString(R.string.accept))).perform(click())
        onView(withText(R.string.cluster_deleted)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withText("Test Cluster")).check(ViewAssertions.doesNotExist())
    }
}
