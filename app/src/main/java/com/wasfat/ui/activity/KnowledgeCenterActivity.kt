package com.wasfat.ui.activity

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
import com.wasfat.ui.adapter.OrganicRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.BuySellType
import com.wasfat.ui.pojo.Category
import com.wasfat.ui.pojo.CategoryResponsePOJO
import com.wasfat.utils.ProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KnowledgeCenterActivity : BaseBindingActivity() {

    var binding: ActivityKnowledgeCenterBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList: ArrayList<Category> = ArrayList()
    lateinit var parentCategory: Category
    lateinit var type: BuySellType
    //  var viewModel: VendorViewModel? = null

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
       // fetchCategoriesOfParentFromAPI()

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

    private fun categoryItemClicked(category: Category) {
        //UtilityMethod.showToastMessageError(mActivity!!,category.toString())
        UploadIdeasActivity.startActivity(mActivity!!, category, false)
    }

    private fun fetchCategoriesOfParentFromAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()
        rootObject.addProperty("UserId", sessionManager!!.userId)
        val jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<CategoryResponsePOJO> = apiService1.knowledgeCenter(gsonObject)
        call1.enqueue(object : Callback<CategoryResponsePOJO?> {
            override fun onResponse(
                call: Call<CategoryResponsePOJO?>,
                response: Response<CategoryResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        categoryList = response.body()!!.categoryList
                        val shareIdRVAdapter = OrganicRVAdapter(mActivity,
                            { category -> categoryItemClicked(category) }
                            , categoryList)
                        binding!!.rvCategory.adapter = shareIdRVAdapter
                    }
                }
            }

            override fun onFailure(call: Call<CategoryResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })


    }


}