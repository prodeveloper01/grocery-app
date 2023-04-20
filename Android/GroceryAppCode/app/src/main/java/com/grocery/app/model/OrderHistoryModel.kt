package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class OrderHistoryModel (
    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("ordered_date")
    val ordered_date: String? = null,

    @field:SerializedName("payment_type")
    val paymentType: String? = null,

    @field:SerializedName("total_price")
    val totalPrice: String? = null,

    @field:SerializedName("qty")
    val qty: Int? = null,

    @field:SerializedName("order_number")
    val orderNumber: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("order_type")
    val orderType: Int? = null,

    @field:SerializedName("status")
    val status: Int? = null
)