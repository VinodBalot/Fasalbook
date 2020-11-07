package com.wasfat.ui.pojo

import java.io.Serializable

data class CategoryResponsePOJO(
    val categoryList: ArrayList<Category>
)
data class Category(
    val PKID: Int,
    val CategoryName: String,
    val CatDesc: String,
    val CategoryImage: String,
    val ParentCategoryID: Int
) : Serializable