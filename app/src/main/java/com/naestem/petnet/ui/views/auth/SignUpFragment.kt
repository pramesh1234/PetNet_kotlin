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
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.databinding.FragmentSignUpBinding
import com.naestem.petnet.helper.FormValidator
import com.naestem.petnet.model.UserData
import com.naestem.petnet.ui.viewmodel.LaunchViewModel


class SignUpFragment : MyBaseFragment() {
    private val viewModel: LaunchViewModel by lazy {
        ViewModelProvider(this)[LaunchViewModel::class.java]
    }
    var auth:FirebaseAuth?=null
    private lateinit var binding: FragmentSignUpBinding
    private var errorMsg = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        setUpLoader(viewModel)
        initViews()
        return binding.root
    }

    override fun onErrorCalled(it: String?) {
        it?.let { it1 -> showSnackBar(it1) }
    }

    override fun initObservers() {
        viewModel.signUpSuccessLiveData.observe(this) {
            val user = UserData(
                auth?.uid!!,
                binding.usernameET.text.toString(),
                binding.emailET.text.toString(),
                "",
                binding.passwordET.text.toString(),
                null,
                emailVerified = false,
                mobileNoVerified = false
            )
            viewModel.saveUser(user)
        }
        viewModel.userSavedSuccessLiveData.observe(this){
            findNavController().navigate(R.id.action_signUpFragment_to_phoneFragment)
        }
        viewModel.isEmailAndPhoneValidationSuccess.observe(this) {
              viewModel.signUp(binding.emailET.text.toString(),binding.passwordET.text.toString())
        }
    }


    private fun initViews() {
        auth = Firebase.auth
        binding.createAccountBtn.setOnClickListener {
            if (validateSignUpForm()) {
                viewModel.checkEmailAndPhoneIsUnique(
                    binding.emailET.text.toString()
                )

            } else {
                showSnackBar(errorMsg)
            }
        }
        binding.backBtnIV.setOnClickListener {
            it.findNavController().popBackStack()
        }
        val alreadyLoginClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                p0.findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                val color = ContextCompat.getColor(requireContext(), typedValue.resourceId)
                ds.color = color
            }
        }
        val alreadyLoginSS = SpannableString(getString(R.string.already_have_an_account_log_in))
        alreadyLoginSS.setSpan(alreadyLoginClickableSpan, 24, 30, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        binding.alreadyLoginTV.movementMethod = LinkMovementMethod.getInstance()
        val boldSpan = StyleSpan(Typeface.BOLD)
        alreadyLoginSS.setSpan(boldSpan, 24, 30, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        binding.alreadyLoginTV.text = alreadyLoginSS

        val termsAndConditionClickableSpan = object : ClickableSpan(){
            override fun onClick(p0: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                val color = ContextCompat.getColor(requireContext(), typedValue.resourceId)
                ds.color = color
            }

        }
        val privacyPolicyClickableSpan = object : ClickableSpan(){
            override fun onClick(p0: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                val color = ContextCompat.getColor(requireContext(), typedValue.resourceId)
                ds.color = color
            }

        }
        val termsAndConditionSpan = SpannableString(getString(R.string.i_agree_to_terms_amp_condition_nand_privacy_policy))
        termsAndConditionSpan.setSpan(privacyPolicyClickableSpan, 11, 28, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        termsAndConditionSpan.setSpan(termsAndConditionClickableSpan, 33, 47, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        binding.termsAndConditionCB.movementMethod = LinkMovementMethod.getInstance()
        val termsAndConditionBoldSpan = StyleSpan(Typeface.BOLD)
        val privacyPolicyBoldSpan = StyleSpan(Typeface.BOLD)
        termsAndConditionSpan.setSpan(termsAndConditionBoldSpan, 11, 28, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        termsAndConditionSpan.setSpan(privacyPolicyBoldSpan, 33, 47, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        binding.termsAndConditionCB.text = termsAndConditionSpan
    }

    private fun validateSignUpForm(): Boolean {
        var isValid = true
        if (!FormValidator.requiredField(binding.usernameET.text.toString(), 1)) {
            isValid = false
            errorMsg = getString(R.string.name_empty)
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