package com.example.bodycomposition.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bodycomposition.R
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentMainBinding
import com.example.bodycomposition.model.DataViewModel
import com.example.bodycomposition.utils.RequirePermissions
import com.example.bodycomposition.utils.UserType

class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val viewModel: DataViewModel by activityViewModels()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun bindData() {
        binding.apply {
            mainFragment = this@MainFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions on create
        if (!RequirePermissions.allPermissionsGranted(this)) {
            RequirePermissions.requestPermissions(this)
        }
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

    companion object {
        private const val TAG = "MainFragment"
    }

}