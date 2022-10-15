package com.naestem.petnet.ui.views.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.naestem.petnet.base.views.MyBaseFragment
import com.naestem.petnet.databinding.FragmentAccountBinding
import com.naestem.petnet.manager.SharedPrefManager
import com.naestem.petnet.ui.viewmodel.AccountViewModel
import com.naestem.petnet.ui.views.auth.LoginFragment

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AccountFragment : MyBaseFragment() {
    lateinit var binding: FragmentAccountBinding
    private var param1: String? = null
    private var param2: String? = null
    val viewModel by lazy {
        ViewModelProvider(this)[AccountViewModel::class.java]
    }

    override fun onErrorCalled(it: String?) {
        it?.let { error -> showSnackBar(error) }
    }

    override fun initObservers() {
        viewModel.logoutSuccess.observe(this) {
            SharedPrefManager.getInstance(requireContext()).clearPreference()
            val i = Intent(requireContext(), LoginFragment::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        setUpLoader(viewModel)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.logoutBtn.setOnClickListener {
            showConfirmation("Cancel","Ok","Logout","Are you sure?",object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    viewModel.logout()
                }

            })

        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}