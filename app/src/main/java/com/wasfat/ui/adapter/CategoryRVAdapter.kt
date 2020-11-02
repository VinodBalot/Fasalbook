package com.wasfat.ui.adapter

import android.app.Activity
import android.view.View
import androidx.databinding.ViewDataBinding
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import kotlinx.android.synthetic.main.view_category_list_item.view.*

class CategoryRVAdapter(
    private val context: Activity?,
    private val onClickListener: View.OnClickListener?,
    private val categoryList: ArrayList<String>
) : RecyclerBaseAdapter() {
    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_category_list_item

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.txtCategoryName.text = categoryList[position]

    }

    override fun getItemCount(): Int = categoryList.size//items.size
}