package com.wasfat.ui.pojo

import java.io.Serializable

data class UserFarmsResponsePOJO(
    val FarmList: ArrayList<UserFarms>
)

data class UserFarms(
    val PKID: Int,
    val FarmName: String,
    val Address: String,
    val ContactNo: String,
    val Facilities: String,
    val Attraction: String,
    val lat: String,
    val lng: String,
    val UserId: String,
    val Published: Boolean,
    val BlockId: Int,
    val CityId: Int,
    val StateId: Int,
    val Price: Float,
    val EmailId: String,
    val Website: String,
    val ImageList: ArrayList<FarmImage>
) : Serializable

data class FarmImage(
    val PKID : String,
    val ImageName : String,
    val Path : String
) : Serializable