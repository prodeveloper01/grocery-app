package com.grocery.app.fragment

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.activity.*
import com.grocery.app.api.ApiClient
import com.grocery.app.api.RestResponse
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.base.BaseFragmnet
import com.grocery.app.model.*
import com.grocery.app.utils.*
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.getCurrancy
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference.Companion.getBooleanPref
import com.grocery.app.utils.SharePreference.Companion.getStringPref
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.cvCart
import kotlinx.android.synthetic.main.fragment_search.rlCount
//import kotlinx.android.synthetic.main.fragment_search.cvCart
import kotlinx.android.synthetic.main.fragment_search.rlItem
import kotlinx.android.synthetic.main.fragment_search.tvCartCount
import kotlinx.android.synthetic.main.fragment_search.tvCartFoodPrice
import kotlinx.android.synthetic.main.fragment_search.tvCount
//import kotlinx.android.synthetic.main.fragment_search.tvCount
import kotlinx.android.synthetic.main.fragment_search.tvNoDataFound
import kotlinx.android.synthetic.main.fragmnet_home.*
import kotlinx.android.synthetic.main.row_variation.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SearchFragment : BaseFragmnet() {
    var strSearch: String = ""
    var CURRENT_PAGES: Int = 1
    var TOTAL_PAGES: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var pastVisiblesItems: Int = 0
    var searchItemList: ArrayList<SearchProductModel>? = ArrayList()
    var searchAdapter: BaseAdaptor<SearchProductModel>? = null
    var manager1: GridLayoutManager? = null
    var isLoding=true
    var weightAndPriceList = ArrayList<WeightAndPriceModel>()
    private val cameraPermissionRequestCode = 1

    override fun setView(): Int {
        return R.layout.fragment_search
    }

    override fun init(view: View) {
        Common.getCurrentLanguage(activity!!, false)
        setSearchAdaptor()
        tvNoDataFound.visibility = View.VISIBLE
        rvSearch.visibility = View.GONE
        edSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (v.text.isNotEmpty()) {
                    performSearch(v.text.toString())
                } else {
                    tvNoDataFound.visibility = View.VISIBLE
                    rvSearch.visibility = View.GONE
                }
                return@OnEditorActionListener true
            }
            false
        })


        if (getBooleanPref(activity!!, SharePreference.isLogin)) {
            if (isCheckNetwork(activity!!)) {
                rlCount!!.visibility = View.GONE
                callApiCartCount(false)
            } else {
//                rlCount!!.visibility = View.GONE
                tvNoDataFound.visibility = View.VISIBLE
                rvSearch.visibility = View.GONE
                alertErrorOrValidationDialog(activity!!,resources.getString(R.string.no_internet))
            }
        }



        cvCart.setOnClickListener {
            if (getBooleanPref(activity!!, SharePreference.isLogin)) {
                startActivity(Intent(activity!!,CartActivity::class.java).putExtra("isActivity", resources.getString(R.string.is_Cart_Act_home)))
            } else {
                openActivity(LoginActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
//            startScanning()
        }

        manager1 = GridLayoutManager(activity!!, 2, GridLayoutManager.VERTICAL, false)
        rvSearch.layoutManager = manager1


        rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = manager1!!.childCount
                    totalItemCount = manager1!!.itemCount
                    pastVisiblesItems = manager1!!.findFirstVisibleItemPosition()
                    if (isLoding&&CURRENT_PAGES < TOTAL_PAGES) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            CURRENT_PAGES += 1
                            isLoding=false
                            if (isCheckNetwork(activity!!)) {
                                callApiSearchFood(strSearch, false)
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


    private fun startScanning() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCameraWithScanner()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionRequestCode && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraWithScanner()
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CAMERA
                )
            ) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivityForResult(intent, cameraPermissionRequestCode)
            }
        }
    }

    private fun openCameraWithScanner() {
        BarcodeScanningActivity.start(requireActivity())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraPermissionRequestCode) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCameraWithScanner()
            }
        }
    }

    private fun performSearch(strSearch: String) {
        this.strSearch = strSearch
        CURRENT_PAGES= 1
        TOTAL_PAGES= 0
        visibleItemCount= 0
        totalItemCount = 0
        pastVisiblesItems = 0
        isLoding=true
        if (isCheckNetwork(activity!!)) {
            callApiSearchFood(strSearch, true)
        } else {
            tvNoDataFound.visibility = View.VISIBLE
            rvSearch.visibility = View.GONE
            alertErrorOrValidationDialog(
                activity!!,
                resources.getString(R.string.no_internet)
            )
        }
    }


    private fun callApiSearchFood(strSearch: String?, isFirstTime: Boolean?) {
        if (isFirstTime!!) {
            searchItemList!!.clear()
            showLoadingProgress(activity!!)
        }else{
            rlPaginationBack.visibility=View.VISIBLE
        }

        val map = HashMap<String, String>()
        map["keyword"] = strSearch!!
        if (getBooleanPref(activity!!, SharePreference.isLogin)) {
            map["user_id"] = getStringPref(activity!!, SharePreference.userId)!!
        }else{
            map["user_id"] = ""
        }
        val call = ApiClient.getClient.getSearchByItem(map, CURRENT_PAGES.toString())
        call.enqueue(object : Callback<RestResponse<PaginationModel<SearchProductModel>>> {
            override fun onResponse(
                call: Call<RestResponse<PaginationModel<SearchProductModel>>>,
                response: Response<RestResponse<PaginationModel<SearchProductModel>>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<PaginationModel<SearchProductModel>> =
                        response.body()!!
                    if (restResponce.status == 1) {
                        if (isFirstTime) {
                            dismissLoadingProgress()
                        }else{
                            rlPaginationBack.visibility=View.GONE
                        }
                        tvNoDataFound.visibility = View.GONE
                        rvSearch.visibility = View.VISIBLE
                        val PaginationResponse: PaginationModel<SearchProductModel> =
                            restResponce.data!!
                        CURRENT_PAGES = PaginationResponse.currentPage!!
                        TOTAL_PAGES = PaginationResponse.lastPage!!
                        isLoding=true
                        if (PaginationResponse.data!!.size > 0) {
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
                            searchItemList!!.addAll(restResponce.data.data)
                            searchAdapter!!.notifyDataSetChanged()

                        } else {
                            tvNoDataFound.visibility = View.VISIBLE
                            rvSearch.visibility = View.GONE
                        }






                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        tvNoDataFound.visibility = View.VISIBLE
                        rvSearch.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(
                call: Call<RestResponse<PaginationModel<SearchProductModel>>>,
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

    private fun setSearchAdaptor() {
        searchAdapter = object : BaseAdaptor<SearchProductModel>(activity!!, searchItemList!!) {
            @SuppressLint("NewApi")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: SearchProductModel,
                position: Int
            ) {
                val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                val tvFoodPriceGrid: TextView = holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                val tvDiscountPrice: TextView = holder.itemView.findViewById(R.id.tvDiscountPrice)
                val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)
                val icLike: ImageView = holder.itemView.findViewById(R.id.icLike)
                tvFoodName.text = searchItemList!!.get(position).itemName

                Glide.with(activity!!).load(searchItemList!!.get(position).itemimage!!.image)
                    .into(ivFood)
                holder.itemView.setOnClickListener {
                    startActivity(
                        Intent(activity!!, ItemDetailActivity::class.java).putExtra(
                            "Item_Id",
                            searchItemList!![position].id.toString()
                        ).putExtra("isItemActivity", resources.getString(R.string.is_item_Act_home))
                    )
                }
                val tvWeight: TextView = holder.itemView.findViewById(R.id.tvWeight)
                var price=""
//                var productPrice=""
                if(searchItemList!![position].getvariation.size>0){
                    tvWeight.text = searchItemList!![position].getvariation[0].weight
                    price= searchItemList!![position].getvariation[0].price.toString()
//                    productPrice= searchItemList!![position].getvariation[0].productPrice.toString()
                }else{
                    tvWeight.text = searchItemList!![position].variation!![0].weight
                    price= searchItemList!![position].variation!![0].price!!
//                    productPrice= searchItemList!![position].variation!![0].productPrice!!
                }

                tvFoodPriceGrid.text = getCurrancy(activity!!).plus(
                   String.format(
                       Locale.US,
                       "%,.2f",
                       price.toDouble()
                   )
                )


//                if(productPrice!="null")
//                {
//                    tvDiscountPrice.text = getCurrancy(activity!!).plus(
//                        String.format(
//                            Locale.US,
//                            "%,.2f",
//                            productPrice.toDouble()
//                        )
//                    )
//                }
                tvDiscountPrice.paintFlags = tvDiscountPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                val ivCart: ImageView = holder.itemView.findViewById(R.id.ivAddCart)
                val ivOutOfStock: LinearLayout = holder.itemView.findViewById(R.id.ivOutOfStock)
                if(searchItemList!!.get(position).isOutOfStock!!){
                    ivOutOfStock.visibility=View.VISIBLE
                    ivCart.background=ResourcesCompat.getDrawable(resources,R.drawable.bg_gray_corner_10,null)
                }else{
                    ivOutOfStock.visibility=View.GONE
                    ivCart.background=ResourcesCompat.getDrawable(resources,R.drawable.bg_green_corner_10,null)
                }

                tvFoodPriceGrid.visibility = View.VISIBLE
                tvDiscountPrice.visibility = View.VISIBLE

                val ivCopyFood: ImageView = holder.itemView.findViewById(R.id.ivCopyFood)
                Glide.with(activity!!)
                    .load(searchItemList!!.get(position).itemimage!!.image)
                    .into(ivCopyFood)
                ivCart.setOnClickListener {
                    if(searchItemList!![position].variation!!.size>1){
                        alertVariationDialog(
                            activity!!,
                            searchItemList!![position].variation!!,
                            searchItemList!![position].id.toString(),
                            ivCopyFood,
                            searchItemList!![position].itemName!!,
                            searchItemList!![position].itemimage!!.image_name!!
                        )
                    }else{
                        if (getBooleanPref(activity!!, SharePreference.isLogin)) {
                            val map = HashMap<String, String>()
                            map["item_id"] = searchItemList!![position].id.toString()
                            map["qty"] = "1"
                            map["item_name"]=searchItemList!![position].itemName!!
                            map["image_name"]=searchItemList!![position].itemimage!!.image_name!!
                            map["price"] = String.format(Locale.US, "%.2f", searchItemList!![position].variation!![0].price!!.toDouble())
                            map["weight"] = searchItemList!![position].variation!![0].weight!!
                            map["variation_id"] =searchItemList!![position].variation!![0].id.toString()
                            map["user_id"] = getStringPref(activity!!, SharePreference.userId)!!
                            callApiAddToCart(map, ivCopyFood)
                        } else {
                            openActivity(LoginSelectActivity::class.java)
                            activity!!.finish()
                            activity!!.finishAffinity()
                        }
                    }


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

                if (searchItemList!!.get(position).isFavorite == 0) {
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
                    if (getBooleanPref(activity!!, SharePreference.isLogin)) {
                        if (searchItemList!!.get(position).isFavorite == 0) {
                            val map = HashMap<String, String>()
                            map.put("item_id", searchItemList!!.get(position).id.toString())
                            map.put("user_id", getStringPref(activity!!, SharePreference.userId)!!)
                            if (isCheckNetwork(activity!!)) {
                                callApiFavourite(map, position)
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
                return R.layout.row_foodsubcategory
            }

        }
        rvSearch.adapter = searchAdapter
        rvSearch.itemAnimator = DefaultItemAnimator()
        rvSearch.isNestedScrollingEnabled = true



    }

    private fun callApiFavourite(
        map: HashMap<String, String>,
        position: Int
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
                        searchItemList!!.get(position).isFavorite = 1
                        searchAdapter!!.notifyItemChanged(position)
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

    private fun callApiCartCount(isCart: Boolean) {
        showLoadingProgress(activity!!)
        val map = HashMap<String, String>()
        map.put("user_id", SharePreference.getStringPref(activity!!, SharePreference.userId)!!)
        val call = ApiClient.getClient.getCartCount(map)
        call.enqueue(object : Callback<RestResponse<CartCountModel>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<RestResponse<CartCountModel>>,
                response: Response<RestResponse<CartCountModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    if(isCart){
                        Common.isCartTrueOut=true
                    }
                    val restResponce: RestResponse<CartCountModel> = response.body()!!
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
                                tvCartFoodPrice.text = getCurrancy(activity!!)  + String.format(Locale.US, "%,.2f", restResponce.data.cartprice!!.toDouble())
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
        val call= ApiClient.getClient.getAddToCart(map)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                Common.dismissLoadingProgress()
                val restResponce: SingleResponse = response.body()!!
                if (response.code() == 200) {
                    if (restResponce.status == 1) {
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
                        callApiSearchFood(edSearch.text.toString(),true)
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
        if (Common.isCartTrueOut) {
            if (isCheckNetwork(activity!!)) {
//                callApiCartCount(Common.isCartTrueOut)
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
                for(i in 0 until listVarition.size) {
                    listVarition[i].isSelect = false
                }
                for(i in 0 until listVarition.size){
                    if(listVarition[i].stock!!>0){
                        listVarition[i].isSelect=true
                        break
                    }
                }
                dialog.dismiss()
            }
            val tvSubmit: TextView = m_view.findViewById(R.id.tvSubmit)
            tvSubmit.setOnClickListener {
                dialog.dismiss()
                if (getBooleanPref(activity!!, SharePreference.isLogin)) {
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
                    val map = HashMap<String, String>()
                    map["item_id"] = strId
                    map["qty"] = "1"
                    map["price"] = String.format(Locale.US, "%.2f", strPrice.toDouble())
                    map["weight"] = strWeight
                    map["variation_id"] =strVariationId
                    map["user_id"] = getStringPref(activity!!, SharePreference.userId)!!
                    map["image_name"] = strImage
                    map["item_name"] = strName
                    if (isCheckNetwork(activity!!)) {
                        callApiAddToCart(map, ivCopyFood)
                        for (i in 0 until listVarition.size) {
                            listVarition[i].isSelect = false
                        }
                        listVarition[0].isSelect = true
                        setVariationAdaptor(listVarition, rvVariation)
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