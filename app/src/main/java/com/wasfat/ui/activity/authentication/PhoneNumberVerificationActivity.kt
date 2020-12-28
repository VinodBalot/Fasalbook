package com.wasfat.ui.activity.authentication

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityPhoneNumberVerificationBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.activity.OTPVerificationActivity
import com.wasfat.ui.base.BaseBindingActivity
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

    var generatedOTP: String = ""
    var phoneNumber: String = ""

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

                    when (verificationType) {
                        PhoneVerificationType.NEW_SIGN_UP -> {
                           // sendOtpForNewSignUP(phoneNumber)
                            val intent = Intent(mActivity!!, OTPVerificationActivity::class.java)
                            intent.putExtra("phoneNumber", phoneNumber)
                            intent.putExtra("PhoneVerificationType", "NEW_SIGN_UP")
                            startActivity(intent)
                        }
                        PhoneVerificationType.FORGOT_PASSWORD -> {
                          //  sendOtpForForgotPassword(phoneNumber)
                            val intent = Intent(mActivity!!, OTPVerificationActivity::class.java)
                            intent.putExtra("phoneNumber", phoneNumber)
                            intent.putExtra("PhoneVerificationType", "FORGOT_PASSWORD")
                            startActivity(intent)
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
            }
        }
    }
}