package com.wasfat.ui.home.adapter

import android.app.Activity
import android.view.View
import androidx.databinding.ViewDataBinding
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import kotlinx.android.synthetic.main.view_item_list.view.*

class ItemRVAdapter(
    private val context: Activity?,
    private val onClickListener: View.OnClickListener?
    // private val items: ArrayList<Item>
) : RecyclerBaseAdapter() {
    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_item_list

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        try {
            viewDataBinding.root.llMain.tag = position
            viewDataBinding.root.llMain.setOnClickListener(onClickListener)
        } catch (e: Exception) {

        }
    }

    override fun getItemCount(): Int = 6 // items.size
}