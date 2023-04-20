package com.grocery.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.base.BaseAdaptor
import com.grocery.app.model.ListResponse
import com.grocery.app.model.RattingModel
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_ratting.*
import kotlinx.android.synthetic.main.activity_ratting.ivBack
import kotlinx.android.synthetic.main.activity_ratting.tvNoDataFound
import kotlinx.android.synthetic.main.dlg_write_review.view.*
import kotlinx.android.synthetic.main.row_ratting.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class RattingActivity:BaseActivity() {
    var rattingList:ArrayList<RattingModel>?=ArrayList()
    var rattingAdapter: BaseAdaptor<RattingModel>?=null
    override fun setLayout(): Int {
        return R.layout.activity_ratting
    }
    override fun initView() {
        Common.getCurrentLanguage(this@RattingActivity, false)
        if(Common.isCheckNetwork(this@RattingActivity)){
            callApiRatting(false)
        }else{
            alertErrorOrValidationDialog(
                this@RattingActivity,
                resources.getString(R.string.no_internet)
            )
        }
        if(SharePreference.getStringPref(this@RattingActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        ivBack.setOnClickListener {
           finish()
        }
        ivAddWallpaper.setOnClickListener {
            if(SharePreference.getBooleanPref(this@RattingActivity, SharePreference.isLogin)){
              mWriteReviewDialog(this@RattingActivity)
            }else {
                openActivity(LoginSelectActivity::class.java)
                finish()
                finishAffinity()
            }

        }

        swiperefresh.setOnRefreshListener {
            if(Common.isCheckNetwork(this@RattingActivity)){
                swiperefresh.isRefreshing=false
                rattingList!!.clear()
                callApiRatting(false)
            }else{
                alertErrorOrValidationDialog(this@RattingActivity, resources.getString(R.string.no_internet))
            }
        }
    }

    private fun callApiRatting(isReview:Boolean) {
        if(!isReview){
            showLoadingProgress(this@RattingActivity)
        }else{
            rattingList!!.clear()
        }
        val call = ApiClient.getClient.getRetting()
        call.enqueue(object : Callback<ListResponse<RattingModel>> {
            override fun onResponse(
                call: Call<ListResponse<RattingModel>>,
                response: Response<ListResponse<RattingModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: ListResponse<RattingModel> = response.body()!!
                    if (restResponce.status==1) {
                        if (restResponce.data!!.size > 0) {
                            rvRatting.visibility= View.VISIBLE
                            tvNoDataFound.visibility= View.GONE
                            for(i in 0 until restResponce.data.size){
                                val retting=restResponce.data.get(i)
                                rattingList!!.add(retting)
                            }
                            setFoodCategoryAdaptor(rattingList!!)
                        }else{
                            rvRatting.visibility= View.GONE
                            tvNoDataFound.visibility= View.VISIBLE
                        }
                    } else if (restResponce.status==0) {
                        dismissLoadingProgress()
                        rvRatting.visibility= View.GONE
                        tvNoDataFound.visibility= View.VISIBLE
                    }
                }
            }
            override fun onFailure(call: Call<ListResponse<RattingModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@RattingActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun setFoodCategoryAdaptor(rattingList:ArrayList<RattingModel>) {
        rattingAdapter = object : BaseAdaptor<RattingModel>(this@RattingActivity, rattingList) {
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: RattingModel,
                position: Int
            ) {
                holder!!.itemView.tvRattingName.text= rattingList[position].name
                holder.itemView.tvRattingDate.text= rattingList[position].createdAt
                holder.itemView.tvRattingDiscription.text= rattingList[position].comment

                when {
                    rattingList[position].ratting.equals("1") -> {
                        holder.itemView.ivRatting.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ratting1,null))
                        holder.itemView.ivRatting.imageTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                    }
                    rattingList[position].ratting.equals("2") -> {
                        holder.itemView.ivRatting.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ratting2,null))
                        holder.itemView.ivRatting.imageTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                    }
                    rattingList[position].ratting.equals("3") -> {
                        holder.itemView.ivRatting.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ratting3,null))
                        holder.itemView.ivRatting.imageTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                    }
                    rattingList[position].ratting.equals("4") -> {
                        holder.itemView.ivRatting.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ratting4,null))
                        holder.itemView.ivRatting.imageTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                    }
                    rattingList[position].ratting.equals("5") -> {
                        holder.itemView.ivRatting.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ratting5,null))
                        holder.itemView.ivRatting.imageTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                    }
                }
            }
            override fun setItemLayout(): Int {
                return R.layout.row_ratting
            }

        }
        rvRatting.adapter = rattingAdapter
        rvRatting.layoutManager = LinearLayoutManager(this@RattingActivity)
        rvRatting.itemAnimator = DefaultItemAnimator()
        rvRatting.isNestedScrollingEnabled = true
    }


    @SuppressLint("NewApi")
    fun mWriteReviewDialog(act: Activity) {
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
            dialog.setCancelable(true)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_write_review, null, false)
            dialog.setContentView(mView)
            val finalDialog: Dialog = dialog
            val edDiscription = mView.findViewById<EditText>(R.id.edDescription)
            val ivStar1 = mView.findViewById<ImageView>(R.id.ivStar1)
            val ivStar2 = mView.findViewById<ImageView>(R.id.ivStar2)
            val ivStar3 = mView.findViewById<ImageView>(R.id.ivStar3)
            val ivStar4 = mView.findViewById<ImageView>(R.id.ivStar4)
            val ivStar5 = mView.findViewById<ImageView>(R.id.ivStar5)

            var rattingValue =1

            ivStar1.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
            ivStar2.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
            ivStar3.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
            ivStar4.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
            ivStar5.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
            ivStar1.setOnClickListener {
                ivStar1.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar2.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
                ivStar3.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
                ivStar4.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
                ivStar5.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
                rattingValue=1
            }
            ivStar2.setOnClickListener {
                ivStar1.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar2.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar3.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
                ivStar4.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
                ivStar5.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
                rattingValue=2
            }
            ivStar3.setOnClickListener {
                ivStar1.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar2.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar3.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar4.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
                ivStar5.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
                rattingValue=3
            }
            ivStar4.setOnClickListener {
                ivStar1.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar2.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar3.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar4.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar5.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.gray)
                rattingValue=4
            }
            ivStar5.setOnClickListener {
                ivStar1.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar2.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar3.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar4.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                ivStar5.imageTintList = AppCompatResources.getColorStateList(this@RattingActivity,R.color.colorPrimary)
                rattingValue=5
            }

            mView.tvUpdate.setOnClickListener {
                if(edDiscription.text.toString() == ""){
                    alertErrorOrValidationDialog(act,resources.getString(R.string.validation_comment))
                }else{
                    if (Common.isCheckNetwork(act)) {
                        finalDialog.dismiss()
                        callApiPutRatting(rattingValue.toString(),edDiscription.text.toString())
                    } else {
                        alertErrorOrValidationDialog(act, resources.getString(R.string.no_internet))
                    }
                }
            }

            if (!act.isFinishing) dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiPutRatting(rattingValue: String, discription: String) {
        showLoadingProgress(this@RattingActivity)
        val map = HashMap<String, String>()
        map["user_id"] = SharePreference.getStringPref(
            this@RattingActivity,
            SharePreference.userId
        )!!
        map["ratting"] = rattingValue
        map["comment"] = discription
        val call = ApiClient.getClient.setRatting(map)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NewApi")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {

                if (response.code() == 200) {
                    val restResponce: SingleResponse = response.body()!!
                    if (restResponce.status==1) {
                        if(Common.isCheckNetwork(this@RattingActivity)){
                            callApiRatting(true)
                        }else{
                            alertErrorOrValidationDialog(
                                this@RattingActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }
                }else{
                    dismissLoadingProgress()
                    val jsonObj: JSONObject = JSONObject(response.errorBody()!!.string())
                    if (jsonObj.getInt("status")==0) {
                        alertErrorOrValidationDialog(
                            this@RattingActivity,
                            jsonObj.getString("message")
                        )
                    }
                }
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@RattingActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@RattingActivity, false)
    }
}