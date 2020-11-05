package com.wasfat.ui.pojo

data class CityResponsePOJO(
    val citylist: ArrayList<Citylist>
)
data class Citylist(
    val Id: Int,
    val Name: String
)