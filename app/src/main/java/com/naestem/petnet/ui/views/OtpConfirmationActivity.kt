package com.naestem.petnet.ui.views

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naestem.petnet.base.views.MyBaseCompatActivity
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.databinding.ActivityOtpConfirmationBinding
import com.naestem.petnet.helper.FormValidator
import com.naestem.petnet.model.ErrorModel
import com.naestem.petnet.model.UserData
import com.naestem.petnet.ui.viewmodel.LaunchViewModel
import java.util.concurrent.TimeUnit

class OtpConfirmationActivity : MyBaseCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityOtpConfirmationBinding
    private var storedVerificationId: String? = ""
    lateinit var email: String
    lateinit var password: String
    lateinit var fullName: String
    lateinit var phoneNumber: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    val viewModel by lazy {
        ViewModelProvider(this)[LaunchViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpLoader(viewModel)
        auth = Firebase.auth
        initViews()
    }

    override fun initObservers() {
        viewModel.phoneAuthSuccessLiveData.observe(this) {
            viewModel.signUp(email, password)
        }
        viewModel.signUpSuccessLiveData.observe(this) {
            val user = UserData(
                auth.uid!!,
                fullName,
                email,
                phoneNumber,
                password,
                null,
                false,
                true
            )
            viewModel.saveUser(user)
        }
        viewModel.userSavedSuccessLiveData.observe(this) {
            val intent = Intent(this@OtpConfirmationActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onErrorCalled(it: ErrorModel?) {
        if (it != null) {
            showSnackBar(it.message)
        }
    }

    private fun initViews() {
        phoneNumber = intent.getStringExtra(AppConstants.PHONE_NUMBER).toString()
        fullName = intent.getStringExtra(AppConstants.FULL_NAME).toString()
        email = intent.getStringExtra(AppConstants.EMAIL).toString()
        password = intent.getStringExtra(AppConstants.PASSWORD).toString()

        startPhoneNumberVerification(phoneNumber)

        binding.submitBtn.setOnClickListener {
            if (FormValidator.requiredField(binding.otpET.text.toString(), 6)) {
                verifyPhoneNumberWithCode(storedVerificationId, binding.otpET.text.toString())
            }
        }
        binding.resendBtn.setOnClickListener {
            resendVerificationCode(phoneNumber, resendToken)
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
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

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        viewModel.phoneAuth(credential)
    }


    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
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
        if (token != null) {
            optionsBuilder.setForceResendingToken(token)
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

}