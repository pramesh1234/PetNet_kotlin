package com.naestem.petnet.ui.views.launch

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseCompatActivity
import com.naestem.petnet.databinding.ActivityForgotPasswordBinding
import com.naestem.petnet.helper.FormValidator
import com.naestem.petnet.model.ErrorModel
import com.naestem.petnet.ui.viewmodel.LaunchViewModel

class ForgotPasswordActivity : MyBaseCompatActivity() {
    var errorMsg = ""
    private val viewModel by lazy {
        ViewModelProvider(this)[LaunchViewModel::class.java]
    }
    lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpLoader(viewModel)
        initViews()
    }

    private fun initViews() {
        binding.submitBtn.setOnClickListener {
            if (validForm()) {
                viewModel.forgotPassword(binding.emailPhoneET.text.toString())
            } else {
                showSnackBar(errorMsg)
            }
        }
    }

    override fun initObservers() {
        viewModel.forgotPasswordSuccess.observe(this, {
           showSnackBar(getString(R.string.email_sent))
        })
    }

    override fun onErrorCalled(it: ErrorModel?) {
        if (it != null) {
            showSnackBar(it.message)
        }
    }

    private fun validForm(): Boolean {
        var isValid = true
        if (!FormValidator.requiredField(binding.emailPhoneET.text.toString(), 1)) {
            errorMsg = "Email field is empty"
            isValid = false
        } else if (!FormValidator.validateEmail(binding.emailPhoneET.text.toString())) {
            errorMsg = "Email is invalid"
            isValid = false
        }
        return isValid
    }
}