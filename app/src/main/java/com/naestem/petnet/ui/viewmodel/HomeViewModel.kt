package com.naestem.petnet.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.naestem.petnet.base.viewmodels.MyBaseViewModel
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.helper.LoaderStatus
import com.naestem.petnet.manager.SharedPrefManager
import com.naestem.petnet.model.AddressModel
import com.naestem.petnet.utils.JsonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(application: Application) : MyBaseViewModel(application) {
    val locationLiveData = MutableLiveData<Location>()
    private val auth = Firebase.auth
    val addressUpdatedLiveData = MutableLiveData<Boolean>()
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://petnet-1f56e-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    @SuppressLint("MissingPermission")
    fun getLocation(fusedLocationClient: FusedLocationProviderClient) {
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            val location = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
            locationLiveData.postValue(location)
            isLoading.postValue(LoaderStatus.success)
        }
    }

    fun updateAddress(address: AddressModel) {
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            val userId = auth.uid
            if (userId != null) {
                databaseReference.child(AppConstants.BARTER).child(AppConstants.DOC)
                    .child(AppConstants.USER).child(userId).child(AppConstants.ADDRESS)
                    .setValue(address).await()
                addressUpdatedLiveData.postValue(true)

            }
            isLoading.postValue(LoaderStatus.success)
        }
    }
}