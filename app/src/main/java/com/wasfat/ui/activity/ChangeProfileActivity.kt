package com.wasfat.ui.activity

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
import com.wasfat.databinding.ActivityChangeProfileBinding
import com.wasfat.databinding.ActivityFeedbackBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.AddWriteUpIdeaResponse
import com.wasfat.utils.ProgressDialog
import com.wasfat.utils.UtilityMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeProfileActivity : BaseBindingActivity() {

    var binding: ActivityChangeProfileBinding? = null
    var onClickListener: View.OnClickListener? = null

    lateinit var phoneNumber: String

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ChangeProfileActivity::class.java)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_profile)
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
                        binding!!.edtFeedback.text.toString().trim()
                    )
                ) {
                    sendFeedback(binding!!.edtFeedback.text.toString().trim())
                }
            }
            R.id.imvBack -> {
                finish()
            }
        }
    }

    private fun isValidFormData(
        feedback: String
    ): Boolean {

        if (TextUtils.isEmpty(feedback)) {
            UtilityMethod.showToastMessageError(
                mActivity!!,
                getString(R.string.label_enter_suggestion)
            )
            return false
        }
        return true
    }


    private fun sendFeedback(feedback: String) {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("UserName", sessionManager!!.userId)
        rootObject.addProperty("Feedback", feedback)

        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<AddWriteUpIdeaResponse> = apiService1.feedback(gsonObject)
        call1.enqueue(object : Callback<AddWriteUpIdeaResponse?> {
            override fun onResponse(
                call: Call<AddWriteUpIdeaResponse?>,
                response: Response<AddWriteUpIdeaResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        UtilityMethod.showToastMessageSuccess(
                            mActivity!!,
                            getString(R.string.label_suggestion_added)
                        )
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<AddWriteUpIdeaResponse?>, t: Throwable) {
                Log.d("1234", "error  : " + t.message)
                ProgressDialog.hideProgressDialog()
            }
        })

    }

}