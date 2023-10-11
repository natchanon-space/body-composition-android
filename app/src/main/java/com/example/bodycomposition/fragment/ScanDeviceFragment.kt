package com.example.bodycomposition.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.bodycomposition.R
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentScanDeviceBinding

class ScanDeviceFragment : BaseFragment<FragmentScanDeviceBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentScanDeviceBinding {
        return FragmentScanDeviceBinding.inflate(inflater, container, false)
    }

    override fun bindData() {
        binding.apply {
            scanDeviceFragment = this@ScanDeviceFragment
        }
    }

    fun onSubmit() {
        findNavController().navigate(R.id.action_scanDeviceFragment_to_visualizeFragment)
    }
}
