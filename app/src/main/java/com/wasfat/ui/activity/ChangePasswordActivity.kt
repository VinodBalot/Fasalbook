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
import com.wasfat.databinding.ActivityChangePasswordBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.ChangePasswordResponse
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : BaseBindingActivity() {

    var binding: ActivityChangePasswordBinding? = null
    var onClickListener: View.OnClickListener? = null
    // var viewModel: VendorViewModel? = null

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
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
        binding!!.btnChangePassword.setOnClickListener(onClickListener)
        binding!!.imvBack.setOnClickListener(onClickListener)

    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.btnChangePassword -> {

                if (isValidFormData(
                        binding!!.edtOldPassword.text.toString(),
                        binding!!.edtNewPassword.text.toString(),
                        binding!!.edtConfirmPassword.text.toString()
                    )
                ) {
                    callAPIChangePassword()
                }
            }
            R.id.imvBack -> {
                finish()
            }
        }

    }

    private fun isValidFormData(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Boolean {
        if (TextUtils.isEmpty(oldPassword)) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.enter_old_password)
            )
            return false
        }


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

        if (newPassword == confirmPassword) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.label_password_doesnot_match)
            )
            return false
        }

        return true
    }

    private fun callAPIChangePassword() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("UserName", sessionManager!!.userId)
        rootObject.addProperty("OldPassword", binding!!.edtOldPassword.text.toString())
        rootObject.addProperty("NewPassword", binding!!.edtNewPassword.text.toString())
        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<ChangePasswordResponse> = apiService1.changePassword(gsonObject)
        call1.enqueue(object : Callback<ChangePasswordResponse?> {
            override fun onResponse(
                call: Call<ChangePasswordResponse?>,
                response: Response<ChangePasswordResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        if (response.body()!!.Response == "success") {
                            UtilityMethod.showToastMessageSuccess(
                                mActivity!!,
                                getString(R.string.label_password_change_successful)
                            )
                            finish()
                        } else {
                            UtilityMethod.showToastMessageError(
                                mActivity!!,
                                getString(R.string.label_user_no_registerd)
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })
    }


}