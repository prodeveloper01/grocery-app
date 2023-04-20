package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class BannerModel(
        @field:SerializedName("image")
        val image: String? = null,
        @field:SerializedName("id")
        val id: Int? = null,
        @field:SerializedName("item_id")
        val item_id: Int? = null,
        @field:SerializedName("cat_id")
        val cat_id: Int? = null,
        @field:SerializedName("type")
        val type: String? = null,
        @field:SerializedName("category_name")
        val category_name: String? = null)