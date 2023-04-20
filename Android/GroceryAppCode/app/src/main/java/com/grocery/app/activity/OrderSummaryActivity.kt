package com.grocery.app.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.ListResopone
import com.grocery.app.api.RestResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.model.ListResponse
import com.grocery.app.model.OrderDetailItemModel
import com.grocery.app.model.PromocodeModel
import com.grocery.app.model.SummaryModel
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.getCurrancy
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.getStringPref
import com.grocery.app.utils.SharePreference.Companion.isCurrancy
import com.grocery.app.utils.SharePreference.Companion.isMaximum
import com.grocery.app.utils.SharePreference.Companion.isMiniMum
import com.grocery.app.utils.SharePreference.Companion.userId
import kotlinx.android.synthetic.main.activity_ordersummary.*
import kotlinx.android.synthetic.main.activity_ordersummary.ivBack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class OrderSummaryActivity:BaseActivity() {

    var summaryModel= SummaryModel()
    var promocodeList:ArrayList<PromocodeModel>?=null
    var discountAmount="0.00"
    var discountPer=""
    var promocodePrice:Float= 0.0F
    var selectType="0"
    var address=""
    var building=""
    var Landmark=""
    var lat=""
    var lon=""
    var date=""
    var pincode=""
    var OrderTotal:Double=0.0
    override fun setLayout(): Int {
        return R.layout.activity_ordersummary
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        Common.getCurrentLanguage(this@OrderSummaryActivity, false)
        promocodeList= ArrayList()
        selectType=intent.getStringExtra("select_type")!!
        address=intent.getStringExtra("address")!!.toString()
        building=intent.getStringExtra("building")!!.toString()
        Landmark=intent.getStringExtra("Landmark")!!.toString()
        lat=intent.getStringExtra("lat")!!.toString()
        lon=intent.getStringExtra("lon")!!.toString()
        date=intent.getStringExtra("date")!!.toString()
        pincode=intent.getStringExtra("pincode")!!.toString()
        tvDiscountOffer.text=getCurrancy(this@OrderSummaryActivity)+""+"0.00"

        if(Common.isCheckNetwork(this@OrderSummaryActivity)){
            callApiOrderSummary()
        }else{
            alertErrorOrValidationDialog(
                this@OrderSummaryActivity,
                resources.getString(R.string.no_internet)
            )
        }

        if(SharePreference.getStringPref(this@OrderSummaryActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }

        tvbtnPromocode.setOnClickListener {
            if(Common.isCheckNetwork(this@OrderSummaryActivity)){
                callApiPromocode()
            }else{
                alertErrorOrValidationDialog(
                    this@OrderSummaryActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        }

        ivBack.setOnClickListener {
            finish()
        }

        tvApply.setOnClickListener {
            if(tvApply.text.toString() == "Apply"){
                if(edPromocode.text.toString() != ""){
                    callApiCheckPromocode()
                }
            }else if(tvApply.text.toString() == "Remove") {
                tvbtnPromocode.visibility=View.VISIBLE
                tvPromoCodeApply.text=""
                tvDiscountOffer.text=getCurrancy(this@OrderSummaryActivity)+"0.00"
                edPromocode.text = ""
                tvApply.text=resources.getString(R.string.apply_)
                if(selectType=="1"){
                    val orderTax:Double=(OrderTotal*summaryModel.tax!!.toDouble())/100.toDouble()
                    tvOrderTotalPrice.text=getCurrancy(this@OrderSummaryActivity).plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            OrderTotal
                        )
                    )
                    tvOrderTaxPrice.text=getCurrancy(this@OrderSummaryActivity).plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            orderTax
                        )
                    )
                    tvTitleTex.text="Tax (${summaryModel.tax}%)"
                    tvOrderDeliveryCharge.text=getCurrancy(this@OrderSummaryActivity).plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            summaryModel.deliveryCharge!!.toDouble()
                        )
                    )
                    val totalprice=OrderTotal+orderTax+summaryModel.deliveryCharge!!.toDouble()
                    tvOrderTotalCharge.text=getCurrancy(this@OrderSummaryActivity).plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            totalprice
                        )
                    )
                    discountPer=""
                    discountAmount="0.00"
                }else{
                    val orderTax:Double=(OrderTotal*summaryModel.tax!!.toDouble())/100.toDouble()
                    tvOrderTotalPrice.text=getCurrancy(this@OrderSummaryActivity).plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            OrderTotal
                        )
                    )
                    tvOrderTaxPrice.text=getCurrancy(this@OrderSummaryActivity).plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            orderTax
                        )
                    )
                    tvTitleTex.text="Tax (${summaryModel.tax}%)"
                    tvOrderDeliveryCharge.text=getCurrancy(this@OrderSummaryActivity).plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            0.00
                        )
                    )
                    val totalprice=OrderTotal+orderTax+0.00
                    tvOrderTotalCharge.text=getCurrancy(this@OrderSummaryActivity).plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            totalprice
                        )
                    )
                    discountPer=""
                    discountAmount="0.00"
                }
            }
        }

        tvProceedtoPay.setOnClickListener {
            if(OrderTotal>=getStringPref(this@OrderSummaryActivity,isMiniMum)!!.toDouble()&&OrderTotal<=getStringPref(this@OrderSummaryActivity,isMaximum)!!.toDouble()){
                val intent=Intent(this@OrderSummaryActivity, OrderPaymentActivity::class.java)
                val strTotalCharge=tvOrderTotalCharge.text.toString().replace(
                    getCurrancy(this@OrderSummaryActivity),
                    ""
                )
                val strFinalPrice=strTotalCharge.replace(",","")
                Common.getLog("getOrderTotal",OrderTotal.toString())
                val orderTax:Double=(OrderTotal*summaryModel.tax!!.toDouble())/100
                intent.putExtra(
                    "getAmount",
                    String.format(Locale.US, "%.2f", strFinalPrice.toDouble())
                )
                intent.putExtra("getTax", summaryModel.tax)
                intent.putExtra("getTaxAmount", String.format(Locale.US, "%.2f", orderTax))
                intent.putExtra("promocode", tvPromoCodeApply.text.toString())
                intent.putExtra("discount_pr", discountPer)
                intent.putExtra("discount_amount", discountAmount)
                intent.putExtra("order_notes", edNotes.text.toString())
                intent.putExtra("order_type", selectType)
                intent.putExtra("ordered_date", date)
                if(selectType=="1"){
                    intent.putExtra(
                        "delivery_charge", String.format(
                            Locale.US,
                            "%.2f",
                            summaryModel.deliveryCharge!!.toDouble()
                        )
                    )
                    intent.putExtra("building", building)
                    intent.putExtra("landmark", Landmark)
                    intent.putExtra("lat", lon)
                    intent.putExtra("lon", lat)
                    intent.putExtra("getAddress", address)
                    intent.putExtra("pincode", pincode)
                    startActivity(intent)
                }else if(selectType=="2"){
                    intent.putExtra("delivery_charge", "0.00")
                    intent.putExtra("building", "no")
                    intent.putExtra("landmark", "no")
                    intent.putExtra("lat", "no")
                    intent.putExtra("lon", "no")
                    intent.putExtra("getAddress", "no")
                    intent.putExtra("pincode", "no")
                    startActivity(intent)
                }
            }else {
                alertErrorOrValidationDialog(this@OrderSummaryActivity,"Order amount must be between ${getStringPref(this@OrderSummaryActivity,isCurrancy)+getStringPref(this@OrderSummaryActivity,isMiniMum)} and ${getStringPref(this@OrderSummaryActivity,isCurrancy)+getStringPref(this@OrderSummaryActivity, isMaximum)}")
            }
        }
    }

    private fun callApiOrderSummary() {
        Common.showLoadingProgress(this@OrderSummaryActivity)
        val map = HashMap<String, String>()
        map["user_id"] = getStringPref(this@OrderSummaryActivity, userId)!!
        if (selectType == "1") {
            map["pincode"] = pincode
        }
        else if (selectType == "2") {
            map["pincode"] = ""
        }

        val call = ApiClient.getClient.getSummary(map)
        call.enqueue(object : Callback<ListResopone<OrderDetailItemModel>> {
            override fun onResponse(
                call: Call<ListResopone<OrderDetailItemModel>>,
                response: Response<ListResopone<OrderDetailItemModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: ListResopone<OrderDetailItemModel> = response.body()!!
                    if (restResponce.status == 1) {
                        if (restResponce.data!!.isNullOrEmpty()) {
                            rvOrderItemFood.visibility = View.GONE
                        } else {
                            rvOrderItemFood.visibility = View.VISIBLE
                            val foodCategoryList = restResponce.data
                            val summary = restResponce.summery
                            setFoodCategoryAdaptor(foodCategoryList, summary)
                        }
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        rvOrderItemFood.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<ListResopone<OrderDetailItemModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    act = this@OrderSummaryActivity,
                    msg = resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    @SuppressLint("SetTextI18n")
    private fun setFoodCategoryAdaptor(
        foodCategoryList: ArrayList<OrderDetailItemModel>,
        summary: SummaryModel?
    ) {
        if(foodCategoryList.size>0){
            setFoodCategoryAdaptor(foodCategoryList)
        }
        summaryModel=summary!!


        for(i in 0 until foodCategoryList.size){
            OrderTotal+= foodCategoryList[i].itemPrice!!.toDouble() * foodCategoryList[i].qty!!.toInt()
        }


        if(selectType=="1"){
            val orderTax:Double=(OrderTotal*summary.tax!!.toDouble())/100.toDouble()
            tvOrderTotalPrice.text=getCurrancy(this@OrderSummaryActivity)+String.format(
                Locale.US,
                "%,.2f",
                OrderTotal
            )
            tvOrderTaxPrice.text=getCurrancy(this@OrderSummaryActivity)+String.format(
                Locale.US,
                "%,.2f",
                orderTax
            )
            tvTitleTex.text="Tax (${summary.tax}%)"
            tvOrderDeliveryCharge.text=getCurrancy(this@OrderSummaryActivity)+String.format(
                Locale.US,
                "%,.2f",
                summaryModel.deliveryCharge!!.toDouble()
            )

            val totalprice=OrderTotal+orderTax+summary.deliveryCharge!!.toDouble()
            tvOrderTotalCharge.text=getCurrancy(this@OrderSummaryActivity)+String.format(
                Locale.US,
                "%,.2f",
                totalprice
            )
        }else{
            val orderTax:Double=(OrderTotal*summary.tax!!.toDouble())/100.toFloat()
            tvOrderTotalPrice.text=getCurrancy(this@OrderSummaryActivity)+String.format(
                Locale.US,
                "%,.2f",
                OrderTotal
            )
            tvOrderTaxPrice.text=getCurrancy(this@OrderSummaryActivity)+String.format(
                Locale.US,
                "%,.2f",
                orderTax
            )
            tvTitleTex.text="Tax (${summary.tax}%)"
            tvOrderDeliveryCharge.text=getCurrancy(this@OrderSummaryActivity)+String.format(
                Locale.US,
                "%,.2f",
                0.00
            )
            val totalprice=OrderTotal+orderTax+0.00
            tvOrderTotalCharge.text=getCurrancy(this@OrderSummaryActivity)+String.format(
                Locale.US,
                "%,.2f",
                totalprice
            )
        }
    }

    fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderDetailItemModel>) {
        val orderHistoryAdapter = object : BaseAdaptor<OrderDetailItemModel>(
            this@OrderSummaryActivity,
            orderHistoryList
        ) {
            @SuppressLint("SetTextI18n", "NewApi", "UseCompatLoadingForDrawables")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: OrderDetailItemModel,
                position: Int
            ) {
                val tvOrderFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                val ivFoodItem: ImageView = holder.itemView.findViewById(R.id.ivFoodCart)
                val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPrice)
                val tvQtyNumber: TextView = holder.itemView.findViewById(R.id.tvItemQty)
                val tvWeight: TextView = holder.itemView.findViewById(R.id.tvWeight)
                tvWeight.text = orderHistoryList[position].weight
                Glide.with(this@OrderSummaryActivity)
                    .load(orderHistoryList[position].itemimage!!.image)
                    .into(ivFoodItem)

                tvOrderFoodName.text = orderHistoryList[position].itemName

                val price=orderHistoryList[position].itemPrice!!.toDouble()*orderHistoryList[position].qty!!
                tvPrice.text = getCurrancy(this@OrderSummaryActivity) + String.format(Locale.US, "%,.2f", price)
                tvQtyNumber.text = orderHistoryList[position].qty.toString()


                val rlFood: RelativeLayout = holder.itemView.findViewById(R.id.rlFood)
                val i=position%5

                if(i==0){
                    rlFood.backgroundTintList=ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color1,
                            null
                        )
                    )
                }else if(i==1){
                    rlFood.backgroundTintList=ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color2,
                            null
                        )
                    )
                }else  if(i==2){
                    rlFood.backgroundTintList=ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color3,
                            null
                        )
                    )
                }else  if(i==3){
                    rlFood.backgroundTintList=ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color4,
                            null
                        )
                    )
                }else if(i==4){
                    rlFood.backgroundTintList=ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.color5,
                            null
                        )
                    )
                }

            }
            override fun setItemLayout(): Int {
                return R.layout.row_orderproduct
            }
        }
        rvOrderItemFood.adapter = orderHistoryAdapter
        rvOrderItemFood.layoutManager = LinearLayoutManager(this@OrderSummaryActivity)
        rvOrderItemFood.itemAnimator = DefaultItemAnimator()
        rvOrderItemFood.isNestedScrollingEnabled = true
    }

   //:::::::::::::Pincode Api::::::::::://
    private fun callApiPromocode() {
        Common.showLoadingProgress(this@OrderSummaryActivity)
        val map = HashMap<String, String>()
        map["user_id"] = getStringPref(this@OrderSummaryActivity, userId)!!
        val call = ApiClient.getClient.getPromoCodeList()
        call.enqueue(object : Callback<ListResponse<PromocodeModel>> {
            override fun onResponse(
                call: Call<ListResponse<PromocodeModel>>,
                response: Response<ListResponse<PromocodeModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: ListResponse<PromocodeModel> = response.body()!!
                    if (restResponce.status == 1) {
                        if (restResponce.data!!.size > 0) {
                            promocodeList = restResponce.data
                        }
                        openDialogPromocode()
                    }
                }
            }

            override fun onFailure(call: Call<ListResponse<PromocodeModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@OrderSummaryActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun openDialogPromocode() {
        val dialog = Dialog(this@OrderSummaryActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.windowAnimations = R.style.DialogAnimation
        dialog.window!!.attributes = lp
        dialog.setContentView(R.layout.dlg_procode)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val ivCancel = dialog.findViewById<ImageView>(R.id.ivCancel)
        val rvPromocode = dialog.findViewById<RecyclerView>(R.id.rvPromoCode)
        val tvNoDataFound = dialog.findViewById<TextView>(R.id.tvNoDataFound)
        if(promocodeList!!.size>0){
            rvPromocode.visibility= View.VISIBLE
            tvNoDataFound.visibility= View.GONE
            setPromocodeAdaptor(promocodeList!!, rvPromocode, dialog)
        }else{
            rvPromocode.visibility= View.GONE
            tvNoDataFound.visibility= View.VISIBLE
        }
        ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    open fun setPromocodeAdaptor(
        promocodeList: ArrayList<PromocodeModel>,
        rvPromocode: RecyclerView,
        dialog: Dialog
    ) {
        val orderHistoryAdapter = object : BaseAdaptor<PromocodeModel>(
            this@OrderSummaryActivity,
            promocodeList
        ) {
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: PromocodeModel,
                position: Int
            ) {
                val tvTitleOrderNumber: TextView = holder!!.itemView.findViewById(R.id.tvTitleOrderNumber)
                val tvPromocode: TextView = holder.itemView.findViewById(R.id.tvPromocode)
                val tvPromocodeDescription: TextView = holder.itemView.findViewById(R.id.tvPromocodeDescription)
                val tvCopyCode: TextView = holder.itemView.findViewById(R.id.tvCopyCode)

                tvTitleOrderNumber.text = promocodeList[position].offerName
                tvPromocode.text = promocodeList[position].offerCode
                tvPromocodeDescription.text = promocodeList[position].description

                tvCopyCode.setOnClickListener {
                    dialog.dismiss()
                    promocodePrice=promocodeList.get(position).offerAmount!!.toFloat()
                    edPromocode.text=promocodeList.get(position).offerCode
                    /*val clipboardManager = this@OrderSummaryActivity
                        .getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clipData = ClipData.newPlainText(promocodeList.get(position).offerCode,promocodeList.get(position).offerCode)
                    clipboardManager.setPrimaryClip(clipData)*/
                }


            }

            override fun setItemLayout(): Int {
                return R.layout.row_promocode
            }


        }
        rvPromocode.adapter = orderHistoryAdapter
        rvPromocode.layoutManager = LinearLayoutManager(this@OrderSummaryActivity)
        rvPromocode.itemAnimator = DefaultItemAnimator()
        rvPromocode.isNestedScrollingEnabled = true
    }

    private fun callApiCheckPromocode() {
        Common.showLoadingProgress(this@OrderSummaryActivity)
        val map = HashMap<String, String>()
        map["offer_code"] = edPromocode.text.toString()
        map["user_id"] = getStringPref(this@OrderSummaryActivity, userId)!!
        val call = ApiClient.getClient.setApplyPromocode(map)
        call.enqueue(object : Callback<RestResponse<PromocodeModel>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<RestResponse<PromocodeModel>>,
                response: Response<RestResponse<PromocodeModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: RestResponse<PromocodeModel> = response.body()!!
                    if (restResponce.status == 1) {
                        tvbtnPromocode.visibility=View.GONE
                        rlDiscount.visibility = View.VISIBLE
                        tvDiscountOffer.text = "-" + restResponce.data!!.offerAmount!! + "%"
                        tvPromoCodeApply.text = restResponce.data.offerCode!!
                        tvApply.text = "Remove"
                        if (selectType == "1") {
                            val subtotalCharge =
                                (OrderTotal * restResponce.data.offerAmount!!.toDouble()) / 100
                            val total = OrderTotal - subtotalCharge
                            val ordreTax =
                                (OrderTotal * summaryModel.tax!!.toDouble()) / 100
                            val mainTotal =
                                ordreTax + total + summaryModel.deliveryCharge!!.toDouble()
                            tvDiscountOffer.text =
                                "-" + getCurrancy(this@OrderSummaryActivity) + String.format(
                                    Locale.US,
                                    "%,.2f",
                                    subtotalCharge
                                )
                            tvOrderTotalCharge.text =
                                getCurrancy(this@OrderSummaryActivity) + String.format(
                                    Locale.US,
                                    "%,.2f",
                                    mainTotal
                                )
                            discountAmount = subtotalCharge.toString()
                            discountPer = restResponce.data.offerAmount
                        } else {
                            val subtotalCharge =
                                (OrderTotal * restResponce.data.offerAmount!!.toDouble()) / 100
                            val total = OrderTotal - subtotalCharge
                            val ordreTax =
                                (OrderTotal * summaryModel.tax!!.toDouble()) / 100
                            val mainTotal = ordreTax + total + 0.00
                            tvDiscountOffer.text =
                                "-" + getCurrancy(this@OrderSummaryActivity) + String.format(
                                    Locale.US,
                                    "%,.2f",
                                    subtotalCharge
                                )
                            tvOrderTotalCharge.text =
                                getCurrancy(this@OrderSummaryActivity) + String.format(
                                    Locale.US,
                                    "%,.2f",
                                    mainTotal
                                )
                            discountAmount = subtotalCharge.toString()
                            discountPer = restResponce.data.offerAmount
                        }
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        edPromocode.setText("")
                        rlDiscount.visibility = View.GONE
                        tvApply.text = "Apply"
                        alertErrorOrValidationDialog(
                            this@OrderSummaryActivity,
                            restResponce.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<PromocodeModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@OrderSummaryActivity,
                    resources.getString(R.string.error_msg)
                )
            }


        })
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@OrderSummaryActivity, false)
    }
}


