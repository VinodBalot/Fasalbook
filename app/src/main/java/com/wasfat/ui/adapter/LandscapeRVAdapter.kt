package com.wasfat.ui.home.adapter

import android.app.Activity
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.ui.pojo.UserLandscapeProduct
import kotlinx.android.synthetic.main.view_item_list.view.*

class LandscapeRVAdapter(
    private val context: Activity,
    private val onClickListener: (UserLandscapeProduct) -> Unit,
    private val items: ArrayList<UserLandscapeProduct>
) : RecyclerBaseAdapter() {

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_item_list

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) = try {

        var image = ""

        if (items[position].ImageList.isNotEmpty()) {

            items[position].ImageList.forEach {

                if (it.Path != "" && it.ImageName != "") {

                    image = it.Path + "/" + it.ImageName

                }

            }
        }

        Log.d("TAG", "putViewDataBinding: " + image)

        Glide.with(context).load(image).placeholder(R.drawable.no_image_available)
            .into(viewDataBinding.root.ivItemIcon)
        viewDataBinding.root.txtItemName.text = items[position].ProductName
        viewDataBinding.root.llMain.tag = position
        viewDataBinding.root.llMain.setOnClickListener { onClickListener(items[position]) }


    } catch (e: Exception) {

    }

    override fun getItemCount(): Int = items.size // items.size
}