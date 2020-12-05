package com.wasfat.ui.activity

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
import com.wasfat.databinding.ActivityFarmDetailsBinding
import com.wasfat.databinding.ActivityItemDetailsBinding
import com.wasfat.ui.adapter.ImageListRVAdapter
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.UserFarms
import com.wasfat.ui.pojo.UserProduct
import kotlinx.android.synthetic.main.view_item_list.view.*

class FarmDetailsActivity : BaseBindingActivity() {

    var binding: ActivityFarmDetailsBinding? = null
    var onClickListener: View.OnClickListener? = null

    var imageList: ArrayList<String> = ArrayList()
    var imageListRVAdapter: ImageListRVAdapter? = null

    lateinit var  userFarm : UserFarms

    companion object {

        fun startActivity(activity: Activity, userFarm : UserFarms,isClear: Boolean) {
            val intent = Intent(activity, FarmDetailsActivity::class.java)
            intent.putExtra("userFarm", userFarm)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_farm_details)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

        //Getting product from parent
        userFarm = (intent.getSerializableExtra("userFarm") as? UserFarms)!!
    }

    override fun initializeObject() {
        onClickListener = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding!!.textTitle.text =
                Html.fromHtml(userFarm.FarmName, Html.FROM_HTML_MODE_COMPACT);
        } else {
            binding!!.textTitle.text= Html.fromHtml(userFarm.FarmName);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding!!.txtFarmName.text =
                Html.fromHtml(userFarm.FarmName, Html.FROM_HTML_MODE_COMPACT);
        } else {
            binding!!.txtFarmName.text= Html.fromHtml(userFarm.FarmName);
        }

        binding!!.txtFarmAddress.text = userFarm.Address
        binding!!.txtFarmEmail.text = userFarm.EmailId
        binding!!.txtFarmContactNo.text = userFarm.ContactNo
        binding!!.txtFarmWebsite.text = userFarm.Website
        binding!!.txtFarmPrice.text = userFarm.Price.toString()
        binding!!.txtFarmFacilities.text = userFarm.Facilities
        binding!!.txtFarmAttraction.text = userFarm.Attraction

        setAdapter()

    }

    private fun setAdapter() {

        userFarm.ImageList.forEach {
            if(it.ImageName.isNotEmpty()){
                val image = it.Path + "/" + it.ImageName
                imageList.add(image)
            }
        }

        val layoutManager1 = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
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