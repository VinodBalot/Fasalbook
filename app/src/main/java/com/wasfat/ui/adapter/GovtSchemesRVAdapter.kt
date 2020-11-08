package com.wasfat.ui.adapter

import android.app.Activity
import android.view.View
import androidx.databinding.ViewDataBinding
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.ui.pojo.Category
import com.wasfat.ui.pojo.Event
import com.wasfat.ui.pojo.EventCategory
import com.wasfat.ui.pojo.GovtSchemes
import kotlinx.android.synthetic.main.view_category_list_item.view.*

class GovtSchemesRVAdapter(
    private val context: Activity?,
    private val onClickListener:(GovtSchemes) -> Unit,
    private val govtSchemesList: ArrayList<GovtSchemes>
) : RecyclerBaseAdapter() {
    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_category_list_item

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) {
        viewDataBinding.root.txtCategoryName.text = govtSchemesList[position].GSName
        viewDataBinding.root.llView.tag = position
        viewDataBinding.root.llView.setOnClickListener { onClickListener(govtSchemesList[position]) }

    }

    override fun getItemCount(): Int = govtSchemesList.size//items.size
}