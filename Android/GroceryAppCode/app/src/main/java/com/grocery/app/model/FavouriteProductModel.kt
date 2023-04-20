package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class FavouriteProductModel (
    @field:SerializedName("itemimage")
    val itemimage: ItemImageModel? = null,

    @field:SerializedName("favorite_id")
    var favorite_id: Int? = null,

    @field:SerializedName("item_price")
    val itemPrice: String? = null,

    @field:SerializedName("item_name")
    val itemName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("weight")
    val weight: String? = null,

    @field:SerializedName("item_status")
    val item_status: Int? = null,

    @field:SerializedName("isOutOfStock")
    var isOutOfStock: Boolean? = null,

    @field:SerializedName("variation")
    var variation: ArrayList<VariationModel>? = null,

    @field:SerializedName("get_variation")
    var getvariation: ArrayList<VariationModel> = ArrayList()
)