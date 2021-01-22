package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.wasfat.R
import com.wasfat.databinding.ActivityProductDetailsBinding
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.Category
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseBindingActivity() {

    var binding: ActivityProductDetailsBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList: ArrayList<Category> = ArrayList()
    var image = ""
    var productName = ""
    var qty = ""

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ProductDetailsActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details)
        //  viewModel = ViewModelProvider(this).get(VendorViewModel::class.java)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
        setIntentData()
    }

    private fun setIntentData() {
        image = intent.getStringExtra("image").toString()
        productName = intent.getStringExtra("productName").toString()
        qty = intent.getStringExtra("qty").toString()
        txtProductName.text = productName
        txtQty.text = qty
        Glide.with(mActivity!!).load(image).placeholder(R.drawable.no_image_available)
            .into(binding!!.imvProduct)
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