package com.wasfat.ui.activity.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityForgotPasswordBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.OTPVerificationResponse
import com.wasfat.ui.pojo.PhoneVerificationType
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : BaseBindingActivity() {

    var binding: ActivityForgotPasswordBinding? = null
    var onClickListener: View.OnClickListener? = null

    lateinit var phoneNumber: String

    companion object {

        fun startActivity(activity: Activity, phoneNumber: String, isClear: Boolean) {
            val intent = Intent(activity, ForgotPasswordActivity::class.java)
            intent.putExtra("phoneNumber", phoneNumber)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        // viewModel = ViewModelProvider(this).get(VendorViewModel::class.java)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this

        phoneNumber = intent.getStringExtra("phoneNumber").toString()

    }

    override fun setListeners() {
        binding!!.btnChangePassword.setOnClickListener(onClickListener)
        binding!!.imvBack.setOnClickListener(onClickListener)

    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.btnChangePassword -> {
                if(isValidFormData(
                        binding!!.edtNewPassword.text.toString().trim(),
                    binding!!.edtConfirmPassword.text.toString().trim())){

                    updateUserPassword(binding!!.edtNewPassword.text.toString().trim())
                }
            }
            R.id.imvBack -> {
                finish()
            }
        }
    }

    private fun isValidFormData(
        newPassword: String,
        confirmPassword: String
    ): Boolean {

        if (TextUtils.isEmpty(newPassword)) {
            UtilityMethod.showToastMessageError(mActivity!!, getString(R.string.enter_password))
            return false
        }

        if (newPassword.length < 6) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.minimum_6_characters)
            )
            return false
        }

        if (newPassword != confirmPassword) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.label_password_doesnot_match)
            )
            return false
        }

        return true
    }


    private fun updateUserPassword(newPassword: String) {

        ProgressDialog.showProgressDialog(mActivity!!)

        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("UserName", phoneNumber)
        rootObject.addProperty("NewPassword", newPassword)

        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<OTPVerificationResponse> = apiService1.updateAccountPassword(gsonObject)
        call1.enqueue(object : Callback<OTPVerificationResponse?> {
            override fun onResponse(
                call: Call<OTPVerificationResponse?>,
                response: Response<OTPVerificationResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.Response == "Sent") {
                            UtilityMethod.showToastMessageSuccess(
                                mActivity!!,
                                getString(R.string.label_password_change_successful)
                            )
                            finish()
                        } else {
                            UtilityMethod.showToastMessageError(
                                mActivity!!,
                               getString(R.string.label_password_not_changed)
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<OTPVerificationResponse?>, t: Throwable) {
                Log.d("1234", "error  : " + t.message)
                ProgressDialog.hideProgressDialog()
            }
        })

    }

}