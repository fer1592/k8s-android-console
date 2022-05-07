package com.fer1592.k8s_android_console.data.db

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fer1592.k8s_android_console.data.model.Cluster

@Database(entities = [Cluster::class], version = 1)
abstract class ClusterDatabase : RoomDatabase() {
    abstract fun clusterDao(): ClusterDAO

    companion object {
        private val lock = Any()
        private const val DB_NAME = "ClusterDatabase"
        private var INSTANCE: ClusterDatabase? = null

        fun getInstance(application: Application): ClusterDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(application, ClusterDatabase::class.java, DB_NAME).build()
                }
            }
            return INSTANCE!!
        }
    }
}
