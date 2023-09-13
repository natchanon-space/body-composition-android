package com.example.bodycomposition.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bodycomposition.R
import com.example.bodycomposition.databinding.FragmentMainBinding
import com.example.bodycomposition.model.DataViewModel
import com.example.bodycomposition.model.UserType
import com.example.bodycomposition.utils.RequirePermissions.REQUIRED_PERMISSIONS

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val viewModel: DataViewModel by activityViewModels()

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(requireContext(),
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                // TODO: add something e.g. exit the app or ask again
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentMainBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        binding?.apply {
            mainFragment = this@MainFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions on create
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    fun goToGuestInput() {
        Log.d("Nav", findNavController().toString())
        viewModel.setUserType(UserType.GUEST)
        findNavController().navigate(R.id.action_mainFragment_to_guestInputFragment)
    }

    fun goToLogin() {
        viewModel.setUserType(UserType.AUTH)
        findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
    }

    fun goToFaceRegistration() {
        findNavController().navigate(R.id.action_mainFragment_to_faceRegistrationFragment)
    }
}