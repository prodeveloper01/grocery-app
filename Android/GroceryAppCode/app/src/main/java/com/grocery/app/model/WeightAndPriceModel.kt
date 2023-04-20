package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class WeightAndPriceModel(
    @field:SerializedName("weight")
    var strWeight:String="",
    @field:SerializedName("price")
    var strPrice:String="",
    @field:SerializedName("isSelect")
    var isSelect:Boolean=false
)
