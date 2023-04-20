package com.grocery.app.activity

import android.annotation.SuppressLint
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
import com.grocery.app.utils.Common
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_forgetpassword.*
import kotlinx.android.synthetic.main.activity_forgetpassword.ivBack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ForgetPasswordActivity:BaseActivity() {
    override fun setLayout(): Int {
       return R.layout.activity_forgetpassword
    }

    override fun initView() {
        Common.getCurrentLanguage(this@ForgetPasswordActivity, false)
        tvSignup.setOnClickListener {
            openActivity(RegistrationActivity::class.java)
            finish()
        }
        if(SharePreference.getStringPref(this@ForgetPasswordActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }

        ivBack.setOnClickListener {
            finish()
        }
    }

    fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.tvSend -> {
                if(edEmail.text.toString() == ""){
                    Common.showErrorFullMsg(this@ForgetPasswordActivity,resources.getString(R.string.validation_all))
                }else if (!Common.isValidEmail(edEmail.text.toString())) {
                    Common.showErrorFullMsg(this@ForgetPasswordActivity,resources.getString(R.string.validation_valid_email))
                }else{
                    val hasmap = HashMap<String, String>()
                    hasmap["email"] = edEmail.text.toString()
                    if(Common.isCheckNetwork(this@ForgetPasswordActivity)){
                        callApiForgetpassword(hasmap)
                    }else{
                        Common.alertErrorOrValidationDialog(
                            this@ForgetPasswordActivity,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }
            }
        }
    }

    private fun callApiForgetpassword(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ForgetPasswordActivity)
        val call = ApiClient.getClient.setforgotPassword(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status==1) {
                        Common.dismissLoadingProgress()
                        successfulDialog(
                            this@ForgetPasswordActivity,
                            restResponse.message.toString()
                        )
                    }
                    else {
                        Common.dismissLoadingProgress()
                        Common.showErrorFullMsg(this@ForgetPasswordActivity,restResponse.message.toString())
                    }
                }else{
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(this@ForgetPasswordActivity,resources.getString(R.string.error_msg))
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(this@ForgetPasswordActivity,resources.getString(R.string.error_msg))
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
        Common.getCurrentLanguage(this@ForgetPasswordActivity, false)
    }
}