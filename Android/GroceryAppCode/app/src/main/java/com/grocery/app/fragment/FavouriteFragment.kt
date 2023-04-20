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
import com.grocery.app.activity.CartActivity
import com.grocery.app.activity.ItemDetailActivity
import com.grocery.app.activity.LoginActivity
import com.grocery.app.activity.LoginSelectActivity
import com.grocery.app.api.ApiClient
import com.grocery.app.api.RestResponse
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.base.BaseFragmnet
import com.grocery.app.model.*
import com.grocery.app.utils.*
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.getCurrancy
import com.grocery.app.utils.Common.getCurrentLanguage
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference.Companion.userId
import kotlinx.android.synthetic.main.dlg_confomation.view.*
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.fragment_favourite.cvCart
import kotlinx.android.synthetic.main.fragment_favourite.rlCount
import kotlinx.android.synthetic.main.fragment_favourite.rlItem
import kotlinx.android.synthetic.main.fragment_favourite.rlPaginationBack
import kotlinx.android.synthetic.main.fragment_favourite.tvCartCount
import kotlinx.android.synthetic.main.fragment_favourite.tvCartFoodPrice
import kotlinx.android.synthetic.main.fragment_favourite.tvCount
import kotlinx.android.synthetic.main.fragment_favourite.tvNoDataFound
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.row_variation.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FavouriteFragment:BaseFragmnet() {
    var CURRENT_PAGES: Int = 1
    var TOTAL_PAGES: Int = 0
    var favouriteItemList: ArrayList<FavouriteProductModel>? = ArrayList()
    var favouriteAdapter: BaseAdaptor<FavouriteProductModel>? = null
    var manager1: GridLayoutManager? = null

    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var pastVisiblesItems: Int = 0

    var isLoding=true

    override fun setView(): Int {
        return R.layout.fragment_favourite
    }

    override fun init(view: View) {
        getCurrentLanguage(activity!!, false)
        setFavouriteAdaptor()
        tvNoDataFound.visibility = View.VISIBLE
        rvFavourite.visibility = View.GONE
        if (Common.isCheckNetwork(activity!!)) {
            callApiFavourite(true)
        } else {
            tvNoDataFound.visibility = View.VISIBLE
            rvFavourite.visibility = View.GONE
            alertErrorOrValidationDialog(activity!!,resources.getString(R.string.no_internet))
        }

        cvCart.setOnClickListener {
            startActivity(Intent(activity!!,CartActivity::class.java).putExtra("isActivity",resources.getString(R.string.is_Cart_Act_home)))
        }

        manager1= GridLayoutManager(activity!!,2, GridLayoutManager.VERTICAL,false)
        rvFavourite.layoutManager=manager1


        rvFavourite.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = manager1!!.getChildCount()
                    totalItemCount = manager1!!.getItemCount()
                    pastVisiblesItems = manager1!!.findFirstVisibleItemPosition()
                    if (isLoding&&CURRENT_PAGES < TOTAL_PAGES) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            CURRENT_PAGES += 1
                            isLoding=false
                            if (Common.isCheckNetwork(activity!!)) {
                                callApiFavourite(false)
                            } else {
                                alertErrorOrValidationDialog(
                                    activity!!,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    private fun callApiFavourite(isFirstTime: Boolean?) {
        if(isFirstTime!!){
            favouriteItemList!!.clear()
            showLoadingProgress(activity!!)
        }else{
            rlPaginationBack.visibility=View.VISIBLE
        }
        val map = HashMap<String, String>()
        map["user_id"] = SharePreference.getStringPref(activity!!, userId)!!
        val call = ApiClient.getClient.getFavouriteByItem(map, CURRENT_PAGES.toString())
        call.enqueue(object : Callback<RestResponse<PaginationModel<FavouriteProductModel>>> {
            override fun onResponse(
                call: Call<RestResponse<PaginationModel<FavouriteProductModel>>>,
                response: Response<RestResponse<PaginationModel<FavouriteProductModel>>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<PaginationModel<FavouriteProductModel>> = response.body()!!
                    if (restResponce.status==1) {
                        if(!isFirstTime) {
                            rlPaginationBack.visibility=View.GONE
                        }
                        tvNoDataFound.visibility=View.GONE
                        rvFavourite.visibility=View.VISIBLE
                        val PaginationResponse: PaginationModel<FavouriteProductModel> = restResponce.data!!
                        CURRENT_PAGES =  PaginationResponse.currentPage!!
                        TOTAL_PAGES = PaginationResponse.lastPage!!
                        val listPrice = ArrayList<String>()
                        val listWeight = ArrayList<String>()
                        if(PaginationResponse.data!!.size>0){
                            for(i in 0 until restResponce.data.data!!.size){
                                restResponce.data.data[i].variation!![0].isSelect=true
                                var getpos=0
                                var isOnlyOneStack=false
                                for(j in 0 until restResponce.data.data[i].variation!!.size){
                                    if(restResponce.data.data[i].variation!![j].stock==0){
                                        getpos=getpos+1
                                    }
                                }

                                /*if(getpos==restResponce.data.data[i].variation!!.size-1){
                                    isOnlyOneStack=true
                                }else{
                                    isOnlyOneStack=false
                                }*/

                                if(getpos==restResponce.data.data[i].variation!!.size){
                                    restResponce.data.data[i].isOutOfStock=true
                                }else{
                                    restResponce.data.data[i].isOutOfStock=false
                                    for(j in 0 until restResponce.data.data[i].variation!!.size) {
                                        if (restResponce.data.data[i].variation!![j].stock!! > 0) {
                                            restResponce.data.data[i].getvariation.add(restResponce.data.data[i].variation!![j])
                                        }
                                    }
                                    restResponce.data.data[i].getvariation[0].isSelect=true
                                }
                            }
                            favouriteItemList!!.addAll(restResponce.data.data)
                            favouriteAdapter!!.notifyDataSetChanged()
                            if (SharePreference.getBooleanPref(activity!!, SharePreference.isLogin)) {
                                if (Common.isCheckNetwork(activity!!)) {
                                    callApiCartCount(true,false)
                                }else {
                                    alertErrorOrValidationDialog(activity!!,resources.getString(R.string.no_internet))
                                }
                            }
                        }else{
                            tvNoDataFound.visibility=View.VISIBLE
                            rvFavourite.visibility=View.GONE
                            if (SharePreference.getBooleanPref(activity!!, SharePreference.isLogin)) {
                                if (Common.isCheckNetwork(activity!!)) {
                                    callApiCartCount(true,false)
                                }else {
                                    alertErrorOrValidationDialog(activity!!,resources.getString(R.string.no_internet))
                                }
                            }
                        }

                    } else if (restResponce.status==0) {
                        tvNoDataFound.visibility=View.GONE
                        rvFavourite.visibility=View.VISIBLE
                        Common.dismissLoadingProgress()
                        alertErrorOrValidationDialog(activity!!, restResponce.message)
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<PaginationModel<FavouriteProductModel>>>, t: Throwable) {
                Common.dismissLoadingProgress()
                alertErrorOrValidationDialog(activity!!,resources.getString(R.string.error_msg))
            }
        })
    }

    fun setFavouriteAdaptor() {
        favouriteAdapter = object : BaseAdaptor<FavouriteProductModel>(activity!!, favouriteItemList!!) {
            @SuppressLint("NewApi")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: FavouriteProductModel,
                position: Int
            ) {
                val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                val tvFoodPriceGrid: TextView = holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                val tvDiscountPrice: TextView = holder.itemView.findViewById(R.id.tvDiscountPrice)
                val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)
                val icLike: ImageView = holder.itemView.findViewById(R.id.icLike)
                tvFoodName.text = favouriteItemList!!.get(position).itemName

                Glide.with(activity!!).load(favouriteItemList!!.get(position).itemimage!!.image).into(ivFood)

                holder.itemView.setOnClickListener {
                    startActivity(
                        Intent(activity!!, ItemDetailActivity::class.java).putExtra(
                            "Item_Id",
                            favouriteItemList!![position].id.toString()
                        ).putExtra("isItemActivity", resources.getString(R.string.is_item_Act_home))
                    )
                }

                val ivCopyFood: ImageView = holder.itemView.findViewById(R.id.ivCopyFood)
                Glide.with(activity!!)
                    .load(favouriteItemList!!.get(position).itemimage!!.image)
                    .into(ivCopyFood)
                val ivCart: ImageView = holder.itemView.findViewById(R.id.ivAddCart)
                ivCart.setOnClickListener {
                    if(favouriteItemList!![position].variation!!.size>1){
                        alertVariationDialog(
                            activity!!,
                            favouriteItemList!![position].variation!!,
                            favouriteItemList!![position].id.toString(),
                            ivCopyFood,
                            favouriteItemList!![position].itemName!!,
                            favouriteItemList!![position].itemimage!!.image_name!!,
                            favouriteItemList!![position].getvariation
                        )
                    }else{
                        val map = HashMap<String, String>()
                        map["item_id"] = favouriteItemList!![position].id.toString()
                        map["qty"] = "1"
                        map["item_name"]=favouriteItemList!![position].itemName!!
                        map["image_name"]=favouriteItemList!![position].itemimage!!.image_name!!
                        map["price"] = String.format(Locale.US, "%.2f", favouriteItemList!![position].variation!![0].price!!.toDouble())
                        map["weight"] = favouriteItemList!![position].variation!![0].weight!!
                        map["variation_id"] =favouriteItemList!![position].variation!![0].id.toString()
                        map["user_id"] = SharePreference.getStringPref(activity!!, userId)!!
                        callApiAddToCart(map, ivCopyFood)
                    }
                }

                val tvWeight: TextView = holder.itemView.findViewById(R.id.tvWeight)
                val ivOutOfStock: LinearLayout = holder.itemView.findViewById(R.id.ivOutOfStock)
                if(favouriteItemList!!.get(position).isOutOfStock!!){
                    ivOutOfStock.visibility=View.VISIBLE
                    ivCart.background=ResourcesCompat.getDrawable(resources,R.drawable.bg_gray_corner_10,null)
                }else{
                    ivOutOfStock.visibility=View.GONE
                    ivCart.background=ResourcesCompat.getDrawable(resources,R.drawable.bg_green_corner_10,null)
                }

                var price=""
//                var productPrice=""

                if(favouriteItemList!![position].getvariation.size>0){
                    tvWeight.text = favouriteItemList!![position].getvariation[0].weight
                    price= favouriteItemList!![position].getvariation[0].price.toString()
//                    productPrice= favouriteItemList!![position].getvariation[0].productPrice.toString()
                }else{
                    tvWeight.text = favouriteItemList!![position].variation!![0].weight
                    price= favouriteItemList!![position].variation!![0].price!!
//                    productPrice= favouriteItemList!![position].variation!![0].productPrice!!
                }

                tvFoodPriceGrid.text = getCurrancy(activity!!).plus(String.format(Locale.US,"%,.2f",price.toDouble()))
//                if(productPrice!="null")
//                {
//                    tvDiscountPrice.text = getCurrancy(activity!!).plus(String.format(Locale.US,"%,.2f",productPrice.toDouble()))
//
//                }

                tvDiscountPrice.paintFlags = tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                tvFoodPriceGrid.visibility= View.VISIBLE
                tvDiscountPrice.visibility= View.VISIBLE


                val rvFood: RelativeLayout = holder.itemView.findViewById(R.id.rvFood)
                val i=position%5

                if(i==0){
                    rvFood.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.color1,null))
                }else if(i==1){
                    rvFood.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.color2,null))
                }else  if(i==2){
                    rvFood.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.color3,null))
                }else  if(i==3){
                    rvFood.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.color4,null))
                }else if(i==4){
                    rvFood.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.color5,null))
                }

                icLike.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_favourite_like,null))
                icLike.imageTintList = ColorStateList.valueOf(Color.RED)

                icLike.setOnClickListener {
                    if (SharePreference.getBooleanPref(activity!!, SharePreference.isLogin)) {
                        mExitDialog(position)
                    } else {
                        openActivity(LoginActivity::class.java)
                        activity!!.finish()
                    }

                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_foodsubcategory
            }

        }
        rvFavourite.adapter = favouriteAdapter
        rvFavourite.itemAnimator = DefaultItemAnimator()
        rvFavourite.isNestedScrollingEnabled = true
    }

    private fun callApiFavourite(
        map: HashMap<String, String>,
        position: Int
    ) {
        showLoadingProgress(activity!!)
        val call = ApiClient.getClient.setRemovefavorite(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status==1) {
                        Common.dismissLoadingProgress()
                        favouriteItemList!!.removeAt(position)
                        favouriteAdapter!!.notifyDataSetChanged()
                        if(favouriteItemList!!.size==0){
                            tvNoDataFound.visibility=View.VISIBLE
                            rvFavourite.visibility=View.GONE
                        }
                    } else if (restResponse.status==0) {
                        Common.dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiAddToCart(map: HashMap<String, String>,targetView:ImageView) {
        showLoadingProgress(activity!!)
        val call = ApiClient.getClient.getAddToCart(map)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                Common.dismissLoadingProgress()
                val restResponce: SingleResponse = response.body()!!
                if (response.code() == 200) {
                    if (restResponce.status==1) {
                        makeFlyAnimation(targetView)
                    }
                    else {
                        alertErrorOrValidationDialog(activity!!, restResponce.message)
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    alertErrorOrValidationDialog(activity!!, error.getString("message"))
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun makeFlyAnimation(targetView: ImageView) {
        CircleAnimationUtil().attachActivity(activity!!).setTargetView(targetView).setMoveDuration(1000)
            .setDestView(cvCart).setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    if (Common.isCheckNetwork(activity!!)) {
                        callApiFavourite(true)
                    } else {
                        alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
                    }
                }
                override fun onAnimationCancel(animation: Animator?) {

                }
                override fun onAnimationRepeat(animation: Animator?) {}
            }).startAnimation()
    }

    private fun callApiCartCount(isFristTime: Boolean,isCart:Boolean) {
        if (!isFristTime||isCart) {
           showLoadingProgress(activity!!)
        }
        val map = HashMap<String, String>()
        map["user_id"] = SharePreference.getStringPref(activity!!,userId)!!
        val call = ApiClient.getClient.getCartCount(map)
        call.enqueue(object : Callback<RestResponse<CartCountModel>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<RestResponse<CartCountModel>>,
                response: Response<RestResponse<CartCountModel>>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponce: RestResponse<CartCountModel> = response.body()!!
                    if(isCart){
                        Common.isCartTrueOut=false
                    }
                    if (isAdded) {
                        if (restResponce.status == 0) {
                            tvCount!!.text = "0"
                            rlItem.visibility = View.GONE
                        } else {
                            if (restResponce.data!!.cartcount!!.toInt()>0) {
                                rlCount!!.visibility = View.VISIBLE
                                rlItem.visibility = View.GONE
                                tvCount!!.text = restResponce.data.cartcount
                                tvCartCount!!.text = restResponce.data.cartcount
                                tvCartFoodPrice.text = Common.getCurrancy(activity!!)  + String.format(Locale.US, "%,.2f", restResponce.data.cartprice!!.toDouble())
                                rlItem.setOnClickListener {
                                    startActivity(Intent(activity!!,CartActivity::class.java).putExtra("isActivity",resources.getString(R.string.is_Cart_Act_home)))
                                }
                            } else {
                                rlItem.visibility = View.GONE
                                tvCount!!.text = restResponce.data.cartcount
                            }
                        }
                    }

                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    Common.dismissLoadingProgress()
                    alertErrorOrValidationDialog(activity!!, error.getString("message"))
                }
            }

            override fun onFailure(call: Call<RestResponse<CartCountModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(activity!!, false)
        if(Common.isCartTrueOut){
            if (Common.isCheckNetwork(activity!!)) {
                callApiCartCount(false,Common.isCartTrueOut)
            } else {
                alertErrorOrValidationDialog(activity!!,resources.getString(R.string.no_internet))
            }
        }
    }

    @SuppressLint("InflateParams")
    fun mExitDialog(position: Int) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(activity!!, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(activity!!)
            val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)
            mView.tvDesc.text=resources.getString(R.string.fev_list_alert)
            val finalDialog: Dialog = dialog
            mView.tvYes.setOnClickListener {
                finalDialog.dismiss()
                val map = HashMap<String, String>()
                map["favorite_id"] = favouriteItemList!![position].favorite_id.toString()
                if (Common.isCheckNetwork(activity!!)) {
                    callApiFavourite(map, position)
                } else {
                    alertErrorOrValidationDialog(
                        activity!!,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
            mView.tvNo.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun alertVariationDialog(
            act: Activity,
            listVarition: ArrayList<VariationModel>,
            strId: String,
            ivCopyFood: ImageView,
            strName: String,
            strImage: String,
            getvariation: ArrayList<VariationModel>
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
                map["user_id"] = SharePreference.getStringPref(act, userId)!!
                map["image_name"] = strImage
                map["item_name"] = strName
                if (Common.isCheckNetwork(act)) {
                    callApiAddToCart(map, ivCopyFood)
                } else {
                    alertErrorOrValidationDialog(
                            act,
                            resources.getString(R.string.no_internet)
                    )
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
                holder.itemView.tvPrice.text =getCurrancy(activity!!)+String.format(Locale.US, "%.2f", foodCategoryList[position].price!!.toDouble())

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
            layoutManager = LinearLayoutManager(activity!!)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
        }

    }


}