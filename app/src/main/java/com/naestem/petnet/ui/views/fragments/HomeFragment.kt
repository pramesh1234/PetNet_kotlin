package com.naestem.petnet.ui.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.databinding.FragmentHomeBinding
import com.naestem.petnet.manager.SharedPrefManager
import com.naestem.petnet.model.AddressModel
import com.naestem.petnet.ui.viewmodel.HomeViewModel
import com.naestem.petnet.ui.views.AddPetActivity
import com.naestem.petnet.utils.JsonUtils


class HomeFragment : MyBaseFragment() {
    private val homeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }
    private lateinit var binding: FragmentHomeBinding
    override fun onErrorCalled(it: String?) {

    }

    override fun initObservers() {
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setUpLoader(homeViewModel)
        initViews()
        return binding.root
    }

    private fun initViews() {
        val address =
            SharedPrefManager.getInstance(requireContext()).getPreference(AppConstants.ADDRESS)
        val addressModel = JsonUtils.parseJson<AddressModel>(address!!)
        binding.location.text = "${addressModel.subLocality}, ${addressModel.city}"
        binding.addPetBtn.setOnClickListener {
            val intent = Intent(context, AddPetActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}