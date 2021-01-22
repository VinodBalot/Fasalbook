package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityMobileVerficationOtpBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.activity.authentication.ForgotPasswordActivity
import com.wasfat.ui.activity.authentication.RegisterActivity
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.OTPVerificationResponse
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class OTPVerificationActivity : BaseBindingActivity() {

    var binding: ActivityMobileVerficationOtpBinding? = null
    var onClickListener: View.OnClickListener? = null
    var verificationType: String = ""
    var phoneNumber: String = ""
    var generatedOTP: String = ""

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, OTPVerificationActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobile_verfication_otp)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
        timer.start()
        getIntentData()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            phoneNumber = intent.extras!!.getString("phoneNumber").toString()
            verificationType = intent.extras!!.getString("PhoneVerificationType").toString()
            if (verificationType == "NEW_SIGN_UP") {
                sendOtpForNewSignUP(phoneNumber)
            } else {
                sendOtpForForgotPassword(phoneNumber)
            }
        }
    }


    override fun setListeners() {
        binding!!.btnVerify.setOnClickListener(onClickListener)
        binding!!.tvOtpResentTime.setOnClickListener(onClickListener)
        binding!!.imvBack.setOnClickListener(onClickListener)
    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.btnVerify -> {
                val otp = binding!!.otpView.text.toString()
                Log.d("1234", "otp : " + otp);
                if (otp.length == 6) {
                    Log.d("1234", "otp 1: " + otp);
                    verifyOTP(otp)
                } else {
                    Log.d("1234", "otp 2: " + otp);
                    UtilityMethod.showErrorToastMessage(
                        mActivity!!,
                        getString(R.string.please_enter_otp)
                    )
                }
            }
            R.id.imvBack -> {
                finish()
            }
        }

    }

    private val timer = object : CountDownTimer(59000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding!!.tvOtpResentTime.text =
                "${getString(R.string.label_resentOtp_time)} 00:${millisUntilFinished % 59000 / 1000} ${getString(
                    R.string.label_second
                )}"
        }

        override fun onFinish() {
            binding!!.tvOtpResentTime.text = getString(R.string.label_resend_code)
        }
    }


    private fun verifyOTP(userOtp: String) {

        Log.d("1234", "otp 3 : ");
        if (generatedOTP != "") {
            Log.d("1234", "otp  4: " + generatedOTP);
            if (userOtp == generatedOTP) {
                Log.d("1234", "otp  5: " + generatedOTP);
                UtilityMethod.showToastMessageSuccess(
                    mActivity!!,
                    getString(R.string.label_verification_success)
                )
                if (verificationType == "NEW_SIGN_UP") {
                    RegisterActivity.startActivity(mActivity!!, phoneNumber, false)
                    finish()
                } else {
                    ForgotPasswordActivity.startActivity(mActivity!!, phoneNumber, false)
                    finish()
                }
            } else {
                UtilityMethod.showToastMessageError(
                    mActivity!!,
                    getString(R.string.label_verification_failed)
                )
            }
        } else {
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