package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class ItemDetailImageModel (
    @field:SerializedName("itemimage")
    val itemimage: String? = null,
    @field:SerializedName("item_id")
    val itemId: Int? = null,
    @field:SerializedName("image_name")
    val image_name: String? = null
)