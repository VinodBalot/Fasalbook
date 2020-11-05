package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityLoginBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.LoginResponse
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseBindingActivity() {

    var binding: ActivityLoginBinding? = null
    var onClickListener: View.OnClickListener? = null

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

                if (isValidFormData(
                        binding!!.edtMobile.text.toString(),
                        binding!!.edtPassword.text.toString()
                    )
                ) {
                    callLoginDataAPI()
                }

            }
        }

    }

    private fun isValidFormData(
        mobile: String,
        password: String
    ): Boolean {
        if (TextUtils.isEmpty(mobile)) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.enter_mobile_number)
            )
            return false
        }


        if (TextUtils.isEmpty(password)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_password))
            return false
        }

        if (password.length < 6) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.minimum_6_characters)
            )
            return false
        }

        return true
    }


    private fun callLoginDataAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("MobileNo", binding!!.edtMobile.text.toString())
        rootObject.addProperty("Password", binding!!.edtPassword.text.toString())
        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<LoginResponse> = apiService1.loginUser(gsonObject)
        call1.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.IsValidated) {
                            UtilityMethod.showToastMessageSuccess(
                                mActivity!!,
                                getString(R.string.label_user_registerd)
                            )
                            sessionManager!!.userId = binding!!.edtMobile.text.toString()
                            sessionManager!!.setLogin(true)
                            val intent = Intent(mActivity!!, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        } else {
                            UtilityMethod.showToastMessageError(
                                mActivity!!,
                                getString(R.string.label_user_no_registerd)
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }


}