package com.wasfat.ui.activity.knowledgecenter

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityKnowledgeCenterBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.home.adapter.KnowledgeCenterRVAdapter
import com.wasfat.ui.pojo.*
import com.wasfat.utils.ProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KnowledgeCenterActivity : BaseBindingActivity() {

    var binding: ActivityKnowledgeCenterBinding? = null
    var onClickListener: View.OnClickListener? = null
    var ideaList: ArrayList<UserIdea> = ArrayList()


    companion object {

        fun startActivity(
            activity: Activity,
            isClear: Boolean
        ) {
            val intent = Intent(activity, KnowledgeCenterActivity::class.java)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_knowledge_center)
        //  viewModel = ViewModelProvider(this).get(VendorViewModel::class.java)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {

        onClickListener = this
        val layoutManager1 = LinearLayoutManager(mActivity)
        binding!!.rvCategory.layoutManager = layoutManager1
        binding!!.rvCategory.setHasFixedSize(true)
        fetchCategoriesOfParentFromAPI()

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

    private fun ideaItemClicked(idea: UserIdea) {

        //UtilityMethod.showToastMessageError(mActivity!!,idea.toString())

        KnowledgeCenterDetailsActivity.startActivity(mActivity!!, idea, false)

    }

    private fun fetchCategoriesOfParentFromAPI() {

        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("UserId", sessionManager!!.userId)
        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject

        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<UserIdeasResponsePOJO> = apiService1.knowledgeCenter(gsonObject)
        call1.enqueue(object : Callback<UserIdeasResponsePOJO?> {
            override fun onResponse(
                call: Call<UserIdeasResponsePOJO?>,
                response: Response<UserIdeasResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        ideaList = response.body()!!.IdeaList

                        val shareIdRVAdapter = KnowledgeCenterRVAdapter(mActivity!!,
                            { idea -> ideaItemClicked(idea) }
                            , ideaList)

                        binding!!.rvCategory.adapter = shareIdRVAdapter

                        if(ideaList.isEmpty()){
                            binding!!.textNoRecordFound.visibility = View.VISIBLE
                            binding!!.rvCategory.visibility = View.GONE
                        }else{
                            binding!!.textNoRecordFound.visibility = View.GONE
                            binding!!.rvCategory.visibility = View.VISIBLE
                        }

                    }
                }
            }

            override fun onFailure(call: Call<UserIdeasResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }


}