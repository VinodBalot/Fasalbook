package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfat.R
import com.wasfat.databinding.ActivityOrganicAgricultureBinding
import com.wasfat.ui.adapter.OrganicRVAdapter
import com.wasfat.ui.base.BaseBindingActivity

class OrganicAgricultureActivity : BaseBindingActivity() {

    var binding: ActivityOrganicAgricultureBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList: ArrayList<String> = ArrayList()
    //  var viewModel: VendorViewModel? = null

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, OrganicAgricultureActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_organic_agriculture)
        //  viewModel = ViewModelProvider(this).get(VendorViewModel::class.java)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
        if (intent.extras != null) {
            binding!!.textTitle.text =
                intent.getBundleExtra("bundle")!!.getString("agriculture").toString()
        }
        setAdapter()
    }

    private fun setAdapter() {

        //Dummy data
        categoryList.add("Food Grains")
        categoryList.add("Vegetable")
        categoryList.add("Fruits")
        categoryList.add("Herbs & Exotic Vegies")
        categoryList.add("Dry Fruits")
        categoryList.add("Plantation Crops")
        categoryList.add("Spice & Condiments")
        categoryList.add("Medicinal Plants")
        categoryList.add("Oil Seed & Fiber Crops")
        categoryList.add("Fodder Crops")

        val layoutManager1 = LinearLayoutManager(mActivity)
        binding!!.rvCategory.layoutManager = layoutManager1
        binding!!.rvCategory.setHasFixedSize(true)
        val organicRVAdapter = OrganicRVAdapter(mActivity, onClickListener, categoryList)
        binding!!.rvCategory.adapter = organicRVAdapter

    }

    override fun setListeners() {
        binding!!.imvBack.setOnClickListener(onClickListener)

    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.imvBack -> {
                finish()
            }
            R.id.llView -> {
                ItemListActivity.startActivity(mActivity!!, null, false)
            }
        }

    }


}