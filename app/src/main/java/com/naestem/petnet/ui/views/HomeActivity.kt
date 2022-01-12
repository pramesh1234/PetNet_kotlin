package com.naestem.petnet.ui.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseCompatActivity
import com.naestem.petnet.databinding.ActivityHomeBinding
import com.naestem.petnet.model.ErrorModel
import com.naestem.petnet.ui.viewmodel.HomeViewModel

class HomeActivity : MyBaseCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    val viewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpLoader(viewModel)
        initViews()

    }

    override fun initObservers() {

    }

    override fun onErrorCalled(it: ErrorModel?) {
        if (it != null) {
            showSnackBar(it.message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initViews() {
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        navView.setupWithNavController(navController)

    }
}