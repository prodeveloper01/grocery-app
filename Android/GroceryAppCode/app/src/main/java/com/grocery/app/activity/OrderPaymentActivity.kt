package com.grocery.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.PaymentListResponce
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.model.PaymentItemModel
import com.grocery.app.utils.CallBackSuccess
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.Common.showErrorFullMsg
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.NewStripeDataController
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.getStringPref
import com.grocery.app.utils.SharePreference.Companion.userEmail
import com.grocery.app.utils.SharePreference.Companion.userId
import com.grocery.app.utils.SharePreference.Companion.userMobile
import com.grocery.app.utils.SharePreference.Companion.userName
import com.grocery.app.utils.SharePreference.Companion.userProfile
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import com.stripe.android.view.CardMultilineWidget
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.tvNoDataFound
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_payment.ivBack
import kotlinx.android.synthetic.main.row_payment.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderPaymentActivity:BaseActivity(), PaymentResultListener,CallBackSuccess {
    var callBackSuccess: CallBackSuccess?=null
    var newStripeDataController: NewStripeDataController?=null
    var strGetData=""
    var strRezorPayKey=""
    var listData=ArrayList<PaymentItemModel>()
    override fun setLayout(): Int {
        return R.layout.activity_payment
    }


    override fun initView() {
        callBackSuccess=this@OrderPaymentActivity as CallBackSuccess
        Common.getCurrentLanguage(this@OrderPaymentActivity, false)
        if(SharePreference.getStringPref(this@OrderPaymentActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        if(isCheckNetwork(this@OrderPaymentActivity)){
           callApiPayment()
        }else{
           alertErrorOrValidationDialog(this@OrderPaymentActivity, resources.getString(R.string.no_internet))
        }

        ivBack.setOnClickListener {
            finish()
        }

        tvPaynow.setOnClickListener {
            if(strGetData == "Wallet"){
                if(listData[0].isSelect){
                    if(listData[0].wallet_amount!!.toDouble()>=intent.getStringExtra("getAmount")!!.toDouble()){
                        if (isCheckNetwork(this@OrderPaymentActivity)) {
                            showLoadingProgress(this@OrderPaymentActivity)
                            val hasmap = HashMap<String, String>()
                            hasmap["user_id"] = getStringPref(this@OrderPaymentActivity, userId)!!
                            hasmap["order_total"] = intent.getStringExtra("getAmount")!!
                            hasmap["razorpay_payment_id"] = ""
                            hasmap["payment_type"] = "3"
                            hasmap["promocode"] = intent.getStringExtra("promocode")!!
                            hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
                            hasmap["discount_pr"] = intent.getStringExtra("discount_pr")!!
                            hasmap["tax"] = intent.getStringExtra("getTax")!!
                            hasmap["tax_amount"] = intent.getStringExtra("getTaxAmount")!!
                            hasmap["delivery_charge"] = intent.getStringExtra("delivery_charge")!!
                            hasmap["ordered_date"] =intent.getStringExtra("ordered_date")!!
                            hasmap["order_type"] =intent.getStringExtra("order_type")!!
                            hasmap["order_notes"] =intent.getStringExtra("order_notes")!!
                            hasmap["order_from "] ="android"
                            if(intent.getStringExtra("getAddress").equals("no")){
                                hasmap["address"] =""
                                hasmap["lat"] =""
                                hasmap["lang"] =""
                                hasmap["building"] =""
                                hasmap["landmark"] =""
                                hasmap["pincode"] = ""
                            }else{
                                hasmap["address"] = intent.getStringExtra("getAddress")!!
                                hasmap["lat"] = intent.getStringExtra("lat")!!
                                hasmap["lang"] = intent.getStringExtra("lon")!!
                                hasmap["building"] = intent.getStringExtra("building")!!
                                hasmap["landmark"] = intent.getStringExtra("landmark")!!
                                hasmap["pincode"] = intent.getStringExtra("pincode")!!
                            }
                            callApiOrder(hasmap)
                        } else {
                            alertErrorOrValidationDialog(
                                this@OrderPaymentActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    } else{
                        showErrorFullMsg(
                            this@OrderPaymentActivity,
                            resources.getString(R.string.payment_amount_issue)
                        )
                    }
                }

            } else if(strGetData == "COD"){
                if (isCheckNetwork(this@OrderPaymentActivity)) {
                    showLoadingProgress(this@OrderPaymentActivity)
                    val hasmap = HashMap<String, String>()
                    hasmap["user_id"] = getStringPref(this@OrderPaymentActivity, userId)!!
                    hasmap["order_total"] = intent.getStringExtra("getAmount")!!
                    hasmap["razorpay_payment_id"] = ""
                    hasmap["payment_type"] = "0"
                    hasmap["promocode"] = intent.getStringExtra("promocode")!!
                    hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
                    hasmap["discount_pr"] = intent.getStringExtra("discount_pr")!!
                    hasmap["tax"] = intent.getStringExtra("getTax")!!
                    hasmap["tax_amount"] = intent.getStringExtra("getTaxAmount")!!
                    hasmap["delivery_charge"] = intent.getStringExtra("delivery_charge")!!
                    hasmap["ordered_date"] =intent.getStringExtra("ordered_date")!!
                    hasmap["order_type"] =intent.getStringExtra("order_type")!!
                    hasmap["order_notes"] =intent.getStringExtra("order_notes")!!
                    hasmap["order_from "] ="android"
                    if(intent.getStringExtra("getAddress").equals("no")){
                        hasmap["address"] =""
                        hasmap["lat"] =""
                        hasmap["lang"] =""
                        hasmap["building"] =""
                        hasmap["landmark"] =""
                        hasmap["pincode"] = ""
                    }else{
                        hasmap["address"] = intent.getStringExtra("getAddress")!!
                        hasmap["lat"] = intent.getStringExtra("lat")!!
                        hasmap["lang"] = intent.getStringExtra("lon")!!
                        hasmap["building"] = intent.getStringExtra("building")!!
                        hasmap["landmark"] = intent.getStringExtra("landmark")!!
                        hasmap["pincode"] = intent.getStringExtra("pincode")!!
                    }
                    callApiOrder(hasmap)
                } else {
                    alertErrorOrValidationDialog(
                            this@OrderPaymentActivity,
                            resources.getString(R.string.no_internet)
                    )
                }
            }else  if(strGetData == "RazorPay"){
                if (isCheckNetwork(this@OrderPaymentActivity)) {
                    showLoadingProgress(this@OrderPaymentActivity)
                    startPayment()
                }else{
                    alertErrorOrValidationDialog(
                            this@OrderPaymentActivity,
                            resources.getString(R.string.no_internet)
                    )
                }
            }else if(strGetData == "Stripe"){
                if (isCheckNetwork(this@OrderPaymentActivity)) {
                    successfulStripeDialog(this@OrderPaymentActivity)
                }else{
                    alertErrorOrValidationDialog(
                            this@OrderPaymentActivity,
                            resources.getString(R.string.no_internet)
                    )
                }
            }else if(strGetData == ""){
                showErrorFullMsg(
                        this@OrderPaymentActivity,
                        resources.getString(R.string.payment_option_error)
                )
            }
        }

    }

    override fun onBackPressed() {
        finish()
    }


    private fun callApiPayment() {
        showLoadingProgress(this@OrderPaymentActivity)
        val map = HashMap<String, String>()
        map.put("user_id", getStringPref(this@OrderPaymentActivity, userId)!!)
        val call = ApiClient.getClient.getPaymentType(map)
        call.enqueue(object : Callback<PaymentListResponce> {
            override fun onResponse(
                    call: Call<PaymentListResponce>,
                    response: Response<PaymentListResponce>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: PaymentListResponce = response.body()!!
                    if (restResponce.status == 1) {
                        val listPaymentItemModel=PaymentItemModel()
                        listPaymentItemModel.isSelect=false
                        listPaymentItemModel.environment=0
                        listPaymentItemModel.livePublicKey=""
                        listPaymentItemModel.testPublicKey=""
                        listPaymentItemModel.paymentName="Wallet"
                        listPaymentItemModel.wallet_amount=restResponce.walletamount
                        listData.add(listPaymentItemModel)
                        if (restResponce.data!!.size > 0) {
                            listData.addAll(restResponce.data)
                        }
                        setPaymentItemAdaptor(listData)
                    }
                } else {
                    dismissLoadingProgress()
                    rvCartFood.visibility = View.GONE
                    tvNoDataFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<PaymentListResponce>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                        this@OrderPaymentActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun startPayment() {
        val activity: Activity = this
        val co = Checkout()
        try {
            co.setKeyID(strRezorPayKey)
            val amount=intent.getStringExtra("getAmount")!!.toDouble()*100
            val options = JSONObject()
            options.put("name", getStringPref(this@OrderPaymentActivity, userName))
            options.put("description", "Order Payment")
            options.put("image", getStringPref(this@OrderPaymentActivity, userProfile))
            options.put("currency", "INR")
            options.put("amount", amount.toString())
            val prefill = JSONObject()
            prefill.put("email", getStringPref(this@OrderPaymentActivity, userEmail))
            prefill.put("contact", getStringPref(this@OrderPaymentActivity, userMobile))
            options.put("prefill", prefill)
            val theme = JSONObject()
            theme.put("color", "#263238")
            options.put("theme", theme)
            co.open(activity, options)
        }catch (e: Exception){
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            Toast.makeText(this, "Payment failed $errorCode \n $response", Toast.LENGTH_LONG).show()
            dismissLoadingProgress()
        }catch (e: Exception){
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try{
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] = getStringPref(this@OrderPaymentActivity, userId)!!
            hasmap["order_total"] = intent.getStringExtra("getAmount")!!
            hasmap["razorpay_payment_id"] = razorpayPaymentId!!
            hasmap["payment_type"] = "1"
            hasmap["promocode"] = intent.getStringExtra("promocode")!!
            hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
            hasmap["discount_pr"] = intent.getStringExtra("discount_pr")!!
            hasmap["tax"] = intent.getStringExtra("getTax")!!
            hasmap["tax_amount"] = intent.getStringExtra("getTaxAmount")!!
            hasmap["delivery_charge"] = intent.getStringExtra("delivery_charge")!!
            hasmap["order_type"] = intent.getStringExtra("order_type")!!
            hasmap["order_notes"] = intent.getStringExtra("order_notes")!!
            hasmap["order_from "] = "android"
            hasmap["ordered_date"] =intent.getStringExtra("ordered_date")!!
            if(intent.getStringExtra("getAddress").equals("no")){
               hasmap["address"] =""
               hasmap["lat"] =""
               hasmap["lang"] =""
               hasmap["building"] =""
               hasmap["landmark"] =""
               hasmap["pincode"] = ""
            }else{
               hasmap["address"] = intent.getStringExtra("getAddress")!!
               hasmap["lat"] = intent.getStringExtra("lat")!!
               hasmap["lang"] = intent.getStringExtra("lon")!!
               hasmap["building"] = intent.getStringExtra("building")!!
               hasmap["landmark"] = intent.getStringExtra("landmark")!!
               hasmap["pincode"] = intent.getStringExtra("pincode")!!
            }
            callApiOrder(hasmap)
        }catch (e: Exception){
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    private fun callApiOrder(map: HashMap<String, String>){
        val call = ApiClient.getClient.setOrderPayment(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        dismissLoadingProgress()
                        openActivity(PaymentsuccessfulActivity::class.java)
                        finish()
                    } else if (restResponse.status == 1) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                                this@OrderPaymentActivity,
                                restResponse.message
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                            this@OrderPaymentActivity,
                            error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                        this@OrderPaymentActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    @SuppressLint("InflateParams")
    fun successfulDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = mView.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                startActivity(Intent(this@OrderPaymentActivity, DashboardActivity::class.java).putExtra("pos", "4"))
                finish()
                finishAffinity()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun successfulStripeDialog(act: Activity) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_stripe_view, null, false)
            val cvStripe: CardMultilineWidget = m_view.findViewById(R.id.cvStripe)
            val tvOk: TextView = m_view.findViewById(R.id.tvOk)
            val ivCancle: ImageView = m_view.findViewById(R.id.ivCancle)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                if(cvStripe.card!=null){
                    startStripPayment(cvStripe.card!!)
                }
            }
            ivCancle.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    fun startStripPayment(card: Card){
        if (!card.validateCard()) {
            Toast.makeText(applicationContext, "Invalid card", Toast.LENGTH_SHORT).show()
        } else {
            newStripeDataController!!.CreateToken(card, this@OrderPaymentActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@OrderPaymentActivity, false)
    }

    override fun onstart() {
        showLoadingProgress(this@OrderPaymentActivity)
    }

    override fun success(token: Token?) {
        try{
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] = getStringPref(this@OrderPaymentActivity, userId)!!
            hasmap["order_total"]=intent.getStringExtra("getAmount")!!
            hasmap["stripeToken"]=token!!.id
            hasmap["stripeEmail"]=getStringPref(this@OrderPaymentActivity, userEmail)!!
            hasmap["payment_type"]="2"
            hasmap["promocode"] = intent.getStringExtra("promocode")!!
            hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
            hasmap["discount_pr"] = intent.getStringExtra("discount_pr")!!
            hasmap["tax"] = intent.getStringExtra("getTax")!!
            hasmap["tax_amount"] = intent.getStringExtra("getTaxAmount")!!
            hasmap["delivery_charge"] = intent.getStringExtra("delivery_charge")!!
            hasmap["order_type"] = intent.getStringExtra("order_type")!!
            hasmap["order_notes"] = intent.getStringExtra("order_notes")!!
            hasmap["ordered_date"] =intent.getStringExtra("ordered_date")!!
            hasmap["order_from "] = "android"
            if(intent.getStringExtra("getAddress").equals("no")){
                hasmap["address"] =""
                hasmap["lat"] =""
                hasmap["lang"] =""
                hasmap["building"] =""
                hasmap["landmark"] =""
                hasmap["pincode"] = ""
            }else{
                hasmap["address"] = intent.getStringExtra("getAddress")!!
                hasmap["lat"] = intent.getStringExtra("lat")!!
                hasmap["lang"] = intent.getStringExtra("lon")!!
                hasmap["building"] = intent.getStringExtra("building")!!
                hasmap["landmark"] = intent.getStringExtra("landmark")!!
                hasmap["pincode"] = intent.getStringExtra("pincode")!!
            }
            callApiOrder(hasmap)
        }catch (e: Exception){
            Common.dismissLoadingProgress()
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    fun setPaymentItemAdaptor(paymentItemList: ArrayList<PaymentItemModel>) {
        val orderHistoryAdapter = object : BaseAdaptor<PaymentItemModel>(
                this@OrderPaymentActivity,
                paymentItemList
        ) {
            @SuppressLint("SetTextI18n")
            override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: PaymentItemModel,
                    position: Int
            ) {
               if(paymentItemList[position].isSelect){
                   holder!!.itemView.llPaymentView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
                   holder!!.itemView.tvPaymentName.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
               }else{
                   holder!!.itemView.llPaymentView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
                   holder!!.itemView.tvPaymentName.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
               }
               if(paymentItemList[position].paymentName=="Wallet"){
                   holder.itemView.ivPayment.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_wallet, null)!!)
                   holder.itemView.tvPaymentName.text="My Wallet ("+Common.getCurrancy(this@OrderPaymentActivity)+paymentItemList[position].wallet_amount+")"
               } else if(paymentItemList[position].paymentName=="COD"){
                   holder.itemView.ivPayment.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_cod, null)!!)
                   holder.itemView.tvPaymentName.text=resources.getString(R.string.cash_payment)
               }else if(paymentItemList[position].paymentName=="RazorPay"){
                   if(paymentItemList[position].environment==1){
                       strRezorPayKey=paymentItemList[position].testPublicKey!!
                   }else{
                       strRezorPayKey=paymentItemList[position].livePublicKey!!
                   }
                   holder.itemView.ivPayment.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_online, null)!!)
                   holder.itemView.tvPaymentName.text=resources.getString(R.string.razorpay_payment)
               }else if(paymentItemList[position].paymentName=="Stripe"){
                   if(paymentItemList[position].environment==1){
                       newStripeDataController=NewStripeDataController(this@OrderPaymentActivity,paymentItemList[position].testPublicKey)
                   }else{
                       newStripeDataController=NewStripeDataController(this@OrderPaymentActivity,paymentItemList[position].livePublicKey)
                   }
                   holder.itemView.ivPayment.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_stripe, null)!!)
                   holder.itemView.tvPaymentName.text=resources.getString(R.string.stripe_payment)
               }


               holder.itemView.setOnClickListener {
                  for(i in 0 until paymentItemList.size){
                      paymentItemList[i].isSelect=false
                  }
                  paymentItemList[position].isSelect=true
                  strGetData=paymentItemList[position].paymentName!!
                  notifyDataSetChanged()
               }

            }
            override fun setItemLayout(): Int {
                return R.layout.row_payment
            }


        }
        rvPayment.apply {
            adapter = orderHistoryAdapter
            layoutManager = LinearLayoutManager(this@OrderPaymentActivity)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
        }

    }

    override fun failer(error: Exception?) {
        dismissLoadingProgress()
    }
}