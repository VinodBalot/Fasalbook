package com.wasfat.ui.activity.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityPhoneNumberVerificationBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.AddFarmItemResponse
import com.wasfat.ui.pojo.BuySellType
import com.wasfat.ui.pojo.OTPVerificationResponse
import com.wasfat.ui.pojo.PhoneVerificationType
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PhoneNumberVerificationActivity : BaseBindingActivity() {

    var binding: ActivityPhoneNumberVerificationBinding? = null
    var onClickListener: View.OnClickListener? = null

    lateinit var verificationType: PhoneVerificationType

    var generatedOTP : String = ""
    var phoneNumber : String = ""

    companion object {

        fun startActivity(activity: Activity, type: PhoneVerificationType, isClear: Boolean) {
            val intent = Intent(activity, PhoneNumberVerificationActivity::class.java)
            intent.putExtra("type", type)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_number_verification)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this

        verificationType = intent.getSerializableExtra("type") as PhoneVerificationType

    }

    override fun setListeners() {
        binding!!.imvBack.setOnClickListener(onClickListener)
        binding!!.btnSendOtp.setOnClickListener(onClickListener)
        binding!!.btnVerify.setOnClickListener(onClickListener)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
            R.id.btnSendOtp -> {
                if (binding!!.edtPhoneNumber.text.isNotEmpty()) {

                    phoneNumber = binding!!.edtPhoneNumber.text.toString().trim()

                    when(verificationType){

                        PhoneVerificationType.NEW_SIGN_UP -> {
                            sendOtpForNewSignUP(phoneNumber)
                        }

                        PhoneVerificationType.FORGOT_PASSWORD ->{
                            sendOtpForForgotPassword(phoneNumber)
                        }

                    }

                } else {
                    UtilityMethod.showToastMessageDefault(
                        mActivity!!,
                        getString(R.string.label_enter_your_phone)
                    )
                }
            }
            R.id.btnVerify -> {
                if (binding!!.edtOtp.text.isNotEmpty()) {

                    verifyOTP(binding!!.edtOtp.text.toString().trim())

                } else {
                    UtilityMethod.showToastMessageDefault(
                        mActivity!!,
                        getString(R.string.label_enter_your_phone)
                    )
                }
            }
        }
    }

    private fun verifyOTP(userOtp: String) {

        if(generatedOTP != ""){

            if(userOtp == generatedOTP){

                UtilityMethod.showToastMessageSuccess(
                    mActivity!!,
                    getString(R.string.label_verification_success)
                )

                when(verificationType){

                    PhoneVerificationType.NEW_SIGN_UP -> {
                        RegisterActivity.startActivity(mActivity!!, phoneNumber, false)
                        finish()
                    }

                    PhoneVerificationType.FORGOT_PASSWORD ->{
                        ForgotPasswordActivity.startActivity(mActivity!!, phoneNumber, false)
                        finish()
                    }

                }

            }else{

                UtilityMethod.showToastMessageError(
                    mActivity!!,
                    getString(R.string.label_verification_failed)
                )
            }

        }else{

            UtilityMethod.showToastMessageError(
                mActivity!!,
             getString(R.string.label_send_otp_first)
            )
        }

    }

    private fun sendOtpForNewSignUP(phoneNumber: String) {

        ProgressDialog.showProgressDialog(mActivity!!)

        generatedOTP = generateOtp()

        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("UserName", phoneNumber)
        rootObject.addProperty("Text", generatedOTP)

        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<OTPVerificationResponse> = apiService1.sendOtpToThisNumber(gsonObject)
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
                                getString(R.string.label_otp_sent)
                            )
                        } else {
                            UtilityMethod.showToastMessageError(
                                mActivity!!,
                                getString(R.string.label_otp_not_sent)
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

    private fun sendOtpForForgotPassword(phoneNumber: String) {

        ProgressDialog.showProgressDialog(mActivity!!)

        generatedOTP = generateOtp()

        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("UserName", phoneNumber)
        rootObject.addProperty("Text", generatedOTP)

        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<OTPVerificationResponse> = apiService1.sendOtpForForgetPassword(gsonObject)
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
                                getString(R.string.label_otp_sent)
                            )
                        } else {
                            UtilityMethod.showToastMessageError(
                                mActivity!!,
                                getString(R.string.label_otp_not_sent)
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

    private fun generateOtp()
    : String {
        val random = Random()
        return String.format("%06d", random.nextInt(999999) + 1)
    }

}