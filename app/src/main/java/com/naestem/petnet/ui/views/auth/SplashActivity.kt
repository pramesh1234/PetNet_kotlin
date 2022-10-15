package com.naestem.petnet.ui.views.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.databinding.FragmentSplashBinding

class SplashActivity : MyBaseFragment() {
    lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    override fun initObservers() {
        TODO("Not yet implemented")
    }

    override fun onErrorCalled(it: String?) {
        TODO("Not yet implemented")
    }

    private fun initViews() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            if(sharedPrefManager.getBooleanPreference(AppConstants.IS_USER_LOGGED_IN)){
//                val intent = Intent(this, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
//            }else{
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//
//        }, WAIT_TIME)
//    }
    }
}