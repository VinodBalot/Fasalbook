package com.wasfat.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wasfat.ui.pojo.Category
import kotlinx.android.synthetic.main.view_home_category_list_item.view.*

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(category: Category) {

        itemView.txtCategoryName.text = category.CategoryName

        //Glide.with(itemView.context).load(movieModel.moviePicture!!).into(itemView.imageMovie)
    }

}