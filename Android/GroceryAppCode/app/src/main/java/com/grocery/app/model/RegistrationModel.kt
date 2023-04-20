package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class RegistrationModel (
    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("profile_image")
    val profile_image: String? = null,

    @field:SerializedName("referral_code")
    val referral_code: String? = null
)