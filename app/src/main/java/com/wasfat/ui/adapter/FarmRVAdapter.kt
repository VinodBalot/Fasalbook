package com.wasfat.ui.home.adapter

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.ui.pojo.Category
import com.wasfat.ui.pojo.UserFarms
import com.wasfat.ui.pojo.UserProduct
import com.wasfat.utils.UtilityMethod
import kotlinx.android.synthetic.main.view_farm_item_list.view.*
import kotlinx.android.synthetic.main.view_item_list.view.*
import kotlinx.android.synthetic.main.view_item_list.view.ivItemIcon
import kotlinx.android.synthetic.main.view_item_list.view.llMain
import kotlinx.android.synthetic.main.view_item_list.view.txtItemName
import kotlin.system.exitProcess

class FarmRVAdapter(
    private val context: Activity,
    private val onClickListener:(UserFarms) -> Unit,
    private val items: ArrayList<UserFarms>
) : RecyclerBaseAdapter() {

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_farm_item_list

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) = try {

        viewDataBinding.root.txtItemName.text = items[position].FarmName
        viewDataBinding.root.llMain.tag = position
        viewDataBinding.root.llMain.setOnClickListener{ onClickListener(items[position]) }

        var image = ""

        if(items[position].ImageList.isNotEmpty()){

            items[position].ImageList.forEach {

                if(it.Path != "" && it.ImageName != ""){

                    image = it.Path + "/" + it.ImageName

                }

            }
        }

        Log.d("TAG", "putViewDataBinding: " + image)

        Glide.with(context).load(image).into(viewDataBinding.root.ivItemIcon)
        viewDataBinding.root.txtFarmAddress.text = items[position].Address

       viewDataBinding.root.txtFarmEmail.text = items[position].EmailId
        viewDataBinding.root.txtFarmWebsite.text = items[position].Website

    } catch (e: Exception) {

    }

    override fun getItemCount(): Int = items.size // items.size
}