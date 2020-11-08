package com.wasfat.ui.adapter

import android.app.Activity
import android.view.View
import androidx.databinding.ViewDataBinding
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.ui.pojo.Category
import com.wasfat.ui.pojo.EventCategory
import kotlinx.android.synthetic.main.view_category_list_item.view.*

class EventCategoryRVAdapter(
    private val context: Activity?,
    private val onClickListener:(EventCategory) -> Unit,
    private val categoryList: ArrayList<EventCategory>
) : RecyclerBaseAdapter() {
    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_category_list_item

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.txtCategoryName.text = categoryList[position].CategoryName
        viewDataBinding.root.llView.tag = position
        viewDataBinding.root.llView.setOnClickListener { onClickListener(categoryList[position]) }

    }

    override fun getItemCount(): Int = categoryList.size//items.size
}