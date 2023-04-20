package com.grocery.app.activity

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.RestResponse
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.model.*
import com.grocery.app.utils.*
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.getCurrancy
import com.grocery.app.utils.Common.getCurrentLanguage
import com.grocery.app.utils.Common.isCartCategoryOut
import com.grocery.app.utils.Common.isCartTrueOut
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference.Companion.getBooleanPref
import com.grocery.app.utils.SharePreference.Companion.getStringPref
import com.grocery.app.utils.SharePreference.Companion.isLogin
import com.grocery.app.utils.SharePreference.Companion.userId
import kotlinx.android.synthetic.main.activity_categorybyproduct.*
import kotlinx.android.synthetic.main.activity_categorybyproduct.ivBack
import kotlinx.android.synthetic.main.activity_categorybyproduct.tvNoDataFound
import kotlinx.android.synthetic.main.row_variation.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CategoryByProductActivity : BaseActivity() {

    var productItemList: ArrayList<ProductItemModel>? = ArrayList()
    var productAdapter: BaseAdaptor<ProductItemModel>? = null
    var manager1: GridLayoutManager? = null
    var CURRENT_PAGES: Int = 1
    var TOTAL_PAGES: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var pastVisiblesItems = 0
    var isLoding = true
    override fun setLayout(): Int {
        return R.layout.activity_categorybyproduct
    }

    override fun initView() {
        getCurrentLanguage(this@CategoryByProductActivity, false)
        tvTitleName.text = intent.getStringExtra("cat_name").toString().toUpperCase(Locale.US)
        setLatestProductAdaptor()
        ivBack.setOnClickListener {
            finish()
        }
        if(getStringPref(this@CategoryByProductActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        cvCart.setOnClickListener {
            if (getBooleanPref(this@CategoryByProductActivity, isLogin)) {
                startActivity(
                    Intent(
                        this@CategoryByProductActivity,
                        CartActivity::class.java
                    ).putExtra("isActivity", resources.getString(R.string.is_Cart_Act_catby))
                )
            } else {
                openActivity(LoginActivity::class.java)
                finish()
                finishAffinity()
            }
        }

        if (intent.getStringExtra("cat_id").equals("type_1")) {
            if (isCheckNetwork(this@CategoryByProductActivity)) {
                callApiLatestProduct(true)
            } else {
                alertErrorOrValidationDialog(
                    this@CategoryByProductActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        } else if (intent.getStringExtra("cat_id").equals("type_2")) {
            if (isCheckNetwork(this@CategoryByProductActivity)) {
                callApiExploreProduct(true)
            } else {
                alertErrorOrValidationDialog(
                    this@CategoryByProductActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        } else {
            if (isCheckNetwork(this@CategoryByProductActivity)) {
                callApiCatgoeyByProduct(true)
            } else {
                alertErrorOrValidationDialog(
                    this@CategoryByProductActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        }


        swiperefresh.setOnRefreshListener { // Your code to refresh the list here.
            if (isCheckNetwork(this@CategoryByProductActivity)) {
                swiperefresh.isRefreshing = false
                CURRENT_PAGES = 1
                TOTAL_PAGES = 0
                visibleItemCount = 0
                totalItemCount = 0
                pastVisiblesItems = 0
                isLoding = true
                productItemList!!.clear()
                if (intent.getStringExtra("cat_id").equals("type_1")) {
                    if (isCheckNetwork(this@CategoryByProductActivity)) {
                        callApiLatestProduct(true)
                    } else {
                        alertErrorOrValidationDialog(
                            this@CategoryByProductActivity,
                            resources.getString(R.string.no_internet)
                        )
                    }
                } else if (intent.getStringExtra("cat_id").equals("type_2")) {
                    if (isCheckNetwork(this@CategoryByProductActivity)) {
                        callApiExploreProduct(true)
                    } else {
                        alertErrorOrValidationDialog(
                            this@CategoryByProductActivity,
                            resources.getString(R.string.no_internet)
                        )
                    }
                } else {
                    if (isCheckNetwork(this@CategoryByProductActivity)) {
                        callApiCatgoeyByProduct(true)
                    } else {
                        alertErrorOrValidationDialog(
                            this@CategoryByProductActivity,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }
            } else {
                alertErrorOrValidationDialog(
                    this@CategoryByProductActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        }

        manager1 =
            GridLayoutManager(this@CategoryByProductActivity, 2, GridLayoutManager.VERTICAL, false)
        rvCategoryByProduct.layoutManager = manager1



        rvCategoryByProduct.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = manager1!!.getChildCount()
                    totalItemCount = manager1!!.getItemCount()
                    pastVisiblesItems = manager1!!.findFirstVisibleItemPosition()
                    if (isLoding && CURRENT_PAGES < TOTAL_PAGES) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            CURRENT_PAGES += 1
                            isLoding = false
                            if (intent.getStringExtra("cat_id").equals("type_1")) {
                                if (isCheckNetwork(this@CategoryByProductActivity)) {
                                    callApiLatestProduct(false)
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@CategoryByProductActivity,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            } else if (intent.getStringExtra("cat_id").equals("type_2")) {
                                if (isCheckNetwork(this@CategoryByProductActivity)) {
                                    callApiExploreProduct(false)
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@CategoryByProductActivity,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            } else {
                                if (isCheckNetwork(this@CategoryByProductActivity)) {
                                    callApiCatgoeyByProduct(false)
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@CategoryByProductActivity,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    //:::::::::::::CategoryByProduct Product Api:::::::::::::://
    private fun callApiCatgoeyByProduct(isFristTime: Boolean) {
        if (isFristTime) {
            showLoadingProgress(this@CategoryByProductActivity)
        } else {
            rlPaginationBack.visibility = View.VISIBLE
        }
        val map = HashMap<String, String>()
        map["cat_id"] = intent.getStringExtra("cat_id")!!
        if (getBooleanPref(this@CategoryByProductActivity, isLogin)) {
            map["user_id"] = getStringPref(this@CategoryByProductActivity, userId)!!
        } else {
            map["user_id"] = ""
        }
        val call = ApiClient.getClient.getCategoryByItem(map, CURRENT_PAGES.toString())
        call.enqueue(object : Callback<RestResponse<PaginationModel<ProductItemModel>>> {
            override fun onResponse(
                call: Call<RestResponse<PaginationModel<ProductItemModel>>>,
                response: Response<RestResponse<PaginationModel<ProductItemModel>>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<PaginationModel<ProductItemModel>> =
                        response.body()!!
                    if (restResponce.status == 1) {
                        tvNoDataFound.visibility = View.GONE
                        rvCategoryByProduct.visibility = View.VISIBLE
                        isLoding = true
                        val PaginationResponse: PaginationModel<ProductItemModel> =
                            restResponce.data!!
                        TOTAL_PAGES = PaginationResponse.lastPage!!
                        if (CURRENT_PAGES == 1 && PaginationResponse.data!!.size == 0) {
                            tvNoDataFound.visibility = View.VISIBLE
                            rvCategoryByProduct.visibility = View.GONE
                        } else {
                            tvNoDataFound.visibility = View.GONE
                            rvCategoryByProduct.visibility = View.VISIBLE
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
                                    restResponce.data.data[i].getvariation[0].isSelect = true
                                }
                            }
                            productItemList!!.addAll(restResponce.data.data)
                            productAdapter!!.notifyDataSetChanged()
                        }
                        if (isFristTime) {
                            if (getBooleanPref(this@CategoryByProductActivity, isLogin)) {
                                if (isCheckNetwork(this@CategoryByProductActivity)) {
                                    rlCount!!.visibility = View.VISIBLE
                                    callApiCartCount(true, false)
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@CategoryByProductActivity,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            } else {
                                rlCount!!.visibility = View.GONE
                                dismissLoadingProgress()
                            }
                        } else {
                            rlPaginationBack.visibility = View.GONE
                        }
                    } else if (restResponce.status.toString().equals("0")) {
                        dismissLoadingProgress()
                        tvNoDataFound.visibility = View.VISIBLE
                        rvCategoryByProduct.visibility = View.GONE
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status").equals("2")) {
                        dismissLoadingProgress()
                        Common.setLogout(this@CategoryByProductActivity)
                    } else {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@CategoryByProductActivity,
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
                    this@CategoryByProductActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //:::::::::::::Latest Product Api:::::::::::::://
    private fun callApiLatestProduct(isFirstTime: Boolean) {
        if (isFirstTime) {
            showLoadingProgress(this@CategoryByProductActivity)
        } else {
            rlPaginationBack.visibility = View.VISIBLE
        }

        val map = HashMap<String, String>()
        if (getBooleanPref(this@CategoryByProductActivity, isLogin)) {
            map["user_id"] = getStringPref(this@CategoryByProductActivity, userId)!!
        } else {
            map["user_id"] = ""
        }
        val call = ApiClient.getClient.getLatestItemPagination(map, CURRENT_PAGES.toString())
        call.enqueue(object : Callback<RestResponse<PaginationModel<ProductItemModel>>> {
            override fun onResponse(
                call: Call<RestResponse<PaginationModel<ProductItemModel>>>,
                response: Response<RestResponse<PaginationModel<ProductItemModel>>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<PaginationModel<ProductItemModel>> =
                        response.body()!!
                    if (restResponce.status.toString().equals("1")) {
                        tvNoDataFound.visibility = View.GONE
                        rvCategoryByProduct.visibility = View.VISIBLE
                        val PaginationResponse: PaginationModel<ProductItemModel> =
                            restResponce.data!!
                        isLoding = true
                        TOTAL_PAGES = PaginationResponse.lastPage!!
                        if (CURRENT_PAGES == 1 && PaginationResponse.data!!.size == 0) {
                            tvNoDataFound.visibility = View.VISIBLE
                            rvCategoryByProduct.visibility = View.GONE
                        } else {
                            tvNoDataFound.visibility = View.GONE
                            rvCategoryByProduct.visibility = View.VISIBLE
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
                                        if(restResponce.data.data[i].variation!![j].stock!=null){
                                            if (restResponce.data.data[i].variation!![j].stock!! > 0) {
                                              restResponce.data.data[i].getvariation.add(restResponce.data.data[i].variation!![j])
                                           }
                                        }

                                    }
                                    restResponce.data.data[i].getvariation[0].isSelect = true
                                }
                            }
                            productItemList!!.addAll(restResponce.data.data)
                            productAdapter!!.notifyDataSetChanged()
                        }

                        if (isFirstTime) {
                            if (getBooleanPref(
                                    this@CategoryByProductActivity,
                                    SharePreference.isLogin
                                )
                            ) {
                                if (isCheckNetwork(this@CategoryByProductActivity)) {
                                    callApiCartCount(isFristTime = true, isCart = false)
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@CategoryByProductActivity,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            } else {
                                dismissLoadingProgress()
                            }
                        } else {
                            rlPaginationBack.visibility = View.GONE
                        }

                    } else if (restResponce.status.toString().equals("0")) {
                        dismissLoadingProgress()
                        tvNoDataFound.visibility = View.VISIBLE
                        rvCategoryByProduct.visibility = View.GONE
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status").equals("2")) {
                        dismissLoadingProgress()
                        Common.setLogout(this@CategoryByProductActivity)
                    } else {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@CategoryByProductActivity,
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
                    this@CategoryByProductActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    //:::::::::::::Explore Product Api:::::::::::::://
    private fun callApiExploreProduct(isFirstTime: Boolean) {
        if (isFirstTime) {
            showLoadingProgress(this@CategoryByProductActivity)
        } else {
            rlPaginationBack.visibility = View.VISIBLE
        }
        val map = HashMap<String, String>()
        if (getBooleanPref(this@CategoryByProductActivity, isLogin)) {
            map["user_id"] = getStringPref(this@CategoryByProductActivity, userId)!!
        } else {
            map["user_id"] = ""
        }
        val call = ApiClient.getClient.getExploreItemPagination(map, CURRENT_PAGES.toString())
        call.enqueue(object : Callback<RestResponse<PaginationModel<ProductItemModel>>> {
            override fun onResponse(
                call: Call<RestResponse<PaginationModel<ProductItemModel>>>,
                response: Response<RestResponse<PaginationModel<ProductItemModel>>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<PaginationModel<ProductItemModel>> =
                        response.body()!!
                    if (restResponce.status.toString().equals("1")) {
                        tvNoDataFound.visibility = View.GONE
                        rvCategoryByProduct.visibility = View.VISIBLE
                        val PaginationResponse: PaginationModel<ProductItemModel> =
                            restResponce.data!!
                        isLoding = true
                        TOTAL_PAGES = PaginationResponse.lastPage!!
                        if (CURRENT_PAGES == 1 && PaginationResponse.data!!.size == 0) {
                            tvNoDataFound.visibility = View.VISIBLE
                            rvCategoryByProduct.visibility = View.GONE
                        } else {
                            tvNoDataFound.visibility = View.GONE
                            rvCategoryByProduct.visibility = View.VISIBLE
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
                                    restResponce.data.data[i].getvariation[0].isSelect = true
                                }
                            }
                            productItemList!!.addAll(restResponce.data.data)
                            productAdapter!!.notifyDataSetChanged()
                        }
                        if (isFirstTime) {
                            if (getBooleanPref(this@CategoryByProductActivity, isLogin)) {
                                if (isCheckNetwork(this@CategoryByProductActivity)) {
                                    callApiCartCount(true, false)
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@CategoryByProductActivity,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            } else {
                                dismissLoadingProgress()
                            }
                        } else {
                            rlPaginationBack.visibility = View.GONE
                        }
                    } else if (restResponce.status.toString().equals("0")) {
                        dismissLoadingProgress()
                        tvNoDataFound.visibility = View.VISIBLE
                        rvCategoryByProduct.visibility = View.GONE
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status").equals("2")) {
                        dismissLoadingProgress()
                        Common.setLogout(this@CategoryByProductActivity)
                    } else {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@CategoryByProductActivity,
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
                    this@CategoryByProductActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    fun setLatestProductAdaptor() {
        productAdapter = object :
            BaseAdaptor<ProductItemModel>(this@CategoryByProductActivity, productItemList!!) {
            @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: ProductItemModel,
                position: Int
            ) {
                val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                val tvFoodPriceGrid: TextView = holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                val tvDiscountPrice: TextView = holder.itemView.findViewById(R.id.tvDiscountPrice)
                val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)
                val icLike: ImageView = holder.itemView.findViewById(R.id.icLike)
                tvFoodName.text = productItemList!![position].itemName

                var price = ""
                var productPrice = ""
                var weight = ""
                val tvWeight: TextView = holder.itemView.findViewById(R.id.tvWeight)
                if (productItemList!![position].getvariation.size > 0) {
                    tvWeight.text = productItemList!![position].getvariation[0].weight
                    price = productItemList!![position].getvariation[0].price.toString()
//                    productPrice = productItemList!![position].getvariation[0].productPrice.toString()
                    weight = productItemList!![position].getvariation[0].weight!!
                } else {
                    tvWeight.text = productItemList!![position].variation!![0].weight
                    price = productItemList!![position].variation!![0].price!!
//                    productPrice = productItemList!![position].variation!![0].price!!
                    weight = productItemList!![position].variation!![0].weight!!
                }

                if (getStringPref(
                        this@CategoryByProductActivity,
                        SharePreference.SELECTED_LANGUAGE
                    ).equals(resources.getString(R.string.language_hindi))
                ) {
                    tvFoodPriceGrid.text =
                        String.format(Locale.US, "%,.2f", price.toDouble()) + Common.getCurrancy(
                            this@CategoryByProductActivity
                        )

                } else {
                    tvFoodPriceGrid.text =
                        Common.getCurrancy(this@CategoryByProductActivity) + String.format(
                            Locale.US,
                            "%,.2f",
                            price.toDouble()
                        )

                }



                if(productPrice!="null"&&productPrice.isNotEmpty())
                {
                        if (getStringPref(
                        this@CategoryByProductActivity,
                        SharePreference.SELECTED_LANGUAGE
                    ).equals(resources.getString(R.string.language_hindi))
                ) {

                    tvDiscountPrice.text =
                        String.format(Locale.US, "%,.2f", productPrice.toDouble()) + Common.getCurrancy(
                            this@CategoryByProductActivity
                        )
                } else {

                    tvDiscountPrice.text =
                        Common.getCurrancy(this@CategoryByProductActivity) + String.format(
                            Locale.US,
                            "%,.2f",
                            productPrice.toDouble()
                        )
                }
                }
                tvDiscountPrice.paintFlags = tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                Glide.with(this@CategoryByProductActivity)
                    .load(productItemList!![position].itemimage!!.image)
                    .into(ivFood)


                val ivCart: ImageView = holder.itemView.findViewById(R.id.ivAddCart)
                val ivOutOfStock: LinearLayout = holder.itemView.findViewById(R.id.ivOutOfStock)

                if (productItemList!![position].isOutOfStock!!) {
                    ivOutOfStock.visibility = View.VISIBLE
                    ivCart.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.bg_gray_corner_10, null)
                } else {
                    ivOutOfStock.visibility = View.GONE
                    ivCart.background =
                        ResourcesCompat.getDrawable(resources, R.drawable.bg_green_corner_10, null)
                }


                holder.itemView.setOnClickListener {
                    startActivity(
                        Intent(
                            this@CategoryByProductActivity,
                            ItemDetailActivity::class.java
                        ).putExtra("Item_Id", productItemList!![position].id.toString()).putExtra(
                            "isItemActivity",
                            resources.getString(R.string.is_item_Act_catby)
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


                val ivCopyFood: ImageView = holder.itemView.findViewById(R.id.ivCopyFood)
                Glide.with(this@CategoryByProductActivity)
                    .load(productItemList!!.get(position).itemimage!!.image)
                    .into(ivCopyFood)
                ivCart.setOnClickListener {
                    if (productItemList!![position].variation!!.size > 1) {
                        alertVariationDialog(
                            this@CategoryByProductActivity,
                            productItemList!![position].variation!!,
                            productItemList!![position].id.toString(),
                            ivCopyFood,
                            productItemList!![position].itemName!!,
                            productItemList!![position].itemimage!!.image_name!!
                        )
                    } else if (productItemList!![position].variation!!.size == 1) {
                        if (getBooleanPref(this@CategoryByProductActivity, isLogin)) {
                            val map = HashMap<String, String>()
                            map["item_id"] = productItemList!![position].id.toString()
                            map["qty"] = "1"
                            map["item_name"] = productItemList!![position].itemName!!
                            map["image_name"] = productItemList!![position].itemimage!!.image_name!!
                            map["price"] = String.format(
                                Locale.US,
                                "%.2f",
                                productItemList!![position].variation!![0].price!!.toDouble()
                            )
                            map["weight"] = productItemList!![position].variation!![0].weight!!
                            map["variation_id"] =
                                productItemList!![position].variation!![0].id.toString()
                            map["user_id"] = getStringPref(this@CategoryByProductActivity, userId)!!
                            callApiAddToCart(map, ivCopyFood)
                        } else {
                            openActivity(LoginSelectActivity::class.java)
                            finish()
                            finishAffinity()
                        }
                    }
                }


                tvFoodPriceGrid.visibility = View.VISIBLE

                if (productItemList!![position].isFavorite == 0) {
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
                    if (getBooleanPref(this@CategoryByProductActivity, isLogin)) {
                        if (productItemList!![position].isFavorite == 0) {
                            val map = HashMap<String, String>()
                            map["item_id"] = productItemList!![position].id.toString()
                            map["user_id"] = getStringPref(this@CategoryByProductActivity, userId)!!
                            if (isCheckNetwork(this@CategoryByProductActivity)) {
                                callApiFavourite(map, position, productItemList!!)
                            } else {
                                alertErrorOrValidationDialog(
                                    this@CategoryByProductActivity,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    } else {
                        openActivity(LoginSelectActivity::class.java)
                        finish()
                        finishAffinity()
                    }

                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_foodsubcategory
            }

        }
        rvCategoryByProduct.adapter = productAdapter
        rvCategoryByProduct.itemAnimator = DefaultItemAnimator()
        rvCategoryByProduct.isNestedScrollingEnabled = true
    }

    private fun callApiFavourite(
        map: HashMap<String, String>,
        position: Int,
        productList: ArrayList<ProductItemModel>
    ) {
        showLoadingProgress(this@CategoryByProductActivity)
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
                        Common.isFavouriteOut = true
                        productList.get(position).isFavorite = 1
                        productAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@CategoryByProductActivity,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CategoryByProductActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiAddToCart(map: HashMap<String, String>, targetView: ImageView) {
        showLoadingProgress(this@CategoryByProductActivity)
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
                        isCartTrueOut = true
                        makeFlyAnimation(targetView)
                    } else {
                        alertErrorOrValidationDialog(
                            this@CategoryByProductActivity,
                            restResponce.message
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    alertErrorOrValidationDialog(
                        this@CategoryByProductActivity,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CategoryByProductActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun makeFlyAnimation(targetView: ImageView) {
        CircleAnimationUtil().attachActivity(this@CategoryByProductActivity)
            .setTargetView(targetView).setMoveDuration(1000)
            .setDestView(cvCart).setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    productItemList!!.clear()
                    CURRENT_PAGES = 1
                    TOTAL_PAGES = 0
                    visibleItemCount = 0
                    totalItemCount = 0
                    pastVisiblesItems = 0
                    if (intent.getStringExtra("cat_id").equals("type_1")) {
                        if (isCheckNetwork(this@CategoryByProductActivity)) {
                            callApiLatestProduct(true)
                        } else {
                            alertErrorOrValidationDialog(
                                this@CategoryByProductActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    } else if (intent.getStringExtra("cat_id").equals("type_2")) {
                        if (isCheckNetwork(this@CategoryByProductActivity)) {
                            callApiExploreProduct(true)
                        } else {
                            alertErrorOrValidationDialog(
                                this@CategoryByProductActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    } else {
                        if (isCheckNetwork(this@CategoryByProductActivity)) {
                            callApiCatgoeyByProduct(true)
                        } else {
                            alertErrorOrValidationDialog(
                                this@CategoryByProductActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }
                    /* if (isCheckNetwork(this@CategoryByProductActivity)) {
                         callApiCartCount(false,false)
                     } else {
                         alertErrorOrValidationDialog(this@CategoryByProductActivity, resources.getString(R.string.no_internet))
                     }*/
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {}
            }).startAnimation()
    }

    private fun callApiCartCount(isFristTime: Boolean, isCart: Boolean) {
        if (!isFristTime) {
            showLoadingProgress(this@CategoryByProductActivity)
        }
        val map = HashMap<String, String>()
        map.put(
            "user_id",
            SharePreference.getStringPref(this@CategoryByProductActivity, SharePreference.userId)!!
        )
        val call = ApiClient.getClient.getCartCount(map)
        call.enqueue(object : Callback<RestResponse<CartCountModel>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<RestResponse<CartCountModel>>,
                response: Response<RestResponse<CartCountModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    if (isCart) {
                        isCartCategoryOut = false
                    }
                    val restResponce: RestResponse<CartCountModel> = response.body()!!
                    if (restResponce.status == 0) {
                        tvCount!!.text = "0"
                        rlItem.visibility = View.GONE
                    } else {
                        if (restResponce.data!!.cartcount!!.toInt() > 0) {
                            rlItem.visibility = View.GONE
                            tvCount!!.text = restResponce.data.cartcount
                            tvCartCount!!.text = restResponce.data.cartcount
                            tvCartFoodPrice.text =
                                Common.getCurrancy(this@CategoryByProductActivity) + String.format(
                                    Locale.US,
                                    "%,.2f",
                                    restResponce.data.cartprice!!.toDouble()
                                )
                            rlItem.setOnClickListener {
                                startActivity(
                                    Intent(
                                        this@CategoryByProductActivity,
                                        CartActivity::class.java
                                    ).putExtra(
                                        "isActivity",
                                        resources.getString(R.string.is_Cart_Act_catby)
                                    )
                                )
                            }
                        } else {
                            rlItem.visibility = View.GONE
                            tvCount!!.text = "0"
                        }
                    }

                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        this@CategoryByProductActivity,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<RestResponse<CartCountModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CategoryByProductActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(this@CategoryByProductActivity, false)
        if (isCartCategoryOut) {
            if (isCheckNetwork(this@CategoryByProductActivity)) {
                callApiCartCount(false, isCartCategoryOut)
            } else {
                alertErrorOrValidationDialog(
                    this@CategoryByProductActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        getCurrentLanguage(this@CategoryByProductActivity, false)
    }

    fun alertVariationDialog(
        act: Activity,
        listVarition: ArrayList<VariationModel>,
        strId: String,
        ivCopyFood: ImageView,
        strName: String,
        strImage: String
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
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_variation, null, false)
            val rvVariation: RecyclerView = mView.findViewById(R.id.rvVariation)
            setVariationAdaptor(listVarition, rvVariation)
            val tvCancle: TextView = mView.findViewById(R.id.tvCancle)
            val tvFoodName: TextView = mView.findViewById(R.id.tvFoodName)
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
            val tvSubmit: TextView = mView.findViewById(R.id.tvSubmit)
            tvSubmit.setOnClickListener {
                dialog.dismiss()
                if (getBooleanPref(this@CategoryByProductActivity,isLogin)) {
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
                    map["user_id"] = getStringPref(act, userId)!!
                    map["image_name"] = strImage
                    map["item_name"] = strName
                    if (isCheckNetwork(act)) {
                        callApiAddToCart(map, ivCopyFood)
                    } else {
                        alertErrorOrValidationDialog(
                            act,
                            resources.getString(R.string.no_internet)
                        )
                    }
                } else {
                    openActivity(LoginSelectActivity::class.java)
                    finish()
                    finishAffinity()
                }
            }

            dialog.setContentView(mView)
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
            this@CategoryByProductActivity,
            foodCategoryList
        ) {
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: VariationModel,
                position: Int
            ) {
                holder!!.itemView.tvWeight.text = foodCategoryList[position].weight
                holder.itemView.tvPrice.text =
                    getCurrancy(this@CategoryByProductActivity) + String.format(
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

        rvVariation.apply {
            adapter = foodCategoryAdapter
            layoutManager = LinearLayoutManager(this@CategoryByProductActivity)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
        }

    }


}