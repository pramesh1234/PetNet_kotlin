package com.naestem.petnet.base.views

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.naestem.petnet.R
import com.naestem.petnet.base.viewmodels.MyBaseViewModel
import com.naestem.petnet.helper.LoaderStatus
import com.naestem.petnet.manager.SharedPrefManager
import com.naestem.petnet.model.ErrorModel
import java.util.Observer

abstract class MyBaseCompatActivity : AppCompatActivity() {
    val WAIT_TIME: Long = 3000
    private var mBaseView: ViewGroup? = null
    private var mLoaderView: View? = null
    private var progressShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initProgress()
    }
    val sharedPrefManager: SharedPrefManager
        get() {
            return SharedPrefManager.getInstance(this)
        }
    //To hide Keyboard
    fun hideKeyboard() {
        val inputMethodManager =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager;
        val view = this.currentFocus;
        if (view != null)
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0);
    }

    //To show keyboard
    fun showKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // check if no view has focus:
        val view = this.currentFocus
        if (view != null)
            inputManager.toggleSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.SHOW_FORCED,
                0
            )
    }
    protected fun setUpLoader(viewModel: MyBaseViewModel) {
        viewModel.isLoading.observe(this, {
            if (it.equals(LoaderStatus.loading))
                showProgress()
            else
                hideProgress()
        })

        viewModel.errorMediatorLiveData.observe(this, {
            it?.let {
                onErrorCalled(it)

            }
        })

        initObservers()

    }
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun initProgress() {
        mBaseView = this.findViewById(android.R.id.content)
        mLoaderView = View.inflate(this, R.layout.loader, null)
    }
    fun showSnackBar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
    abstract fun initObservers()

    protected abstract fun onErrorCalled(it: ErrorModel?)
    private fun showProgress(){
        hideKeyboard()
        if (!progressShown) {
            mBaseView!!.addView(mLoaderView)
            progressShown = true

        }

    }
    private fun hideProgress(){
        hideKeyboard()
        if (progressShown) {
            mBaseView!!.removeView(mLoaderView)
            progressShown = false

        }}
}
