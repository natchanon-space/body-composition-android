package com.example.bodycomposition.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.bodycomposition.databinding.FragmentVisualizeBinding
import com.example.bodycomposition.model.DataViewModel

class VisualizeFragment : Fragment() {

    private lateinit var binding: FragmentVisualizeBinding

    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentVisualizeBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        // TODO: remove debug message
        Log.d("view-model", viewModel.height.value.toString())
        Log.d("view-model", viewModel.dateOfBirth.value.toString())
        Log.d("view-model", viewModel.userType.value.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("view-model", "destroyed!!")
    }
}