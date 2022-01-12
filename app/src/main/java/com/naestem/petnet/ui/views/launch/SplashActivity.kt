package com.naestem.petnet.ui.views.launch

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.naestem.petnet.base.views.MyBaseCompatActivity
import com.naestem.petnet.databinding.ActivitySplashBinding
import com.naestem.petnet.model.ErrorModel

class SplashActivity : MyBaseCompatActivity() {
    lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    override fun initObservers() {
        TODO("Not yet implemented")
    }

    override fun onErrorCalled(it: ErrorModel?) {
        TODO("Not yet implemented")
    }

    private fun initViews() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, WAIT_TIME)
    }
}