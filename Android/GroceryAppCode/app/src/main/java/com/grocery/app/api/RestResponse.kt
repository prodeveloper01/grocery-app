package com.grocery.app.api
import com.google.gson.annotations.SerializedName

data class RestResponse<T> (
    @field:SerializedName("data")
    val data: T? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("currency")
    val currency: String? = null,

    @field:SerializedName("max_order_qty")
    val max_order_qty: Int? = null,

    @field:SerializedName("min_order_amount")
    val min_order_amount: Int? = null,

    @field:SerializedName("max_order_amount")
    val max_order_amount: Int? = null,

    @field:SerializedName("referral_amount")
    val referral_amount: String? = null,

    @field:SerializedName("map")
    val map: String? = null
)