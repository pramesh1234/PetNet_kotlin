package com.naestem.petnet.ui.views.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.databinding.FragmentPhoneBinding
import com.naestem.petnet.ui.views.main.MainActivity


class PhoneFragment : MyBaseFragment() {
    lateinit var binding: FragmentPhoneBinding

    override fun onErrorCalled(it: String?) {

    }

    override fun initObservers() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }
    fun initViews(){
        binding.submitBtn.setOnClickListener {
            if (binding.phoneET.text.toString().length==10){
            val action = PhoneFragmentDirections.actionPhoneFragmentToOtpVerificationFragment(binding.phoneET.text.toString())
                it.findNavController().navigate(action)
            }
        }
binding.skipTV.setOnClickListener{
    val i = Intent(requireContext(), MainActivity::class.java)
    startActivity(i)
}
    }
}