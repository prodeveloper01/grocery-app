package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class ItemImageModel (
    @field:SerializedName("image")
    var image: String? = null,

    @field:SerializedName("image_name")
    var image_name: String? = null,

    @field:SerializedName("id")
    var id: Int? = null
)