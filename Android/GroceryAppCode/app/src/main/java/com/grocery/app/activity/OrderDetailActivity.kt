package com.grocery.app.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.ListResopone
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.model.OrderDetailModel
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.getCurrancy
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_orderdetail.*
import kotlinx.android.synthetic.main.activity_orderdetail.ivBack
import kotlinx.android.synthetic.main.dlg_confomation.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class OrderDetailActivity:BaseActivity() {
    var OrderTotal:Double=0.0
    override fun setLayout(): Int {
        return R.layout.activity_orderdetail
    }

    override fun initView() {
        if(SharePreference.getStringPref(this@OrderDetailActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        tvOrderCancalled.visibility=View.GONE
        Common.getCurrentLanguage(this@OrderDetailActivity, false)
        if(Common.isCheckNetwork(this@OrderDetailActivity)){
            callApiOrderDetail()
        }else{
           alertErrorOrValidationDialog(
              this@OrderDetailActivity,
              resources.getString(R.string.no_internet)
           )
        }

        ivBack.setOnClickListener {
            finish()
        }

        tvOrderCancalled.setOnClickListener {
            mCancalOrderDialog(intent.getStringExtra("order_id")!!)
        }
    }

    override fun onBackPressed() {
        finish()
    }


    private fun callApiOrderDetail() {
        showLoadingProgress(this@OrderDetailActivity)
        val map = HashMap<String, String>()
        map["order_id"] = intent.getStringExtra("order_id")!!
        val call = ApiClient.getClient.getOrderDetails(map)
        call.enqueue(object : Callback<ListResopone<OrderDetailModel>> {
            override fun onResponse(
                call: Call<ListResopone<OrderDetailModel>>,
                response: Response<ListResopone<OrderDetailModel>>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponce: ListResopone<OrderDetailModel> = response.body()!!
                    if (restResponce.status==1) {
                        setFoodDetailData(restResponce)
                    } else if (restResponce.status==0) {
                        Common.dismissLoadingProgress()
                        rvOrderItemFood.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<ListResopone<OrderDetailModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                t.printStackTrace()
                alertErrorOrValidationDialog(
                    this@OrderDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setFoodDetailData(response:ListResopone<OrderDetailModel>) {
        if(response.data!!.size>0){
            for(i in 0 until response.data.size){
                OrderTotal+= response.data[i].itemPrice!!.toDouble() * response.data[i].qty!!.toInt()
            }

            setFoodCategoryAdaptor(response.data)
        }
        cvTrackOrder.visibility=View.VISIBLE
        val status=intent.getStringExtra("order_status")!!.toInt()



        if(response.orderType==2){
            ivStatus3.visibility=View.GONE
            tvOrderStatus3.visibility=View.GONE
            view3.visibility=View.GONE
            v3.visibility=View.GONE
            //cvPincode.visibility=View.GONE
            when (status) {
                5 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    tvOrderStatus1.text=resources.getString(R.string.order_cancelled_you)
                    tvOrderStatus2.text=resources.getString(R.string.order_ready)
                    tvOrderStatus3.text=resources.getString(R.string.order_delivered1)
                    tvOrderCancalled.visibility=View.GONE
                }
                6 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    tvOrderStatus1.text=resources.getString(R.string.order_cancelled_admin)
                    tvOrderStatus2.text=resources.getString(R.string.order_ready)
                    tvOrderStatus3.text=resources.getString(R.string.order_delivered1)
                    tvOrderCancalled.visibility=View.GONE
                }
                1 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    tvOrderCancalled.visibility=View.VISIBLE
                }
                2 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    tvOrderCancalled.visibility=View.GONE
                }
                4 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    tvOrderCancalled.visibility=View.GONE
                }
            }

            cvDeliveryAddress.visibility= View.GONE
            cvDriverInformation.visibility= View.GONE
            if(response.summery!!.promocode==null){
                rlDiscount.visibility= View.GONE
                if(response.summery.orderNotes==null){
                    tvNotes.text=""
                    cvOrderNote.visibility= View.GONE
                }else{
                    cvOrderNote.visibility= View.VISIBLE
                    tvNotes.text=response.summery.orderNotes
                }
                if(response.address!=null){
                    tvOrderAddress.text=response.address
                    edLandmark.text=response.landmark
                    edBuilding.text=response.building
                }

                tvOrderTotalPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",OrderTotal)
                tvOrderTaxPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",response.summery.tax!!.toDouble())
                tvOrderDeliveryCharge.text=getCurrancy(this@OrderDetailActivity)+"0.00"

                val getTex:Double=(OrderTotal*response.summery.tax.toDouble())/100.toDouble()
                tvTitleTex.text="Tax (${response.summery.tax}%)"
                tvOrderTaxPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f", getTex)
                val totalprice=OrderTotal+getTex+0.00
                tvOrderTotalCharge.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f", totalprice)
            }else{
                rlDiscount.visibility= View.VISIBLE
                if(response.summery.orderNotes==null){
                    tvNotes.text=""
                    cvOrderNote.visibility= View.GONE
                }else{
                    cvOrderNote.visibility= View.VISIBLE
                    tvNotes.text=response.summery.orderNotes
                }

                if(response.address!=null){
                    tvOrderAddress.text=response.address
                    edLandmark.text=response.landmark
                    edBuilding.text=response.building
                }

                tvOrderTotalPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",OrderTotal)
                tvOrderTaxPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",OrderTotal)
                tvOrderDeliveryCharge.text=getCurrancy(this@OrderDetailActivity)+"0.00"

                val getTex:Double=(OrderTotal*response.summery.tax!!.toDouble())/100.toDouble()
                tvTitleTex.text="Tax (${response.summery.tax}%)"
                tvOrderTaxPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f", getTex)

                tvDiscountOffer.text ="-"+getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f", response.summery.discountAmount!!.toDouble())
                tvPromoCodeApply.text =response.summery.promocode.toString()

                val subtotal=OrderTotal-response.summery.discountAmount.toDouble()
                val totalprice=subtotal+getTex+0.00
                tvOrderTotalCharge.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f", totalprice)
            }
        }else{
            ivStatus3.visibility=View.VISIBLE
            tvOrderStatus3.visibility=View.VISIBLE
            view3.visibility=View.VISIBLE
            v3.visibility=View.VISIBLE
            //cvPincode.visibility=View.VISIBLE

            if(response.pincode!=null){
               edPincode.text=response.pincode.toString()
            }else{
               edPincode.text=""
            }

            when (status) {
                5 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    tvOrderStatus1.text=resources.getString(R.string.order_cancelled_you)
                    tvOrderStatus2.text=resources.getString(R.string.order_ready)
                    tvOrderStatus4.text=resources.getString(R.string.order_delivered)
                    ivStatus3.visibility=View.GONE
                    tvOrderStatus3.visibility=View.GONE
                    view3.visibility=View.GONE
                    v3.visibility=View.GONE
                    tvOrderCancalled.visibility=View.GONE
                }
                6 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    tvOrderStatus1.text=resources.getString(R.string.order_cancelled_admin)
                    tvOrderStatus2.text=resources.getString(R.string.order_ready)
                    tvOrderStatus4.text=resources.getString(R.string.order_delivered)
                    ivStatus3.visibility=View.GONE
                    tvOrderStatus3.visibility=View.GONE
                    view3.visibility=View.GONE
                    v3.visibility=View.GONE
                    tvOrderCancalled.visibility=View.GONE
                }
                1 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    ivStatus3.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    tvOrderCancalled.visibility=View.VISIBLE
                }
                2 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus3.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    tvOrderCancalled.visibility=View.GONE
                }
                3 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus3.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_check,null))
                    tvOrderCancalled.visibility=View.GONE
                }
                4 -> {
                    ivStatus1.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus2.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus3.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    ivStatus4.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_round_uncheck,null))
                    tvOrderCancalled.visibility=View.GONE
                }
            }
            cvDeliveryAddress.visibility= View.VISIBLE
            if(intent.getStringExtra("order_status")!! == "3" || intent.getStringExtra("order_status")!! == "4"){
                cvDriverInformation.visibility= View.VISIBLE
                llCall.setOnClickListener {
                    //if(response.getMobile()!=null){
                    val call: Uri = Uri.parse("tel:${response.summery!!.driverMobile}")
                    val surf = Intent(Intent.ACTION_DIAL, call)
                    startActivity(surf)
                    //}
                }
                tvUserName.text=response.summery!!.driverName

                Glide.with(this@OrderDetailActivity).load(response.summery.driverProfileImage).placeholder(ResourcesCompat.getDrawable(resources,R.drawable.ic_placeholder,null)).centerCrop().into(ivUserDetail)
            }else{
                cvDriverInformation.visibility= View.GONE
            }

            if(response.summery!!.promocode==null){
                rlDiscount.visibility= View.GONE
                if(response.summery.orderNotes==null){
                    tvNotes.text=""
                    cvOrderNote.visibility= View.GONE
                }else{
                    cvOrderNote.visibility= View.VISIBLE
                    tvNotes.text=response.summery.orderNotes
                }
                if(response.address!=null){
                    tvOrderAddress.text=response.address
                    edLandmark.text=response.landmark
                    edBuilding.text=response.building
                }
                tvOrderTotalPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",OrderTotal)
                tvOrderTaxPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",response.summery.tax!!.toDouble())
                tvOrderDeliveryCharge.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",response.summery.deliveryCharge!!.toDouble())

                val getTex:Double=(OrderTotal*response.summery.tax.toDouble())/100.toDouble()
                tvTitleTex.text="Tax (${response.summery.tax}%)"
                tvOrderTaxPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f", getTex)
                val totalprice=OrderTotal+getTex+response.summery.deliveryCharge.toDouble()
                tvOrderTotalCharge.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f", totalprice)
            }else{
                rlDiscount.visibility= View.VISIBLE
                if(response.summery.orderNotes==null){
                    tvNotes.text=""
                    cvOrderNote.visibility= View.GONE
                }else{
                    cvOrderNote.visibility= View.VISIBLE
                    tvNotes.text=response.summery.orderNotes
                }
                if(response.address!=null){
                    tvOrderAddress.text=response.address
                    edLandmark.text=response.landmark
                    edBuilding.text=response.building
                }
                tvOrderTotalPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",OrderTotal)
                tvOrderTaxPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",response.summery.tax!!.toDouble())
                tvOrderDeliveryCharge.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",response.summery.deliveryCharge!!.toDouble())

                val getTex:Double=(OrderTotal*response.summery.tax.toDouble())/100
                tvTitleTex.text="Tax (${response.summery.tax}%)"
                tvOrderTaxPrice.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f", getTex)

                tvDiscountOffer.text ="-"+getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f",response.summery.discountAmount!!.toFloat())
                tvPromoCodeApply.text =response.summery.promocode.toString()

                val subtotal=OrderTotal-response.summery.discountAmount.toDouble()
                val totalprice=subtotal+getTex+response.summery.deliveryCharge.toFloat()
                tvOrderTotalCharge.text=getCurrancy(this@OrderDetailActivity)+String.format(Locale.US,"%,.02f", totalprice)
            }
        }

    }

    private fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderDetailModel>) {
        val orderHistoryAdapter = object : BaseAdaptor<OrderDetailModel>(this@OrderDetailActivity, orderHistoryList) {
            @SuppressLint("SetTextI18n", "NewApi")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: OrderDetailModel,
                position: Int
            ) {

                val tvOrderFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                val ivFoodItem: ImageView = holder.itemView.findViewById(R.id.ivFoodCart)
                val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPrice)
                val tvQtyNumber: TextView = holder.itemView.findViewById(R.id.tvItemQty)

                Glide.with(this@OrderDetailActivity)
                    .load(orderHistoryList[position].itemimage)
                    .into(ivFoodItem)

                val tvWeight: TextView = holder.itemView.findViewById(R.id.tvWeight)
                tvWeight.text = orderHistoryList[position].weight

                tvOrderFoodName.text = orderHistoryList[position].itemName

                val price=orderHistoryList[position].itemPrice!!.toDouble()*orderHistoryList[position].qty!!.toInt()
                tvPrice.text = getCurrancy(this@OrderDetailActivity) +String.format(Locale.US, "%,.2f", price)
                tvQtyNumber.text = orderHistoryList[position].qty.toString()

                Glide.with(this@OrderDetailActivity).load(orderHistoryList[position].itemimage!!)
                    .placeholder(ResourcesCompat.getDrawable(resources,R.drawable.ic_placeholder,null))
                    .into(ivFoodItem)

                val rlFood: RelativeLayout = holder.itemView.findViewById(R.id.rlFood)

                when (position%5) {
                    0 -> {
                        rlFood.backgroundTintList=ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color1,
                                null
                            )
                        )
                    }
                    1 -> {
                        rlFood.backgroundTintList=ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color2,
                                null
                            )
                        )
                    }
                    2 -> {
                        rlFood.backgroundTintList=ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color3,
                                null
                            )
                        )
                    }
                    3 -> {
                        rlFood.backgroundTintList=ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color4,
                                null
                            )
                        )
                    }
                    4 -> {
                        rlFood.backgroundTintList=ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color5,
                                null
                            )
                        )
                    }
                }


            }
            override fun setItemLayout(): Int {
                return R.layout.row_orderproduct
            }

        }
        rvOrderItemFood.adapter = orderHistoryAdapter
        rvOrderItemFood.layoutManager = LinearLayoutManager(this@OrderDetailActivity)
        rvOrderItemFood.itemAnimator = DefaultItemAnimator()
        rvOrderItemFood.isNestedScrollingEnabled = true
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@OrderDetailActivity, false)
    }

    override fun onPause() {
        super.onPause()
        Common.getCurrentLanguage(this@OrderDetailActivity, false)
    }

    @SuppressLint("InflateParams")
    fun mCancalOrderDialog(strOrderId:String) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(this@OrderDetailActivity, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(this@OrderDetailActivity)
            val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)
            mView.tvDesc.text=resources.getString(R.string.order_cancel_desc)
            val finalDialog: Dialog = dialog
            mView.tvYes.setOnClickListener {
                finalDialog.dismiss()
                val map=HashMap<String, String>()
                map["order_id"]=strOrderId
                if(Common.isCheckNetwork(this@OrderDetailActivity)){
                    callApiOrder(map)
                }else{
                    alertErrorOrValidationDialog(
                        this@OrderDetailActivity,
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



    private fun callApiOrder(map: HashMap<String, String>){
        showLoadingProgress(this@OrderDetailActivity)
        val call = ApiClient.getClient.setOrderCancel(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        Common.dismissLoadingProgress()
                        Common.isCancelledOrder=true
                        finish()
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@OrderDetailActivity,
                            restResponse.message
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    Common.dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        this@OrderDetailActivity,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@OrderDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

}