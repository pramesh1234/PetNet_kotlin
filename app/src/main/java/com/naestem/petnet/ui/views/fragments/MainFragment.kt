package com.naestem.petnet.ui.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.databinding.FragmentMainBinding

class MainFragment : MyBaseFragment() {
    lateinit var binding: FragmentMainBinding
    override fun onErrorCalled(it: String?) {

    }

    override fun initObservers() {

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentMainBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }
    fun initViews(){
binding.navView.background = null
        binding.navView.menu.get(2).isEnabled = false
    }
}