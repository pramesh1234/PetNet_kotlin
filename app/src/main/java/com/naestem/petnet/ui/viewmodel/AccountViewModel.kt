package com.naestem.petnet.ui.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.naestem.petnet.base.viewmodels.MyBaseViewModel
import com.naestem.petnet.helper.LoaderStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AccountViewModel(application: Application) : MyBaseViewModel(application) {
    var logoutSuccess = MutableLiveData<Boolean>()
    fun logout() {
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            FirebaseAuth.getInstance().signOut()
            logoutSuccess.postValue(true)
            isLoading.postValue(LoaderStatus.success)
        }
    }
}