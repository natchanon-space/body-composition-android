package com.example.bodycomposition.fragment

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentVisualizeBinding
import com.example.bodycomposition.model.DataViewModel
import com.example.bodycomposition.utils.Constant.DATE_FORMAT
import com.example.bodycomposition.utils.UserInfo
import java.time.format.DateTimeFormatter

class VisualizeFragment : BaseFragment<FragmentVisualizeBinding>() {

    private val viewModel: DataViewModel by activityViewModels()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVisualizeBinding {
        return FragmentVisualizeBinding.inflate(inflater, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData() {
        binding.apply {
            viewModel = this.viewModel
        }

        val currentUserInfo: UserInfo = viewModel.userInfo.value!!
        binding.name.text = currentUserInfo.name
        binding.height.text = currentUserInfo.height.toString()
        binding.date.text = currentUserInfo.dateOfBirth.format(DateTimeFormatter.ofPattern(DATE_FORMAT))

        Log.d(TAG, "data in viewModel name: ${viewModel.userInfo.value?.name} height: ${viewModel.userInfo.value?.height} dob: ${viewModel.userInfo.value?.dateOfBirth}")
    }

    companion object {
        private const val TAG = "VisualizeFragment"
    }
}