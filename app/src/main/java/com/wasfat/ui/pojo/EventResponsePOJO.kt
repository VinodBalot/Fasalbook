package com.wasfat.ui.pojo

import java.io.Serializable

data class EventResponsePOJO(
    val eventList: ArrayList<Event>
)
data class Event(
    val PKID: Int,
    val EventName: String,
    val EventURL: String,
    val Details: String
) : Serializable