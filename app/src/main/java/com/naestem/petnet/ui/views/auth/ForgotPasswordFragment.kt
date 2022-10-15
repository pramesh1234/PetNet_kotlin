package com.naestem.petnet.ui.views.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.databinding.FragmentForgotPasswordBinding
import com.naestem.petnet.helper.FormValidator
import com.naestem.petnet.ui.viewmodel.LaunchViewModel

class ForgotPasswordFragment : MyBaseFragment() {
    private var errorMsg = ""
    private val viewModel by lazy {
        ViewModelProvider(this)[LaunchViewModel::class.java]
    }
    lateinit var binding: FragmentForgotPasswordBinding

    private fun initViews() {
        binding.submitBtn.setOnClickListener {
            if (validForm()) {
                viewModel.forgotPassword(binding.emailET.text.toString())
            } else {
                showSnackBar(errorMsg)
            }
        }
        binding.backBtnIV.setOnClickListener {
            it.findNavController().popBackStack()
        }
    }

    override fun onErrorCalled(it: String?) {

    }

    override fun initObservers() {
        viewModel.forgotPasswordSuccess.observe(this) {
            showSnackBar(getString(R.string.email_sent))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    private fun validForm(): Boolean {
        var isValid = true
      if (!FormValidator.validateEmail(binding.emailET.text.toString())) {
            errorMsg = getString(R.string.not_valid_email)
            isValid = false
        }
        return isValid
    }
}