package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityEventBinding
import com.wasfat.databinding.ActivityEventCategoryBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.adapter.EventCategoryRVAdapter
import com.wasfat.ui.adapter.EventRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.Event
import com.wasfat.ui.pojo.EventCategory
import com.wasfat.ui.pojo.EventCategoryResponsePOJO
import com.wasfat.ui.pojo.EventResponsePOJO
import com.wasfat.utils.ProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventActivity : BaseBindingActivity() {

    var binding : ActivityEventBinding? = null
    var onClickListener: View.OnClickListener? = null
    var eventList: ArrayList<Event> = ArrayList()
    lateinit var eventCategory: EventCategory

    companion object {

        fun startActivity(activity: Activity, eventCategory: EventCategory, isClear: Boolean) {
            val intent = Intent(activity, EventActivity::class.java)
           intent.putExtra("eventCategory", eventCategory)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

        eventCategory = intent.getSerializableExtra("eventCategory") as EventCategory

    }

    override fun initializeObject() {

        onClickListener = this

        binding!!.textTitle.text = getText(R.string.label_events_category)

        setAdapter()
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

    private fun setAdapter() {

        val layoutManager1 = LinearLayoutManager(mActivity)
        binding!!.rvEvents.layoutManager = layoutManager1
        binding!!.rvEvents.setHasFixedSize(true)

        fetchEventCategoriesFromAPI()

    }

    private fun categoryItemClicked(event: Event) {

        Log.d("c", "eventItemClicked: " + event.EventName + "  " + event.PKID)

        WebViewActivity.startActivity(mActivity!!,event,false)

    }

    private fun fetchEventCategoriesFromAPI(){

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("CategoryId",eventCategory.PKID)
        rootObject.addProperty("LanguageId", "1")

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject

        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<EventResponsePOJO> = apiService1.getEventsById(gsonObject)

        call1.enqueue(object : Callback< EventResponsePOJO?> {
            override fun onResponse(
                call: Call< EventResponsePOJO?>,
                response: Response< EventResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {

                        eventList = response.body()!!.eventList

                        val eventRVAdapter = EventRVAdapter(mActivity,
                            { event -> categoryItemClicked(event) },
                            eventList)

                        binding!!.rvEvents.adapter = eventRVAdapter

                    }
                }
            }

            override fun onFailure(call: Call< EventResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }

}