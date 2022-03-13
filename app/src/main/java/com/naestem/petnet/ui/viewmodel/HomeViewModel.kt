package com.naestem.petnet.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.naestem.petnet.base.viewmodels.MyBaseViewModel
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.helper.LoaderStatus
import com.naestem.petnet.model.AddPetModel
import com.naestem.petnet.model.AddressModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel(application: Application) : MyBaseViewModel(application) {
    val locationLiveData = MutableLiveData<Location>()
    private val auth = Firebase.auth
    val petDataList = MutableLiveData<AddPetModel>()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPetImageList(): Flow<AddPetModel> = callbackFlow{
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(AddPetModel::class.java)?.let { trySend(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        val ref = databaseReference.child(AppConstants.BARTER).child(AppConstants.DOC)
            .child(AppConstants.PET_DATA)
        ref.addChildEventListener(childEventListener)
        awaitClose {
            ref.removeEventListener(childEventListener)
        }

    }
}