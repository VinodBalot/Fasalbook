package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.wasfat.R
import com.wasfat.databinding.ActivitySellBinding
import com.wasfat.ui.base.BaseBindingActivity

class SellActivity : BaseBindingActivity() {

    var binding: ActivitySellBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList: ArrayList<String> = ArrayList()
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
    }


    override fun setListeners() {
        binding!!.rlAgriculture.setOnClickListener(onClickListener)
        binding!!.imvBack.setOnClickListener(onClickListener)

    }


    override fun onClick(view: View?) {

        when (view!!.id) {
            R.id.rlAgriculture -> {
                AgricultureActivity.startActivity(mActivity!!, null, false)
            }
            R.id.imvBack -> {
                finish()
            }
        }

    }


}