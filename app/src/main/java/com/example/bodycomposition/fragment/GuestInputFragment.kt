package com.example.bodycomposition.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bodycomposition.R
import com.example.bodycomposition.databinding.FragmentGuestInputBinding
import com.example.bodycomposition.model.DataViewModel

class GuestInputFragment : Fragment() {

    private lateinit var binding: FragmentGuestInputBinding

    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentGuestInputBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        binding.apply {
            guestInputFragment = this@GuestInputFragment
        }
    }

    fun onSubmit() {
        viewModel.setData(
            dateOfBirth = binding.date.text.toString(),
            height = binding.height.text.toString()
        )
        findNavController().navigate(R.id.action_guestInputFragment_to_visualizeFragment)
    }

    companion object {
        private const val TAG = "GuestInputFragment"
    }
}