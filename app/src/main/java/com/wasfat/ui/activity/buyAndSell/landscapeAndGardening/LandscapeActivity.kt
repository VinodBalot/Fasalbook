package com.wasfat.ui.activity.buyAndSell.landscapeAndGardening

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.wasfat.R
import com.wasfat.databinding.ActivityAlliedServicesBinding
import com.wasfat.databinding.ActivityLandscapeBinding
import com.wasfat.ui.activity.buyAndSell.agricultureAndAlliedServices.AlliedServicesActivity
import com.wasfat.ui.base.BaseBindingActivity
import com.wasfat.ui.pojo.BuySellType
import com.wasfat.ui.pojo.Category

class LandscapeActivity : BaseBindingActivity() {

    var binding: ActivityLandscapeBinding? = null
    var onClickListener: View.OnClickListener? = null

    lateinit var parentCategory: Category
    lateinit var type: BuySellType

    companion object {

        fun startActivity(
            activity: Activity,
            category: Category,
            type: BuySellType,
            isClear: Boolean
        ) {
            val intent = Intent(activity, LandscapeActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("type", type)
            if (isClear) intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }

    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_landscape)
        binding!!.lifecycleOwner = this
    }

    override fun createActivityObject() {
        mActivity = this

        type = intent.getSerializableExtra("type") as BuySellType

        //Getting parent category from parent
        parentCategory = (intent.getSerializableExtra("category") as? Category)!!
    }

    override fun initializeObject() {
        onClickListener = this
        binding!!.textTitle.text = parentCategory.CategoryName
    }

    override fun setListeners() {

    }

    override fun onClick(p0: View?) {

    }

}