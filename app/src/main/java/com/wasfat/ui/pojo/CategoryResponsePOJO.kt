package com.wasfat.ui.pojo

data class CategoryResponsePOJO(
    val categoryList: ArrayList<Category>
)
data class Category(
    val PKID: Int,
    val CategoryName: String,
    val CatDesc: String,
    val CategoryImage: String,
    val ParentCategoryID: Int
)