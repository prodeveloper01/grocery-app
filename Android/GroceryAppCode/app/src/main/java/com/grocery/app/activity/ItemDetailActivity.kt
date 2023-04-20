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
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.grocery.app.R
import com.grocery.app.adaptor.ImageSliderAdaptor
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
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference.Companion.getBooleanPref
import com.grocery.app.utils.SharePreference.Companion.getStringPref
import com.grocery.app.utils.SharePreference.Companion.isLogin
import com.grocery.app.utils.SharePreference.Companion.isMiniMumQty
import com.grocery.app.utils.SharePreference.Companion.userId
import kotlinx.android.synthetic.main.activity_itemdetail.*
import kotlinx.android.synthetic.main.activity_itemdetail.ivBack
import kotlinx.android.synthetic.main.activity_itemdetail.rlItem
import kotlinx.android.synthetic.main.activity_itemdetail.tvFoodName
import kotlinx.android.synthetic.main.row_relateditems.*
import kotlinx.android.synthetic.main.row_variation.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ItemDetailActivity:BaseActivity() {
    var timer: Timer? = null
    private var currentPage = 0
    private var imagelist: ArrayList<String>? = null
    private var listWeight: ArrayList<String>? = null
    private var listPrice: ArrayList<String>? = null
    private var strPrice: String = ""
    private var strProductPrice: String = ""
    private var strWeight: String = ""
    private var imagelistPlaceHolder: ArrayList<Drawable>? = null
    var itemModel: ItemDetailModel? = null
    var qty = 0
    val productList=ArrayList<ProductItemModel>()
    var getActivity=""
    var manager:LinearLayoutManager?=null
    var CURRENT_PAGES: Int = 1
    var TOTAL_PAGES: Int = 0
    var visibleItemCount= 0
    var totalItemCount= 0
    var pastVisiblesItems= 0
    var isLoding=true
    var catId=""
    var VariationId=0
 //   var weightAndPriceList = ArrayList<WeightAndPriceModel>()
    override fun setLayout(): Int {
       return R.layout.activity_itemdetail
    }

    override fun initView() {
        Common.getCurrentLanguage(this@ItemDetailActivity, false)
        listWeight= ArrayList()
        listPrice=ArrayList()

        getActivity=intent.getStringExtra("isItemActivity")!!

        manager= LinearLayoutManager(this@ItemDetailActivity,LinearLayoutManager.HORIZONTAL,false)

        val rand = Random()
        val intRandom: Int = rand.nextInt(Common.colorArray.size)
        viewBack.backgroundTintList=ColorStateList.valueOf(Color.parseColor("#${Common.colorArray[intRandom]}"))
        rvItemRelatedProduct.itemAnimator = DefaultItemAnimator()
        rvItemRelatedProduct.layoutManager=manager
        rvItemRelatedProduct.isNestedScrollingEnabled = true
        setRelativeProductAdaptor(productList)
        tvDiscountPrice.paintFlags = tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        if (isCheckNetwork(this@ItemDetailActivity)) {
            callApiFoodDetail(intent.getStringExtra("Item_Id")!!,false,true)
        } else {
            alertErrorOrValidationDialog(this@ItemDetailActivity, resources.getString(R.string.no_internet))
        }

        ivBack.setOnClickListener {
            finish()
        }
        if(SharePreference.getStringPref(this@ItemDetailActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        ivCart.setOnClickListener {
          if (getBooleanPref(this@ItemDetailActivity,isLogin)) {
              startActivity(Intent(this@ItemDetailActivity,CartActivity::class.java).putExtra("isActivity",resources.getString(R.string.is_Cart_Act_item_Detail)))
          }else{
             openActivity(LoginActivity::class.java)
             finish()
             finishAffinity()
          }
        }

        rlCartItem.setOnClickListener {
            if (getBooleanPref(this@ItemDetailActivity,isLogin)) {
                startActivity(Intent(this@ItemDetailActivity,CartActivity::class.java).putExtra("isActivity",resources.getString(R.string.is_Cart_Act_item_Detail)))
            }else{
                openActivity(LoginActivity::class.java)
                finish()
                finishAffinity()
            }
        }


        rvItemRelatedProduct.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dx > 0) {
                    visibleItemCount = manager!!.getChildCount()
                    totalItemCount = manager!!.getItemCount()
                    pastVisiblesItems = manager!!.findFirstVisibleItemPosition()
                    if (isLoding&&CURRENT_PAGES < TOTAL_PAGES) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            CURRENT_PAGES += 1
                            isLoding=false
                            callApiRelativeProduct(catId, false, true, false, itemModel!!.id.toString())

                        }
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n", "NewApi")
    private fun setRestaurantData(restResponce: ItemDetailModel?, isQtyUpdate: Boolean) {
        itemModel = restResponce
        imagelist = ArrayList()
        imagelistPlaceHolder = ArrayList()
       // imagelist!!.clear()
        Common.getLog("getLog",restResponce!!.images!!.size.toString()+"  "+restResponce!!.images!!)

        if (restResponce.images!!.size > 0) {
            for (i in 0 until restResponce.images.size) {
                imagelist!!.add(restResponce.images[i]!!.itemimage!!.toString())
                Common.getLog("getLogInFor",restResponce!!.images!![i]!!.itemimage.toString())
            }
            loadPagerImages(imagelist!!)
        } else {
            imagelistPlaceHolder!!.add(ResourcesCompat.getDrawable(resources,R.drawable.ic_placeholder,null)!!)
            loadPagerImages(imagelistPlaceHolder!!)
        }

        listWeight!!.clear()
        listPrice!!.clear()

        for(i in 0 until restResponce.variation!!.size){
            listWeight!!.add(restResponce.variation!![i].weight!!)
            listPrice!!.add(restResponce.variation!![i].price!!)
        }
        val adapter = ArrayAdapter(
            this@ItemDetailActivity,
            R.layout.textview_spinner,
            listWeight!!
        )
        adapter.setDropDownViewResource(R.layout.textview_spinner)
        spWeight.adapter = adapter

        for(i in 0 until restResponce.variation!!.size){
           if(restResponce.variation!![i].stock!!>0){
              tvVariation.text= restResponce.variation!![i].weight!!
              strPrice=restResponce.variation!![i].price!!
//              strProductPrice=restResponce.variation!![i].productPrice.toString()
              strWeight=restResponce.variation!![i].weight!!
              spWeight.setSelection(i)
              break
           }
        }

        spWeight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                tvVariation.text= listWeight!![position]
                strPrice=listPrice!![position]
                strWeight=listWeight!![position]
                VariationId=restResponce.variation!![position].id!!
                tvFoodWeightType.text =strWeight
                if(restResponce.variation!![position].stock!!>0){
                    tvAddtoCart.visibility=View.VISIBLE
                    rlItem.visibility=View.GONE
                    rlQty.visibility=View.VISIBLE
                }else{
                    tvAddtoCart.visibility=View.GONE
                    rlItem.visibility=View.VISIBLE
                    rlQty.visibility=View.GONE
                }
                tvFoodQty.text="1"
                tvFoodPrice.text = getCurrancy(this@ItemDetailActivity)+String.format(Locale.US,"%,.2f",strPrice.toDouble())
//                if(strProductPrice!="null"&&strProductPrice.isNotEmpty())
//                {
//                    tvDiscountPrice.text = getCurrancy(this@ItemDetailActivity)+String.format(Locale.US,"%,.2f",strProductPrice.toDouble())
//                }else
//                {
//                    tvDiscountPrice.text=""
//                }
                qty=1
                val getPrice = qty * strPrice.toDouble()
                tvAddtoCart.text = resources.getString(R.string.addtocart)+" "+getCurrancy(this@ItemDetailActivity).plus(String.format(Locale.US,"%,.2f",getPrice))
            }
        }

        rlPrice.setOnClickListener {
            spWeight.performClick()
        }

        tvFoodName.text = restResponce.itemName

        tvFoodType.text = restResponce.categoryName
        tvTime.text = restResponce.deliveryTime
        tvDetail.text = restResponce.itemDescription


        tvBarndName.text=restResponce.brand
        tvManufacturer.text=restResponce.manufacturer
        tvCountryOfOrigin.text=restResponce.country_origin
        tvIngredientType.text=restResponce.ingredient_type

        Log.e("GetError>>",strPrice+"/////")
       // tvAddtoCart.text = resources.getString(R.string.addtocart)+" "+getCurrancy(this@ItemDetailActivity).plus(String.format(Locale.US,"%,.2f",strPrice.toDouble()))

        if(tvFoodQty.text.isNotEmpty())
        {
            qty=tvFoodQty.text.toString().toInt()
        }
        ivMinus.setOnClickListener {
            if (qty > 1) {
                qty -= 1
                tvFoodQty.text = qty.toString()
                val getPrice = qty * strPrice.toDouble()
                tvAddtoCart.text = resources.getString(R.string.addtocart)+" "+getCurrancy(this@ItemDetailActivity).plus(String.format(Locale.US,"%,.2f",getPrice))
            }
        }

        ivPlus.setOnClickListener {
            if(qty<getStringPref(this@ItemDetailActivity,isMiniMumQty)!!.toInt()){
                qty += 1
                tvFoodQty.text = qty.toString()
                val getPrice =  qty * strPrice.toDouble()
                tvAddtoCart.text =  resources.getString(R.string.addtocart)+" "+ getCurrancy(this@ItemDetailActivity).plus(String.format(Locale.US,"%,.2f",getPrice))
            }else{
                alertErrorOrValidationDialog(this@ItemDetailActivity,resources.getString(R.string.max_limit))
            }
        }

        /*
          map["item_id"] = strId
                map["qty"] = "1"
                map["price"] = String.format(Locale.US, "%.2f", strPrice.toDouble())
                map["weight"] = strWeight

                map["user_id"] = getStringPref(activity!!, SharePreference.userId)!!

        */
        tvAddtoCart.setOnClickListener {
            if (getBooleanPref(this@ItemDetailActivity,isLogin)) {
                if (isCheckNetwork(this@ItemDetailActivity)) {
                    val getitem=tvFoodPrice.text.toString().replace(getCurrancy(this@ItemDetailActivity),"").toDouble()*tvFoodQty.text.toString().toInt()
                    val map = HashMap<String, String>()
                    map["item_id"] = itemModel!!.id.toString()
                    map["qty"]=tvFoodQty.text.toString()
                    map["price"]=String.format(Locale.US,"%.2f",tvFoodPrice.text.toString().replace(getCurrancy(this@ItemDetailActivity),"").toDouble())
                    map["weight"]=spWeight.selectedItem.toString()
                    map["user_id"] = getStringPref(this@ItemDetailActivity, userId)!!
                    map["variation_id"]=VariationId.toString()
                    map["image_name"] = itemModel!!.images!![0]!!.image_name!!
                    map["item_name"] = tvFoodName.text.toString()
                    callApiAddToCart(map,false,null)
                } else {
                    alertErrorOrValidationDialog(this@ItemDetailActivity,resources.getString(R.string.no_internet))
                }
            } else {
                openActivity(LoginSelectActivity::class.java)
                finish()
                finishAffinity()
            }
        }
    }

    private fun callApiFoodDetail(strFoodId: String,isQtyUpdate: Boolean,isLoadImage:Boolean) {
        if(!isQtyUpdate){
            showLoadingProgress(this@ItemDetailActivity)
        }
        val map = HashMap<String, String>()
        map["item_id"] = strFoodId
        map["user_id"] = getStringPref(this@ItemDetailActivity, userId)!!
        val call = ApiClient.getClient.getItemDetail(map)
        call.enqueue(object : Callback<RestResponse<ItemDetailModel>> {
            override fun onResponse(
                call: Call<RestResponse<ItemDetailModel>>,
                response: Response<RestResponse<ItemDetailModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<ItemDetailModel> = response.body()!!
                    setRestaurantData(restResponce.data,isLoadImage)
                    callApiRelativeProduct(restResponce.data!!.cat_id.toString(),isQtyUpdate,false,false,strFoodId)
                }
            }

            override fun onFailure(call: Call<RestResponse<ItemDetailModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ItemDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiAddToCart(map:HashMap<String,String>,isRelatedProduct:Boolean,ivCopyImage:ImageView?) {
        showLoadingProgress(this@ItemDetailActivity)
        val price = tvAddtoCart.text.toString().replace(resources.getString(R.string.addtocart)+" "+getCurrancy(this@ItemDetailActivity),"")
        Common.getLog("getPrice", price)
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
                   if (restResponce.status==1) {
                       if(isRelatedProduct){
                           if(getActivity == resources.getString(R.string.is_item_Act_home)){
                               Common.isCartTrueOut=true
                           }else if(getActivity == resources.getString(R.string.is_Cart_Act_catby)){
                               Common.isCartCategoryOut=true
                               Common.isCartTrueOut=true
                           }
                           makeFlyAnimation(ivCopyImage!!)
                       }else{
                           qty=0
                           tvFoodQty.text = "1"
//                           tvFoodWeightType.text= itemModel!!.variation!![0].weight!!
//                           tvFoodPrice.text = getCurrancy(this@ItemDetailActivity).plus(String.format(Locale.US,"%,.2f",itemModel!!.variation!![0].price!!.toDouble()))
//                           tvAddtoCart.text = resources.getString(R.string.addtocart)+" "+getCurrancy(this@ItemDetailActivity).plus(String.format(Locale.US,"%,.2f",itemModel!!.variation!![0].price!!.toDouble()))
                           if (isCheckNetwork(this@ItemDetailActivity)) {
                               callApiFoodDetail(itemModel!!.id.toString(),false,true)
                           } else {
                               alertErrorOrValidationDialog(this@ItemDetailActivity, resources.getString(R.string.no_internet))
                           }
                       }
                   }
                    else {
                       alertErrorOrValidationDialog(this@ItemDetailActivity, restResponce.message)
                   }

                }else {
                    val error = JSONObject(response.errorBody()!!.string())
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(this@ItemDetailActivity, error.getString("message"))
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                 dismissLoadingProgress()
                 alertErrorOrValidationDialog(
                     act = this@ItemDetailActivity,
                     msg = resources.getString(R.string.error_msg)
                 )
            }
        })
    }


    private fun loadPagerImages(imageHase: ArrayList<*>) {
        Common.getLog("getLogIn",imageHase.size.toString()+"  "+Gson().toJson(imageHase))
        val adapter = ImageSliderAdaptor(this@ItemDetailActivity, imageHase)
        tabLayout.setupWithViewPager(viewPager, true)
        viewPager.adapter = adapter
       val handler = Handler(Looper.getMainLooper())

        if (imageHase.size == 1) {
            tabLayout.visibility = View.GONE
        }
        val runnable = Runnable {
            if (currentPage == imageHase.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post(runnable)
            }
        }, 500, 3000)


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }


    override fun onPause() {
        super.onPause()
        if (timer != null)
            timer!!.cancel()
    }

    //:::::::::::::Relative Product Api:::::::::::::://
    private fun callApiRelativeProduct(
        strCatId: String,
        isQtyUpdate: Boolean,
        isPageLoad: Boolean,
        isAddtoCart: Boolean,
        strFoodId: String
    ) {
        catId=strCatId
        val map = HashMap<String, String>()
        map["cat_id"] = strCatId
        map["item_id"] = strFoodId
        if (getBooleanPref(this@ItemDetailActivity,isLogin)) {
            map["user_id"] = getStringPref(this@ItemDetailActivity,userId)!!
        }else{
            map["user_id"] = ""
        }
        if(isAddtoCart){
            showLoadingProgress(this@ItemDetailActivity)
        }
        if(isPageLoad){
            llProgressbar.visibility=View.VISIBLE
        }else{
            llProgressbar.visibility=View.GONE
        }
        val call = ApiClient.getClient.getRelatedProduct(map,CURRENT_PAGES.toString())
        call.enqueue(object : Callback<RestResponse<PaginationModel<ProductItemModel>>> {
            override fun onResponse(
                call: Call<RestResponse<PaginationModel<ProductItemModel>>>,
                response: Response<RestResponse<PaginationModel<ProductItemModel>>>
            ) {
                if (response.code() == 200) {
                    val restResponse:RestResponse<PaginationModel<ProductItemModel>> = response.body()!!
                    if (restResponse.status == 1) {
                        llProgressbar.visibility=View.GONE
                        if(!isPageLoad){
                            productList.clear()
                        }
                        TOTAL_PAGES=restResponse.data!!.lastPage!!.toInt()
                        val paginationModel:PaginationModel<ProductItemModel> = restResponse.data!!
                        if(paginationModel.data!!.size>0){
                            tvItem.visibility=View.GONE
                            rvItemRelatedProduct.visibility=View.VISIBLE
                            for(i in 0 until paginationModel.data.size){
                                val productList=paginationModel.data
                                productList[i].variation!![0].isSelect=true
                                var getpos=0
                                for(j in 0 until  productList[i].variation!!.size){
                                    if(productList[i].variation!![j].stock==0){
                                        getpos=getpos+1
                                    }
                                }
                                if(getpos==productList[i].variation!!.size){
                                    productList[i].isOutOfStock=true
                                }else{
                                    productList[i].isOutOfStock=false
                                    for(j in 0 until productList[i].variation!!.size) {
                                        if (productList[i].variation!![j].stock!! > 0) {
                                            productList[i].getvariation.add(productList[i].variation!![j])
                                        }
                                    }
                                    productList[i].getvariation[0].isSelect=true
                                }
                            }
                            productList.addAll(paginationModel.data)
                            relativeProductAdapter!!.notifyDataSetChanged()
                        }else{
                            tvItem.visibility=View.VISIBLE
                            rvItemRelatedProduct.visibility=View.GONE
                        }


                        if (getBooleanPref(this@ItemDetailActivity,isLogin)) {
                            if(!isQtyUpdate){
                                if (isCheckNetwork(this@ItemDetailActivity)) {
                                    callApiCartCount(
                                        isFristTime = true,
                                        isQtyUpdate = false,
                                        strMsg = ""
                                    )
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@ItemDetailActivity,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }else{
                                dismissLoadingProgress()
                            }
                        }else {
                            dismissLoadingProgress()
                            rlCount!!.visibility = View.GONE
                        }

                    } else if (restResponse.status==0) {
                        if (getBooleanPref(this@ItemDetailActivity,isLogin)) {
                            if (isCheckNetwork(this@ItemDetailActivity)) {
                                callApiCartCount(
                                    isFristTime = true,
                                    isQtyUpdate = false,
                                    strMsg = ""
                                )
                            } else {
                                alertErrorOrValidationDialog(
                                    this@ItemDetailActivity,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        } else {
                            dismissLoadingProgress()
                            rlCount!!.visibility = View.GONE
                            alertErrorOrValidationDialog(
                                this@ItemDetailActivity,
                                restResponse.message
                            )
                        }
                    }
                } else{
                    val error= JSONObject(response.errorBody()!!.string())
                    if(error.getString("status") == "2"){
                        dismissLoadingProgress()
                        Common.setLogout(this@ItemDetailActivity)
                    }else{
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ItemDetailActivity,
                            error.getString("message")
                        )
                    }
                }

            }

            override fun onFailure(call: Call<RestResponse<PaginationModel<ProductItemModel>>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ItemDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    var relativeProductAdapter:BaseAdaptor<ProductItemModel>?=null
    private fun setRelativeProductAdaptor(productList: ArrayList<ProductItemModel>) {
        relativeProductAdapter = object : BaseAdaptor<ProductItemModel>(this@ItemDetailActivity, productList) {
            @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: ProductItemModel,
                position: Int
            ) {
                val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                val tvFoodPriceGrid: TextView = holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                val tvDiscountPrice: TextView = holder.itemView.findViewById(R.id.tvDiscountPriceText)
                val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)
                val llOutOfStock: LinearLayout = holder.itemView.findViewById(R.id.llOutOfStock)
                val tvWeight: TextView = holder.itemView.findViewById(R.id.tvWeight)
                tvFoodName.text = productList[position].itemName

                var price=""
//                var productPrice=""

                if(productList[position].getvariation.size>0){
                    tvWeight.text = productList[position].getvariation[0].weight
                    price= productList[position].getvariation[0].price.toString()
//                    productPrice= productList[position].getvariation[0].productPrice.toString()
                }else{
                    tvWeight.text = productList[position].variation!![0].weight
                    price= productList[position].variation!![0].price!!
//                    productPrice= productList[position].variation!![0].productPrice!!
                }

                if (getStringPref(this@ItemDetailActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))) {
                    tvFoodPriceGrid.text = String.format(Locale.US,"%,.2f", price.toDouble()) + getCurrancy(this@ItemDetailActivity)
                } else {
                    tvFoodPriceGrid.text = getCurrancy(this@ItemDetailActivity) + String.format(Locale.US,"%,.2f",price.toDouble())
                }

//                if(productPrice!="null")
//                {
//                    if (getStringPref(this@ItemDetailActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))) {
//                        tvDiscountPrice.text = String.format(Locale.US,"%,.2f", productPrice.toDouble()) + getCurrancy(this@ItemDetailActivity)
//                    } else {
//                        tvDiscountPrice.text = getCurrancy(this@ItemDetailActivity) + String.format(Locale.US,"%,.2f",productPrice.toDouble())
//                    }
//                }
                tvDiscountPrice.paintFlags = tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                Glide.with(this@ItemDetailActivity).load(productList[position].itemimage!!.image)
                    .into(ivFood)
                val ivCopyFood: ImageView = holder.itemView.findViewById(R.id.ivCopyFood)
                Glide.with(this@ItemDetailActivity)
                    .load(productList[position].itemimage!!.image)
                    .into(ivCopyFood)

                holder.itemView.setOnClickListener {
                    if (isCheckNetwork(this@ItemDetailActivity)){
                        val rand = Random()
                        val intRandom: Int = rand.nextInt(Common.colorArray.size)
                        viewBack.backgroundTintList=ColorStateList.valueOf(Color.parseColor("#${Common.colorArray[intRandom]}"))
                        callApiFoodDetail(productList[position].id.toString(),false,true)
                    } else {
                        alertErrorOrValidationDialog(this@ItemDetailActivity, resources.getString(R.string.no_internet))
                    }
                }


                val ivCart: ImageView = holder.itemView.findViewById(R.id.ivAddCart)
                ivCart.setOnClickListener {
                    if(productList[position].variation!!.size>1){
                        alertVariationDialog(
                            this@ItemDetailActivity,
                            productList[position].variation!!,
                            productList[position].id.toString(),
                            ivCopyFood,
                            productList[position].itemName!!,
                            productList[position].itemimage!!.image_name!!
                        )
                    }else if(productList[position].variation!!.size==1){
                        if (getBooleanPref(this@ItemDetailActivity,isLogin)) {
                            val map = HashMap<String, String>()
                            map["item_id"] = productList[position].id.toString()
                            map["qty"] = "1"
                            map["item_name"]=productList[position].itemName!!
                            map["image_name"]=productList[position].itemimage!!.image_name!!
                            map["price"] = String.format(Locale.US, "%.2f", productList[position].variation!![0].price!!.toDouble())
                            map["weight"] = productList[position].variation!![0].weight!!
                            map["variation_id"] =productList[position].variation!![0].id.toString()
                            map["user_id"] = getStringPref(this@ItemDetailActivity,userId)!!
                            callApiAddToCart(map,true,ivCopyFood)
                        } else {
                            openActivity(LoginSelectActivity::class.java)
                            finish()
                            finishAffinity()
                        }
                    }

                }

                val rvFood: RelativeLayout = holder.itemView.findViewById(R.id.rvFood)
                val i=position%5

                when (i) {
                    0 -> {
                        rvFood.backgroundTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.color1,null))
                    }
                    1 -> {
                        rvFood.backgroundTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.color2,null))
                    }
                    2 -> {
                        rvFood.backgroundTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.color3,null))
                    }
                    3 -> {
                        rvFood.backgroundTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.color4,null))
                    }
                    4 -> {
                        rvFood.backgroundTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.color5,null))
                    }
                }

                if(productList.get(position).isOutOfStock!!){
                    llOutOfStock.visibility=View.VISIBLE
                    ivCart.background=ResourcesCompat.getDrawable(resources,R.drawable.bg_gray_corner_10,null)
                }else{
                    llOutOfStock.visibility=View.GONE
                    ivCart.background=ResourcesCompat.getDrawable(resources,R.drawable.bg_green_corner_10,null)
                }

                val Frist: View = holder.itemView.findViewById(R.id.ViewFrist)
                val Last: View = holder.itemView.findViewById(R.id.ViewLast)

                when (position) {
                    0 -> {
                        Frist.visibility=View.VISIBLE
                        Last.visibility=View.GONE
                    }
                    (productList.size-1) -> {
                        Frist.visibility=View.GONE
                        Last.visibility=View.VISIBLE
                    }
                    else -> {
                        Frist.visibility=View.GONE
                        Last.visibility=View.GONE
                    }
                }


                tvFoodPriceGrid.visibility = View.VISIBLE
                tvDiscountPrice.visibility = View.VISIBLE

            }

            override fun setItemLayout(): Int {
                return R.layout.row_relateditems
            }

        }

        rvItemRelatedProduct.adapter = relativeProductAdapter

    }


    private fun callApiCartCount(isFristTime: Boolean,isQtyUpdate:Boolean,strMsg:String) {
        if(!isFristTime){
            showLoadingProgress(this@ItemDetailActivity)
        }
        val map = HashMap<String, String>()
        map["user_id"] = getStringPref(this@ItemDetailActivity, userId)!!
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
                    if (restResponce.status==1) {
                        if(restResponce.data!!.cartcount!!.toInt()>0&&!isFristTime) {
                            tvCartCount!!.text = restResponce.data.cartcount
                            tvCartFoodPrice.text = getCurrancy(this@ItemDetailActivity) + String.format(Locale.US, "%,.2f",restResponce.data.cartprice!!.toDouble())
                            rlCartItem.setOnClickListener {
                                startActivity(Intent(this@ItemDetailActivity,CartActivity::class.java).putExtra("isActivity",resources.getString(R.string.is_Cart_Act_item_Detail)))
                            }
                        }else if(!isFristTime){
                            rlCartItem.visibility = View.GONE
                        }
                        tvCount.text=restResponce.data.cartcount
                        if(isQtyUpdate){
                            Common.isCartTrueOut=true
                            if (isCheckNetwork(this@ItemDetailActivity)) {
                                callApiFoodDetail(itemModel!!.id.toString(),true,false)
                            } else {
                                alertErrorOrValidationDialog(this@ItemDetailActivity, resources.getString(R.string.no_internet))
                            }
                        }else{
                            timer = Timer()
                            val handler = Handler(Looper.getMainLooper())
                            val runnable = Runnable {
                                if (currentPage == imagelist!!.size) {
                                    currentPage = 0
                                }
                                viewPager.setCurrentItem(currentPage++, true)
                            }

                            if (imagelist!!.size == 1) {
                                tabLayout.visibility = View.GONE
                            }
                            timer!!.schedule(object : TimerTask() {
                                override fun run() {
                                    handler.post(runnable)
                                }
                            }, 4000, 3000)
                        }
                       /* if(!isFristTime){

                        }*/
                    } else if (restResponce.status==0) {
                        dismissLoadingProgress()
                        rlCartItem.visibility = View.GONE
                        tvCount!!.text = "0"
                        if(isQtyUpdate){
                            alertErrorOrValidationDialog(
                                act = this@ItemDetailActivity,
                                msg = strMsg
                            )
                        }
                        if(!isFristTime){
                            timer = Timer()
                            val handler = Handler(Looper.getMainLooper())
                            val runnable = Runnable {
                                if (currentPage == imagelist!!.size) {
                                    currentPage = 0
                                }
                                viewPager.setCurrentItem(currentPage++, true)
                            }

                            if (imagelist!!.size == 1) {
                                tabLayout.visibility = View.GONE
                            }
                            timer!!.schedule(object : TimerTask() {
                                override fun run() {
                                    handler.post(runnable)
                                }
                            }, 4000, 3000)
                        }
                    }

                }else{
                    val error= JSONObject(response.errorBody()!!.string())
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        this@ItemDetailActivity,
                        error.getString("message")
                    )
                    if(!isFristTime){
                        timer = Timer()
                        val handler = Handler(Looper.getMainLooper())
                        val Update = Runnable {
                            if (currentPage == imagelist!!.size) {
                                currentPage = 0
                            }
                            viewPager.setCurrentItem(currentPage++, true)
                        }

                        if (imagelist!!.size == 1) {
                            tabLayout.visibility = View.GONE
                        }
                        timer!!.schedule(object : TimerTask() {
                            override fun run() {
                                handler.post(Update)
                            }
                        }, 4000, 3000)
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<CartCountModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ItemDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ItemDetailActivity, false)
        if (getBooleanPref(this@ItemDetailActivity,isLogin)) {
            rlCount.visibility=View.VISIBLE
            if(Common.isCartTrue){
                if (isCheckNetwork(this@ItemDetailActivity)) {
                    if(getActivity == resources.getString(R.string.is_item_Act_home)){
                        Common.isCartTrueOut=true
                    }else if(getActivity == resources.getString(R.string.is_Cart_Act_catby)){
                        Common.isCartCategoryOut=true
                        Common.isCartTrueOut=true
                    }
                    Common.isCartTrue=false
                    callApiFoodDetail(itemModel!!.id.toString(),false,true)
                } else {
                    alertErrorOrValidationDialog(this@ItemDetailActivity, resources.getString(R.string.no_internet))
                }
            }/*else{
                rlCount.visibility=View.GONE
                timer = Timer()
                val handler = Handler(Looper.getMainLooper())
                val Update = Runnable {
                    if (currentPage == imagelist!!.size) {
                        currentPage = 0
                    }
                    viewPager.setCurrentItem(currentPage++, true)
                }
                if (imagelist!!.size == 1) {
                    tabLayout.visibility = View.GONE
                }
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(Update)
                    }
                }, 4000, 3000)
            }*/
        }else{
            rlCount.visibility=View.GONE
           /* timer = Timer()
            val handler = Handler(Looper.getMainLooper())
            val Update = Runnable {
                if (currentPage == imagelist!!.size) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage++, true)
            }
            if (imagelist!!.size == 1) {
                tabLayout.visibility = View.GONE
            }
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(Update)
                }
            }, 4000, 3000)*/
        }

    }

    private fun makeFlyAnimation(targetView: ImageView) {
        CircleAnimationUtil().attachActivity(this@ItemDetailActivity).setTargetView(targetView).setMoveDuration(1000)
            .setDestView(rlCart).setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCheckNetwork(this@ItemDetailActivity)) {
                        CURRENT_PAGES= 1
                        TOTAL_PAGES= 0
                        visibleItemCount= 0
                        totalItemCount= 0
                        pastVisiblesItems= 0
                        isLoding=true
                        callApiRelativeProduct(catId, false, false, true, itemModel!!.id.toString())
                       // callApiCartCount(false,false,"")
                    } else {
                        alertErrorOrValidationDialog(this@ItemDetailActivity, resources.getString(R.string.no_internet))
                    }
                }
                override fun onAnimationCancel(animation: Animator?) {

                }
                override fun onAnimationRepeat(animation: Animator?) {}
            }).startAnimation()
    }

    fun alertVariationDialog(
            act: Activity,
            listVarition: ArrayList<VariationModel>,
            strId: String,
            ivCopyFood: ImageView,
            strName: String,
            strImage:String
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
            tvFoodName.text=strName
            val finalDialog: Dialog = dialog
            tvCancle.setOnClickListener {
                finalDialog.dismiss()
                for(i in 0 until listVarition.size) {
                    listVarition[i].isSelect = false
                }
                for(i in 0 until listVarition.size){
                    if(listVarition[i].stock!!>0){
                        listVarition[i].isSelect=true
                        break
                    }
                }
            }
            val tvSubmit: TextView = m_view.findViewById(R.id.tvSubmit)
            tvSubmit.setOnClickListener {

                if (getBooleanPref(this@ItemDetailActivity,isLogin)) {
                    var getPos=0
                    var strPrice = ""
                    var strWeight = ""
                    var strVariationId = ""

                    for (i in 0 until listVarition.size) {
                        if (listVarition[i].isSelect) {
                            strPrice = listVarition[i].price!!
                            strWeight = listVarition[i].weight!!
                            strVariationId = listVarition[i].id.toString()
                            getPos=i
                        }
                    }
                    finalDialog.dismiss()
                    val map = HashMap<String, String>()
                    map["item_id"] = strId
                    map["qty"] = "1"
                    map["price"] = String.format(Locale.US, "%.2f", strPrice.toDouble())
                    map["weight"] = strWeight
                    map["variation_id"] =strVariationId
                    map["user_id"] = getStringPref(this@ItemDetailActivity, SharePreference.userId)!!
                    map["image_name"] = strImage
                    map["item_name"] = strName
                    if (isCheckNetwork(this@ItemDetailActivity)) {
                        callApiAddToCart(map,true,ivCopyFood)

                    } else {
                        alertErrorOrValidationDialog(
                            this@ItemDetailActivity,
                            resources.getString(R.string.no_internet)
                        )
                    }
                } else {
                    openActivity(LoginSelectActivity::class.java)
                    finish()
                    finishAffinity()
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
                this@ItemDetailActivity,
                foodCategoryList
        ) {
            @SuppressLint("SetTextI18n")
            override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: VariationModel,
                    position: Int
            ) {
                holder!!.itemView.tvWeight.text = foodCategoryList[position].weight
                holder.itemView.tvPrice.text = getCurrancy(this@ItemDetailActivity)+String.format(Locale.US, "%.2f", foodCategoryList[position].price!!.toDouble())

                if(foodCategoryList[position].stock!!>0){
                    holder.itemView.tvOutOfStock.visibility=View.GONE
                    holder.itemView.ivCheck.visibility=View.VISIBLE
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
                }else{
                    holder.itemView.tvOutOfStock.visibility=View.VISIBLE
                    holder.itemView.ivCheck.visibility=View.GONE
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
            layoutManager = LinearLayoutManager(this@ItemDetailActivity)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
        }
    }
}