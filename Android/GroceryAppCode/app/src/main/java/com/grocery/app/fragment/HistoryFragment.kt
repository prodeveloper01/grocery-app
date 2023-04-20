package com.grocery.app.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.activity.OrderDetailActivity
import com.grocery.app.api.ApiClient
import com.grocery.app.api.ListResopone
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.base.BaseFragmnet
import com.grocery.app.model.OrderHistoryModel
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.getCurrancy
import com.grocery.app.utils.Common.getCurrentLanguage
import com.grocery.app.utils.Common.getDate
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.userId
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_history.tvNoDataFound
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HistoryFragment : BaseFragmnet(){
    var orderHistoryList=ArrayList<OrderHistoryModel>()
    var orderHistoryAdaptor:BaseAdaptor<OrderHistoryModel>?=null
    override fun setView(): Int {
        return R.layout.fragment_history
    }

    override fun init(view: View) {
        getCurrentLanguage(activity!!, false)
        setFoodCategoryAdaptor(orderHistoryList)

        if (isCheckNetwork(activity!!)) {
            callApiOrderHistory()
        } else {
            alertErrorOrValidationDialog(activity!!,resources.getString(R.string.no_internet))
        }

        swiperefresh.setOnRefreshListener { // Your code to refresh the list here.
            if (isCheckNetwork(activity!!)) {
                swiperefresh.isRefreshing=false
                callApiOrderHistory()
            } else {
                alertErrorOrValidationDialog(activity!!,resources.getString(R.string.no_internet))
            }
        }
    }

    private fun callApiOrderHistory() {
        showLoadingProgress(activity!!)
        val map = HashMap<String, String>()
        map["user_id"] = SharePreference.getStringPref(activity!!, userId)!!
        val call = ApiClient.getClient.getOrderHistory(map)
        call.enqueue(object : Callback<ListResopone<OrderHistoryModel>> {
            override fun onResponse(
                call: Call<ListResopone<OrderHistoryModel>>,
                response: Response<ListResopone<OrderHistoryModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResopone<OrderHistoryModel> = response.body()!!
                    if (restResponce.status==1) {
                        dismissLoadingProgress()
                        tvNoDataFound.visibility=View.GONE
                        rvOrderHistory.visibility=View.VISIBLE
                        val orderHistoryResponse: ArrayList<OrderHistoryModel> = restResponce.data!!
                        if(orderHistoryResponse.isNullOrEmpty()){
                            tvNoDataFound.visibility=View.VISIBLE
                            rvOrderHistory.visibility=View.GONE
                        }else{
                            if(orderHistoryResponse.size>0){
                                orderHistoryList.clear()
                                tvNoDataFound.visibility=View.GONE
                                rvOrderHistory.visibility=View.VISIBLE
                                for (i in 0 until orderHistoryResponse.size) {
                                    val orderHistoryModel = orderHistoryResponse[i]
                                    orderHistoryList.add(orderHistoryModel)
                                }
                                orderHistoryAdaptor!!.notifyDataSetChanged()
                            }else{
                                tvNoDataFound.visibility=View.VISIBLE
                                rvOrderHistory.visibility=View.GONE
                            }

                        }
                    } else if (restResponce.status==0) {
                        tvNoDataFound.visibility=View.GONE
                        rvOrderHistory.visibility=View.VISIBLE
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(activity!!, restResponce.message)
                    }
                }
            }

            override fun onFailure(call: Call<ListResopone<OrderHistoryModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(activity!!,resources.getString(R.string.error_msg))
            }
        })
    }

    fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderHistoryModel>) {
        orderHistoryAdaptor= object : BaseAdaptor<OrderHistoryModel>(activity!!, orderHistoryList) {
                @SuppressLint("SetTextI18n", "NewApi", "UseCompatLoadingForDrawables")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: OrderHistoryModel,
                    position: Int
                ) {
                    val tvOrderNumber: TextView = holder!!.itemView.findViewById(R.id.tvOrderNumber)
                    val tvPrice: TextView = holder.itemView.findViewById(R.id.tvOrderPrice)
                    val tvOrderStatus: TextView = holder.itemView.findViewById(R.id.tvOrderStatus)
                    val tvPaymentType: TextView = holder.itemView.findViewById(R.id.tvPaymentType)
                    val tvOrderDate: TextView = holder.itemView.findViewById(R.id.tvOrderDate)
                    val tvOrderType: TextView = holder.itemView.findViewById(R.id.tvOrderType)
                    val rlOrder: RelativeLayout = holder.itemView.findViewById(R.id.rlBottom)

                    tvOrderNumber.text = orderHistoryList.get(position).orderNumber.toString()
                    tvPrice.text = getCurrancy(activity!!)+String.format(Locale.US,"%,.2f",orderHistoryList[position].totalPrice!!.toDouble())

                    if(orderHistoryList.get(position).orderType==2){
                        tvOrderType.text="Pickup"
                    }else if(orderHistoryList.get(position).orderType==1){
                        tvOrderType.text="Delivery"
                    }

                    if(orderHistoryList[position].ordered_date==null){
                        tvOrderDate.text=""
                    }else{
                        tvOrderDate.text=getDate(orderHistoryList[position].ordered_date!!)
                    }

                    if(orderHistoryList.get(position).paymentType!!.toInt()==0){
                        tvPaymentType.text = resources.getString(R.string.cash_payment)
                    }else if(orderHistoryList.get(position).paymentType!!.toInt()==1){
                        tvPaymentType.text = resources.getString(R.string.razorpay_payment)
                    }else if(orderHistoryList.get(position).paymentType!!.toInt()==2){
                        tvPaymentType.text = resources.getString(R.string.stripe_payment)
                    }else{
                        tvPaymentType.text = resources.getString(R.string.wallet_payment)
                    }

                    if(orderHistoryList.get(position).status==5){
                         rlOrder.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.status1,null))
                         tvOrderStatus.text=resources.getString(R.string.order_cancelled_you)
                    }else if(orderHistoryList.get(position).status==6){
                         rlOrder.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.status1,null))
                         tvOrderStatus.text=resources.getString(R.string.order_cancelled_admin)
                    }else{
                        if(orderHistoryList.get(position).orderType==1){
                            if(orderHistoryList.get(position).status==1){
                                rlOrder.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.status1,null))
                                tvOrderStatus.text=resources.getString(R.string.order_place)
                            }else if(orderHistoryList.get(position).status==2) {
                                rlOrder.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.status2,null))
                                tvOrderStatus.text=resources.getString(R.string.order_ready)
                            }else if(orderHistoryList.get(position).status==3){
                                rlOrder.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.status3,null))
                                tvOrderStatus.text=resources.getString(R.string.on_the_way)
                            }else if(orderHistoryList.get(position).status==4){
                                rlOrder.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.status4,null))
                                tvOrderStatus.text=resources.getString(R.string.order_delivered)
                            }
                        }else{
                            if(orderHistoryList[position].status==1){
                                rlOrder.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.status1,null))
                                tvOrderStatus.text=resources.getString(R.string.order_place)
                            }else if(orderHistoryList[position].status==2) {
                                rlOrder.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.status2,null))
                                tvOrderStatus.text=resources.getString(R.string.order_ready)
                            }else if(orderHistoryList[position].status==4){
                                rlOrder.backgroundTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.status3,null))
                                tvOrderStatus.text=resources.getString(R.string.order_delivered)
                            }
                        }
                    }



                    holder.itemView.setOnClickListener {
                        startActivity(Intent(activity!!,OrderDetailActivity::class.java).putExtra("order_id",orderHistoryList[position].id.toString()).putExtra("order_status",orderHistoryList[position].status.toString()))
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_orderdelivery
                }

            }
        rvOrderHistory.adapter = orderHistoryAdaptor
        rvOrderHistory.layoutManager = LinearLayoutManager(activity!!)
        rvOrderHistory.itemAnimator = DefaultItemAnimator()
        rvOrderHistory.isNestedScrollingEnabled = true
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(activity!!, false)
        if(Common.isCancelledOrder){
            if (isCheckNetwork(activity!!)) {
                Common.isCancelledOrder=false
                orderHistoryList.clear()
                callApiOrderHistory()
            } else {
                alertErrorOrValidationDialog(activity!!,resources.getString(R.string.no_internet))
            }
        }
    }
}