package com.fer1592.k8s_android_console.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.fer1592.k8s_android_console.R
import com.fer1592.k8s_android_console.databinding.FragmentSetClusterBinding
import com.fer1592.k8s_android_console.view.activities.MainActivity
import com.fer1592.k8s_android_console.viewmodel.ClusterViewModel

class SetClusterFragment : Fragment() {
    private var _binding: FragmentSetClusterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetClusterBinding.inflate(inflater, container, false)
        val view = binding.root

        val application = requireNotNull(this.activity).application

        // Get list of auth methods
        val authMethods = resources.getStringArray(R.array.auth_methods).toList()

        // We get the cluster id from the parameters and create the view model
        val clusterId = SetClusterFragmentArgs.fromBundle(requireArguments()).clusterId
        val clusterViewModel = ViewModelProvider(this)[ClusterViewModel::class.java]
        clusterViewModel.getCluster(clusterId, authMethods)

        if (activity is MainActivity) {
            if (clusterId == (-1).toLong()) {
                (activity as AppCompatActivity).supportActionBar?.title =
                    getString(R.string.add_cluster)
            } else (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.update_cluster)
        }
        binding.clusterViewModel = clusterViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Set the spinner options and selected value
        ArrayAdapter.createFromResource(
            application,
            R.array.auth_methods,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.clusterAuthenticationMethod.adapter = adapter
        }

        // Sets observer to navigate back when a cluster is created/deleted/updated
        clusterViewModel.navigateToClusterList.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                view.findNavController()
                    .navigate(R.id.action_setClusterFragment_to_clustersFragment)
                clusterViewModel.onNavigatedToClusterList()
            }
        }

        // Sets observer to show or hide error messages
        clusterViewModel.isInputValid.observe(viewLifecycleOwner) { isValid ->
            if (isValid == true || isValid == null) {
                binding.clusterNameInputLayout.error = null
                binding.clusterAddressInputLayout.error = null
                binding.clusterPortInputLayout.error = null
                binding.clusterBearerTokenInputLayout.error = null
            } else {
                if (clusterViewModel.cluster?.value?.validName == true) binding.clusterNameInputLayout.error = null
                else binding.clusterNameInputLayout.error = getString(R.string.validation_empty_cluster_name)

                if (clusterViewModel.cluster?.value?.validClusterAddress == true) binding.clusterAddressInputLayout.error = null
                else binding.clusterAddressInputLayout.error = getString(R.string.validation_invalid_cluster_address)

                if (clusterViewModel.cluster?.value?.validClusterPort == true) binding.clusterPortInputLayout.error = null
                else binding.clusterPortInputLayout.error = getString(R.string.validation_invalid_port_address)

                if (clusterViewModel.cluster?.value?.validClusterBearerToken == true) binding.clusterBearerTokenInputLayout.error = null
                else binding.clusterBearerTokenInputLayout.error = getString(R.string.validation_empty_cluster_bearer_token)
            }
        }

        // Sets observer to show a TOAST when connection is or not successful
        clusterViewModel.connectionTestSuccessful.observe(viewLifecycleOwner) { connectionTestSuccessful ->
            connectionTestSuccessful?.let {
                if (it) Toast.makeText(context, R.string.connection_succeeded, Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, R.string.connection_failed, Toast.LENGTH_SHORT).show()
            }
        }

        // Sets observer to show progress bar and disable add/update buttons when submiting data
        clusterViewModel.processingData.observe(viewLifecycleOwner) {
            binding.addCluster.isEnabled = !it
            binding.updateCluster.isEnabled = !it
            if (it) showLoading()
            else hideLoading()
        }

        // Sets observer to hide the progress bar when data is being load for edition
        clusterViewModel.cluster?.observe(viewLifecycleOwner) {
            hideLoading()
        }

        showLoading()
        return view
    }

    private fun showLoading() {
        (activity as MainActivity).binding.mainProgressBar.visibility = View.VISIBLE
        (activity as MainActivity).binding.navHostFragment.visibility = View.GONE
    }

    private fun hideLoading() {
        (activity as MainActivity).binding.mainProgressBar.visibility = View.GONE
        (activity as MainActivity).binding.navHostFragment.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
