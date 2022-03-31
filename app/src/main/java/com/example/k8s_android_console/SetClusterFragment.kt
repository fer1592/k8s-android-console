package com.example.k8s_android_console

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.k8s_android_console.databinding.FragmentSetClusterBinding


class SetClusterFragment : Fragment() {
    private var _binding : FragmentSetClusterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetClusterBinding.inflate(inflater, container, false)
        val view = binding.root

        // Get Room Database
        val application = requireNotNull(this.activity).application
        val dao = ClusterDatabase.getInstance(application).clusterDAO

        // Get list of auth methods
        val authMethods = resources.getStringArray(R.array.auth_methods).toList()

        // We get the cluster id from the parameters and create the view model
        val clusterId = SetClusterFragmentArgs.fromBundle(requireArguments()).clusterId
        val clusterViewModelFactory = ClusterViewModelFactory(dao, clusterId, authMethods)
        val clusterViewModel = ViewModelProvider(this, clusterViewModelFactory)[ClusterViewModel::class.java]

        if(clusterId == (-1).toLong()) {
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.add_cluster)
        }
        else (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.update_cluster)

        binding.clusterViewModel = clusterViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Set the spinner options and selected value
        ArrayAdapter.createFromResource(application, R.array.auth_methods, R.layout.spinner_item).also { adapter ->
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

        // Sets observer that displays or hide error messages when input validation occurs
        clusterViewModel.clusterNameEmpty.observe(viewLifecycleOwner) { clusterNameEmpty ->
            if(clusterNameEmpty) binding.clusterNameInputLayout.error = getString(R.string.validation_empty_cluster_name)
            else binding.clusterNameInputLayout.error = null
        }

        clusterViewModel.clusterAddressInvalid.observe(viewLifecycleOwner) { clusterAddressInvalid ->
            if(clusterAddressInvalid) binding.clusterAddressInputLayout.error = getString(R.string.validation_invalid_cluster_address)
            else binding.clusterAddressInputLayout.error = null
        }

        clusterViewModel.clusterPortInvalid.observe(viewLifecycleOwner) { clusterPortInvalid ->
            if(clusterPortInvalid) binding.clusterPortInputLayout.error = getString(R.string.validation_invalid_port_address)
            else binding.clusterPortInputLayout.error = null
        }

        clusterViewModel.clusterUsernameEmpty.observe(viewLifecycleOwner) { clusterUsernameEmpty ->
            if(clusterUsernameEmpty) binding.clusterUsernameInputLayout.error = getString(R.string.validation_empty_cluster_username)
            else binding.clusterUsernameInputLayout.error = null
        }

        clusterViewModel.clusterPasswordEmpty.observe(viewLifecycleOwner) { clusterPasswordEmpty ->
            if(clusterPasswordEmpty) binding.clusterPasswordInputLayout.error = getString(R.string.validation_empty_cluster_password)
            else binding.clusterPasswordInputLayout.error = null
        }

        clusterViewModel.clusterBearerTokenEmpty.observe(viewLifecycleOwner) { clusterBearerTokenEmpty ->
            if(clusterBearerTokenEmpty) binding.clusterBearerTokenInputLayout.error = getString(R.string.validation_empty_cluster_bearer_token)
            else binding.clusterBearerTokenInputLayout.error = null
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}