package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class OrderDetailModel(
    @field:SerializedName("itemimage")
    val itemimage: String? = null,

    @field:SerializedName("total_price")
    val totalPrice: String? = null,

    @field:SerializedName("item_id")
    val itemId: Int? = null,

    @field:SerializedName("item_price")
    val itemPrice: String? = null,

    @field:SerializedName("qty")
    val qty: String? = null,

    @field:SerializedName("weight")
    val weight: String? = null,

    @field:SerializedName("item_name")
    val itemName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null
)