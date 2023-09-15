package com.example.bodycomposition.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bodycomposition.databinding.FragmentAddFaceBinding
import com.example.bodycomposition.model.FaceRegistrationViewModel

class AddFaceFragment : Fragment() {

    private lateinit var binding: FragmentAddFaceBinding

    private val viewModel: FaceRegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentAddFaceBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        Log.d(TAG, "(onViewCreated) has faceBitmap ${viewModel.faceBitmap.value != null}")

        binding.apply {
            addFaceFragment = this@AddFaceFragment
            viewModel = this.viewModel
        }
    }

    companion object {
        const val TAG = "AddFaceFragment"
    }
}