package com.naestem.petnet.ui.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naestem.petnet.R
import com.naestem.petnet.adapters.ImageListAdapter
import com.naestem.petnet.base.views.MyBaseCompatActivity
import com.naestem.petnet.databinding.ActivityAddPetBinding
import com.naestem.petnet.helper.FormValidator
import com.naestem.petnet.model.AddPetModel
import com.naestem.petnet.model.AddressModel
import com.naestem.petnet.model.ErrorModel
import com.naestem.petnet.ui.viewmodel.AddPetViewModel
import java.util.*

class AddPetActivity : MyBaseCompatActivity() {
    private lateinit var binding: ActivityAddPetBinding
    var imageList: ArrayList<Uri>? = null
    var adoption = true
    var species: String? = null
    var breed: String? = null
    var age: String? = null
    var description: String? = null
    var location: String? = null
    var price: String? = null
    var errorMsg: String? = null
    var addressModel: AddressModel? = null

    val viewModel by lazy {
        ViewModelProvider(this)[AddPetViewModel::class.java]
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpLoader(viewModel)
        initViews()
    }

    override fun initObservers() {
        viewModel.imageUrlListLiveData.observe(this) {
            val petModel = AddPetModel(
                species,
                breed,
                addressModel,
                description,
                it,
                age,
                adoption,
                price,
                null,
                System.currentTimeMillis(),
                System.currentTimeMillis()
            )
            viewModel.addPet(petModel)
        }
        viewModel.petDataAdded.observe(this) {
            showSnackBar("Data added.")
        }
        viewModel.locationLiveData.observe(this) {
            val addresses: List<Address>
            val geocoder = Geocoder(this, Locale.getDefault());

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
            addressModel = AddressModel(
                subLocality,
                postalCode,
                city,
                state,
                country,
                it.longitude,
                it.latitude
            )
            location = "$subLocality, $city"
            binding.locationTV.text = location
        }
    }

    override fun onErrorCalled(it: ErrorModel?) {

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
        ArrayAdapter.createFromResource(
            this,
            R.array.planets_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.speciesAutoCompleteTV.setAdapter(adapter)
        }
        binding.getLocationBtn.setOnClickListener {
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
        binding.priceRB.setOnClickListener {
            if ((it as RadioButton).isChecked) {
                adoption = false
                binding.priceIL.visibility = View.VISIBLE
            }
        }
        binding.adoptionRB.setOnClickListener {
            if ((it as RadioButton).isChecked) {
                adoption = true
                binding.priceIL.visibility = View.GONE
            }
        }
        binding.cameraIV.setOnClickListener {
            selectImage()
        }
        binding.submitBtn.setOnClickListener {
            breed = binding.breedET.text.toString()
            price = binding.priceET.text.toString()
            age = binding.ageET.text.toString()
            description = binding.descriptionET.text.toString()
            species = binding.speciesAutoCompleteTV.text.toString()
            if (validateTheForm()) {
                viewModel.addPetPhotos(imageList!!)
            } else {
                showSnackBar(errorMsg!!)
            }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                val imagesUri = data?.clipData
                imagesUri?.let { images ->
                    imageList = ArrayList()
                    for (i in 0 until images.itemCount) {
                        imageList!!.add(images.getItemAt(i).uri)
                    }
                    val imageAdapter = ImageListAdapter(this, imageList!!)
                    binding.imagesRV.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    binding.imagesRV.adapter = imageAdapter
                }
            }
        }
    }

    private fun validateTheForm(): Boolean {
        if (!FormValidator.requiredField(species, 1)) {
            errorMsg = "Please select the species"
            return false
        } else if (!FormValidator.requiredField(breed, 1)) {
            errorMsg = "Please enter the breed"
            return false
        } else if (!FormValidator.requiredField(age, 1)) {
            errorMsg = "Please enter the age"
            return false
        } else if (!FormValidator.requiredField(description, 1)) {
            errorMsg = "Please enter the description"
            return false
        } else if (!adoption) {
            if (!FormValidator.requiredField(price, 1)) {
                errorMsg = "Please enter the price"
                return false
            }
        } else if (imageList == null && imageList?.size == 0) {
            errorMsg = "Please select the images"
            return false
        } else if (!FormValidator.requiredField(location, 1)) {
            errorMsg = "Please add location"
            return false
        }
        return true
    }
}
