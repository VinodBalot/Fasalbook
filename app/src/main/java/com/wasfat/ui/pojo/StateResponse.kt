package com.wasfat.ui.pojo

class StateResponse : ArrayList<StateResponseItem>()
data class StateResponseItem(
    val PKID: Int,
    val StateName: String
)