package com.wasfat.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfat.R
import com.wasfat.databinding.ActivityItemDetailsBinding
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.Category
import com.wasfat.ui.pojo.UserProduct
import kotlinx.android.synthetic.main.view_item_list.view.*

class ItemDetailsActivity : BaseBindingActivity() {

    var binding: ActivityItemDetailsBinding? = null
    var onClickListener: View.OnClickListener? = null

    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null

    lateinit var  product : UserProduct

    //  var viewModel: VendorViewModel? = null
    val RequestPermissionCode = 7

    companion object {

        fun startActivity(activity: Activity, product: UserProduct, unitName : String, isClear: Boolean) {
            val intent = Intent(activity, ItemDetailsActivity::class.java)
            intent.putExtra("product", product)
            intent.putExtra("unitName", unitName)
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

        //Getting product from parent
        product = (intent.getSerializableExtra("product") as? UserProduct)!!

    }

    override fun initializeObject() {
        onClickListener = this

        binding!!.textTitle.text = product.ProductName
        binding!!.txtProductName.text = product.ProductName
        binding!!.txtItemUnit.text = intent.getStringExtra("unitName")
        binding!!.txtQty.text = product.Qty

        binding!!.txtSpecifications.text = product.ProductSmallDesc

        setAdapter()

    }

    private fun setAdapter() {

        product.ImageList.forEach {
            if(it.ImageName.isNotEmpty()){
                val image = it.Path + "/" + it.ImageName
                imageList.add(image)
            }
        }

        val layoutManager1 = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding!!.rvImageList.layoutManager = layoutManager1
        binding!!.rvImageList.setHasFixedSize(true)

        imageListRVAdapter = ImageListRVAdapter(this,onClickListener,imageList)
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