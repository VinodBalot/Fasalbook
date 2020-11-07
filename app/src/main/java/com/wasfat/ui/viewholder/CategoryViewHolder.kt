package com.wasfat.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfat.ui.pojo.Category
import kotlinx.android.synthetic.main.item_home_category_list.view.*

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(category: Category, clickListener: (Category) -> Unit) {

        itemView.txtCategoryName.text = category.CategoryName

        itemView.setOnClickListener{clickListener(category)}

        //Glide.with(itemView.context).load(movieModel.moviePicture!!).into(itemView.imageMovie)
    }

}