package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class CartModel (
    @field:SerializedName("itemimage")
    val itemimage: ItemImageModel? = null,

    @field:SerializedName("item_id")
    val itemId: Int? = null,

    @field:SerializedName("price")
    val price: String? = null,

    @field:SerializedName("qty")
    val qty: Int? = null,

    @field:SerializedName("item_name")
    val itemName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("weight")
    val weight: String? = null,

    @field:SerializedName("variation_id")
    val variation_id: Int? = null
)