package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wasfat.R
import com.wasfat.databinding.ActivityItemDetailsBinding
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity

class ItemDetailsActivity : BaseBindingActivity() {

    var binding: ActivityItemDetailsBinding? = null
    var onClickListener: View.OnClickListener? = null
    var categoryList: ArrayList<String> = ArrayList()
    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null
    var rlImage: RelativeLayout? = null
    var rvImage: RecyclerView? = null
    var imvAddMore: ImageView? = null

    //  var viewModel: VendorViewModel? = null
    val RequestPermissionCode = 7

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, ItemDetailsActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item_details)
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