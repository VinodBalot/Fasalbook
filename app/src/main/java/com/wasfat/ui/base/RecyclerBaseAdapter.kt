package com.os.urservice.ui.components.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerBaseAdapter :
    RecyclerView.Adapter<RecyclerBaseAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
        DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            viewType, parent, false
        )
    )

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val obj: Any? = getViewModel(position)
        holder.bindObj(obj!!)
        putViewDataBinding(holder.binding, position)
    }

    override fun getItemViewType(position: Int) = getLayoutIdForPosition(position)


    abstract fun getLayoutIdForPosition(position: Int): Int

    abstract fun getViewModel(position: Int): Any?

    abstract fun putViewDataBinding(viewDataBinding: ViewDataBinding, position: Int)

    open class RecyclerViewHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindObj(obj: Any) {
//            if (obj is DrawerData) binding.setVariable(BR.drawerData, obj)
            /*
            if (obj is NotificationList.Notification) binding.setVariable(BR.notifications, obj)
            else if (obj is Deal) binding.setVariable(BR.deal, obj)
            else if (obj is StoreLocation) binding.setVariable(BR.location, obj)
            else if (obj is StoreReview) binding.setVariable(BR.review, obj)*/

        }
    }
}