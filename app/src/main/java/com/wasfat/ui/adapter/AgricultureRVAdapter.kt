package com.wasfat.ui.adapter

import android.app.Activity
import android.view.View
import androidx.databinding.ViewDataBinding
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.ui.pojo.Category
import kotlinx.android.synthetic.main.item_category_2_list.view.*

class AgricultureRVAdapter(
    private val context: Activity?,
    private val onClickListener: (Category) -> Unit,
    private val categoryList: ArrayList<Category>
) : RecyclerBaseAdapter() {
    override fun getLayoutIdForPosition(position: Int): Int = R.layout.item_category_2_list

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.txtCategory2Name.text = categoryList[position].CategoryName
        viewDataBinding.root.llView.tag = position
        viewDataBinding.root.llView.setOnClickListener { onClickListener(categoryList[position]) }
    }

    override fun getItemCount(): Int = categoryList.size//items.size
}