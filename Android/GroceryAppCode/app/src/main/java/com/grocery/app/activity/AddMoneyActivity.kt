package com.grocery.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.PaymentListResponce
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.model.PaymentItemModel
import com.grocery.app.utils.CallBackSuccess
import com.grocery.app.utils.Common
import com.grocery.app.utils.NewStripeDataController
import com.grocery.app.utils.SharePreference
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import com.stripe.android.view.CardMultilineWidget
import kotlinx.android.synthetic.main.activity_add_money.*
import kotlinx.android.synthetic.main.activity_add_money.ivBack
import kotlinx.android.synthetic.main.activity_add_money.rvPayment
import kotlinx.android.synthetic.main.row_payment.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

class AddMoneyActivity : BaseActivity(), PaymentResultListener, CallBackSuccess {

    private val isSelect = false
    private var orderType = 0
    var strRezorPayKey = ""
    var newStripeDataController: NewStripeDataController? = null
    var listData = ArrayList<PaymentItemModel>()
    var strGetData = ""
    var callBackSuccess: CallBackSuccess? = null

    override fun setLayout(): Int = R.layout.activity_add_money

    override fun initView() {
        callBackSuccess = this@AddMoneyActivity
        Checkout.preload(this@AddMoneyActivity)

        ivBack.setOnClickListener {
            finish()
        }

        if (SharePreference.getStringPref(this@AddMoneyActivity, SharePreference.SELECTED_LANGUAGE)
                .equals(resources.getString(R.string.language_hindi))
        ) {
            ivBack.rotation = 180F
        } else {
            ivBack.rotation = 0F
        }


        if (Common.isCheckNetwork(this@AddMoneyActivity)) {
            callApiPayment()
        } else {
            Common.alertErrorOrValidationDialog(
                this@AddMoneyActivity,
                resources.getString(R.string.no_internet)
            )
        }
        tvProceedToPayment.setOnClickListener {
            if (edAmount.text.isEmpty() || edAmount.text.toString() == ".") {
                Common.showErrorFullMsg(this@AddMoneyActivity, resources.getString(R.string.empty_amount))
            } else if (!Common.isValidAmount(edAmount.text.toString())) {
                Common.showErrorFullMsg(this@AddMoneyActivity, resources.getString(R.string.valid_amount))
            } else {
                when (strGetData) {
                    "RazorPay" -> {
                        Common.showLoadingProgress(this@AddMoneyActivity)
                        startPayment()
                    }
                    "Stripe" -> {
                        successfulStripeDialog(this@AddMoneyActivity)
                    }
                    "" -> {
                        Common.showErrorFullMsg(
                            this@AddMoneyActivity,
                            resources.getString(R.string.payment_option_error)
                        )

                    }
                }
            }


        }
    }


    private fun successfulStripeDialog(act: Activity) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_stripe_view, null, false)
            val cvStripe: CardMultilineWidget = mView.findViewById(R.id.cvStripe)
            val tvOk: TextView = mView.findViewById(R.id.tvOk)
            val ivCancle: ImageView = mView.findViewById(R.id.ivCancle)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                if (cvStripe.card != null) {
                    startStripPayment(cvStripe.card!!)
                }
            }
            ivCancle.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun startStripPayment(card: Card) {
        if (!card.validateCard()) {
            Toast.makeText(applicationContext, "Invalid card", Toast.LENGTH_SHORT).show()
        } else {
            newStripeDataController?.CreateToken(card, this@AddMoneyActivity)
        }
    }

    private fun startPayment() {
        Common.getLog("test", edAmount.text.toString())
        val activity: Activity = this
        val co = Checkout()
        try {
            co.setKeyID(strRezorPayKey)
            val amount = edAmount.text.toString().toDouble() * 100
            Common.getLog("test", amount.toString())
            val options = JSONObject()
            options.put(
                "name",
                SharePreference.getStringPref(this@AddMoneyActivity, SharePreference.userName)
            )
            options.put("description", "Wallet Recharge")
            options.put(
                "image",
                SharePreference.getStringPref(this@AddMoneyActivity, SharePreference.userProfile)
            )
            options.put("currency", "INR")
            options.put("amount", String.format(Locale.US, "%d", amount.toLong()).toString())
            val prefill = JSONObject()
            prefill.put(
                "email",
                SharePreference.getStringPref(this@AddMoneyActivity, SharePreference.userEmail)
            )
            prefill.put(
                "contact",
                SharePreference.getStringPref(this@AddMoneyActivity, SharePreference.userMobile)
            )
            options.put("prefill", prefill)
            val theme = JSONObject()
            theme.put("color", "#263238")
            options.put("theme", theme)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        val razorPayRequest = HashMap<String, String>()
        razorPayRequest["user_id"] =
            SharePreference.getStringPref(this@AddMoneyActivity, SharePreference.userId).toString()
        val roundValue = ceil(edAmount.text.toString().toDouble())

        razorPayRequest["amount"] = String.format(Locale.US, "%.2f", roundValue)
        razorPayRequest["payment_id"] = "1"
        razorPayRequest["order_type"] = "3"
        callAddMoney(razorPayRequest)

    }


    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@AddMoneyActivity, false)
    }


    private fun callApiPayment() {
        Common.showLoadingProgress(this@AddMoneyActivity)
        val map = HashMap<String, String>()
        map["user_id"] = SharePreference.getStringPref(
            this@AddMoneyActivity,
            SharePreference.userId
        )!!
        val call = ApiClient.getClient.getPaymentType(map)
        call.enqueue(object : Callback<PaymentListResponce> {
            override fun onResponse(
                call: Call<PaymentListResponce>,
                response: Response<PaymentListResponce>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponce: PaymentListResponce = response.body()!!
                    if (restResponce.status == 1) {

                        if (restResponce.data!!.size > 0) {


                            for (i in 0 until restResponce.data.size) {
                                if (restResponce.data[i].paymentName == "Stripe" || restResponce.data[i].paymentName == "RazorPay") {
                                    listData.add(restResponce.data[i])
                                }

                            }

                        }
                        setPaymentItemAdaptor(listData)
                    }
                } else {
                    Common.dismissLoadingProgress()


                }
            }

            override fun onFailure(call: Call<PaymentListResponce>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@AddMoneyActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    fun setPaymentItemAdaptor(paymentItemList: ArrayList<PaymentItemModel>) {
        val orderHistoryAdapter = object : BaseAdaptor<PaymentItemModel>(
            this@AddMoneyActivity,
            paymentItemList
        ) {
            @SuppressLint("SetTextI18n")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: PaymentItemModel,
                position: Int
            ) {
                if (paymentItemList[position].isSelect) {
                    holder!!.itemView.llPaymentView.setBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimary,
                            null
                        )
                    )

                    holder!!.itemView.tvPaymentName.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
                } else {
                    holder!!.itemView.llPaymentView.setBackgroundColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.white,
                            null
                        )
                    )
                    holder!!.itemView.tvPaymentName.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
                }
                when (paymentItemList[position].paymentName) {
                    "Wallet" -> {
                        holder.itemView.ivPayment.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_wallet,
                                null
                            )!!
                        )
                        holder.itemView.tvPaymentName.text =
                            paymentItemList[position].paymentName + " (" + Common.getCurrancy(this@AddMoneyActivity) + paymentItemList[position].wallet_amount + ")"
                    }
                    "COD" -> {
                        holder.itemView.ivPayment.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_cod,
                                null
                            )!!
                        )
                        holder.itemView.tvPaymentName.text =resources.getString(R.string.cash_payment)
                    }
                    "RazorPay" -> {
                        strRezorPayKey = if (paymentItemList[position].environment == 1) {
                            paymentItemList[position].testPublicKey!!
                        } else {
                            paymentItemList[position].livePublicKey!!
                        }
                        holder.itemView.ivPayment.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_online,
                                null
                            )!!
                        )
                        holder.itemView.tvPaymentName.text =resources.getString(R.string.razorpay_payment)
                    }
                    "Stripe" -> {
                        newStripeDataController = if (paymentItemList[position].environment == 1) {
                            NewStripeDataController(
                                this@AddMoneyActivity,
                                paymentItemList[position].testPublicKey
                            )
                        } else {
                            NewStripeDataController(
                                this@AddMoneyActivity,
                                paymentItemList[position].livePublicKey
                            )
                        }

                        holder.itemView.ivPayment.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_stripe,
                                null
                            )!!
                        )
                        holder.itemView.tvPaymentName.text = resources.getString(R.string.stripe_payment)
                    }
                }


                holder.itemView.setOnClickListener {
                    for (i in 0 until paymentItemList.size) {
                        paymentItemList[i].isSelect = false
                    }
                    paymentItemList[position].isSelect = true
                    strGetData = paymentItemList[position].paymentName!!
                    notifyDataSetChanged()
                }

            }

            override fun setItemLayout(): Int {
                return R.layout.row_payment
            }
        }
        rvPayment.apply {
            adapter = orderHistoryAdapter
            layoutManager = LinearLayoutManager(this@AddMoneyActivity)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
        }

    }

    private fun callAddMoney(map: HashMap<String, String>) {
        val call = ApiClient.getClient.addMoney(map)
        Log.e("GsonReq", Gson().toJson(map))
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        Common.dismissLoadingProgress()
                        Common.showSuccessFullMsg(
                            this@AddMoneyActivity,
                            restResponse.message.toString()
                        )


                        val intent = Intent(this@AddMoneyActivity, MyWalletActivity::class.java)
                        setResult(500, intent)
                        finish()


                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@AddMoneyActivity,
                            restResponse.message
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()

                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@AddMoneyActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try {
            Toast.makeText(this, "Payment failed $errorCode \n $response", Toast.LENGTH_LONG).show()
            Common.dismissLoadingProgress()
        } catch (e: Exception) {
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onstart() {
        Common.showLoadingProgress(this@AddMoneyActivity)
    }

    override fun success(token: Token?) {

        val stripeRequest = HashMap<String, String>()
        stripeRequest["user_id"] =
            SharePreference.getStringPref(this@AddMoneyActivity, SharePreference.userId).toString()
        val roundValue = ceil(edAmount.text.toString().toDouble())

        stripeRequest["amount"] = String.format(Locale.US, "%.2f", roundValue)
        stripeRequest["stripeToken"] = token!!.id
        stripeRequest["order_type"] = "4"
        callAddMoney(stripeRequest)
    }

    override fun failer(error: java.lang.Exception?) {
        Common.dismissLoadingProgress()
        Common.getToast(this@AddMoneyActivity, error?.message.toString())
    }


}