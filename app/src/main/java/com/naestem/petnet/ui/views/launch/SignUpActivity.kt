package com.naestem.petnet.ui.views.launch

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseCompatActivity
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.databinding.ActivitySignUpBinding
import com.naestem.petnet.helper.FormValidator
import com.naestem.petnet.model.ErrorModel
import com.naestem.petnet.ui.viewmodel.LaunchViewModel
import com.naestem.petnet.ui.views.OtpConfirmationActivity

class SignUpActivity : MyBaseCompatActivity() {
    private val viewModel: LaunchViewModel by lazy {
        ViewModelProvider(this)[LaunchViewModel::class.java]
    }
    private lateinit var binding: ActivitySignUpBinding
    private var errorMsg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpLoader(viewModel)
        initViews()
    }

    override fun initObservers() {
        viewModel.signUpSuccessLiveData.observe(this, {

        })
        viewModel.isEmailAndPhoneValidationSuccess.observe(this, {
            val intent = Intent(this, OtpConfirmationActivity::class.java)
            intent.putExtra(AppConstants.PHONE_NUMBER, binding.phoneET.text.toString())
            intent.putExtra(AppConstants.FULL_NAME, binding.fullNameET.text.toString())
            intent.putExtra(AppConstants.EMAIL, binding.emailET.text.toString())
            intent.putExtra(AppConstants.PASSWORD, binding.passwordET.text.toString())
            startActivity(intent)
        })
    }

    override fun onErrorCalled(it: ErrorModel?) {
        it?.message?.let { it1 -> showSnackBar(it1) }
    }

    private fun initViews() {
        binding.signUpBtn.setOnClickListener {
            if (validateSignUpForm()) {
                viewModel.checkEmailAndPhoneIsUnique(
                    binding.emailET.text.toString(),
                    binding.phoneET.text.toString()
                )

            } else {
                showSnackBar(errorMsg)
            }
        }
        binding.loginTV.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateSignUpForm(): Boolean {
        var isValid = true
        if (!FormValidator.requiredField(binding.fullNameET.text.toString(), 1)) {
            isValid = false
            errorMsg = getString(R.string.name_empty)
        } else if (!FormValidator.requiredField(binding.phoneET.text.toString(), 10)) {
            isValid = false
            errorMsg = getString(R.string.no_is_not_valid)
        } else if (!FormValidator.validateEmail(binding.emailET.text.toString())) {
            isValid = false
            errorMsg = getString(R.string.email_not_valid)
        } else if (!FormValidator.requiredField(binding.passwordET.text.toString(), 6)) {
            isValid = false
            errorMsg = getString(R.string.password_not_valid)
        }
        return isValid
    }
}