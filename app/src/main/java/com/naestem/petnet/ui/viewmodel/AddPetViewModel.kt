package com.naestem.petnet.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.naestem.petnet.base.viewmodels.MyBaseViewModel
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.helper.LoaderStatus
import com.naestem.petnet.model.AddPetModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddPetViewModel(application: Application) : MyBaseViewModel(application) {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance("https://petnet-1f56e-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    var petDataAdded = MutableLiveData<Boolean>()
    private val storage = Firebase.storage
    private val auth = Firebase.auth
    var storageRef = storage.reference
    var imageUrlListLiveData = MutableLiveData<ArrayList<String>>()
    val locationLiveData = MutableLiveData<Location>()
    fun addPetPhotos(imageList: ArrayList<Uri>) {
        val urlList = ArrayList<String>()
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            for (image in 0 until imageList.size) {
                var imagesRef: StorageReference? =
                    storageRef.child("petImages/${auth.uid}/${System.currentTimeMillis()}")
                imagesRef?.putFile(imageList[image])?.await()
                val ref = imagesRef?.downloadUrl?.await()
                urlList.add(ref.toString())
            }
            imageUrlListLiveData.postValue(urlList)
            isLoading.postValue(LoaderStatus.success)
        }
    }

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

    fun addPet(pet: AddPetModel) {
        CoroutineScope(coroutineContext).launch {
            isLoading.postValue(LoaderStatus.loading)
            pet.createdBy = auth.uid
            databaseReference.child(AppConstants.BARTER).child(AppConstants.DOC)
                .child(AppConstants.PET_DATA).push().setValue(pet).await()
            petDataAdded.postValue(true)
            isLoading.postValue(LoaderStatus.success)
        }
    }
}