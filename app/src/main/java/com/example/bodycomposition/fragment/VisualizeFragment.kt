package com.example.bodycomposition.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentVisualizeBinding
import com.example.bodycomposition.model.DataViewModel

class VisualizeFragment : BaseFragment<FragmentVisualizeBinding>() {

    private val viewModel: DataViewModel by activityViewModels()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVisualizeBinding {
        return FragmentVisualizeBinding.inflate(inflater, container, false)
    }

    override fun bindData() {
        binding.apply {
            viewModel = this.viewModel
        }

        Log.d(TAG, "data in viewModel name: ${viewModel.userInfo.value?.name} height: ${viewModel.userInfo.value?.height}")
    }

    companion object {
        private const val TAG = "VisualizeFragment"
    }
}