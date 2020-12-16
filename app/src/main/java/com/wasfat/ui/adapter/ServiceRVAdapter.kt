package com.wasfat.ui.home.adapter

import android.app.Activity
import androidx.databinding.ViewDataBinding
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.ui.pojo.UserServiceProduct
import kotlinx.android.synthetic.main.view_item_list.view.*
import kotlinx.android.synthetic.main.view_item_list.view.llMain
import kotlinx.android.synthetic.main.view_item_list.view.txtItemName
import kotlinx.android.synthetic.main.view_service_item_list.view.*

class ServiceRVAdapter(
    private val context: Activity,
    private val onClickListener: (UserServiceProduct) -> Unit,
    private val items: ArrayList<UserServiceProduct>
) : RecyclerBaseAdapter() {

    override fun getLayoutIdForPosition(position: Int): Int = R.layout.view_service_item_list

    override fun getViewModel(position: Int): Any? = 0//items[position]

    override fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int) = try {
        viewDataBinding.root.txtItemName.text = items[position].ProductName
        viewDataBinding.root.txtServiceDescription.text = items[position].ServiceOffered
        viewDataBinding.root.txtServiceSpecification.text = items[position].ProductSmallDesc
        viewDataBinding.root.llMain.tag = position
        viewDataBinding.root.llMain.setOnClickListener { onClickListener(items[position]) }
    } catch (e: Exception) {

    }

    override fun getItemCount(): Int = items.size // items.size
}