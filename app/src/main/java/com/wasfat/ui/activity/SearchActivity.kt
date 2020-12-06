package com.wasfat.ui.activity

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wasfat.R
import com.wasfat.databinding.ActivityProductSearchBinding
import com.wasfat.network.ApiResponse
import com.wasfat.ui.base.BaseBindingActivity


class SearchActivity : BaseBindingActivity() {

    var binding: ActivityProductSearchBinding? = null
    var onClickListener: View.OnClickListener? = null

/*    private var productList: ArrayList<ProductListData> = ArrayList()
    private var productRVAdapter: ProductListRVAdapter? = null*/

    var selectedItemPosition = -1


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
        getIntentData()
        binding!!.searchView.requestFocus()
        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding!!.searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        binding!!.searchView.setIconifiedByDefault(true);
        binding!!.searchView.isFocusable = true;
        binding!!.searchView.isIconified = false;
        binding!!.searchView.requestFocusFromTouch()
        binding!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
               // callProductListDataApi(query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })



    }

    private fun getIntentData() {
        if (intent.extras != null) {
            var categoryId = intent.getBundleExtra("bundle")!!.getString("categoryId")

        }
    }

    override fun setListeners() {


    }

    private fun setAdapters() {
       /* binding!!.rvProducts.visibility = View.VISIBLE
        val layoutManager1 = GridLayoutManager(this, 2)
        binding!!.rvProducts.layoutManager = layoutManager1
        binding!!.rvProducts.setHasFixedSize(true)
        productRVAdapter = ProductListRVAdapter(mActivity!!, data!!, onClickListener)
        binding!!.rvProducts.adapter = productRVAdapter
*/
    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
        }

    }

}