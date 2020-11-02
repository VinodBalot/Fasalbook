package com.wasfat.ui.home.adapter

import android.app.Activity
import android.view.View
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.utils.Constants

class CartItemRVAdapter(
    private val context: Activity?,
    private val onClickListener: View.OnClickListener?
   // private val items: ArrayList<Item>
) : RecyclerBaseAdapter() {
    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_banner_list_item

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        try {
       /*     viewDataBinding.root.imvDelete.tag = position
            viewDataBinding.root.imvMinus.tag = position
            viewDataBinding.root.imvAdd.tag = position
            viewDataBinding.root.imvDelete.setOnClickListener(onClickListener)
            viewDataBinding.root.imvMinus.setOnClickListener(onClickListener)
            viewDataBinding.root.imvAdd.setOnClickListener(onClickListener)
            viewDataBinding.root.tvProductItemName.text = items[position].name
            viewDataBinding.root.tvPrice.text =
                Constants.CURRENCY + " " + items[position].price.substring(
                    0,
                    items[position].price.indexOf('.')
                )
            viewDataBinding.root.tvQuantity.text = items[position].quantity.toString()
            Glide.with(context!!).load(items[position].product!!.base_image.original_image_url)
                .placeholder(R.drawable.no_image_available).into(viewDataBinding.root.imvItem)*/
        } catch (e: Exception) {

        }
    }

    override fun getItemCount(): Int = 0//items.size
}