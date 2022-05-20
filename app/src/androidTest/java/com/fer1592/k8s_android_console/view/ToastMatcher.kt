package com.fer1592.k8s_android_console.view

import android.os.IBinder
import android.view.WindowManager
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

/**
 * Author: http://www.qaautomated.com/2016/01/how-to-test-toast-message-using-espresso.html
 */
class ToastMatcher : TypeSafeMatcher<Root?>() {

    override fun describeTo(description: Description?) {
        description?.appendText("is toast")
    }

    override fun matchesSafely(item: Root?): Boolean {
        val type: Int? = item?.windowLayoutParams?.get()?.type
        @Suppress("DEPRECATION")
        if (type == WindowManager.LayoutParams.TYPE_TOAST || type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY) {
            val windowToken: IBinder = item.decorView.windowToken
            val appToken: IBinder = item.decorView.applicationWindowToken
            if (windowToken === appToken) { // means this window isn't contained by any other windows.
                return true
            }
        }
        return false
    }
}
