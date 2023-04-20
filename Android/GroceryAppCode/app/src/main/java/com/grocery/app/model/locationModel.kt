package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class locationModel(
   @field:SerializedName("lang")
    var lang: String? = null,
   @field:SerializedName("lat")
    var lat: String? = null
)
