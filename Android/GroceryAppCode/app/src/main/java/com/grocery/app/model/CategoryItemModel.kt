package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class CategoryItemModel (
    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)