package com.wasfat.ui.activity.buyAndSell.landscapeAndGardening

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
import com.wasfat.databinding.ActivityDetailsLandscapeBinding
import com.wasfat.databinding.ActivityFarmDetailsBinding
import com.wasfat.ui.activity.HomeActivity
import com.wasfat.ui.activity.buyAndSell.farmtourism.DetailsFarmTourismActivity
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.UserFarms
import com.wasfat.ui.pojo.UserLandscapeProduct

class DetailsLandscapeActivity : BaseBindingActivity() {

    var binding: ActivityDetailsLandscapeBinding? = null
    var onClickListener: View.OnClickListener? = null

    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null

    lateinit var  userLandscapeProduct: UserLandscapeProduct

    companion object {

        fun startActivity(activity: Activity, userLandscapeProduct: UserLandscapeProduct, isClear: Boolean) {
            val intent = Intent(activity, DetailsLandscapeActivity::class.java)
            intent.putExtra("userLandscape", userLandscapeProduct)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details_landscape)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

        //Getting product from parent
        userLandscapeProduct = (intent.getSerializableExtra("userLandscape") as? UserLandscapeProduct)!!
    }

    override fun initializeObject() {
        onClickListener = this

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            binding!!.textTitle.text =
//                Html.fromHtml(userFarm.FarmName, Html.FROM_HTML_MODE_COMPACT);
//        } else {
//            binding!!.textTitle.text= Html.fromHtml(userFarm.FarmName);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            binding!!.txtFarmName.text =
//                Html.fromHtml(userFarm.FarmName, Html.FROM_HTML_MODE_COMPACT);
//        } else {
//            binding!!.txtFarmName.text= Html.fromHtml(userFarm.FarmName);
//        }

        binding!!.txtProductName.text = userLandscapeProduct.ProductName
        binding!!.txtSpecifications.text = userLandscapeProduct.ProductSmallDesc


        setAdapter()

    }

    private fun setAdapter() {

        userLandscapeProduct.ImageList.forEach {
            if(it.ImageName.isNotEmpty()){
                val image = it.Path + "/" + it.ImageName
                imageList.add(image)
            }
        }

        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding!!.rvImageList.layoutManager = layoutManager1
        binding!!.rvImageList.setHasFixedSize(true)

        imageListRVAdapter = ImageListRVAdapter(this,null,imageList)
        binding!!.rvImageList.adapter = imageListRVAdapter

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