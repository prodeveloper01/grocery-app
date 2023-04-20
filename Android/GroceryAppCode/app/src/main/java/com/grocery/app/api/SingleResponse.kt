package com.grocery.app.api

import com.google.gson.annotations.SerializedName

data class SingleResponse(
	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("cart")
	var cart: String? = null
)
