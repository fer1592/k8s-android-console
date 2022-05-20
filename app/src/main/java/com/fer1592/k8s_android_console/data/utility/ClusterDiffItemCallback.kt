package com.fer1592.k8s_android_console.data.utility
import androidx.recyclerview.widget.DiffUtil
import com.fer1592.k8s_android_console.data.model.Cluster

class ClusterDiffItemCallback : DiffUtil.ItemCallback<Cluster>() {
    override fun areItemsTheSame(oldItem: Cluster, newItem: Cluster): Boolean {
        return (oldItem.clusterId == newItem.clusterId)
    }

    override fun areContentsTheSame(oldItem: Cluster, newItem: Cluster): Boolean {
        return (oldItem == newItem)
    }
}
