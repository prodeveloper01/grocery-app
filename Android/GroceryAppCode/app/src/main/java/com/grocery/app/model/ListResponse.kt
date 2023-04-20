package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class ListResponse<T> (
    @field:SerializedName("data")
    val data: ArrayList<T>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)