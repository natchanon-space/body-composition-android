package com.example.bodycomposition.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.icomon.icdevicemanager.model.other.ICConstant
import com.example.bodycomposition.R
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.dao.AppDatabase
import com.example.bodycomposition.dao.User
import com.example.bodycomposition.databinding.FragmentAddFaceBinding
import com.example.bodycomposition.model.DataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddFaceFragment : BaseFragment<FragmentAddFaceBinding>() {

    private val viewModel: DataViewModel by activityViewModels()

    private lateinit var db: AppDatabase

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAddFaceBinding {
        return FragmentAddFaceBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CREATED!")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData() {
        binding.apply {
            addFaceFragment = this@AddFaceFragment
            viewModel = this.viewModel
        }

        binding.imageView.setImageBitmap(viewModel.faceBitmap.value)
        binding.datePicker.setUpPicker(requireFragmentManager())

        db = AppDatabase.getDatabase(requireContext())

        Log.d(TAG, "set binding")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        
        Log.d(TAG, "resumed!")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun register() {
        val dao = db.userDao()

        val name = binding.name.text.toString()
        if (name.contentEquals("") || name == null) {
            Toast.makeText(requireContext(),"Empty name", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Empty name!!")
            return
        }

        val height = binding.height.text.toString()
        if (height.contentEquals("") || height == null) {
            Toast.makeText(requireContext(), "Empty height", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Empty height!!")
            return
        }

        val sex: String = when (binding.spinner.getSelectedItemValue()) {
            ICConstant.ICSexType.ICSexTypeUnknown -> "Unspecified"
            ICConstant.ICSexType.ICSexTypeMale -> "Male"
            ICConstant.ICSexType.ICSexTypeFemal -> "Female"
        }

        Log.d(TAG, "TEST FLOAT_ARRAY[0] ${viewModel.faceVector.value?.get(0)}")

        // Accessing database with coroutine scope
        lifecycleScope.launch(Dispatchers.IO) {
            dao.insertAll(User(0, name, Integer.parseInt(height), binding.datePicker.getLocalDate(), sex, viewModel.faceVector.value))
            Log.d(TAG, dao.getAll().size.toString())

            // Check if data is added
            val userList = dao.getAll()
            val size = userList.size
            Log.d(TAG, "name: ${userList[size-1].name} ${userList[size-1].faceVector} ${userList[size-1].date}")
        }

        Log.d(TAG, "navigate called!!")
        findNavController().navigate(R.id.action_addFaceFragment_to_loginFragment)
    }

    companion object {
        const val TAG = "AddFaceFragment"
    }
}