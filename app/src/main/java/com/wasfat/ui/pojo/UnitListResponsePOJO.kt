package com.wasfat.ui.pojo

data class UnitListResponsePOJO(
    val unitList: ArrayList<Unit>
)
data class Unit(
    val Id: String,
    val Name: String
)