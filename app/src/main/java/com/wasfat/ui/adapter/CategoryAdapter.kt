package com.wasfat.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.os.urservice.ui.components.adapters.RecyclerBaseAdapter
import com.wasfat.R
import com.wasfat.ui.pojo.Category
import com.wasfat.ui.viewholder.CategoryViewHolder
import kotlinx.android.synthetic.main.view_category_list_item.view.*

class CategoryAdapter(
    private val categoryList: ArrayList<Category>,
    private val clickListener: (Category) -> Unit)

    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_category_list, parent, false))
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val categoryViewHolder = viewHolder as CategoryViewHolder
        categoryViewHolder.bindView(categoryList[position],clickListener)
    }

}