package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityLoginBinding
import com.wasfat.databinding.ActivityStaticPageBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.AboutAppResponse
import com.wasfat.utils.ProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StaticPageActivity : BaseBindingActivity() {

    var binding: ActivityStaticPageBinding? = null
    var onClickListener: View.OnClickListener? = null
    lateinit var type: String
    // var viewModel: VendorViewModel? = null

    companion object {

        fun startActivity(activity: Activity, type:String, isClear: Boolean) {
            val intent = Intent(activity, StaticPageActivity::class.java)
            intent.putExtra("type", type)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_static_page)
        // viewModel = ViewModelProvider(this).get(VendorViewModel::class.java)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this

        type = intent.getStringExtra("type") as String

        if(type == "about"){
            getAboutFromAPI()
        }

    }


    private fun getAboutFromAPI(){

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("languageId", 1)

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)

        val call1: Call<AboutAppResponse> = apiService1.getAboutApp(gsonObject)
        call1.enqueue(object : Callback<AboutAppResponse?> {
            override fun onResponse(
                call: Call<AboutAppResponse?>,
                response: Response<AboutAppResponse?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {

                        val content: String = response.body()!!.Content

                        Log.d("TAG", "onResponse: " + content)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding!!.txtContent.text =
                                Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT);
                        } else {
                            binding!!.txtContent.text = Html.fromHtml(content);
                        }

                    }
                }
            }

            override fun onFailure(call: Call<AboutAppResponse?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })
    }


    override fun setListeners() {
        binding!!.imvBack.setOnClickListener(onClickListener)
    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
        }

    }


}