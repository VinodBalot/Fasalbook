package com.wasfat.ui.activity.buyAndSell

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivitySellBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory.getAddressClient
import com.wasfat.ui.activity.HomeActivity
import com.wasfat.ui.activity.buyAndSell.agricultureAndAlliedServices.AlliedServicesCategory1Activity
import com.wasfat.ui.activity.buyAndSell.agricultureAndFarming.AgricultureCategory1Activity
import com.wasfat.ui.activity.buyAndSell.farmtourism.FarmTourismActivity
import com.wasfat.ui.activity.buyAndSell.landscapeAndGardening.LandscapeCategory1Activity
import com.wasfat.ui.adapter.CategoryAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.BuySellType
import com.wasfat.ui.pojo.Category
import com.wasfat.ui.pojo.CategoryResponsePOJO
import com.wasfat.utils.ProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SellActivity : BaseBindingActivity() {

    var binding: ActivitySellBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList: ArrayList<Category> = ArrayList()
    //  var viewModel: VendorViewModel? = null

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, SellActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sell)
        //  viewModel = ViewModelProvider(this).get(VendorViewModel::class.java)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
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

    private fun categoryItemClicked(category: Category) {

        when(category.CategoryName){

            "Agriculture & Farming" -> {
                AgricultureCategory1Activity.startActivity(mActivity!!, category,  BuySellType.SELL, false)
            }
            "Landscape & Gardening" ->{
                LandscapeCategory1Activity.startActivity(mActivity!!, category,  BuySellType.SELL, false)
            }
            "Farm Tourism & Hospitality" ->{
                FarmTourismActivity.startActivity(mActivity!!, category,  BuySellType.SELL, false)
            }
            "Agriculture & Allied Services" ->{
                AlliedServicesCategory1Activity.startActivity(mActivity!!, category,  BuySellType.SELL, false)
            }

        }

    }

    private fun setAdapter() {
        val manager = GridLayoutManager(mActivity, 2)
        binding!!.rvCategories.layoutManager = manager
        binding!!.rvCategories.setHasFixedSize(true)
        fetchAllCategoriesFromAPI()
    }

    private fun fetchAllCategoriesFromAPI() {
        ProgressDialog.showProgressDialog(mActivity!!)
        var gsonObject = JsonObject()
        val rootObject = JsonObject()

        rootObject.addProperty("CategoryId", "0")
        rootObject.addProperty("languageId", sessionManager!!.language)

        var jsonParser = JsonParser()
        gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<CategoryResponsePOJO> = apiService1.getHomeCategories(gsonObject)
        call1.enqueue(object : Callback<CategoryResponsePOJO?> {
            override fun onResponse(
                call: Call<CategoryResponsePOJO?>,
                response: Response<CategoryResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        categoryList = response.body()!!.categoryList
                        val categoryAdapter = CategoryAdapter(categoryList) { category ->
                            categoryItemClicked(category)
                        }
                        binding!!.rvCategories.adapter = categoryAdapter
                    }
                }
            }

            override fun onFailure(call: Call<CategoryResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }


}