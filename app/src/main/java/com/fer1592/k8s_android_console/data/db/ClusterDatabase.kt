package com.fer1592.k8s_android_console.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fer1592.k8s_android_console.data.model.Cluster

@Database(entities = [Cluster::class], version = 1, exportSchema = false)
abstract class ClusterDatabase : RoomDatabase(){
    abstract val clusterDAO : ClusterDAO

    companion object {
        @Volatile
        private var INSTANCE: ClusterDatabase? = null

        fun getInstance(context: Context) : ClusterDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, ClusterDatabase::class.java, "Cluster").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
