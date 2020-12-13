package com.wasfat.ui.activity.event

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityEventCategoryBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.activity.HomeActivity
import com.wasfat.ui.adapter.EventCategoryRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.EventCategory
import com.wasfat.ui.pojo.EventCategoryResponsePOJO
import com.wasfat.utils.ProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventCategoryActivity : BaseBindingActivity() {

    var binding : ActivityEventCategoryBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList: ArrayList<EventCategory> = ArrayList()

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, EventCategoryActivity::class.java)
            if(bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_category)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

    }

    override fun initializeObject() {

        onClickListener = this

        binding!!.textTitle.text = getText(R.string.label_events_category)

        setAdapter()
    }

    override fun setListeners() {

        binding!!.imvBack.setOnClickListener(onClickListener)
        binding!!.btnSubmit.setOnClickListener(onClickListener)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
            R.id.btnSubmit ->{
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setAdapter() {

        val layoutManager1 = LinearLayoutManager(mActivity)
        binding!!.rvEventCategories.layoutManager = layoutManager1
        binding!!.rvEventCategories.setHasFixedSize(true)

        fetchEventCategoriesFromAPI()

    }

    private fun categoryItemClicked(category: EventCategory) {

        Log.d("c", "categoryItemClicked: " + category.CategoryName + "  " + category.PKID)

        EventActivity.startActivity(mActivity!!, category, false)

    }

    private fun fetchEventCategoriesFromAPI(){

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("CategoryId","0")
        rootObject.addProperty("languageId", sessionManager!!.language)

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject

        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<EventCategoryResponsePOJO> = apiService1.getEventCategories(gsonObject)

        call1.enqueue(object : Callback<EventCategoryResponsePOJO?> {
            override fun onResponse(
                call: Call<EventCategoryResponsePOJO?>,
                response: Response<EventCategoryResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {

                        categoryList = response.body()!!.eventCategoryList

                        val eventRVAdapter = EventCategoryRVAdapter(mActivity,
                            { category -> categoryItemClicked(category) },
                            categoryList)

                        binding!!.rvEventCategories.adapter = eventRVAdapter

                    }
                }
            }

            override fun onFailure(call: Call<EventCategoryResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }


}