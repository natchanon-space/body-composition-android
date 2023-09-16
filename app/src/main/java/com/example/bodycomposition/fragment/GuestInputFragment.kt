package com.example.bodycomposition.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bodycomposition.R
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentGuestInputBinding
import com.example.bodycomposition.model.DataViewModel

class GuestInputFragment : BaseFragment<FragmentGuestInputBinding>() {

    private val viewModel: DataViewModel by activityViewModels()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGuestInputBinding {
        return FragmentGuestInputBinding.inflate(inflater, container, false)
    }

    override fun bindData() {
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