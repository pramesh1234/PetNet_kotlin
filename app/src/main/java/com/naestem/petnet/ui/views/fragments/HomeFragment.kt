package com.naestem.petnet.ui.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.naestem.petnet.adapters.PetListAdapter
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.databinding.FragmentHomeBinding
import com.naestem.petnet.manager.SharedPrefManager
import com.naestem.petnet.model.AddPetModel
import com.naestem.petnet.model.AddressModel
import com.naestem.petnet.ui.viewmodel.HomeViewModel
import com.naestem.petnet.ui.views.AddPetActivity
import com.naestem.petnet.utils.JsonUtils


class HomeFragment : MyBaseFragment() {
    private val homeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var adapter: PetListAdapter? = null
    val petDataList = ArrayList<AddPetModel>()
    private lateinit var binding: FragmentHomeBinding
    override fun onErrorCalled(it: String?) {
        it?.let { error -> showSnackBar(error) }
    }

    override fun initObservers() {
        homeViewModel.petDataList.observe(this) {
            Log.e(TAG, "initObservers: ${it.species}")
        }
        lifecycleScope.launchWhenStarted {
            homeViewModel.getPetImageList().collect {
                adapter?.updatePetList(it)
            }
        }
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
        firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent("start_api") {
            param(FirebaseAnalytics.Param.ITEM_ID, "22")
            param(FirebaseAnalytics.Param.ITEM_NAME, "name")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image")

        }

        adapter = PetListAdapter(requireContext())
        binding.petRV.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding.petRV.adapter = adapter
        val address =
            SharedPrefManager.getInstance(requireContext()).getPreference(AppConstants.ADDRESS)
        val addressModel = JsonUtils.parseJson<AddressModel>(address!!)
        binding.location.text = "${addressModel.subLocality}, ${addressModel.city}"

        homeViewModel.getPetImageList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}