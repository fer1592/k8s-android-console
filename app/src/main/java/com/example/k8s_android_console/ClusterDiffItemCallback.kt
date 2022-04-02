package com.example.k8s_android_console
import androidx.recyclerview.widget.DiffUtil

class ClusterDiffItemCallback : DiffUtil.ItemCallback<Cluster>() {
    override fun areItemsTheSame(oldItem: Cluster, newItem: Cluster): Boolean {
        return (oldItem.clusterId == newItem.clusterId)
    }

    override fun areContentsTheSame(oldItem: Cluster, newItem: Cluster): Boolean {
        return (oldItem == newItem)
    }
}