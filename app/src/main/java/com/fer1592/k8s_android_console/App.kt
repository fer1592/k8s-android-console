package com.fer1592.k8s_android_console

import android.app.Application
import com.fer1592.k8s_android_console.data.db.ClusterDatabase

lateinit var db: ClusterDatabase

class App : Application() {
    companion object {
        lateinit var INSTANCE: App
    }

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        db = ClusterDatabase.getInstance(this)
        INSTANCE = this
    }
}