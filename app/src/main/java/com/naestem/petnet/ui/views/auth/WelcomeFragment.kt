package com.naestem.petnet.ui.views.auth

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.naestem.petnet.R
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.databinding.FragmentWelcomeBinding


class WelcomeFragment : MyBaseFragment() {
    lateinit var binding: FragmentWelcomeBinding
    override fun onErrorCalled(it: String?) {
        TODO("Not yet implemented")
    }

    override fun initObservers() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    fun initViews() {
        val alreadyLoginClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                p0.findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                val color = ContextCompat.getColor(requireContext(), typedValue.resourceId)
                ds.color = color
            }
        }
        val alreadyLoginSS = SpannableString(getString(R.string.already_have_an_account_log_in))
        alreadyLoginSS.setSpan(alreadyLoginClickableSpan, 24, 30, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        binding.alreadyLoginTV.movementMethod = LinkMovementMethod.getInstance()
        val boldSpan = StyleSpan(Typeface.BOLD)
        alreadyLoginSS.setSpan(boldSpan, 24, 30, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        binding.alreadyLoginTV.text = alreadyLoginSS
        binding.signInEmailBtn.setOnClickListener {
//            val navHostFragment =
//                requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment
//            val navController = navHostFragment.navController

            it.findNavController().navigate(R.id.action_welcomeFragment_to_signUpFragment)

        }
    }

}