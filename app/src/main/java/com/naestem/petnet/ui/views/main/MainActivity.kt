package com.naestem.petnet.ui.views.main

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseCompatActivity
import com.naestem.petnet.databinding.ActivityMainBinding
import com.naestem.petnet.model.ErrorModel

class MainActivity : MyBaseCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }

    override fun initObservers() {
        TODO("Not yet implemented")
    }

    override fun onErrorCalled(it: ErrorModel?) {
        TODO("Not yet implemented")
    }

}