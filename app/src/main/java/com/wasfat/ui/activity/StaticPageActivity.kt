package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.databinding.DataBindingUtil
import com.wasfat.R
import com.wasfat.databinding.ActivityLoginBinding
import com.wasfat.databinding.ActivityStaticPageBinding
import com.wasfat.ui.base.BaseBindingActivity

class StaticPageActivity : BaseBindingActivity() {

    var binding: ActivityStaticPageBinding? = null
    var onClickListener: View.OnClickListener? = null
    // var viewModel: VendorViewModel? = null

    companion object {

        fun startActivity(activity: Activity, bundle: Bundle?, isClear: Boolean) {
            val intent = Intent(activity, StaticPageActivity::class.java)
            if (bundle != null) intent.putExtra("bundle", bundle)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_static_page)
        // viewModel = ViewModelProvider(this).get(VendorViewModel::class.java)
        binding!!.lifecycleOwner = this

    }

    override fun createActivityObject() {
        mActivity = this
    }

    override fun initializeObject() {
        onClickListener = this
        var pageUrl = intent.getStringExtra("pageURL")
        binding!!.txtContent.text = Html.fromHtml(pageUrl)
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