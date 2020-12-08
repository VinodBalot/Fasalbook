package com.wasfat.ui.pojo

import java.io.Serializable

data class UserLandscapeProductsResponsePOJO(
    val productList: ArrayList<UserLandscapeProduct>
)

data class UserLandscapeProduct(
    val ProductId: Int,
    val ProductName: String,
    val ProductSmallDesc: String,
    val Category: String,
    val UserId: String,
    val Published: String,
    val ImageList: ArrayList<LandscapeProductImage>
) : Serializable

data class LandscapeProductImage(
    val PKID : String,
    val ImageName : String,
    val Path : String
) : Serializable