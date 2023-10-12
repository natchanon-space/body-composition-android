package com.example.bodycomposition.fragment

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bodycomposition.R
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.databinding.FragmentGuestInputBinding
import com.example.bodycomposition.model.DataViewModel
import com.example.bodycomposition.utils.UserInfo
import com.example.bodycomposition.utils.UserType

class GuestInputFragment : BaseFragment<FragmentGuestInputBinding>() {

    private val viewModel: DataViewModel by activityViewModels()

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGuestInputBinding {
        return FragmentGuestInputBinding.inflate(inflater, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData() {
        binding.apply {
            guestInputFragment = this@GuestInputFragment
            viewModel = this.viewModel
        }

        binding.datePicker.setUpPicker(requireFragmentManager())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSubmit() {

        val height = binding.height.text.toString()
        if (height.contentEquals("") || height == null) {
            Toast.makeText(requireContext(), "Empty height", Toast.LENGTH_SHORT).show()
            Log.d(AddFaceFragment.TAG, "Empty height!!")
            return
        }

        // TODO: update userInfo
        viewModel.setUserType(UserType.GUEST)
        viewModel.setUserInfo(UserInfo(binding.datePicker.getLocalDate()!!, height.toInt(), "Guest", 0))

        findNavController().navigate(R.id.action_guestInputFragment_to_visualizeFragment)
    }

    companion object {
        private const val TAG = "GuestInputFragment"
    }
}