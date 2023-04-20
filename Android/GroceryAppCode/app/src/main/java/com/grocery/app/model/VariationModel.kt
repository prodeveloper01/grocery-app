package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class VariationModel(
	@field:SerializedName("item_id")
	val itemId: Int? = null,

	@field:SerializedName("price")
	val price: String? = null,
//	@field:SerializedName("product_price")
//	val productPrice: String? = null,

	@field:SerializedName("weight")
	val weight: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("stock")
	val stock: Int? = null,

	@field:SerializedName("isSelect")
	var isSelect:Boolean=false
)
