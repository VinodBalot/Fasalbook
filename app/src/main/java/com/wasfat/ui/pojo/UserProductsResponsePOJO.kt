package com.wasfat.ui.pojo

import java.io.Serializable

data class UserProductsResponsePOJO(
    val productList: ArrayList<UserProduct>
)

data class UserProduct(
    val ProductId: Int,
    val ProductName: String,
    val ProductSmallDesc: String,
    val Qty: String,
    val UnitId: String,
    val Category: String,
    val UserId: String,
    val Published: String,
    val ImageList: ArrayList<ProductImage>
) : Serializable

data class ProductImage(
    val PKID : String,
    val ImageName : String,
    val Path : String
) : Serializable