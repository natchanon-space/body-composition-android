package com.example.bodycomposition.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentAddFaceBinding
import com.example.bodycomposition.model.FaceRegistrationViewModel

class AddFaceFragment : BaseFragment<FragmentAddFaceBinding>() {

    private val viewModel: FaceRegistrationViewModel by activityViewModels()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddFaceBinding {
        return FragmentAddFaceBinding.inflate(inflater, container, false)
    }

    override fun bindData() {
        binding.apply {
            addFaceFragment = this@AddFaceFragment
            viewModel = this.viewModel
        }

        binding.imageView.setImageBitmap(viewModel.faceBitmap.value)
    }

    companion object {
        const val TAG = "AddFaceFragment"
    }
}