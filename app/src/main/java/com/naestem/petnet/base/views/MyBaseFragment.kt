package com.naestem.petnet.base.views

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.naestem.petnet.R
import com.naestem.petnet.base.viewmodels.MyBaseViewModel
import com.naestem.petnet.helper.LoaderStatus
import com.naestem.petnet.manager.SharedPrefManager
import java.util.*

abstract class MyBaseFragment : Fragment() {
    protected val TAG = this.javaClass.simpleName
    private var mBaseView: ViewGroup? = null
    private var mLoaderView: View? = null
    private var progressShown = false
    private var futureShowProgress = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgress(view)
    }

    //Call this function to set up loaders
    protected fun setUpLoader(
        viewModel: MyBaseViewModel,

        ) {


        val color = ContextCompat.getColor(requireActivity(), R.color.colorSecondary)
        // val doneBitmap = BitmapFactory.decodeResource(resources, R.drawable.wh)

        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it.equals(LoaderStatus.loading))
                showProgress()
            else
                hideProgress()

        })

        viewModel.errorMediatorLiveData.observe(this, Observer
        {
            it?.let {
                var updatedErrorMessage: String? = null
                if (it.message.contains("_")) {
                    updatedErrorMessage = it.message.replace("_", " ")
                    showSnackBar(updatedErrorMessage.lowercase())
                } else {
                    updatedErrorMessage = it.message
                    showSnackBar(updatedErrorMessage.lowercase())
                }


                onErrorCalled(updatedErrorMessage.lowercase())
            }
        })

        initObservers()

    }

    protected abstract fun onErrorCalled(it: String?)

    protected abstract fun initObservers()

    protected fun initProgress(view: View) {
        mBaseView = view as ViewGroup

        mLoaderView = View.inflate(activity, R.layout.loader, null)

        mBaseView?.let {
            if (futureShowProgress)
                showProgress()
        }
    }

    fun getSharedPrefManager(): SharedPrefManager {
        return SharedPrefManager.getInstance(requireContext())
    }

    fun hideKeyboard() {
        activity?.let {
            val imm = it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = it.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(it)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    fun showKeyboard() {
        activity?.let {
            val inputManager =
                it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // check if no view has focus:
            val view = it.currentFocus
            view?.let {
                inputManager.toggleSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.SHOW_FORCED,
                    0
                )
            }
        }
    }


    fun showSnackBar(text: String) {
        val updatedMessage: String = Html.fromHtml(text).toString()

        mBaseView?.let {
            val snackBar = Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                updatedMessage.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                Snackbar.LENGTH_LONG
            )
            snackBar.duration = 2200
            val snackBarView = snackBar.view
            activity?.let {
                snackBarView.setBackgroundColor(ContextCompat.getColor(it, R.color.colorPrimary))

                val tv = snackBarView.findViewById(R.id.snackbar_text) as TextView
                tv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                tv.gravity = Gravity.CENTER
                snackBar.show()
            }
        }

    }

    fun showSnackbarWithDuration(text: String, duration: Int) {
        val updatedMessgae: String = Html.fromHtml(text).toString()

        mBaseView?.let {
            val snackbar = Snackbar.make(
                activity?.findViewById(android.R.id.content)!!,
                updatedMessgae.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                Snackbar.LENGTH_LONG
            )
            snackbar.duration = duration
            val snackbarView = snackbar.view
            activity?.let {
                snackbarView.setBackgroundColor(ContextCompat.getColor(it, R.color.colorSecondary))

                val tv = snackbarView.findViewById(R.id.snackbar_text) as TextView
                tv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.error))
                tv.gravity = Gravity.CENTER
                snackbar.show()
            }
        }

    }

    fun showProgress() {
        hideKeyboard()
        if (mBaseView == null) {
            futureShowProgress = true
        } else if (!progressShown) {
            mBaseView!!.addView(mLoaderView)
            progressShown = true
            futureShowProgress = false
        } else {
            futureShowProgress = false
        }
    }

    fun hideProgress() {
        futureShowProgress = false
        if (progressShown) {
            mBaseView?.removeView(mLoaderView)
            progressShown = false
        }
    }

    protected fun showToast(s: String) {
        activity?.let {
            Toast.makeText(it, s, Toast.LENGTH_LONG).show()
        }
    }

    protected fun showAlertDialogOk(
        title: String,
        message: String,
        listener: DialogInterface.OnClickListener
    ) {
        context?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("OK", listener)
            val mAlertDialog = builder.create()
            mAlertDialog.setCanceledOnTouchOutside(false)
            mAlertDialog.setCanceledOnTouchOutside(false)

            mAlertDialog.setOnShowListener {
                context?.let {
                    mAlertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(it, R.color.black))
                }
            }
            mAlertDialog.show()
        }
    }

    protected fun showConfirmation(
        negativeText: String,
        positiveText: String,
        title: String,
        message: String,
        listener: DialogInterface.OnClickListener
    ) {
        context?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton(positiveText, listener)
            builder.setNegativeButton(negativeText
            ) { dialog, which -> dialog.dismiss() }
            val mAlertDialog = builder.create()
            mAlertDialog.setCanceledOnTouchOutside(false)
            mAlertDialog.setCancelable(false)

            mAlertDialog.setOnShowListener {
                context?.let {
                    mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(it, R.color.colorSecondary))
                    mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(it, R.color.white))
                }
            }
            mAlertDialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}