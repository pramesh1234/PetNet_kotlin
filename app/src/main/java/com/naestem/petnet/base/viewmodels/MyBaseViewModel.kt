package com.naestem.petnet.base.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naestem.petnet.helper.ErrorMessageType
import com.naestem.petnet.helper.LoaderStatus
import com.naestem.petnet.model.ErrorModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class MyBaseViewModel(application: Application) : AndroidViewModel(application) {
    val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + Dispatchers.IO + exceptionHandler
    var errorLiveData = MutableLiveData<String>()

    var isLoading = MutableLiveData<LoaderStatus>()

    val exceptionHandler: CoroutineContext =
        CoroutineExceptionHandler { _, throwable ->
            isLoading.postValue(LoaderStatus.failed)
            errorLiveData.postValue(throwable.message)
            throwable.printStackTrace()
        }
    val errorMediatorLiveData = MediatorLiveData<ErrorModel?>()

    init {
        errorMediatorLiveData.addSource(errorLiveData) { result: String? ->
            result?.let {
                errorMediatorLiveData.postValue(ErrorModel(it, ErrorMessageType.snackbar))
            }
        }
    }
}