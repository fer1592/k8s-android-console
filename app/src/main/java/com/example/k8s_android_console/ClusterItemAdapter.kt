package com.example.k8s_android_console

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.k8s_android_console.databinding.ClusterItemBinding

class ClusterItemAdapter(private val editClickListener: (clusterId: Long) -> Unit, private val deleteClickListener: (cluster: Cluster) -> Unit)
    : ListAdapter<Cluster, ClusterItemAdapter.ClusterItemViewHolder>(ClusterDiffItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int)
        : ClusterItemViewHolder = ClusterItemViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: ClusterItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, editClickListener, deleteClickListener)
    }

    class ClusterItemViewHolder(private val binding: ClusterItemBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun inflateFrom(parent: ViewGroup) : ClusterItemViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ClusterItemBinding.inflate(layoutInflater, parent, false)
                return ClusterItemViewHolder(binding)
            }
        }

        fun bind(item: Cluster, editClickListener: (clusterId: Long) -> Unit, deleteClickListener: (cluster: Cluster) -> Unit) {
            binding.cluster = item
            //Listener to edit the cluster
            binding.editCluster.setOnClickListener {
                editClickListener(item.clusterId)
            }
            //Listener to delete the cluster
            binding.deleteCluster.setOnClickListener {
                deleteClickListener(item)
            }
        }
    }
}