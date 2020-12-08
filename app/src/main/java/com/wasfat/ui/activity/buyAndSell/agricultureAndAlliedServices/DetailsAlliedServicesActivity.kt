package com.wasfat.ui.activity.buyAndSell.agricultureAndAlliedServices

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfat.R
import com.wasfat.databinding.ActivityDetailsAlliedServicesBinding
import com.wasfat.databinding.ActivityDetailsLandscapeBinding
import com.wasfat.ui.activity.HomeActivity
import com.wasfat.ui.activity.buyAndSell.landscapeAndGardening.DetailsLandscapeActivity
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.UserLandscapeProduct
import com.wasfat.ui.pojo.UserServiceProduct

class DetailsAlliedServicesActivity : BaseBindingActivity() {

    private lateinit var userServiceProduct: UserServiceProduct

    var binding: ActivityDetailsAlliedServicesBinding? = null
    var onClickListener: View.OnClickListener? = null

    companion object {

        fun startActivity(activity: Activity, userServiceProduct: UserServiceProduct, isClear: Boolean) {
            val intent = Intent(activity, DetailsLandscapeActivity::class.java)
            intent.putExtra("userServiceProduct", userServiceProduct)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details_allied_services)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

        //Getting product from parent
        userServiceProduct = (intent.getSerializableExtra("userLandscape") as? UserServiceProduct)!!
    }

    override fun initializeObject() {
        onClickListener = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding!!.textTitle.text =
                Html.fromHtml( userServiceProduct.ProductName, Html.FROM_HTML_MODE_COMPACT);
        } else {
            binding!!.textTitle.text= Html.fromHtml( userServiceProduct.ProductName);
        }

        binding!!.txtProductName.text = userServiceProduct.ProductName
        binding!!.txtSpecifications.text = userServiceProduct.ProductSmallDesc
        binding!!.txtServicesOffered.text = userServiceProduct.ServiceOffered

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

}