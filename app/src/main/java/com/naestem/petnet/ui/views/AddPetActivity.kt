package com.naestem.petnet.ui.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.naestem.petnet.R
import com.naestem.petnet.adapters.ImageListAdapter
import com.naestem.petnet.base.views.MyBaseCompatActivity
import com.naestem.petnet.databinding.ActivityAddPetBinding
import com.naestem.petnet.model.ErrorModel
import com.naestem.petnet.ui.viewmodel.AddPetViewModel

class AddPetActivity : MyBaseCompatActivity() {
    private lateinit var binding: ActivityAddPetBinding
    var imageList: ArrayList<Uri>? = null
    var adoption = true
    val viewModel by lazy {
        ViewModelProvider(this)[AddPetViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpLoader(viewModel)
        initViews()
    }

    override fun initObservers() {

    }

    override fun onErrorCalled(it: ErrorModel?) {

    }

    private fun initViews() {
        ArrayAdapter.createFromResource(
            this,
            R.array.planets_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.speciesAutoCompleteTV.setAdapter(adapter)
        }
        binding.getLocationBtn.setOnClickListener {
            showToast("${binding.speciesAutoCompleteTV.text.toString()}")
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

}
