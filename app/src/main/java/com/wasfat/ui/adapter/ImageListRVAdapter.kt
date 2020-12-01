package com.wasfat.ui.adapter

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.ui.activity.FarmDetailsActivity
import kotlinx.android.synthetic.main.view_image_list_item.view.*

class ImageListRVAdapter(
    private val context: Activity?,
    private val onClickListener: View.OnClickListener?,
    private val imageList: ArrayList<String>
) : RecyclerBaseAdapter() {


    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_image_list_item

    override fun getViewModel(position: Int): Any? = imageList[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        try {

            Log.d("123", "data :" + imageList[position])

            viewDataBinding.root.imvRemoveImage.tag = position

            viewDataBinding.root.imvRemoveImage.setOnClickListener(onClickListener)

            Glide.with(context!!)
                .load(imageList[position])
                .placeholder(R.drawable.wheat)
                .into(viewDataBinding.root.imvImage)


        } catch (e: Exception) {

        }
    }

    override fun getItemCount(): Int = imageList.size
}