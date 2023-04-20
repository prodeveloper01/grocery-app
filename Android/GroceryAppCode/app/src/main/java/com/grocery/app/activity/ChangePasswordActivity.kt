package com.grocery.app.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.getCurrentLanguage
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.Common.showErrorFullMsg
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_changepass.*
import kotlinx.android.synthetic.main.activity_changepass.ivBack
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ChangePasswordActivity:BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_changepass
    }

    override fun initView() {
       getCurrentLanguage(this@ChangePasswordActivity, false)
        if(SharePreference.getStringPref(this@ChangePasswordActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
    }

    fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.tvReset -> {
                if(edPassword.text.toString() == ""){
                    showErrorFullMsg(this@ChangePasswordActivity,resources.getString(R.string.validation_all))
                }else if(edNewPassword.text.toString() == ""){
                    showErrorFullMsg(this@ChangePasswordActivity,resources.getString(R.string.validation_all))
                }
//                else if(edNewPassword.text.toString().length<7){
//                    showErrorFullMsg(this@ChangePasswordActivity,resources.getString(R.string.validation_all))
//                }
                else if(edCPassword.text.toString() == ""){
                    showErrorFullMsg(this@ChangePasswordActivity,resources.getString(R.string.validation_all))
                }else if(edCPassword.text.toString() != edNewPassword.text.toString()){
                    showErrorFullMsg(this@ChangePasswordActivity,resources.getString(R.string.validation_valid_cpassword))
                }else{
                    val hasmap = HashMap<String, String>()
                    hasmap["user_id"] =
                        SharePreference.getStringPref(this@ChangePasswordActivity,SharePreference.userId)!!
                    hasmap["old_password"] = edPassword.text.toString()
                    hasmap["new_password"] = edNewPassword.text.toString()
                    if(isCheckNetwork(this@ChangePasswordActivity)){
                        callApiChangepassword(hasmap)
                    }else{
                        alertErrorOrValidationDialog(this@ChangePasswordActivity,resources.getString(R.string.no_internet))
                    }
                }
            }
        }
    }


    private fun callApiChangepassword(hasmap: HashMap<String, String>) {
        showLoadingProgress(this@ChangePasswordActivity)
        val call = ApiClient.getClient.setChangePassword(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                dismissLoadingProgress()
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status.toString() == "1") {
                        finish()
//                        successfulDialog(
//                            this@ChangePasswordActivity,
//                            restResponse.message
//                        )
                    }
                    else {
                        alertErrorOrValidationDialog(this@ChangePasswordActivity, restResponse.message)
                    }
                }else{
                    val restResponse = response.errorBody()!!.string()
                    val jsonObject= JSONObject(restResponse)
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        this@ChangePasswordActivity,
                        jsonObject.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ChangePasswordActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun successfulDialog(act: Activity, msg: String?) {
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
            val mView = mInflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = mView.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                finish()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(this@ChangePasswordActivity, false)
    }

    override fun onPause() {
        super.onPause()
        getCurrentLanguage(this@ChangePasswordActivity, false)
    }
}