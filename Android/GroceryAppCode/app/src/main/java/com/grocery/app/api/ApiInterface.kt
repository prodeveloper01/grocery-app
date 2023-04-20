package com.grocery.app.api

import com.google.gson.JsonObject
import com.grocery.app.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    //Registration Api 1
    @POST("register")
    fun setRegistration(@Body map: HashMap<String, String>): Call<RestResponse<RegistrationModel>>

    //Login Api 2
    @POST("login")
    fun getLogin(@Body map: HashMap<String,String>): Call<RestResponse<LoginModel>>

    //EditProfile Api 3
    @Multipart
    @POST("editprofile")
    fun setProfile(@Part("user_id") userId: RequestBody,@Part("name") name: RequestBody, @Part profileimage: MultipartBody.Part?): Call<RestResponse<LoginModel>>

    //Chnage Password  Api 4
    @POST("changepassword")
    fun setChangePassword(@Body map: HashMap<String,String>):Call<SingleResponse>

    //forgotPassword Api 5
    @POST("forgotPassword")
    fun setforgotPassword(@Body map: HashMap<String,String>):Call<SingleResponse>

    //ApplyPromocode Api 6
    @GET("banner")
    fun getBanner():Call<ListResponse<BannerModel>>

    //Category  Api 7
    @GET("category")
    fun getCategory():Call<ListResponse<CategoryItemModel>>

    //LatestItem  Api 8
    @POST("latestitem")
    fun getLatestItem(@Body map: HashMap<String,String>):Call<RestResponse<PaginationModel<ProductItemModel>>>

    //ExploreItem  Api 9
    @POST("exploreitem")
    fun getExploreItem(@Body map: HashMap<String,String>):Call<RestResponse<PaginationModel<ProductItemModel>>>

    //Item  Api 10
    @POST("item")
    fun getCategoryByItem(@Body map: HashMap<String,String>,@Query("page")strPageNo:String):Call<RestResponse<PaginationModel<ProductItemModel>>>

    //LatestItem  Api 11
    @POST("latestitem")
    fun getLatestItemPagination(@Body map: HashMap<String,String>,@Query("page")strPageNo:String):Call<RestResponse<PaginationModel<ProductItemModel>>>

    //ExploreItem  Api 12
    @POST("exploreitem")
    fun getExploreItemPagination(@Body map: HashMap<String,String>,@Query("page")strPageNo:String):Call<RestResponse<PaginationModel<ProductItemModel>>>

    //AddFavorite Api 13
    @POST("addfavorite")
    fun setAddFavorite(@Body map: HashMap<String,String>):Call<SingleResponse>

    //Removefavorite Api 14
    @POST("removefavorite")
    fun setRemovefavorite(@Body map: HashMap<String,String>):Call<SingleResponse>

    //CartCount Api 15
    @POST("cartcount")
    fun getCartCount(@Body map: HashMap<String,String>):Call<RestResponse<CartCountModel>>

    //Item  Api 16
    @POST("searchitem")
    fun getSearchByItem(@Body map: HashMap<String,String>,@Query("page")strPageNo:String):Call<RestResponse<PaginationModel<SearchProductModel>>>

    //Item  Api 17
    @POST("favoritelist")
    fun getFavouriteByItem(@Body map: HashMap<String,String>,@Query("page")strPageNo:String):Call<RestResponse<PaginationModel<FavouriteProductModel>>>

    //ApplyPromocode Api 18
    @GET("rattinglist")
    fun getRetting():Call<ListResponse<RattingModel>>

    //Getcart  Api 19
    @POST("getcart")
    fun getCartItem(@Body map: HashMap<String,String>):Call<ListResponse<CartModel>>

    //check Status Api 20
    @GET("isopenclose")
    fun getCheckStatus():Call<SingleResponse>

    //QtyUpdate Api 21
    @POST("qtyupdate")
    fun setQtyUpdate(@Body map: HashMap<String,String>):Call<SingleResponse>

    //DeleteCartItem Api 22
    @POST("deletecartitem")
    fun setDeleteCartItem(@Body map: HashMap<String,String>):Call<SingleResponse>

    //Related Product  Api 23
    @POST("relateditem")
    fun getRelatedProduct(@Body map: HashMap<String,String>,@Query("page")strPageNo:String):Call<RestResponse<PaginationModel<ProductItemModel>>>

    //ItemDetail  Api 24
    @POST("itemdetails")
    fun getItemDetail(@Body map: HashMap<String,String>):Call<RestResponse<ItemDetailModel>>

    //Checkpincode Api 25
    @POST("summary")
    fun getSummary(@Body map: HashMap<String,String>):Call<ListResopone<OrderDetailItemModel>>

    //AddCart  Api 26
    @POST("cart")
    fun getAddToCart(@Body map: HashMap<String,String>):Call<SingleResponse>

    //Ratting  Api 27
    @POST("ratting")
    fun setRatting(@Body map: HashMap<String,String>):Call<SingleResponse>

    //OrderPayment Api 28
    @POST("order")
    fun setOrderPayment(@Body map: HashMap<String,String>):Call<SingleResponse>

    //Checkpincode Api 29
    @POST("checkpincode")
    fun setCheckPinCode(@Body map: HashMap<String,String>):Call<SingleResponse>

    //PromoCodeList Api 30
    @GET("promocodelist")
    fun getPromoCodeList():Call<ListResponse<PromocodeModel>>

    //ApplyPromocode Api 31
    @POST("promocode")
    fun setApplyPromocode(@Body map: HashMap<String, String>):Call<RestResponse<PromocodeModel>>

    //Order History Api 32
    @POST("orderhistory")
    fun getOrderHistory(@Body map: HashMap<String, String>):Call<ListResopone<OrderHistoryModel>>

    //OrderPayment Api 33
    @POST("getorderdetails")
    fun getOrderDetails(@Body map: HashMap<String, String>):Call<ListResopone<OrderDetailModel>>

    //PaymentType Api 34
    @POST("paymenttype")
    fun getPaymentType(@Body map: HashMap<String, String>):Call<PaymentListResponce>

    //Wallet Api 35
    @POST("wallet")
    fun getWallet(@Body map: HashMap<String, String>):Call<ListResopone<WalletModel>>

    //OrderCancel Api 36
    @POST("ordercancel")
    fun setOrderCancel(@Body map: HashMap<String, String>):Call<SingleResponse>

    //Checkpincode Api 37
    @POST("resendemailverification")
    fun setResendEmailVerification(@Body map: HashMap<String,String>):Call<SingleResponse>

    //getChatList Api 38
    @POST("emailverify")
    fun setEmailVerify(@Body map: HashMap<String, String>): Call<RestResponse<LoginModel>>

    //getChatList Api 39
    @GET("restaurantslocation")
    fun getLocation(): Call<RestResponse<locationModel>>
    @POST("address")
    fun addAddress(@Body map: HashMap<String,String>):Call<SingleResponse>

    @POST("updateaddress")
    fun updateAddress(@Body map: HashMap<String,String>):Call<SingleResponse>

    @POST("deleteaddress")
    fun deleteAddress(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("getaddress")
    fun getAddress(@Body map: HashMap<String,String>):Call<ListResopone<AddressResponse>>

    @GET("pincode")
    fun getNeighbourhood():Call<ListResopone<PinCodeResponse>>

    @POST("addmoney")
    fun addMoney(@Body map:HashMap<String,String>):Call<SingleResponse>
    @POST("barcodeitemdetails")
    fun barcodeDetails(@Body map:HashMap<String,String>):Call<RestResponse<BarcodeData>>

}