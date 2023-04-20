package com.grocery.app.model

import com.google.gson.annotations.SerializedName

data class ItemDetailModel (
    @field:SerializedName("images")
    val images: ArrayList<ItemDetailImageModel?>? = null,

    @field:SerializedName("category_name")
    val categoryName: String? = null,

    @field:SerializedName("item_price")
    val itemPrice: String? = null,

    @field:SerializedName("item_name")
    val itemName: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("cat_id")
    val cat_id: Int? = null,

    @field:SerializedName("delivery_time")
    val deliveryTime: String? = null,

    @field:SerializedName("item_description")
    val itemDescription: String? = null,

    @field:SerializedName("item_status")
    val itemStatus: Int? = null,

    @field:SerializedName("cartprice")
    val cartprice: String? = null,

    @field:SerializedName("is_on_cart")
    val isOnCart: String? = null,

    @field:SerializedName("weight")
    val weight: String? = null,

    @field:SerializedName("ingredient_type")
    val ingredient_type: String? = null,

    @field:SerializedName("brand")
    val brand: String? = null,

    @field:SerializedName("manufacturer")
    val manufacturer: String? = null,

    @field:SerializedName("country_origin")
    val country_origin: String? = null,

    @field:SerializedName("cartcount")
    val cartcount: String? = null,

    @field:SerializedName("isOutOfStock")
    var isOutOfStock: Boolean? = null,

    @field:SerializedName("variation")
    var variation: ArrayList<VariationModel>? = null,

    @field:SerializedName("get_variation")
    var getvariation: ArrayList<VariationModel> = ArrayList()


)