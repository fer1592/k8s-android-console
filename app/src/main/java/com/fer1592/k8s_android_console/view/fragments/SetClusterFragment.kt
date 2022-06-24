package com.fer1592.k8s_android_console.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.fer1592.k8s_android_console.R
import com.fer1592.k8s_android_console.databinding.FragmentSetClusterBinding
import com.fer1592.k8s_android_console.view.activities.MainActivity
import com.fer1592.k8s_android_console.viewmodel.ClusterViewModel
import com.google.android.material.snackbar.Snackbar

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

        // Sets observer to show SnackBar with a custom message for the user
        clusterViewModel.displayMessage.observe(viewLifecycleOwner) { messageID ->
            messageID?.let {
                Snackbar.make(binding.coordinatorLayout, messageID, Snackbar.LENGTH_SHORT).show()
                clusterViewModel.clearMessages()
            }
        }

        // Sets observer to show progress bar and disable add/update buttons when submiting data
        clusterViewModel.processingData.observe(viewLifecycleOwner) {
            // Hide keyboard when processing data
            activity?.let { mainActivity ->
                val imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE)
                mainActivity.currentFocus?.let { currentFocus -> (imm as InputMethodManager).hideSoftInputFromWindow(currentFocus.applicationWindowToken, 0) }
            }
            binding.addCluster.isEnabled = !it
            binding.updateCluster.isEnabled = !it
            if (it) (activity as MainActivity).showLoading()
            else (activity as MainActivity).hideLoading()
        }

        clusterViewModel.getCluster(clusterId, authMethods)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
