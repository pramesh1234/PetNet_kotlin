package com.naestem.petnet.ui.views.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.databinding.FragmentOtpVerificationBinding
import com.naestem.petnet.helper.OtpHelper
import com.naestem.petnet.ui.viewmodel.LaunchViewModel
import com.naestem.petnet.ui.views.main.MainActivity
import java.util.concurrent.TimeUnit

class OtpVerificationFragment : MyBaseFragment() {
    lateinit var binding: FragmentOtpVerificationBinding
    private var storedVerificationId: String? = ""
    lateinit var auth: FirebaseAuth
    val args: OtpVerificationFragmentArgs by navArgs()
    var resendToken: PhoneAuthProvider.ForceResendingToken?=null
    private val viewModel: LaunchViewModel by lazy {
        ViewModelProvider(this)[LaunchViewModel::class.java]
    }

    override fun onErrorCalled(it: String?) {
        showSnackBar("$it")
    }

    override fun initObservers() {
        viewModel.phoneAuthSuccessLiveData.observe(this) {
           val i = Intent(requireContext(),MainActivity::class.java)
            startActivity(i)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpVerificationBinding.inflate(layoutInflater)
        setUpLoader(viewModel)
        initViews()
        return binding.root
    }

    fun initViews() {
        auth = Firebase.auth
        args.phoneNumber?.let { startPhoneNumberVerification(it) }
        binding.phoneNoTV.text = getString(R.string.enter_6_digit_verification_that_sent,args.phoneNumber)
        binding.firstCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().trim().isNotEmpty()) {
                    binding.secondCodeET.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.secondCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.thirdCodeET.requestFocus()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.thirdCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.fourthCodeET.requestFocus()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.fourthCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.fifthCodeET.requestFocus()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.fifthCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.sixthCodeET.requestFocus()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.sixthCodeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.sixthCodeET.clearFocus()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })


        binding.submitBtn.setOnClickListener {
            if(validation()){
                val otp = "${binding.firstCodeET.text}${binding.secondCodeET.text}${binding.thirdCodeET.text}${binding.fourthCodeET.text}${binding.fifthCodeET.text}${binding.sixthCodeET.text}"
                verifyPhoneNumberWithCode(storedVerificationId, otp)

            }
        }
    }

    private fun validation(): Boolean {
        if (binding.firstCodeET.text.toString().isEmpty()) {
            return false
        }
        if (binding.secondCodeET.text.toString().isEmpty()) {
            return false
        }
        if (binding.thirdCodeET.text.toString().isEmpty()) {
            return false
        }
        if (binding.fourthCodeET.text.toString().isEmpty()) {
            return false
        }
        if (binding.fifthCodeET.text.toString().isEmpty()) {
            return false
        }
        if (binding.sixthCodeET.text.toString().isEmpty()) {
            return false
        }
        return true
    }
    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        viewModel.phoneAuth(credential)
    }
    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    viewModel.phoneAuth(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        e.message?.let { showSnackBar(it) }
                    } else if (e is FirebaseTooManyRequestsException) {
                        e.message?.let { showSnackBar(it) }
                    }
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    showSnackBar("OTP sent")
                    storedVerificationId = verificationId
                    resendToken = token
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}