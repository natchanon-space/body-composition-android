package com.example.bodycomposition.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bodycomposition.R
import com.example.bodycomposition.databinding.FragmentGuestInputBinding

class GuestInputFragment : Fragment() {

    private var binding: FragmentGuestInputBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guest_input, container, false)
    }
}