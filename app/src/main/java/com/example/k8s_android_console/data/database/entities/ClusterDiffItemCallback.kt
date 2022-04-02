package com.example.k8s_android_console.data.database.entities
import androidx.recyclerview.widget.DiffUtil

class ClusterDiffItemCallback : DiffUtil.ItemCallback<ClusterEntity>() {
    override fun areItemsTheSame(oldItem: ClusterEntity, newItem: ClusterEntity): Boolean {
        return (oldItem.clusterId == newItem.clusterId)
    }

    override fun areContentsTheSame(oldItem: ClusterEntity, newItem: ClusterEntity): Boolean {
        return (oldItem == newItem)
    }
}