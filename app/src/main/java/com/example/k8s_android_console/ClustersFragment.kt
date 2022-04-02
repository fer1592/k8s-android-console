package com.example.k8s_android_console

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.k8s_android_console.databinding.FragmentClustersBinding

class ClustersFragment : Fragment() {
    private var _binding : FragmentClustersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Get the data binding and view
        _binding = FragmentClustersBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.clusters)

        // Builds the database if it doesn't exists
        val application = requireNotNull(this.activity).application
        val dao = ClusterDatabase.getInstance(application).clusterDAO

        // Gets the Clusters View Model
        val clusterViewModelFactory = ClustersViewModelFactory(dao)
        val clustersViewModel = ViewModelProvider(this, clusterViewModelFactory)[ClustersViewModel::class.java]

        // Sets the cluster View Model variable in the layout
        binding.clustersViewModel = clustersViewModel

        // Sets layout's lifeCycleOwner so that it can respond to live data
        binding.lifecycleOwner = viewLifecycleOwner

        // Sets adapter for the cluster recycler view and observes for any changes in the clusters
        val clusterAdapter = ClusterItemAdapter{ clusterId ->
            // Changes the navigateToCluster value, to indicate that we want to navigate to a specific cluster to edit it
            clustersViewModel.onClusterClicked(clusterId)
        }

        binding.clustersList.adapter = clusterAdapter
        clustersViewModel.clusters.observe(viewLifecycleOwner, Observer {
            it?.let {
                clusterAdapter.submitList(it)
            }
        })

        // Observe the navigateToCluster live data to navigate to the cluster when the value changes
        clustersViewModel.navigateToCluster.observe(viewLifecycleOwner, Observer { clusterId ->
            clusterId?.let {
                val action = ClustersFragmentDirections.actionClustersFragmentToSetClusterFragment(clusterId)
                this.findNavController().navigate(action)
                clustersViewModel.onClusterNavigated()
            }
        })

        // Set on click listener for the FAB to add a new cluster
        binding.addCluster.setOnClickListener{
            val action = ClustersFragmentDirections.actionClustersFragmentToSetClusterFragment(-1)
            this.findNavController().navigate(action)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}