package com.wasfat.ui.adapter

import android.app.Activity
import android.view.View
import androidx.databinding.ViewDataBinding
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.ui.pojo.Category
import com.wasfat.ui.pojo.Event
import com.wasfat.ui.pojo.EventCategory
import kotlinx.android.synthetic.main.view_category_list_item.view.*

class EventRVAdapter(
    private val context: Activity?,
    private val onClickListener:(Event) -> Unit,
    private val eventList: ArrayList<Event>
) : RecyclerBaseAdapter() {
    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_category_list_item

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.txtCategoryName.text = eventList[position].EventName
        viewDataBinding.root.llView.tag = position
        viewDataBinding.root.llView.setOnClickListener { onClickListener(eventList[position]) }

    }

    override fun getItemCount(): Int = eventList.size//items.size
}