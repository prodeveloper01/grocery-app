package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class SummaryModel (
    @field:SerializedName("driver_name")
    val driverName: String? = null,

    @field:SerializedName("delivery_charge")
    val deliveryCharge: String? = null,

    @field:SerializedName("order_notes")
    val orderNotes: String? = null,

    @field:SerializedName("discount_amount")
    val discountAmount: String? = null,

    @field:SerializedName("driver_mobile")
    val driverMobile: String? = null,

    @field:SerializedName("promocode")
    val promocode: Any? = null,

    @field:SerializedName("order_total")
    val orderTotal: String? = null,

    @field:SerializedName("tax")
    val tax: String? = null,

    @field:SerializedName("driver_profile_image")
    val driverProfileImage: String? = null
)