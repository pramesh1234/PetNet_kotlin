package com.naestem.petnet.ui.views.launch

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseCompatActivity
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.databinding.ActivityLoginBinding
import com.naestem.petnet.helper.FormValidator
import com.naestem.petnet.model.ErrorModel
import com.naestem.petnet.ui.viewmodel.LaunchViewModel
import com.naestem.petnet.ui.views.HomeActivity
import com.naestem.petnet.ui.views.LocationActivity

class LoginActivity : MyBaseCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var errorText = ""
    val viewModel by lazy {
        ViewModelProvider(this)[LaunchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpLoader(viewModel)
        initViews()
    }

    override fun initObservers() {
        viewModel.loginSuccessLiveData.observe(this, {
            if(sharedPrefManager.getBooleanPreference(AppConstants.IS_LOCATION_UPDATED)){
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, LocationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

        })
    }

    override fun onErrorCalled(it: ErrorModel?) {
        if (it != null) {
            showSnackBar(it.message)
        }
    }

    private fun initViews() {
        binding.loginBtn.setOnClickListener {
            hideKeyboard()
            if (validateLoginForm()) {

                viewModel.login(
                    binding.emailPhoneET.text.toString(),
                    binding.passwordET.text.toString()
                )
            } else {
                showSnackBar(errorText)
            }

        }
        binding.signUpTV.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.forgotPasswordTV.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateLoginForm(): Boolean {
        var isValid = true
        if (!FormValidator.validateEmail(binding.emailPhoneET.text.toString())) {
            isValid = false
            errorText = getString(R.string.not_valid_email)
        } else if (!FormValidator.requiredField(binding.passwordET.text.toString(), 6)) {
            errorText = getString(R.string.password_not_valid)
            isValid = false
        }
        return isValid
    }
}