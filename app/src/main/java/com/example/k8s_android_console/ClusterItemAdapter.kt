package com.example.k8s_android_console

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.k8s_android_console.databinding.ClusterItemBinding

class ClusterItemAdapter(val clickListener: (clusterId: Long) -> Unit) : ListAdapter<Cluster, ClusterItemAdapter.ClusterItemViewHolder>(ClusterDiffItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int)
        : ClusterItemViewHolder = ClusterItemViewHolder.inflateFrom(parent)

    override fun onBindViewHolder(holder: ClusterItemAdapter.ClusterItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    class ClusterItemViewHolder(val binding: ClusterItemBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun inflateFrom(parent: ViewGroup) : ClusterItemViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ClusterItemBinding.inflate(layoutInflater, parent, false)
                return ClusterItemViewHolder(binding)
            }
        }

        fun bind(item: Cluster, clickListener: (clusterId: Long) -> Unit) {
            binding.cluster = item
            binding.root.setOnClickListener{
                clickListener(item.clusterId)
            }
        }
    }
}