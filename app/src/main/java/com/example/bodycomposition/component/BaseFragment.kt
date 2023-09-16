package com.example.bodycomposition.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * Base class of Fragment using ViewDataBinding
 */
abstract class BaseFragment<VB: ViewDataBinding>: Fragment() {

    protected lateinit var binding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflateViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        bindData()
    }

    /**
     * Set inflate function of fragment binding:
     * `return VB.inflate(inflater, container, false)`
     */
    abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * Set up data binding:
     * `binding.apply {...}`
     * This is called in `onViewCreated(...)`
     */
    abstract fun bindData()
}