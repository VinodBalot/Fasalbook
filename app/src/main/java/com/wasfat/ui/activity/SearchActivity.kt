package com.wasfat.ui.activity

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.wasfat.R
import com.wasfat.databinding.ActivityProductSearchBinding
import com.wasfat.network.RestApi
import com.wasfat.network.RestApiFactory
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.home.adapter.SearchProductRVAdapter
import com.wasfat.ui.pojo.UserProduct
import com.wasfat.ui.pojo.UserProductsResponsePOJO
import com.wasfat.utils.ProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : BaseBindingActivity() {

    var binding: ActivityProductSearchBinding? = null
    var onClickListener: View.OnClickListener? = null
    var productList: ArrayList<UserProduct> = ArrayList()
    var searchProductRVAdapter: SearchProductRVAdapter? = null

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, SearchActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_search)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
        binding!!.searchView.requestFocus()
        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding!!.searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        binding!!.searchView.setIconifiedByDefault(true);
        binding!!.searchView.isFocusable = true;
        binding!!.searchView.isIconified = false;
        binding!!.searchView.requestFocusFromTouch()
        binding!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                callProductListDataApi(query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


    }

    private fun callProductListDataApi(searchText: String) {
        ProgressDialog.showProgressDialog(mActivity!!)
        val rootObject = JsonObject()
        rootObject.addProperty("StateId", "0")
        rootObject.addProperty("CityId", "0")
        rootObject.addProperty("BlockId", "0")
        rootObject.addProperty("CategoryId", "0")
        rootObject.addProperty("searchText", searchText)

        val jsonParser = JsonParser()
        val gsonObject = jsonParser.parse(rootObject.toString()) as JsonObject
        val apiService1 = RestApiFactory.getAddressClient()!!.create(RestApi::class.java)
        val call1: Call<UserProductsResponsePOJO> = apiService1.getProductsFromSearch(gsonObject)
        call1.enqueue(object : Callback<UserProductsResponsePOJO?> {
            override fun onResponse(
                call: Call<UserProductsResponsePOJO?>,
                response: Response<UserProductsResponsePOJO?>
            ) {
                ProgressDialog.hideProgressDialog()
                if (response.body() != null) {
                    if (response.isSuccessful) {
                        productList.clear()
                        productList = response.body()!!.productList
                        val layoutManager1 = LinearLayoutManager(this@SearchActivity)
                        binding!!.rvProducts.layoutManager = layoutManager1
                        binding!!.rvProducts.setHasFixedSize(true)
                        searchProductRVAdapter =
                            SearchProductRVAdapter(mActivity!!, productList, onClickListener)
                        binding!!.rvProducts.adapter = searchProductRVAdapter
                        if (productList.size == 0) {
                            binding!!.rvProducts.visibility = View.GONE
                            binding!!.txtNoProductFound.visibility = View.VISIBLE
                        } else {
                            Log.d("1234", "product list : " + productList.size)
                            binding!!.rvProducts.visibility = View.VISIBLE
                            binding!!.txtNoProductFound.visibility = View.GONE
                        }

                    }
                }
            }

            override fun onFailure(call: Call<UserProductsResponsePOJO?>, t: Throwable) {
                ProgressDialog.hideProgressDialog()
            }
        })

    }


    override fun setListeners() {


    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
            R.id.llMain -> {
                var position = view.tag as Int
                var intent = Intent(mActivity!!, ProductDetailsActivity::class.java)
                intent.putExtra("productName", productList[position].ProductName)
                intent.putExtra("qty", productList[position].Qty)
                intent.putExtra("image", productList[position].ImageList[0].Path+"/"+productList[position].ImageList[0].ImageName)
                startActivity(intent)
            }
        }

    }

    private fun categoryItemClicked(product: UserProduct) {

        //showItemSelectionDialog(product)

    }

}