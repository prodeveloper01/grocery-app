package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class ProfileModel (
    @field:SerializedName("profile_image")
    val profileImage: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("email")
    val email: String? = null
)