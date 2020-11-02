package com.wasfat.ui.pojo

data class BannerResponse(
    val bannerResponseItem: ArrayList<BannerResponseItem>
)
data class BannerResponseItem(
    val AltText: String,
    val BannerName: String,
    val BannerURL: String,
    val ImageName: String,
    val PKID: Int
)