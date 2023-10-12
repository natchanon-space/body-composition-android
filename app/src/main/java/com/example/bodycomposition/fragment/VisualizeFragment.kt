package com.example.bodycomposition.fragment

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import cn.icomon.icdevicemanager.model.device.ICDevice
import cn.icomon.icdevicemanager.model.device.ICScanDeviceInfo
import com.example.bodycomposition.R
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentVisualizeBinding
import com.example.bodycomposition.model.DataViewModel
import com.example.bodycomposition.utils.Constant.DATE_FORMAT
import com.example.bodycomposition.utils.UserInfo
import java.time.format.DateTimeFormatter

class VisualizeFragment : BaseFragment<FragmentVisualizeBinding>() {

    private val viewModel: DataViewModel by activityViewModels()

    private var device: ICDevice? = null
    private var deviceInfo: ICScanDeviceInfo? = null
    private var unitIndex: Int? = null

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

        binding.deleteButton.isEnabled = false
        binding.addButton.isEnabled = true

        Log.d(TAG, "data in viewModel name: ${viewModel.userInfo.value?.name} height: ${viewModel.userInfo.value?.height} dob: ${viewModel.userInfo.value?.dateOfBirth}")
    }

    fun addDevice() {
        findNavController().navigate(R.id.action_visualizeFragment_to_scanDeviceFragment)
    }

    fun deleteDevice() {

    }

    companion object {
        private const val TAG = "VisualizeFragment"
    }
}