package com.naestem.petnet.ui.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.naestem.petnet.base.viewmodels.MyBaseViewModel
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.helper.LoaderStatus
import com.naestem.petnet.manager.SharedPrefManager
import com.naestem.petnet.model.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LaunchViewModel(application: Application) : MyBaseViewModel(application) {
    private val auth = Firebase.auth

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://petnet-1f56e-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    val signUpSuccessLiveData = MutableLiveData<Boolean>()
    val loginSuccessLiveData = MutableLiveData<Boolean>()
    val phoneAuthSuccessLiveData = MutableLiveData<Boolean>()
    val userSavedSuccessLiveData = MutableLiveData<Boolean>()
    val isEmailAndPhoneValidationSuccess = MutableLiveData<Boolean>()
    val forgotPasswordSuccess = MutableLiveData<Boolean>()

    fun signUp(email: String, password: String) {
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            auth.createUserWithEmailAndPassword(email, password).await()
            signUpSuccessLiveData.postValue(true)
            isLoading.postValue(LoaderStatus.success)
        }
    }

    fun saveUser(user: UserData) {
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            val ref = user.userId?.let {
                databaseReference.child(AppConstants.BARTER).child(AppConstants.DOC)
                    .child(AppConstants.USER).child(it)
            }
            ref?.setValue(user)?.await()
            val userData = ref?.get()?.await()?.getValue(UserData::class.java)
            SharedPrefManager.getInstance(getApplication())
                .setPreference(AppConstants.USER_DATA, userData.toString())
            userSavedSuccessLiveData.postValue(true)
            isLoading.postValue(LoaderStatus.success)
        }
    }

    fun login(email: String, password: String) {
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            auth.signInWithEmailAndPassword(email, password).await()
            val ref =
                databaseReference.child(AppConstants.BARTER).child(AppConstants.DOC)
                    .child(AppConstants.USER).child(auth.uid!!)

            val userData = ref.get().await()?.getValue(UserData::class.java)
            SharedPrefManager.getInstance(getApplication())
                .setPreference(AppConstants.USER_DATA, userData.toString())
            loginSuccessLiveData.postValue(true)
            isLoading.postValue(LoaderStatus.success)
        }
    }

    fun phoneAuth(credential: PhoneAuthCredential) {
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            auth.signInWithCredential(credential).await()
            phoneAuthSuccessLiveData.postValue(true)
            isLoading.postValue(LoaderStatus.success)
        }
    }

    fun checkEmailAndPhoneIsUnique(email: String, phoneNo: String) {
        var emailPresent = true
        var phoneNoPresent = true
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            val emailJob = async {
                emailPresent = databaseReference.child(AppConstants.BARTER).child(AppConstants.DOC)
                    .child(AppConstants.USER).orderByChild("email").equalTo(email).get().await()
                    .exists()
            }
            val phoneNoJob = async {
                phoneNoPresent =
                    databaseReference.child(AppConstants.BARTER).child(AppConstants.DOC)
                        .child(AppConstants.USER).orderByChild("phoneNo").equalTo(phoneNo).get()
                        .await().exists()
            }
            phoneNoJob.await()
            emailJob.await()
            if (!phoneNoPresent && !emailPresent) {
                isEmailAndPhoneValidationSuccess.postValue(true)
            } else {
                if (phoneNoPresent) {
                    throw Exception("Phone no is already registered")

                } else if (emailPresent) {
                    throw Exception("Email is already registered")
                }
            }
            isLoading.postValue(LoaderStatus.success)
        }
    }

    fun forgotPassword(email: String) {
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            auth.sendPasswordResetEmail(email).await()
            forgotPasswordSuccess.postValue(true)
            isLoading.postValue(LoaderStatus.success)
        }
    }
}