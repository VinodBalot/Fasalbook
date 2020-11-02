package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.wasfat.R
import com.wasfat.databinding.ActivityLoginBinding
import com.wasfat.ui.base.BaseBindingActivity

class LoginActivity : BaseBindingActivity() {

    var binding: ActivityLoginBinding? = null
    var onClickListener: View.OnClickListener? = null
    // var viewModel: VendorViewModel? = null

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, LoginActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        // viewModel = ViewModelProvider(this).get(VendorViewModel::class.java)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
    }

    override fun setListeners() {
        binding!!.txtSignup.setOnClickListener(onClickListener)
        binding!!.btnLogin.setOnClickListener(onClickListener)
        binding!!.txtForgotPassword.setOnClickListener(onClickListener)

    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.txtSignup -> {
                RegisterActivity.startActivity(mActivity!!, null, false)
            }
            R.id.txtForgotPassword -> {
                ForgotPasswordActivity.startActivity(mActivity!!, null, false)
            }
            R.id.btnLogin -> {
                val intent = Intent(mActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }

    }


}