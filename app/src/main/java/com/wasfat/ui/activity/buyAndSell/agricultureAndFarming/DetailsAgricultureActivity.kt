package com.wasfat.ui.activity.buyAndSell.agricultureAndFarming

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.wasfat.R
import com.wasfat.databinding.ActivityItemDetailsBinding
import com.wasfat.ui.activity.HomeActivity
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.UserProduct

class DetailsAgricultureActivity : BaseBindingActivity() {

    var binding: ActivityItemDetailsBinding? = null
    var onClickListener: View.OnClickListener? = null

    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null

    lateinit var  product : UserProduct

    //  var viewModel: VendorViewModel? = null
    val RequestPermissionCode = 7

    companion object {

        fun startActivity(activity: Activity, product: UserProduct, unitName : String, priceUnitName : String, isClear: Boolean) {
            val intent = Intent(activity, DetailsAgricultureActivity::class.java)
            intent.putExtra("product", product)
            intent.putExtra("unitName", unitName)
            intent.putExtra("priceUnitName", priceUnitName)
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
        binding!!.txtProductPrice.text = product.Rate
        binding!!.txtPriceUnit.text = intent.getStringExtra("priceUnitName")

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