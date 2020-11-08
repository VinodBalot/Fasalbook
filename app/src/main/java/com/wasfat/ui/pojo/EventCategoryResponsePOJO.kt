package com.wasfat.ui.pojo

import java.io.Serializable

data class EventCategoryResponsePOJO(
    val eventCategoryList: ArrayList<EventCategory>
)
data class EventCategory(
    val PKID: Int,
    val CategoryName: String,
    val CatDesc: String
) : Serializable