package com.wasfat.ui.pojo

import java.io.Serializable

data class GovtSchemesResponsePOJO(
    val govtSchemetList: ArrayList<GovtSchemes>
)
data class GovtSchemes(
    val PKID: Int,
    val GSName: String,
    val GSURL: String,
    val Details: String
) : Serializable