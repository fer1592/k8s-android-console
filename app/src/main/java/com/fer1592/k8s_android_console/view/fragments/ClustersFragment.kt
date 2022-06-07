package com.fer1592.k8s_android_console.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fer1592.k8s_android_console.R
import com.fer1592.k8s_android_console.databinding.FragmentClustersBinding
import com.fer1592.k8s_android_console.view.activities.MainActivity
import com.fer1592.k8s_android_console.view.adapters.ClusterItemAdapter
import com.fer1592.k8s_android_console.viewmodel.ClustersViewModel

class ClustersFragment : Fragment() {
    private var _binding: FragmentClustersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Get the data binding and view
        _binding = FragmentClustersBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.clusters)

        // Gets the Clusters View Model
        val clustersViewModel = ViewModelProvider(this)[ClustersViewModel::class.java]

        // Sets the cluster View Model variable in the layout
        binding.clustersViewModel = clustersViewModel

        // Sets layout's lifeCycleOwner so that it can respond to live data
        binding.lifecycleOwner = viewLifecycleOwner

        // Sets adapter for the cluster recycler view and observes for any changes in the clusters
        val clusterAdapter = ClusterItemAdapter(
            // Changes the navigateToCluster value, to indicate that we want to navigate to a specific cluster to edit it
            { clusterId -> clustersViewModel.onClusterEditClicked(clusterId) },
            // Deletes the cluster
            { cluster ->
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_cluster))
                    .setMessage(String.format(getString(R.string.cluster_delete_confirmation_question), cluster.clusterName))
                    .setPositiveButton(getString(R.string.accept)) { view, _ ->
                        clustersViewModel.deleteCluster(cluster)
                        view.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { view, _ ->
                        view.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                dialog.show()
            }
        )

        binding.clustersList.adapter = clusterAdapter
        clustersViewModel.clusters.observe(viewLifecycleOwner) {
            hideLoading()
            it?.let {
                binding.clustersList.scheduleLayoutAnimation()
                clusterAdapter.submitList(it)
            }
        }

        // Observe the navigateToCluster live data to navigate to the cluster when the value changes
        clustersViewModel.navigateToEditCluster.observe(viewLifecycleOwner) { clusterId ->
            clusterId?.let {
                val action =
                    ClustersFragmentDirections.actionClustersFragmentToSetClusterFragment(clusterId)
                this.findNavController().navigate(action)
                clustersViewModel.onClusterEditNavigated()
            }
        }

        // Sets observer to show progress bar when deleting a cluster
        clustersViewModel.processingData.observe(viewLifecycleOwner) {
            if (it) showLoading()
            else hideLoading()
        }

        // Set on click listener for the FAB to add a new cluster
        binding.addCluster.setOnClickListener {
            val action = ClustersFragmentDirections.actionClustersFragmentToSetClusterFragment(-1)
            this.findNavController().navigate(action)
        }

        showLoading()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.clustersList.scheduleLayoutAnimation()
    }

    private fun showLoading() {
        (activity as MainActivity).binding.mainProgressBar.visibility = View.VISIBLE
        (activity as MainActivity).binding.navHostFragment.visibility = View.GONE
    }

    private fun hideLoading() {
        (activity as MainActivity).binding.mainProgressBar.visibility = View.GONE
        (activity as MainActivity).binding.navHostFragment.visibility = View.VISIBLE
    }
}
