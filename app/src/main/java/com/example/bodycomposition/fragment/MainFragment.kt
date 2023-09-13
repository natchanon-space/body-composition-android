package com.example.bodycomposition.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bodycomposition.R
import com.example.bodycomposition.databinding.FragmentMainBinding
import com.example.bodycomposition.model.DataViewModel
import com.example.bodycomposition.model.UserType

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private val viewModel: DataViewModel by activityViewModels()

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