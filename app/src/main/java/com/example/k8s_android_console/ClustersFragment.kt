package com.example.k8s_android_console

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.k8s_android_console.databinding.FragmentClusterBinding

class ClustersFragment : Fragment() {
    private var _binding : FragmentClusterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Get the data binding and view
        _binding = FragmentClusterBinding.inflate(inflater, container, false)

        // Builds the database if it doesn't exists
        val application = requireActivity().application
        val dao = ClusterDatabase.getInstance(application).clusterDAO

        // Gets the Cluster View Model
        val clusterViewModelFactory = ClusterViewModelFactory(dao)
        val clustersViewModel = ViewModelProvider(this, clusterViewModelFactory).get(ClustersViewModel::class.java)

        // Sets the cluster View Model variable in the layout
        binding.clustersViewModel = clustersViewModel

        // Sets layout's lifeCycleOwner so that it can respond to live data
        binding.lifecycleOwner = viewLifecycleOwner

        // Sets adapter for the cluster recycler view and observes for any changes in the clusters
        val clusterAdapter = ClusterItemAdapter()
        binding.clustersList.adapter = clusterAdapter
        clustersViewModel.clusters.observe(viewLifecycleOwner) {
            it?.let {
                clusterAdapter.submitList(it)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}