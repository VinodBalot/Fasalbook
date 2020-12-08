package com.wasfat.ui.pojo

import java.io.Serializable

data class UserServicesResponsePOJO(
    val serviceList: ArrayList<UserServiceProduct>
)

data class UserServiceProduct(
    val ProductId: Int,
    val ProductName: String,
    val ProductSmallDesc: String,
    val ServiceOffered: String,
    val Category: String,
    val UserId: String,
    val Published: String
) : Serializable
