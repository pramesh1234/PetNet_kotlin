package com.naestem.petnet.ui.views

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.LauncherActivityInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.constants.AppConstants
import com.naestem.petnet.manager.SharedPrefManager
import com.naestem.petnet.model.AddressModel
import com.naestem.petnet.ui.viewmodel.HomeViewModel
import com.naestem.petnet.databinding.FragmentLocationBinding
import com.naestem.petnet.utils.JsonUtils
import java.util.*

class LocationFragment : MyBaseFragment() {
    private lateinit var binding: FragmentLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val viewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }
    private val fields = listOf(Place.Field.ID, Place.Field.NAME)
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val data= result.data
        when (result.resultCode) {
            RESULT_OK -> {
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    Log.i(TAG, "Place: ${place.name}, ${place.id}")
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                // TODO: Handle the error.
                data?.let {
                    val status = Autocomplete.getStatusFromIntent(data)
                    Log.i(TAG, status.statusMessage ?: "")
                }
            }
                RESULT_CANCELED -> {
            // The user canceled the operation.
            }

        }}
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationBinding.inflate(inflater)
        setUpLoader(viewModel)
        initViews()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initViews() {
        Places.initialize(requireContext(), getString(R.string.google_api_key));
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
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
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
binding.selectManuallyTV.setOnClickListener {
    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
        .build(requireContext())
    startForResult.launch(intent)
}
    }

    override fun onErrorCalled(it: String?) {
        TODO("Not yet implemented")
    }

    override fun initObservers() {
        viewModel.locationLiveData.observe(this) {

            val addresses: List<Address>
            val geocoder = Geocoder(requireContext(), Locale.getDefault());

            addresses = geocoder.getFromLocation(
                it.latitude,
                it.longitude,
                1
            ) as List<Address>; // Here 1 represent max location result to returned, by documents it recommended 1 to 5

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
            SharedPrefManager.getInstance(requireContext())
                .setPreference(AppConstants.ADDRESS, JsonUtils.toJson(addressModel))
            SharedPrefManager.getInstance(requireContext())
                .setPreference(AppConstants.IS_LOCATION_UPDATED, true)
            viewModel.updateAddress(addressModel)

        }
        viewModel.addressUpdatedLiveData.observe(this) {
            findNavController().navigate(R.id.action_locationFragment_to_mainFragment)
        }
    }

}