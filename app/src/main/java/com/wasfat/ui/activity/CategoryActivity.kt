package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfat.R
import com.wasfat.databinding.ActivityCategoryBinding
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.adapter.CategoryRVAdapter

class CategoryActivity : BaseBindingActivity() {

    var binding: ActivityCategoryBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList : ArrayList<String> = ArrayList()
    //  var viewModel: VendorViewModel? = null

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, CategoryActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category)
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

    private fun setAdapter() {

        //Dummy data
        categoryList.add("Farmer")
        categoryList.add("Farmer Tourism")
        categoryList.add("Trader/Merchant/Buyer/Input Supplier")
        categoryList.add("Food Processor/Exporter")
        categoryList.add("Farmer Product Company (FPO)")
        categoryList.add("Expert/Service Provider")
        categoryList.add("Landscape & Gardening Sector")
        categoryList.add("Government Institution")
        categoryList.add("Consumer")
        categoryList.add("Others")

        val layoutManager1 = LinearLayoutManager(mActivity)
        binding!!.rvCategory.layoutManager = layoutManager1
        binding!!.rvCategory.setHasFixedSize(true)
        val categoryRVAdapter = CategoryRVAdapter(mActivity, onClickListener, categoryList)
        binding!!.rvCategory.adapter = categoryRVAdapter

    }

    override fun setListeners() {
        // binding!!.imvBack.setOnClickListener(onClickListener)

    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            /*   R.id.imvBack -> {
                   finish()
               }*/
        }

    }


}