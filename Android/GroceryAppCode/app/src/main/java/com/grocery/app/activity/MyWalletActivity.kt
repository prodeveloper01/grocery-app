package com.grocery.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.ListResopone
import com.grocery.app.base.BaseActivity
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.model.WalletModel
import com.grocery.app.utils.Common
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_mywallet.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class MyWalletActivity : BaseActivity() {
    var transactionHistoryList = ArrayList<WalletModel>()
    var transactionAdaptor: BaseAdaptor<WalletModel>? = null
    override fun setLayout(): Int = R.layout.activity_mywallet

    override fun initView() {
        Common.getCurrentLanguage(this@MyWalletActivity, false)
        transactionHistoryList = ArrayList()
        setFoodCategoryAdaptor(transactionHistoryList)

        ivBack.setOnClickListener {
            finish()
        }

        if (SharePreference.getStringPref(this@MyWalletActivity, SharePreference.SELECTED_LANGUAGE)
                .equals(
                    resources.getString(
                        R.string.language_hindi
                    )
                )
        ) {
            ivBack?.rotation = 180F

        } else {
            ivBack?.rotation = 0F

        }
        ivAddMoney?.setOnClickListener {
            startActivityForResult(Intent(this@MyWalletActivity, AddMoneyActivity::class.java), 500)
        }

        if (Common.isCheckNetwork(this@MyWalletActivity)) {
            callApiOrderHistory()
        } else {
            Common.alertErrorOrValidationDialog(
                this@MyWalletActivity,
                resources.getString(R.string.no_internet)
            )
        }



        swiperefresh.setOnRefreshListener { // Your code to refresh the list here.
            if (Common.isCheckNetwork(this@MyWalletActivity)) {
                swiperefresh.isRefreshing = false
                transactionHistoryList.clear()
                callApiOrderHistory()
            } else {
                Common.alertErrorOrValidationDialog(
                    this@MyWalletActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }

    private fun callApiOrderHistory() {
        Common.showLoadingProgress(this@MyWalletActivity)
        val map = HashMap<String, String>()
        map["user_id"] = SharePreference.getStringPref(
            this@MyWalletActivity,
            SharePreference.userId
        )!!
        val call = ApiClient.getClient.getWallet(map)
        call.enqueue(object : Callback<ListResopone<WalletModel>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ListResopone<WalletModel>>,
                response: Response<ListResopone<WalletModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResopone<WalletModel> = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        tvNoDataFound.visibility = View.GONE
                        rvTransactionHistory.visibility = View.VISIBLE
                        val orderHistoryResponse: ArrayList<WalletModel> = restResponce.data!!
                        if (orderHistoryResponse.isNullOrEmpty()) {
                            tvNoDataFound.visibility = View.VISIBLE
                            rvTransactionHistory.visibility = View.GONE
                        } else {
                            if (orderHistoryResponse.size > 0) {
                                tvNoDataFound.visibility = View.GONE
                                rvTransactionHistory.visibility = View.VISIBLE
                                transactionHistoryList.addAll(orderHistoryResponse)
                                transactionAdaptor!!.notifyDataSetChanged()
                            } else {
                                tvNoDataFound.visibility = View.VISIBLE
                                rvTransactionHistory.visibility = View.GONE
                            }

                        }
                        if (restResponce.walletamount == "" || restResponce.walletamount == null) {
                            tvPrice.text = Common.getCurrancy(this@MyWalletActivity) + "00.00"
                        } else {
                            /*      val decim = DecimalFormat("#,###.##")
                                  tvPrice.text = decim.format(restResponce.walletamount)*/
                            /*  tvPrice.text =
                                  Common.getCurrancy(this@MyWalletActivity) + String.format(
                                      Locale.US,
                                      "%,.2f",
                                      restResponce.walletamount
                                  )*/
//                            tvPrice.text = restResponce.walletamount
                            tvPrice.text = Common.getCurrancy(this@MyWalletActivity) + Common.getFormattedNumber(restResponce.walletamount!!)
//                                Common.getFormattedNumber()
                        }
                    } else if (restResponce.status == 0) {
                        tvNoDataFound.visibility = View.GONE
                        rvTransactionHistory.visibility = View.VISIBLE
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@MyWalletActivity,
                            restResponce.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ListResopone<WalletModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@MyWalletActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun setFoodCategoryAdaptor(walletHistoryList: ArrayList<WalletModel>) {
        transactionAdaptor = object : BaseAdaptor<WalletModel>(
            this@MyWalletActivity,
            walletHistoryList
        ) {
            @SuppressLint("SetTextI18n", "NewApi", "UseCompatLoadingForDrawables")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: WalletModel,
                position: Int
            ) {
                val tvOrderNumber: TextView = holder!!.itemView.findViewById(R.id.tvOrderNumber)
                val tvPrice: TextView = holder.itemView.findViewById(R.id.tvOrderPrice)
                val tvOrderStatus: TextView = holder.itemView.findViewById(R.id.tvOrderStatus)
                val tvOrderDate: TextView = holder.itemView.findViewById(R.id.tvOrderDate)
                val ivTrading: ImageView = holder.itemView.findViewById(R.id.ivTrading)

                if (walletHistoryList.get(position).transactionType == "2" || walletHistoryList.get(
                        position
                    ).transactionType == "1"
                ) {
                    tvOrderNumber.text =
                        "Order id : " + walletHistoryList.get(position).orderNumber.toString()
                    if (walletHistoryList.get(position).transactionType == "1") {
                        tvOrderStatus.text = resources.getString(R.string.order_cancelled)
                        tvOrderStatus.setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.status1,
                                null
                            )
                        )
                        tvPrice.text = Common.getCurrancy(this@MyWalletActivity) + String.format(
                            Locale.US,
                            "%,.2f",
                            walletHistoryList[position].wallet!!.toDouble()
                        )
                        tvPrice.setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorPrimary,
                                null
                            )
                        )
                        ivTrading.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_trgreen,
                                null
                            )
                        )
                    } else if (walletHistoryList.get(position).transactionType == "2") {
                        tvOrderStatus.text =resources.getString(R.string.order_confirmed)
                        tvOrderStatus.setTextColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorPrimary,
                                null
                            )
                        )
                        tvPrice.text =
                            "-" + Common.getCurrancy(this@MyWalletActivity) + String.format(
                                Locale.US,
                                "%,.2f",
                                walletHistoryList[position].wallet!!.toDouble()
                            )
                        tvPrice.setTextColor(ResourcesCompat.getColor(resources, R.color.red, null))
                        ivTrading.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_trred,
                                null
                            )
                        )
                    }
                } else if (walletHistoryList[position].transactionType == "4") {
                    tvOrderNumber.text =resources.getString(R.string.wallet_recharge)

                    if (walletHistoryList[position].order_type == "3") {
                        tvOrderStatus.text = resources.getString(R.string.razorpay)

                    } else if (walletHistoryList[position].order_type == "4") {
                        tvOrderStatus.text = resources.getString(R.string.stripe)
                    }
                    val formattedPrice =
                        Common.getFormattedNumber(walletHistoryList[position].wallet!!)

                    tvOrderStatus.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.status4,
                            null
                        )
                    )
                    ivTrading.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_trgreen,
                            null
                        )
                    )
                    /*     tvPrice.text = Common.getCurrancy(this@MyWalletActivity) + String.format(
                             Locale.US,
                             "%,.2f",
                             walletHistoryList[position].wallet!!.toDouble()
                         )*/
                    tvPrice.text = Common.getCurrancy(this@MyWalletActivity) + formattedPrice
                    tvPrice.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.status4,
                            null
                        )
                    )

                } else if (walletHistoryList[position].transactionType == "5") {
                    tvOrderNumber.text =resources.getString(R.string.wallet_recharge)
                        tvOrderStatus.text = resources.getString(R.string.admin_recharge)
                    val formattedPrice =
                        Common.getFormattedNumber(walletHistoryList[position].wallet!!)

                    tvOrderStatus.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.status4,
                            null
                        )
                    )
                    ivTrading.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_trgreen,
                            null
                        )
                    )
                    /*     tvPrice.text = Common.getCurrancy(this@MyWalletActivity) + String.format(
                             Locale.US,
                             "%,.2f",
                             walletHistoryList[position].wallet!!.toDouble()
                         )*/
                    tvPrice.text = Common.getCurrancy(this@MyWalletActivity) + formattedPrice
                    tvPrice.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.status4,
                            null
                        )
                    )

                } else if (walletHistoryList[position].transactionType == "6") {
                    tvOrderNumber.text =resources.getString(R.string.wallet_deduction)
                        tvOrderStatus.text = resources.getString(R.string.admin_recharge)
                    val formattedPrice =
                        Common.getFormattedNumber(walletHistoryList[position].wallet!!)

                    tvOrderStatus.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.status1,
                            null
                        )
                    )
                    ivTrading.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_trred,
                            null
                        )
                    )
                    /*     tvPrice.text = Common.getCurrancy(this@MyWalletActivity) + String.format(
                             Locale.US,
                             "%,.2f",
                             walletHistoryList[position].wallet!!.toDouble()
                         )*/
                    tvPrice.text = "-" + Common.getCurrancy(this@MyWalletActivity) + formattedPrice
                    tvPrice.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.status1,
                            null
                        )
                    )

                }

                else {

                    if (walletHistoryList.get(position).username != null) {
                        tvOrderNumber.text = walletHistoryList.get(position).username.toString()
                    } else {
                        tvOrderNumber.text = ""
                    }
                    ivTrading.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_trgreen,
                            null
                        )
                    )
                    tvOrderStatus.text = resources.getString(R.string.ref_earning)
                    tvOrderStatus.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimary,
                            null
                        )
                    )
                    tvPrice.text = Common.getCurrancy(this@MyWalletActivity) + String.format(
                        Locale.US,
                        "%,.2f",
                        walletHistoryList[position].wallet!!.toDouble()
                    )
                    tvPrice.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimary,
                            null
                        )
                    )
                }

                if (walletHistoryList[position].date == null) {
                    tvOrderDate.text = ""
                } else {
                    tvOrderDate.text = Common.getDate(walletHistoryList[position].date!!)
                }

            }

            override fun setItemLayout(): Int {
                return R.layout.row_transactionhistory
            }

        }

        rvTransactionHistory.apply {
            adapter = transactionAdaptor
            layoutManager = LinearLayoutManager(this@MyWalletActivity)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 500) {

            if (Common.isCheckNetwork(this@MyWalletActivity)) {
                transactionHistoryList.clear()
                callApiOrderHistory()
            } else {
                Common.alertErrorOrValidationDialog(
                    this@MyWalletActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@MyWalletActivity, false)
    }
}