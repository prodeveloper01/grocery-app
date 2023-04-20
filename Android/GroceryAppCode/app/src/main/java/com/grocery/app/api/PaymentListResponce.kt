package com.grocery.app.api

import com.google.gson.annotations.SerializedName
import com.grocery.app.model.PaymentItemModel

data class PaymentListResponce(
   @field:SerializedName("payment")
   val data: ArrayList<PaymentItemModel>? = null,

   @field:SerializedName("message")
   val message: String? = null,

   @field:SerializedName("status")
   val status: Int? = null,

   @field:SerializedName("walletamount")
   val walletamount: String? = null
)
