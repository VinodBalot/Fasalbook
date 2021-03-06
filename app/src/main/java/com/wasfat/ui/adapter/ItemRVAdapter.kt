package com.wasfat.ui.home.adapter

import android.app.Activity
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.ui.pojo.UserProduct
import kotlinx.android.synthetic.main.view_item_list.view.*

class ItemRVAdapter(
    private val context: Activity,
    private val onClickListener: (UserProduct) -> Unit,
    private val items: ArrayList<UserProduct>,
    private val unitList: ArrayList<com.wasfat.ui.pojo.Unit>
) : RecyclerBaseAdapter() {

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_item_list

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) = try {

        viewDataBinding.root.txtItemName.text = items[position].ProductName
        viewDataBinding.root.llMain.tag = position
        viewDataBinding.root.llMain.setOnClickListener { onClickListener(items[position]) }

        var image = ""

        if (items[position].ImageList.isNotEmpty()) {

            items[position].ImageList.forEach {

                if (it.Path != "" && it.ImageName != "") {

                    image = it.Path + "/" + it.ImageName

                }

            }
        }
        Glide.with(context).load(image).placeholder(R.drawable.no_image_available)
            .into(viewDataBinding.root.ivItemIcon)

        viewDataBinding.root.txtItemQuantity.text = items[position].Qty
        viewDataBinding.root.txtItemUnit.text = unitList[items[position].UnitId.toInt()].Name

    } catch (e: Exception) {

    }

    override fun getItemCount(): Int = items.size // items.size
}