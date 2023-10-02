package com.example.bodycomposition.fragment

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.bodycomposition.component.BaseFragment
import com.example.bodycomposition.dao.AppDatabase
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData() {
        binding.apply {
            addFaceFragment = this@AddFaceFragment
            viewModel = this.viewModel
        }

        binding.imageView.setImageBitmap(viewModel.faceBitmap.value)
        binding.datePicker.setUpPicker(requireFragmentManager())

        db = AppDatabase.getDatabase(requireContext())

    }

    fun register() {
        val dao = db.userDao()

        // Accessing database with coroutine scope
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d(TAG, dao.getAll().size.toString())
        }
    }

    companion object {
        const val TAG = "AddFaceFragment"
    }
}