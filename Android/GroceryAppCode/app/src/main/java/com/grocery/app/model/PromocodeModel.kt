package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class PromocodeModel (

    @field:SerializedName("offer_amount")
    val offerAmount: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("offer_code")
    val offerCode: String? = null,

    @field:SerializedName("offer_name")
    val offerName: String? = null
)