package com.wasfat.ui.pojo

class BannerResponse : ArrayList<BannerResponseItem>()
data class BannerResponseItem(
    val AltText: String,
    val BannerName: String,
    val BannerURL: String,
    val ImageName: String,
    val PKID: Int
)