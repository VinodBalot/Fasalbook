package com.wasfat.ui.pojo

import java.io.Serializable

data class UserIdeasResponsePOJO(
    val IdeaList: ArrayList<UserIdea>
)

data class UserIdea(
    val PKID: Int,
    val UserId: String,
    val Title: String,
    val Details: String,
    val Published: Boolean,
    val ImageList: ArrayList<IdeaImage>,
    val NewsCuttings: ArrayList<IdeaImage>,
    val VideoList: ArrayList<IdeaImage>
) : Serializable

data class IdeaImage(
    val PKID : String,
    val ImageName : String,
    val Title: String,
    val Details: String,
    val Path : String
) : Serializable