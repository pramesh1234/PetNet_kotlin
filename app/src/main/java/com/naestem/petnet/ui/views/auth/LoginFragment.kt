package com.naestem.petnet.ui.views.auth

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.databinding.FragmentLoginBinding
import com.naestem.petnet.helper.FormValidator
import com.naestem.petnet.ui.viewmodel.LaunchViewModel

class LoginFragment : MyBaseFragment() {
    private lateinit var binding: FragmentLoginBinding
    private var errorText = ""
    val viewModel by lazy {
        ViewModelProvider(this)[LaunchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        setUpLoader(viewModel)
        initViews()
        return binding.root
    }


    override fun initObservers() {
        viewModel.loginSuccessLiveData.observe(this) {
//            sharedPrefManager.setPreference(AppConstants.IS_USER_LOGGED_IN,true)
//            if (sharedPrefManager.getBooleanPreference(AppConstants.IS_LOCATION_UPDATED)) {
//                val intent = Intent(this, HomeActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish()
//            } else {
//                val intent = Intent(this, LocationActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish()
//            }

        }
    }

    override fun onErrorCalled(it: String?) {
        if (it != null) {
            showSnackBar(it)
        }
    }

    private fun initViews() {

        binding.loginBtn.setOnClickListener {
            hideKeyboard()
            if (validateLoginForm()) {
                viewModel.login(
                    binding.emailET.text.toString(),
                    binding.passwordET.text.toString()
                )
            } else {
                showSnackBar(errorText)
            }

        }

        binding.forgotPasswordTV.setOnClickListener {
            it.findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        binding.backBtnIV.setOnClickListener {
             it.findNavController().popBackStack()
        }
        val dontHaveAccountClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                p0.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                val color = ContextCompat.getColor(requireContext(), typedValue.resourceId)
                ds.color = color
            }
        }
        val newLoginSS = SpannableString(getString(R.string.don_t_have_an_account_sign_up))
        newLoginSS.setSpan(dontHaveAccountClickableSpan, 23, 30, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        binding.dontHaveAccountTV.movementMethod = LinkMovementMethod.getInstance()
        val boldSpan = StyleSpan(Typeface.BOLD)
        newLoginSS.setSpan(boldSpan, 23, 30, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        binding.dontHaveAccountTV.text = newLoginSS
    }

    private fun validateLoginForm(): Boolean {
        var isValid = true
        if (!FormValidator.validateEmail(binding.emailET.text.toString())) {
            isValid = false
            errorText = getString(R.string.not_valid_email)
        } else if (!FormValidator.requiredField(binding.passwordET.text.toString(), 6)) {
            errorText = getString(R.string.password_not_valid)
            isValid = false
        }
        return isValid
    }
}