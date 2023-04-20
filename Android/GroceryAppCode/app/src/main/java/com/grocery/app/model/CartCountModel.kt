package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class CartCountModel (
    @field:SerializedName("cartcount")
    val cartcount: String? = null,

    @field:SerializedName("cartprice")
    val cartprice: String? = null
)