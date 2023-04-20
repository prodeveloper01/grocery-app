package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class RattingModel(
    @field:SerializedName("ratting")
    var ratting: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("created_at")
    var createdAt: String? = null,

    @field:SerializedName("comment")
    var comment: String? = null
)