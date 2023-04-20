package com.grocery.app.fragment

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.activity.*
import com.grocery.app.api.ApiClient
import com.grocery.app.api.RestResponse
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.base.BaseFragmnet
import com.grocery.app.model.*
import com.grocery.app.utils.CircleAnimationUtil
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.getCurrancy
import com.grocery.app.utils.Common.isCartTrueOut
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.Common.isFavouriteOut
import com.grocery.app.utils.Common.setLogout
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.getBooleanPref
import com.grocery.app.utils.SharePreference.Companion.getStringPref
import com.grocery.app.utils.SharePreference.Companion.isCurrancy
import com.grocery.app.utils.SharePreference.Companion.isLogin
import com.grocery.app.utils.SharePreference.Companion.isMaximum
import com.grocery.app.utils.SharePreference.Companion.isMiniMum
import com.grocery.app.utils.SharePreference.Companion.isMiniMumQty
import com.grocery.app.utils.SharePreference.Companion.map_key
import com.grocery.app.utils.SharePreference.Companion.setStringPref
import com.grocery.app.utils.SharePreference.Companion.userId
import com.grocery.app.utils.SharePreference.Companion.userName
import com.grocery.app.utils.SharePreference.Companion.userRefralAmount
import kotlinx.android.synthetic.main.fragmnet_home.*
import kotlinx.android.synthetic.main.row_variation.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomeFragment : BaseFragmnet() {
    val latestProductList = ArrayList<ProductItemModel>()
    var latestProductAdapter: BaseAdaptor<ProductItemModel>? = null

    var exploreProductAdapter: BaseAdaptor<ProductItemModel>? = null
    val exploreProductList = ArrayList<ProductItemModel>()

    override fun setView(): Int {
        return R.layout.fragmnet_home
    }

    @SuppressLint("SetTextI18n")
    override fun init(view: View) {
        Common.getCurrentLanguage(activity!!, false)
        setLatestProductAdaptor(latestProductList)
        setExploreProductAdaptor(exploreProductList)


        if (isAdded) {
            if (getBooleanPref(activity!!, isLogin)) {
                tv_NevProfileName.text = " Hello, \n ${getStringPref(activity!!, userName)}"
            } else {
                tv_NevProfileName.text = " Welcome to,\n " + resources.getString(R.string.app_name)
            }
        }
        ivCart.setOnClickListener {
            if (getBooleanPref(activity!!, isLogin)) {
                startActivity(
                    Intent(activity!!, CartActivity::class.java).putExtra(
                        "isActivity",
                        resources.getString(R.string.is_item_Act_home)
                    )
                )
            } else {
                openActivity(LoginSelectActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }

        cvWallet.setOnClickListener {
            if (getBooleanPref(activity!!, isLogin)) {
                openActivity(MyWalletActivity::class.java)
            } else {
                openActivity(LoginSelectActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }

        }

        cvlocation.setOnClickListener {
            showLoadingProgress(activity!!)
            val call = ApiClient.getClient.getLocation()
            call.enqueue(object : Callback<RestResponse<locationModel>> {
                override fun onResponse(
                    call: Call<RestResponse<locationModel>>,
                    response: Response<RestResponse<locationModel>>
                ) {
                    if (response.code() == 200) {
                        dismissLoadingProgress()
                        val restResponce: RestResponse<locationModel> = response.body()!!
                        if (restResponce.status == 1) {
                            if (restResponce.data!!.lang != null && restResponce.data.lat != null) {
                                val urlAddress =
                                    "http://maps.google.com/maps?q=" + restResponce.data.lat + "," + restResponce.data.lang + "(" + "Store Location" + ")&iwloc=A&hl=es"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlAddress))
                                activity!!.startActivity(intent)
                            }
                        }
                    } else {
                        val error = JSONObject(response.errorBody()!!.string())
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            error.getString("message")
                        )
                    }
                }

                override fun onFailure(call: Call<RestResponse<locationModel>>, t: Throwable) {
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        activity!!,
                        activity!!.resources.getString(R.string.error_msg)
                    )
                }

            })
        }

        if (isCheckNetwork(activity!!)) {
            callApiBanner()

        } else {
            alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
        }

        tvCategoryViewAll.setOnClickListener {
            openActivity(AllCategoryActivity::class.java)
        }

        tvLatestProductViewAll.setOnClickListener {
            startActivity(
                Intent(activity!!, CategoryByProductActivity::class.java).putExtra(
                    "cat_id",
                    "type_1"
                ).putExtra("cat_name", "LATEST PRODUCTS")
            )
        }

        tvExploreProductViewAll.setOnClickListener {
            startActivity(
                Intent(activity!!, CategoryByProductActivity::class.java).putExtra(
                    "cat_id",
                    "type_2"
                ).putExtra("cat_name", "EXPLORE PRODUCTS")
            )
        }
    }


    //:::::::::::::Banner Api:::::::::::::://
    private fun callApiBanner() {
        showLoadingProgress(activity!!)
        val call = ApiClient.getClient.getBanner()
        call.enqueue(object : Callback<ListResponse<BannerModel>> {
            override fun onResponse(
                call: Call<ListResponse<BannerModel>>,
                response: Response<ListResponse<BannerModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResponse<BannerModel> = response.body()!!
                    if (restResponce.status.toString() == "1") {
                        if (restResponce.data!!.size > 0) {
                            val bannerList = restResponce.data
                            loadPagerImages(bannerList)
                            callApiCategoryFood()
                        } else {
                            callApiCategoryFood()
                        }
                    } else if (restResponce.status == 0) {
                        callApiCategoryFood()
                    }
                } else {
                    callApiCategoryFood()
                }
            }

            override fun onFailure(call: Call<ListResponse<BannerModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun loadPagerImages(imageHase: ArrayList<BannerModel>) {
        var pos=0
        val bannerAdapter = object : BaseAdaptor<BannerModel>(activity!!, imageHase) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: BannerModel,
                position: Int
            ) {
                val ivFood: ImageView = holder!!.itemView.findViewById(R.id.ivBannereSlider)
                Glide.with(activity!!).load(imageHase.get(position).image).into(ivFood)
                val view1: View = holder.itemView.findViewById(R.id.view1)
                if (position == 0) {
                    view1.visibility = View.VISIBLE
                } else {
                    view1.visibility = View.GONE
                }
                pos=position
                holder.itemView.setOnClickListener {
                    //intent.getStringExtra("Item_Id")!!
                    if (`val`.type.toString() == "item") {
                        startActivity(
                            Intent(
                                activity!!,
                                ItemDetailActivity::class.java
                            ).putExtra("Item_Id", `val`.item_id.toString()).putExtra(
                                "isItemActivity",
                                resources.getString(R.string.is_item_Act_home)
                            )
                        )
                    } else if (`val`.type.toString() == "category") {
                        startActivity(
                            Intent(activity!!, CategoryByProductActivity::class.java).putExtra(
                                "cat_id",
                                `val`.cat_id.toString()
                            ).putExtra("cat_name", `val`.category_name.toString())
                        )
                    }
                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_bannerslider
            }

        }
        if (isAdded) {
            rvBanner.layoutManager = LinearLayoutManager(
                activity!!,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            rvBanner.adapter = bannerAdapter
            rvBanner.itemAnimator = DefaultItemAnimator()
            LinearSnapHelper().attachToRecyclerView(rvBanner)

        }

        val timer=Timer()
        timer.schedule(AutoScrollTask(pos,rvBanner,imageHase),0,5000L)

    }


    var foodCategoryId = ""


    private class AutoScrollTask(private var position: Int,private var rvBanner: RecyclerView,private var arrayList: ArrayList<BannerModel>) : TimerTask() {
        override fun run() {
            Log.e("arrayLisrSize",arrayList.size.toString())
            if(arrayList.size>1) {

                if (position == arrayList.size - 1) {
                    position=0
                } else
                {
                    position++
                }

            }
            rvBanner.smoothScrollToPosition(position)
        }
    }
    //:::::::::::::Category Api:::::::::::::://
    private fun callApiCategoryFood() {
        val call = ApiClient.getClient.getCategory()
        call.enqueue(object : Callback<ListResponse<CategoryItemModel>> {
            override fun onResponse(
                call: Call<ListResponse<CategoryItemModel>>,
                response: Response<ListResponse<CategoryItemModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResponse<CategoryItemModel> = response.body()!!
                    if (restResponce.status.toString() == "1") {
                        if (restResponce.data!!.size > 0) {
                            val foodCategoryList = restResponce.data
                            foodCategoryId = foodCategoryList.get(0).id.toString()
                            setFoodCategoryAdaptor(foodCategoryList)

                            callApiLatestProduct(false, false)
                        } else {
                            callApiLatestProduct(false, false)
                        }
                    } else if (restResponce.status.toString() == "0") {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            restResponce.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ListResponse<CategoryItemModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun setFoodCategoryAdaptor(foodCategoryList: ArrayList<CategoryItemModel>) {
        if (foodCategoryList.size > 0) {
            if (isAdded) {
                rvFoodCategory.visibility = View.VISIBLE
                tvCategory.visibility = View.GONE
            }
            val foodCategoryAdapter = object : BaseAdaptor<CategoryItemModel>(
                activity!!,
                foodCategoryList
            ) {
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: CategoryItemModel,
                    position: Int
                ) {
                    val tvFoodCategoryName: TextView =
                        holder!!.itemView.findViewById(R.id.tvFoodCategoryName)
                    val ivFoodCategory: ImageView =
                        holder.itemView.findViewById(R.id.ivFoodCategory)
                    val ViewFrist: View = holder.itemView.findViewById(R.id.ViewFrist)
                    val ViewLast: View = holder.itemView.findViewById(R.id.ViewLast)
                    val cvCart: CardView = holder.itemView.findViewById(R.id.cvCart)

                    if (position == 0) {
                        ViewFrist.visibility = View.VISIBLE
                        ViewLast.visibility = View.GONE
                    } else if (position == (foodCategoryList.size - 1)) {
                        ViewFrist.visibility = View.GONE
                        ViewLast.visibility = View.VISIBLE
                    } else {
                        ViewFrist.visibility = View.GONE
                        ViewLast.visibility = View.GONE
                    }
                    val i = position % 5
                    if (i == 0) {
                        cvCart.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color1,
                                null
                            )
                        )
                    } else if (i == 1) {
                        cvCart.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color2,
                                null
                            )
                        )
                    } else if (i == 2) {
                        cvCart.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color3,
                                null
                            )
                        )
                    } else if (i == 3) {
                        cvCart.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color4,
                                null
                            )
                        )
                    } else if (i == 4) {
                        cvCart.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color5,
                                null
                            )
                        )
                    }


                    tvFoodCategoryName.text = foodCategoryList.get(position).categoryName
                    Glide.with(activity!!).load(foodCategoryList.get(position).image)
                        .into(ivFoodCategory)

                    holder.itemView.setOnClickListener {
                        startActivity(
                            Intent(activity!!, CategoryByProductActivity::class.java).putExtra(
                                "cat_id",
                                foodCategoryList[position].id.toString()
                            ).putExtra("cat_name", foodCategoryList[position].categoryName)
                        )
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_homecategory
                }
            }
            if (isAdded) {
                rvFoodCategory.adapter = foodCategoryAdapter
                rvFoodCategory.layoutManager = LinearLayoutManager(
                    activity!!,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                rvFoodCategory.itemAnimator = DefaultItemAnimator()
                rvFoodCategory.isNestedScrollingEnabled = true
            }
        } else {
            rvFoodCategory.visibility = View.GONE
            tvCategory.visibility = View.VISIBLE
        }

    }

    //:::::::::::::Latest Product Api:::::::::::::://
    private fun callApiLatestProduct(isCart: Boolean, isFavourite: Boolean) {
        if (isCart || isFavourite) {
            latestProductList.clear()
            showLoadingProgress(activity!!)
        }
        val map = HashMap<String, String>()
        if (getBooleanPref(activity!!, isLogin)) {
            map["user_id"] = getStringPref(activity!!, SharePreference.userId)!!
        } else {
            map["user_id"] = ""
        }
        val call = ApiClient.getClient.getLatestItem(map)
        call.enqueue(object : Callback<RestResponse<PaginationModel<ProductItemModel>>> {
            override fun onResponse(
                call: Call<RestResponse<PaginationModel<ProductItemModel>>>,
                response: Response<RestResponse<PaginationModel<ProductItemModel>>>
            ) {
                if (response.code() == 200) {
                    if (isCart) {
                        isCartTrueOut = false
                    }
                    if (isFavourite) {
                        isFavouriteOut = false
                    }


                    val restResponce: RestResponse<PaginationModel<ProductItemModel>> =
                        response.body()!!

                    if (restResponce.status.toString() == "1") {
                        if (restResponce.data!!.data!!.size > 0) {
                            rvLatestProduct?.visibility = View.VISIBLE
                            tvLatestProduct?.visibility = View.GONE
                            latestProductList.clear()

                            for (i in 0 until restResponce.data.data!!.size) {
                                restResponce.data.data[i].variation!![0].isSelect = true
                                var getpos = 0
                                for (j in 0 until restResponce.data.data[i].variation!!.size) {
                                    if (restResponce.data.data[i].variation!![j].stock == 0) {
                                        getpos = getpos + 1
                                    }
                                }

                                if (getpos == restResponce.data.data[i].variation!!.size) {
                                    restResponce.data.data[i].isOutOfStock = true
                                } else {
                                    restResponce.data.data[i].isOutOfStock = false
                                    for (j in 0 until restResponce.data.data[i].variation!!.size) {
                                        if (restResponce.data.data[i].variation!![j].stock!! > 0) {
                                            restResponce.data.data[i].getvariation.add(restResponce.data.data[i].variation!![j])
                                        }
                                    }
                                    restResponce.data.data[i].getvariation!![0].isSelect = true
                                }
                            }
                            latestProductList.addAll(restResponce.data.data!!)
                            latestProductAdapter!!.notifyDataSetChanged()
                        } else {
                            rvLatestProduct.visibility = View.GONE
                            tvLatestProduct.visibility = View.VISIBLE
                        }

                        setStringPref(activity!!, isCurrancy, restResponce.currency.toString())
                        setStringPref(
                            activity!!,
                            isMiniMum,
                            restResponce.min_order_amount.toString()
                        )
                        setStringPref(
                            activity!!,
                            isMaximum,
                            restResponce.max_order_amount.toString()
                        )
                        setStringPref(
                            activity!!,
                            isMiniMumQty,
                            restResponce.max_order_qty.toString()
                        )
                        setStringPref(
                            activity!!,
                            userRefralAmount,
                            restResponce.referral_amount.toString()
                        )
                        setStringPref(
                            activity!!,
                            map_key,
                            "AIzaSyAuLjnKdJiQ2K_2eoucQdRD38R3NkWWCX8"
                        )
                        callApiExploreProduct()
                    } else if (restResponce.status.toString() == "0") {
                        callApiExploreProduct()
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status") == "2") {
                        dismissLoadingProgress()
                        setLogout(activity!!)
                    } else {
                        callApiExploreProduct()
                    }
                }

            }

            override fun onFailure(
                call: Call<RestResponse<PaginationModel<ProductItemModel>>>,
                t: Throwable
            ) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun setLatestProductAdaptor(productList: ArrayList<ProductItemModel>) {
        latestProductAdapter = object : BaseAdaptor<ProductItemModel>(activity!!, productList) {
            @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: ProductItemModel,
                position: Int
            ) {
                val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                val tvFoodPriceGrid: TextView = holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                val tvDiscountPrice: TextView =
                    holder.itemView.findViewById(R.id.tvDiscountPriceText)
                val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)


                val icLike: ImageView = holder.itemView.findViewById(R.id.icLike)

                tvFoodName.text = productList.get(position).itemName
                val ViewFrist: View = holder.itemView.findViewById(R.id.ViewFrist)
                val ViewLast: View = holder.itemView.findViewById(R.id.ViewLast)


                val ivCart: ImageView = holder.itemView.findViewById(R.id.ivCart)
                val ivOutOfStock: LinearLayout = holder.itemView.findViewById(R.id.ivOutOfStock)
                val tvWeight: TextView = holder.itemView.findViewById(R.id.tvWeight)
                if (productList.get(position).isOutOfStock!!) {
                    ivOutOfStock.visibility = View.VISIBLE
                    ivCart.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.bg_gray_corner_10, null)
                } else {
                    ivOutOfStock.visibility = View.GONE
                    ivCart.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.bg_green_corner_10, null)
                }
                var price = ""
//                var productPrice = ""
                if (productList[position].getvariation.size > 0) {
                    tvWeight.text = productList[position].getvariation[0].weight
                    price = productList[position].getvariation[0].price.toString()
//                    productPrice = productList[position].getvariation[0].productPrice.toString()
                } else {
                    tvWeight.text = productList[position].variation!![0].weight
                    price = productList[position].variation!![0].price!!
//                    productPrice = productList[position].variation!![0].productPrice!!
                }


                if (getStringPref(activity!!, SharePreference.SELECTED_LANGUAGE).equals(
                        resources.getString(
                            R.string.language_hindi
                        )
                    )
                ) {
                    tvFoodPriceGrid.text = String.format(
                        Locale.US,
                        "%,.2f",
                        price.toDouble()
                    ) + getCurrancy(activity!!)


                } else {
                    tvFoodPriceGrid.text = getCurrancy(activity!!) + String.format(
                        Locale.US,
                        "%,.2f",
                        price.toDouble()
                    )


                }
                tvDiscountPrice.paintFlags =
                    tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

//                if (productPrice != "null") {
//                    if (getStringPref(activity!!, SharePreference.SELECTED_LANGUAGE).equals(
//                            resources.getString(
//                                R.string.language_hindi
//                            )
//                        )
//                    ) {
//
//                        tvDiscountPrice.text = String.format(
//                            Locale.US,
//                            "%,.2f",
//                            productPrice.toDouble()
//                        ) + getCurrancy(activity!!)
//
//                    } else {
//
//
//                        tvDiscountPrice.text = getCurrancy(activity!!) + String.format(
//                            Locale.US,
//                            "%,.2f",
//                            productPrice.toDouble()
//                        )
//
//
//                    }
//                }
                Glide.with(activity!!)
                    .load(productList.get(position).itemimage!!.image)
                    .into(ivFood)

                val ivCopyFood: ImageView = holder.itemView.findViewById(R.id.ivCopyFood)
                Glide.with(activity!!)
                    .load(productList.get(position).itemimage!!.image)
                    .into(ivCopyFood)

                ivCart.setOnClickListener {
                    if (productList[position].variation!!.size > 1) {
                        alertVariationDialog(
                            activity!!,
                            productList[position].variation!!,
                            productList[position].id.toString(),
                            ivCopyFood,
                            productList[position].itemName!!,
                            productList[position].itemimage!!.image_name!!,
                            position
                        )
                    } else if (productList[position].variation!!.size == 1) {
                        if (getBooleanPref(activity!!, isLogin)) {
                            val map = HashMap<String, String>()
                            map["item_id"] = productList[position].id.toString()
                            map["qty"] = "1"
                            map["item_name"] = productList[position].itemName!!
                            map["image_name"] = productList[position].itemimage!!.image_name!!
                            map["price"] = String.format(
                                Locale.US,
                                "%.2f",
                                productList[position].variation!![0].price!!.toDouble()
                            )
                            map["weight"] = productList[position].variation!![0].weight!!
                            map["variation_id"] = productList[position].variation!![0].id.toString()
                            map["user_id"] = getStringPref(activity!!, userId)!!
                            callApiAddToCart(map, ivCopyFood)
                        } else {
                            openActivity(LoginSelectActivity::class.java)
                            activity!!.finish()
                            activity!!.finishAffinity()
                        }

                    }


                }

                holder.itemView.setOnClickListener {
                    startActivity(
                        Intent(activity!!, ItemDetailActivity::class.java).putExtra(
                            "Item_Id",
                            productList[position].id.toString()
                        ).putExtra("isItemActivity", resources.getString(R.string.is_item_Act_home))
                    )
                }
                val rvFood: RelativeLayout = holder.itemView.findViewById(R.id.rvFood)
                val i = position % 5

                if (i == 0) {
                    rvFood.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color1,
                            null
                        )
                    )
                } else if (i == 1) {
                    rvFood.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color2,
                            null
                        )
                    )
                } else if (i == 2) {
                    rvFood.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color3,
                            null
                        )
                    )
                } else if (i == 3) {
                    rvFood.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color4,
                            null
                        )
                    )
                } else if (i == 4) {
                    rvFood.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color5,
                            null
                        )
                    )
                }

                if (position == 0) {
                    ViewFrist.visibility = View.VISIBLE
                    ViewLast.visibility = View.GONE
                } else if (position == (productList.size - 1)) {
                    ViewFrist.visibility = View.GONE
                    ViewLast.visibility = View.VISIBLE
                } else {
                    ViewFrist.visibility = View.GONE
                    ViewLast.visibility = View.GONE
                }
                tvFoodPriceGrid.visibility = View.VISIBLE
                if (productList.get(position).isFavorite == 0) {
                    icLike.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_unlike,
                            null
                        )
                    )
                    icLike.imageTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.red,
                            null
                        )
                    )
                } else {
                    icLike.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_favourite_like,
                            null
                        )
                    )
                    icLike.imageTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.red,
                            null
                        )
                    )
                }



                icLike.setOnClickListener {
                    if (getBooleanPref(activity!!, SharePreference.isLogin)) {
                        if (productList.get(position).isFavorite == 0) {
                            val map = HashMap<String, String>()
                            map["item_id"] = productList.get(position).id.toString()
                            map["user_id"] = getStringPref(activity!!, SharePreference.userId)!!
                            if (isCheckNetwork(activity!!)) {
                                callApiFavourite(map, position, "latestAdaptor")
                            } else {
                                alertErrorOrValidationDialog(
                                    activity!!,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    } else {
                        openActivity(LoginSelectActivity::class.java)
                        activity!!.finish()
                        activity!!.finishAffinity()
                    }

                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_latestitems
            }
        }
        if (isAdded) {
            rvLatestProduct.adapter = latestProductAdapter
            rvLatestProduct.itemAnimator = DefaultItemAnimator()
            rvLatestProduct.layoutManager = LinearLayoutManager(
                activity!!,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            rvLatestProduct.isNestedScrollingEnabled = true
        }
    }

    //:::::::::::::Explore Product Api:::::::::::::://
    private fun callApiExploreProduct() {
        exploreProductList.clear()
        val map = HashMap<String, String>()
        if (getBooleanPref(activity!!, isLogin)) {
            map["user_id"] = getStringPref(activity!!, userId)!!
        } else {
            map["user_id"] = ""
        }
        val call = ApiClient.getClient.getExploreItem(map)
        call.enqueue(object : Callback<RestResponse<PaginationModel<ProductItemModel>>> {
            override fun onResponse(
                call: Call<RestResponse<PaginationModel<ProductItemModel>>>,
                response: Response<RestResponse<PaginationModel<ProductItemModel>>>
            ) {
                if (response.code() == 200) {

                    val restResponce: RestResponse<PaginationModel<ProductItemModel>> =
                        response.body()!!

                    if (restResponce.status.toString().equals("1")) {
                        if (restResponce.data!!.data!!.size > 0) {
                            rvExploreProduct?.visibility = View.VISIBLE
                            tvExploreProduct?.visibility = View.GONE
                            for (i in 0 until restResponce.data.data!!.size) {
                                restResponce.data.data[i].variation!![0].isSelect = true
                                var getpos = 0
                                var isOnlyOneStack = false
                                for (j in 0 until restResponce.data.data[i].variation!!.size) {
                                    if (restResponce.data.data[i].variation!![j].stock == 0) {
                                        getpos = getpos + 1
                                    }
                                }


                                if (getpos == restResponce.data.data[i].variation!!.size) {
                                    restResponce.data.data[i].isOutOfStock = true
                                } else {
                                    restResponce.data.data[i].isOutOfStock = false
                                    for (j in 0 until restResponce.data.data[i].variation!!.size) {
                                        if (restResponce.data.data[i].variation!![j].stock!! > 0) {
                                            restResponce.data.data[i].getvariation.add(restResponce.data.data[i].variation!![j])
                                        }
                                    }
                                    restResponce.data.data[i].getvariation!![0].isSelect = true
                                }
                            }
                            exploreProductList.addAll(restResponce.data.data)
                            exploreProductAdapter!!.notifyDataSetChanged()
                        } else {
                            rvExploreProduct.visibility = View.GONE
                            tvExploreProduct.visibility = View.VISIBLE
                        }

                        if (getBooleanPref(activity!!, isLogin)) {
                            if (isCheckNetwork(activity!!)) {
                                callApiCartCount(false)
                            } else {
                                alertErrorOrValidationDialog(
                                    activity!!,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        } else {
                            dismissLoadingProgress()
                            rlCount!!.visibility = View.GONE
                        }

                    } else if (restResponce.status.toString().equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            restResponce.message
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status").equals("2")) {
                        dismissLoadingProgress()
                        setLogout(activity!!)
                    } else {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            error.getString("message")
                        )
                    }
                }

            }

            override fun onFailure(
                call: Call<RestResponse<PaginationModel<ProductItemModel>>>,
                t: Throwable
            ) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun setExploreProductAdaptor(productList: ArrayList<ProductItemModel>) {
        exploreProductAdapter = object : BaseAdaptor<ProductItemModel>(activity!!, productList) {
            @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: ProductItemModel,
                position: Int
            ) {
                val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                val tvFoodPriceGrid: TextView = holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                val tvDiscountPrice: TextView =
                    holder.itemView.findViewById(R.id.tvDiscountPriceText)

                val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)
                val icLike: ImageView = holder.itemView.findViewById(R.id.icLike)
                val ivCart: ImageView = holder.itemView.findViewById(R.id.ivCart)
                val ivOutOfStock: LinearLayout = holder.itemView.findViewById(R.id.ivOutOfStock)
                val tvWeight: TextView = holder.itemView.findViewById(R.id.tvWeight)
                tvFoodName.text = productList.get(position).itemName

                var price = ""
//                var productPrice = ""
                if (productList[position].getvariation.size > 0) {
                    tvWeight.text = productList[position].getvariation[0].weight
                    price = productList[position].getvariation[0].price.toString()
//                    productPrice = productList[position].getvariation[0].productPrice.toString()
//                    Log.e("exploreProductPrice", productPrice.toString())
                } else {
                    tvWeight.text = productList[position].variation!![0].weight
                    price = productList[position].variation!![0].price!!
//                    productPrice = productList[position].variation!![0].productPrice!!
//                    Log.e("exploreProductPrice", productPrice.toString())

                }

                if (getStringPref(activity!!, SharePreference.SELECTED_LANGUAGE).equals(
                        resources.getString(
                            R.string.language_hindi
                        )
                    )
                ) {
                    tvFoodPriceGrid.text = String.format(
                        Locale.US,
                        "%,.2f",
                        price.toDouble()
                    ) + getCurrancy(activity!!)

                } else {
                    tvFoodPriceGrid.text = getCurrancy(activity!!) + String.format(
                        Locale.US,
                        "%,.2f",
                        price.toDouble()
                    )


                }

//                if (productPrice != "null") {
//                    if (getStringPref(activity!!, SharePreference.SELECTED_LANGUAGE).equals(
//                            resources.getString(
//                                R.string.language_hindi
//                            )
//                        )
//                    ) {
//                        tvDiscountPrice.text = String.format(
//                            Locale.US,
//                            "%,.2f",
//                            productPrice.toDouble()
//                        ) + getCurrancy(activity!!)
//
//                    } else {
//                        tvDiscountPrice.text = getCurrancy(activity!!) + String.format(
//                            Locale.US,
//                            "%,.2f",
//                            productPrice.toDouble()
//                        )
//
//
//                    }
//                }
                tvDiscountPrice.paintFlags =
                    tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                Glide.with(activity!!)
                    .load(exploreProductList.get(position).itemimage!!.image)
                    .into(ivFood)

                val ivCopyFood: ImageView = holder.itemView.findViewById(R.id.ivCopyFood)

                Glide.with(activity!!)
                    .load(exploreProductList.get(position).itemimage!!.image)
                    .into(ivCopyFood)

                ivCart.setOnClickListener {
                    if (productList[position].variation!!.size > 1) {
                        alertVariationDialog(
                            activity!!,
                            productList[position].variation!!,
                            productList[position].id.toString(),
                            ivCopyFood,
                            productList[position].itemName!!,
                            productList[position].itemimage!!.image_name!!,
                            position
                        )
                    } else if (productList[position].variation!!.size == 1) {
                        if (getBooleanPref(activity!!, isLogin)) {
                            val map = HashMap<String, String>()
                            map["item_id"] = productList[position].id.toString()
                            map["qty"] = "1"
                            map["item_name"] = productList[position].itemName!!
                            map["image_name"] = productList[position].itemimage!!.image_name!!
                            map["price"] = String.format(
                                Locale.US,
                                "%.2f",
                                productList[position].variation!![0].price!!.toDouble()
                            )
                            map["weight"] = productList[position].variation!![0].weight!!
                            map["variation_id"] = productList[position].variation!![0].id.toString()
                            map["user_id"] = getStringPref(activity!!, userId)!!
                            callApiAddToCart(map, ivCopyFood)
                        } else {
                            openActivity(LoginSelectActivity::class.java)
                            activity!!.finish()
                            activity!!.finishAffinity()
                        }

                    }
                }

                if (productList[position].isOutOfStock!!) {
                    ivOutOfStock.visibility = View.VISIBLE
                    ivCart.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.bg_gray_corner_10, null)
                } else {
                    ivOutOfStock.visibility = View.GONE
                    ivCart.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.bg_green_corner_10, null)
                }

                // tvWeight.text = productList[position].variation!![0].weight

                //exploreProductList[position].itemPrice!!.split(",")[0]

                holder.itemView.setOnClickListener {
                    startActivity(
                        Intent(
                            activity!!,
                            ItemDetailActivity::class.java
                        ).putExtra("Item_Id", productList[position].id.toString()).putExtra(
                            "isItemActivity",
                            resources.getString(R.string.is_item_Act_home)
                        )
                    )
                }

                val rvFood: RelativeLayout = holder.itemView.findViewById(R.id.rvFood)
                val i = position % 5

                if (i == 0) {
                    rvFood.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color1,
                            null
                        )
                    )
                } else if (i == 1) {
                    rvFood.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color2,
                            null
                        )
                    )
                } else if (i == 2) {
                    rvFood.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color3,
                            null
                        )
                    )
                } else if (i == 3) {
                    rvFood.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color4,
                            null
                        )
                    )
                } else if (i == 4) {
                    rvFood.backgroundTintList = ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color5,
                            null
                        )
                    )
                }

                val ViewFrist: View = holder.itemView.findViewById(R.id.ViewFrist)
                val ViewLast: View = holder.itemView.findViewById(R.id.ViewLast)

                if (position == 0) {
                    ViewFrist.visibility = View.VISIBLE
                    ViewLast.visibility = View.GONE
                } else if (position == (productList.size - 1)) {
                    ViewFrist.visibility = View.GONE
                    ViewLast.visibility = View.VISIBLE
                } else {
                    ViewFrist.visibility = View.GONE
                    ViewLast.visibility = View.GONE
                }

                tvFoodPriceGrid.visibility = View.VISIBLE

                if (productList.get(position).isFavorite == 0) {
                    icLike.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_unlike,
                            null
                        )
                    )
                    icLike.imageTintList = ColorStateList.valueOf(Color.RED)
                } else {
                    icLike.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_favourite_like,
                            null
                        )
                    )
                    icLike.imageTintList = ColorStateList.valueOf(Color.RED)
                }

                icLike.setOnClickListener {
                    if (getBooleanPref(activity!!, isLogin)) {
                        if (productList.get(position).isFavorite == 0) {
                            val map = HashMap<String, String>()
                            map["item_id"] = exploreProductList[position].id.toString()
                            map["user_id"] = getStringPref(activity!!, SharePreference.userId)!!
                            if (isCheckNetwork(activity!!)) {
                                callApiFavourite(map, position, "exploreAdaptor")
                            } else {
                                alertErrorOrValidationDialog(
                                    activity!!,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    } else {
                        openActivity(LoginSelectActivity::class.java)
                        activity!!.finish()
                        activity!!.finishAffinity()
                    }
                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_latestitems
            }

        }
        if (isAdded) {
            rvExploreProduct.adapter = exploreProductAdapter
            rvExploreProduct.itemAnimator = DefaultItemAnimator()
            rvExploreProduct.layoutManager = LinearLayoutManager(
                activity!!,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            rvExploreProduct.isNestedScrollingEnabled = true
        }
    }

    private fun callApiFavourite(
        map: HashMap<String, String>,
        position: Int,
        adaptorType: String
    ) {
        showLoadingProgress(activity!!)
        val call = ApiClient.getClient.setAddFavorite(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        dismissLoadingProgress()
                        if (adaptorType.equals("exploreAdaptor")) {
                            exploreProductList[position].isFavorite = 1
                            for (i in 0 until latestProductList.size) {
                                if (latestProductList[i].id == exploreProductList[position].id) {
                                    latestProductList[i].isFavorite = 1
                                }
                            }
                            exploreProductAdapter!!.notifyItemChanged(position)
                            latestProductAdapter!!.notifyDataSetChanged()
                        } else {
                            latestProductList[position].isFavorite = 1
                            for (i in 0 until exploreProductList.size) {
                                if (exploreProductList[i].id == latestProductList[position].id) {
                                    exploreProductList[i].isFavorite = 1
                                }
                            }
                            exploreProductAdapter!!.notifyDataSetChanged()
                            latestProductAdapter!!.notifyItemChanged(position)
                        }

                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiCartCount(isAddToCart: Boolean) {
        if (isAddToCart) {
            showLoadingProgress(activity!!)
        }
        val map = HashMap<String, String>()
        map["user_id"] = getStringPref(activity!!, SharePreference.userId)!!
        val call = ApiClient.getClient.getCartCount(map)
        call.enqueue(object : Callback<RestResponse<CartCountModel>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<RestResponse<CartCountModel>>,
                response: Response<RestResponse<CartCountModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: RestResponse<CartCountModel> = response.body()!!
                    if (isAdded) {
                        if (restResponce.status == 0) {
                            tvCount!!.text = "0"
                            rlItem.visibility = View.GONE
                        } else {
                            Common.getLog("GetCount", restResponce.data!!.cartcount!!)
                            if (restResponce.data!!.cartcount!!.toInt() > 0) {
                                rlCount!!.visibility = View.VISIBLE
                                rlItem.visibility = View.VISIBLE
                                tvCount!!.text = restResponce.data.cartcount
                                tvCartCount!!.text = restResponce.data.cartcount
                                tvCartFoodPrice.text = getCurrancy(activity!!) + String.format(
                                    Locale.US,
                                    "%,.2f",
                                    restResponce.data.cartprice!!.toDouble()
                                )
                                rlItem.setOnClickListener {
                                    startActivity(
                                        Intent(
                                            activity!!,
                                            CartActivity::class.java
                                        ).putExtra(
                                            "isActivity",
                                            resources.getString(R.string.is_item_Act_home)
                                        )
                                    )
                                }
                            } else {
                                rlItem.visibility = View.GONE
                                tvCount!!.text = restResponce.data.cartcount
                            }
                        }
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        activity!!,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<RestResponse<CartCountModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    private fun callApiAddToCart(map: HashMap<String, String>, targetView: ImageView) {
        showLoadingProgress(activity!!)
        val call = ApiClient.getClient.getAddToCart(map)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                dismissLoadingProgress()
                val restResponce: SingleResponse = response.body()!!
                if (response.code() == 200) {
                    if (restResponce.status == 1) {
                        makeFlyAnimation(targetView)
                    } else {
                        alertErrorOrValidationDialog(activity!!, restResponce.message)
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    alertErrorOrValidationDialog(activity!!, error.getString("message"))
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun makeFlyAnimation(targetView: ImageView) {
        CircleAnimationUtil().attachActivity(activity!!).setTargetView(targetView)
            .setMoveDuration(1000)
            .setDestView(cvCart).setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCheckNetwork(activity!!)) {
                        callApiLatestProduct(true, false)
                    } else {
                        alertErrorOrValidationDialog(
                            activity!!,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {}
            }).startAnimation()
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(activity!!, false)
        if (isCartTrueOut || isFavouriteOut) {
            if (isCheckNetwork(activity!!)) {
                callApiLatestProduct(isCartTrueOut, isFavouriteOut)
            } else {
                alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
            }
        }
    }

    fun alertVariationDialog(
        act: Activity,
        listVarition: ArrayList<VariationModel>,
        strId: String,
        ivCopyFood: ImageView,
        strName: String,
        strImage: String,
        pos: Int
    ) {

        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_variation, null, false)
            val rvVariation: RecyclerView = m_view.findViewById(R.id.rvVariation)
            setVariationAdaptor(listVarition, rvVariation)
            val tvCancle: TextView = m_view.findViewById(R.id.tvCancle)
            val tvFoodName: TextView = m_view.findViewById(R.id.tvFoodName)
            tvFoodName.text = strName
            val finalDialog: Dialog = dialog
            tvCancle.setOnClickListener {
                finalDialog.dismiss()
                for (i in 0 until listVarition.size) {
                    listVarition[i].isSelect = false
                }
                for (i in 0 until listVarition.size) {
                    if (listVarition[i].stock!! > 0) {
                        listVarition[i].isSelect = true
                        break
                    }
                }
            }
            val tvSubmit: TextView = m_view.findViewById(R.id.tvSubmit)
            tvSubmit.setOnClickListener {
                dialog.dismiss()
                if (getBooleanPref(activity!!, isLogin)) {
                    var getPos = 0
                    var strPrice = ""
                    var strWeight = ""
                    var strVariationId = ""
                    for (i in 0 until listVarition.size) {
                        if (listVarition[i].isSelect) {
                            strPrice = listVarition[i].price!!
                            strWeight = listVarition[i].weight!!
                            strVariationId = listVarition[i].id.toString()
                            getPos = i
                        }
                    }
                    val map = HashMap<String, String>()
                    map["item_id"] = strId
                    map["qty"] = "1"
                    map["price"] = String.format(Locale.US, "%.2f", strPrice.toDouble())
                    map["weight"] = strWeight
                    map["variation_id"] = strVariationId
                    map["user_id"] = getStringPref(activity!!, SharePreference.userId)!!
                    map["image_name"] = strImage
                    map["item_name"] = strName
                    if (isCheckNetwork(activity!!)) {
                        callApiAddToCart(map, ivCopyFood)
                    } else {
                        alertErrorOrValidationDialog(
                            activity!!,
                            resources.getString(R.string.no_internet)
                        )
                    }
                } else {
                    openActivity(LoginSelectActivity::class.java)
                    activity!!.finish()
                    activity!!.finishAffinity()
                }
            }

            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun setVariationAdaptor(
        foodCategoryList: ArrayList<VariationModel>,
        rvVariation: RecyclerView
    ) {

        val foodCategoryAdapter = object : BaseAdaptor<VariationModel>(
            activity!!,
            foodCategoryList
        ) {
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: VariationModel,
                position: Int
            ) {
                holder!!.itemView.tvWeight.text = foodCategoryList[position].weight
                holder.itemView.tvPrice.text = getCurrancy(activity!!) + String.format(
                    Locale.US,
                    "%.2f",
                    foodCategoryList[position].price!!.toDouble()
                )

                if (foodCategoryList[position].stock!! > 0) {
                    holder.itemView.tvOutOfStock.visibility = View.GONE
                    holder.itemView.ivCheck.visibility = View.VISIBLE
                    if (foodCategoryList[position].isSelect) {
                        holder.itemView.ivCheck.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_check,
                                null
                            )
                        )
                        holder.itemView.ivCheck.imageTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorPrimary,
                                null
                            )
                        )
                    } else {
                        holder.itemView.ivCheck.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_uncheck,
                                null
                            )
                        )
                        holder.itemView.ivCheck.imageTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorPrimary,
                                null
                            )
                        )
                    }
                } else {
                    holder.itemView.tvOutOfStock.visibility = View.VISIBLE
                    holder.itemView.ivCheck.visibility = View.GONE
                }

                holder.itemView.ivCheck.setOnClickListener {
                    for (i in 0 until foodCategoryList.size) {
                        foodCategoryList[i].isSelect = false
                    }
                    foodCategoryList[position].isSelect = true
                    notifyDataSetChanged()
                }

            }

            override fun setItemLayout(): Int {
                return R.layout.row_variation
            }
        }
        if (isAdded) {
            rvVariation.apply {
                adapter = foodCategoryAdapter
                layoutManager = LinearLayoutManager(activity!!)
                itemAnimator = DefaultItemAnimator()
                isNestedScrollingEnabled = true
            }
        }
    }
}