package com.wasfat.ui.pojo

data class StateResponsePOJO(
    val statelist: ArrayList<Statelist>
)

data class Statelist(
    val Id: Int,
    val Name: String
)