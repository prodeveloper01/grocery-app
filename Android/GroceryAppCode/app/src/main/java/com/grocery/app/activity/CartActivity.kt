package com.grocery.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.model.CartModel
import com.grocery.app.model.ListResponse
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.getLog
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.Common.showSuccessFullMsg
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.getStringPref
import com.grocery.app.utils.SharePreference.Companion.isMiniMumQty
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.ivBack
import kotlinx.android.synthetic.main.activity_cart.tvNoDataFound
import kotlinx.android.synthetic.main.row_cart.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CartActivity : BaseActivity(){
    var cartItemAdapter:BaseAdaptor<CartModel>?=null
    var cartItem:ArrayList<CartModel>?=ArrayList()
    override fun setLayout(): Int {
        return R.layout.activity_cart
    }

    override fun initView() {
        Common.getCurrentLanguage(this@CartActivity, false)
        tvCheckout.visibility = View.GONE

        if(SharePreference.getStringPref(this@CartActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        if (isCheckNetwork(this@CartActivity)) {
            callApiCart(false)
        } else {
            alertErrorOrValidationDialog(this@CartActivity, resources.getString(R.string.no_internet))
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }

        ivHome.setOnClickListener {
            val intent=Intent(this@CartActivity,DashboardActivity::class.java).putExtra("pos","1")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        tvCheckout.setOnClickListener {
            if(isCheckNetwork(this@CartActivity)){
               // callApiIsOpen()
                startActivity(Intent(this@CartActivity,OrderSelectAddressActivity::class.java))
            }else{
                alertErrorOrValidationDialog(this@CartActivity,resources.getString(R.string.no_internet))
            }
        }
    }


    private fun callApiCart(isQty: Boolean) {
        if(!isQty){
            showLoadingProgress(this@CartActivity)
        }
        val map = HashMap<String, String>()
        map["user_id"] = getStringPref(this@CartActivity, SharePreference.userId)!!
        val call = ApiClient.getClient.getCartItem(map)
        call.enqueue(object : Callback<ListResponse<CartModel>> {
            override fun onResponse(
                call: Call<ListResponse<CartModel>>,
                response: Response<ListResponse<CartModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: ListResponse<CartModel> = response.body()!!
                    if (restResponce.status==1) {
                        if (restResponce.data!!.size > 0) {
                            rvCartFood.visibility = View.VISIBLE
                            tvNoDataFound.visibility = View.GONE
                            tvCheckout.visibility = View.VISIBLE
                            cartItem=restResponce.data
                            setFoodCartAdaptor(cartItem!!)

                         //   mGpsStatusDetector = GpsStatusDetector(this@CartActivity)
                          //  mGpsStatusDetector!!.checkGpsStatus()
                        } else {
                            rvCartFood.visibility = View.GONE
                            tvNoDataFound.visibility = View.VISIBLE
                            tvCheckout.visibility = View.GONE
                        }
                    }
                }else{
                    dismissLoadingProgress()
                    rvCartFood.visibility = View.GONE
                    tvNoDataFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ListResponse<CartModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CartActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    @SuppressLint("ResourceType","NewApi","SetTextI18n")
    private fun setFoodCartAdaptor(cartItemList: ArrayList<CartModel>) {
        cartItemAdapter = object : BaseAdaptor<CartModel>(this@CartActivity, cartItem!!) {
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: CartModel,
                position: Int
            ) {
                holder!!.itemView.tvFoodName.text = cartItem!![position].itemName
                val price=cartItem!![position].qty!!*cartItem!![position].price!!.toDouble()
                holder.itemView.tvFoodPrice.text =Common.getCurrancy(this@CartActivity)+String.format(Locale.US,"%,.2f",price)
                holder.itemView.tvFoodQty.text = cartItem!![position].qty.toString()
                Glide.with(this@CartActivity).load(cartItem!![position].itemimage!!.image).into( holder.itemView.ivFoodCart)

                holder.itemView.tvWeight.text= cartItem!![position].weight

                val rvFood: RelativeLayout = holder.itemView.findViewById(R.id.rvFood)

                when (position%5) {
                    0 -> {
                        rvFood.backgroundTintList=ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color1,
                                null
                            )
                        )
                    }
                    1 -> {
                        rvFood.backgroundTintList=ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color2,
                                null
                            )
                        )
                    }
                    2 -> {
                        rvFood.backgroundTintList=ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color3,
                                null
                            )
                        )
                    }
                    3 -> {
                        rvFood.backgroundTintList=ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color4,
                                null
                            )
                        )
                    }
                    4 -> {
                        rvFood.backgroundTintList=ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.color5,
                                null
                            )
                        )
                    }
                }


                holder.itemView.ivDeleteCartItem.setOnClickListener {
                    if (isCheckNetwork(this@CartActivity)) {
                        dlgDeleteConformationDialog(this@CartActivity,resources.getString(R.string.remove_cart),
                            cartItem!![position].id.toString(),position)
                    } else {
                        alertErrorOrValidationDialog(
                            this@CartActivity,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }

                holder.itemView.ivMinus.setOnClickListener {
                    if(cartItem!![position].qty!!> 1){
                        holder.itemView.ivMinus.isClickable =true
                        getLog("Qty>>", cartItem!![position].qty.toString())
                        if (isCheckNetwork(this@CartActivity)) {
                            callApiCartQTYUpdate(cartItemList[position],false)
                        } else {
                            alertErrorOrValidationDialog(
                                this@CartActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }else{
                        holder.itemView.ivMinus.isClickable =false
                        getLog("Qty1>>", cartItem!![position].qty.toString())
                    }
                }

                holder.itemView.ivPlus.setOnClickListener {
                    if(cartItem!![position].qty!!<getStringPref(this@CartActivity,isMiniMumQty)!!.toInt()){
                        if (isCheckNetwork(this@CartActivity)) {
                            callApiCartQTYUpdate(cartItemList[position],true)
                        } else {
                            alertErrorOrValidationDialog(
                                this@CartActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }else{
                        alertErrorOrValidationDialog(
                            this@CartActivity,
                            "${resources.getString(R.string.maximum_allowed)} ${
                                getStringPref(
                                    this@CartActivity,
                                    isMiniMumQty
                                )
                            }"
                        )
                    }
                }
            }
            override fun setItemLayout(): Int {
                return R.layout.row_cart
            }
        }
        rvCartFood.adapter = cartItemAdapter
        rvCartFood.layoutManager = LinearLayoutManager(this@CartActivity)
        rvCartFood.itemAnimator = DefaultItemAnimator()
        rvCartFood.isNestedScrollingEnabled = true
    }

    private fun callApiCartQTYUpdate(cartModel: CartModel,isPlus: Boolean) {
        val qty: Int = if(isPlus){
            cartModel.qty!!+1
        }else{
            cartModel.qty!!-1
        }
        showLoadingProgress(this@CartActivity)
        val map = HashMap<String, String>()
        map["cart_id"] = cartModel.id.toString()
        map["item_id"] = cartModel.itemId.toString()
        map["qty"] = qty.toString()
        map["variation_id"] = cartModel.variation_id.toString()
        map["user_id"] = getStringPref(this@CartActivity, SharePreference.userId)!!
        val call = ApiClient.getClient.setQtyUpdate(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                dismissLoadingProgress()
                if (response.code() == 200) {
                    val restResponce: SingleResponse = response.body()!!
                    if(restResponce.status==1){
                        callApiCart(true)
                    }else{
                        alertErrorOrValidationDialog(this@CartActivity, restResponce.message)
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CartActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
    private fun callApiCartItemDelete(strCartId:String,pos:Int) {
        showLoadingProgress(this@CartActivity)
        val map = HashMap<String, String>()
        map["cart_id"] = strCartId
        val call = ApiClient.getClient.setDeleteCartItem(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: SingleResponse = response.body()!!
                    if(restResponce.status==1){
                        when {
                            intent.getStringExtra("isActivity")!! == resources.getString(R.string.is_Cart_Act_home) -> {
                                Common.isCartTrueOut=true
                            }
                            intent.getStringExtra("isActivity")!! == resources.getString(R.string.is_Cart_Act_item_Detail) -> {
                                Common.isCartTrue=true
                                Common.isCartTrueOut=true
                            }
                            intent.getStringExtra("isActivity")!! == resources.getString(R.string.is_Cart_Act_catby) -> {
                                Common.isCartCategoryOut=true
                                Common.isCartTrueOut=true
                            }
                        }

                        showSuccessFullMsg(this@CartActivity,restResponce.message!!)
                        cartItem!!.removeAt(pos)
                        cartItemAdapter!!.notifyDataSetChanged()
                        if(cartItem!!.size>0){
                            tvCheckout.visibility=View.VISIBLE
                        }else{
                            tvCheckout.visibility=View.GONE
                            rvCartFood.visibility = View.GONE
                            tvNoDataFound.visibility = View.VISIBLE
                            tvCheckout.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CartActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    @SuppressLint("InflateParams")
    fun dlgDeleteConformationDialog(act: Activity, msg: String?, strCartId: String, pos:Int) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvDesc)
            textDesc.text = msg
            val tvOk: TextView = mView.findViewById(R.id.tvYes)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                if (isCheckNetwork(this@CartActivity)) {
                    finalDialog.dismiss()
                    callApiCartItemDelete(strCartId,pos)
                } else {
                    alertErrorOrValidationDialog(
                        this@CartActivity,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
            val tvCancle: TextView = mView.findViewById(R.id.tvNo)
            tvCancle.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }



    override fun onBackPressed() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@CartActivity, false)
    }

    private fun callApiIsOpen() {
        showLoadingProgress(this@CartActivity)
        val call = ApiClient.getClient.getCheckStatus()
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    val restResponce: SingleResponse = response.body()!!
                    if (restResponce.status==1) {
                        dismissLoadingProgress()
                        startActivity(Intent(this@CartActivity,OrderSelectAddressActivity::class.java))
                    } else if (restResponce.status==0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@CartActivity,
                            restResponce.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CartActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }



    override fun onPause() {
        super.onPause()
        Common.getCurrentLanguage(this@CartActivity, false)
    }

}
