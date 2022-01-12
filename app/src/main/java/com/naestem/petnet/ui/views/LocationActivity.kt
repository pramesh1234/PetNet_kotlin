package com.naestem.petnet.ui.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naestem.petnet.base.views.MyBaseCompatActivity
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.databinding.ActivityLocationBinding
import com.naestem.petnet.manager.SharedPrefManager
import com.naestem.petnet.model.AddressModel
import com.naestem.petnet.model.ErrorModel
import com.naestem.petnet.ui.viewmodel.HomeViewModel
import com.naestem.petnet.utils.JsonUtils
import java.util.*

class LocationActivity : MyBaseCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val viewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setUpLoader(viewModel)
        setContentView(binding.root)
        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initViews() {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    viewModel.getLocation(fusedLocationClient)
                } else {

                }
            }
        binding.getCurrentLocationBtn.setOnClickListener {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    viewModel.getLocation(fusedLocationClient)

                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {

                }
                else -> {

                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            }
        }

    }

    override fun initObservers() {
        viewModel.locationLiveData.observe(this, {

            val addresses: List<Address>
            val geocoder = Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(
                it.latitude,
                it.longitude,
                1
            ); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            val address =
                addresses[0].getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality;
            val state = addresses[0].adminArea;
            val country = addresses[0].countryName;
            val postalCode = addresses[0].postalCode;
            val subLocality = addresses[0].subLocality
            val addressModel = AddressModel(
                subLocality,
                postalCode,
                city,
                state,
                country,
                it.longitude,
                it.latitude
            )
            SharedPrefManager.getInstance(this)
                .setPreference(AppConstants.ADDRESS, JsonUtils.toJson(addressModel))
            SharedPrefManager.getInstance(this)
                .setPreference(AppConstants.IS_LOCATION_UPDATED, true)
            viewModel.updateAddress(addressModel)

        })
        viewModel.addressUpdatedLiveData.observe(this, {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        })
    }

    override fun onErrorCalled(it: ErrorModel?) {
        if (it != null) {
            showSnackBar(it.message)
        }
    }
}