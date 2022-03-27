package com.example.k8s_android_console

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.k8s_android_console.databinding.FragmentSetClusterBinding


class SetClusterFragment : Fragment() {
    private var _binding : FragmentSetClusterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetClusterBinding.inflate(inflater, container, false)
        val view = binding.root

        // Get Room Database
        val application = requireNotNull(this.activity).application
        val dao = ClusterDatabase.getInstance(application).clusterDAO

        // We get the cluster id from the parameters and create the view model
        val clusterId = SetClusterFragmentArgs.fromBundle(requireArguments()).clusterId
        val clusterViewModelFactory = ClusterViewModelFactory(dao,clusterId)
        val clusterViewModel = ViewModelProvider(this, clusterViewModelFactory).get(ClusterViewModel::class.java)

        binding.clusterViewModel = clusterViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Sets observer to navigate back when a cluster is created/deleted/updated
        clusterViewModel.navigateToClusterList.observe(viewLifecycleOwner, Observer { navigate ->
            if(navigate) {
                view.findNavController().navigate(R.id.action_setClusterFragment_to_clustersFragment)
                clusterViewModel.onNavigatedToClusterList()
            }
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}