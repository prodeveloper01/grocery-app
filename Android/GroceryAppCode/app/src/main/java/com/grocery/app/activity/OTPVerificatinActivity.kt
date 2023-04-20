package com.grocery.app.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
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
import com.google.firebase.iid.FirebaseInstanceId
import com.grocery.app.api.RestResponse
import com.grocery.app.model.LoginModel
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.showErrorFullMsg
import com.grocery.app.utils.Common.showSuccessFullMsg
import kotlinx.android.synthetic.main.activity_otp.*

import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class OTPVerificatinActivity:BaseActivity() {
    var strEmail: String = ""
    var strToken=""
    override fun setLayout(): Int {
        return R.layout.activity_otp
    }

    override fun initView() {
        strEmail =intent.getStringExtra("email")!!
        strToken= FirebaseInstanceId.getInstance().token.toString()
        tvSend.setOnClickListener {
            Common.closeKeyBoard(this@OTPVerificatinActivity)
            if(edOTP.text.toString()==""){
                showErrorFullMsg(this@OTPVerificatinActivity, resources.getString(R.string.validation_all))
            }else{
                val map = HashMap<String, String>()
                map["email"] = strEmail
                map["otp"] = edOTP.text.toString()
                map["token"] = strToken
                if (Common.isCheckNetwork(this@OTPVerificatinActivity)) {
                    callApiOTP(map)
                } else {
                    alertErrorOrValidationDialog(
                            this@OTPVerificatinActivity,
                            resources.getString(R.string.no_internet)
                    )
                }
            }

        }

        object : CountDownTimer(120000, 1000) {
            override fun onTick(millis: Long) {
                llOTP.visibility= View.GONE
                tvTimer.visibility= View.VISIBLE
                val timer=String.format("%02d:%02d",TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
                tvTimer.text = timer
                if(timer=="00:00"){
                    tvTimer.visibility= View.GONE
                    llOTP.visibility= View.VISIBLE
                }
            }
            override fun onFinish() {
                tvTimer.visibility= View.GONE
                llOTP.visibility= View.VISIBLE
            }
        }.start()

        tvResendOTP.setOnClickListener {
            Common.closeKeyBoard(this@OTPVerificatinActivity)
            val map = HashMap<String, String>()
            map["email"] = strEmail
            if (Common.isCheckNetwork(this@OTPVerificatinActivity)) {
                callApiResendOTP(map)
            } else {
                alertErrorOrValidationDialog(
                    this@OTPVerificatinActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }

    private fun callApiOTP(map: HashMap<String, String>) {
        Common.showLoadingProgress(this@OTPVerificatinActivity)
        val call: Call<RestResponse<LoginModel>> = ApiClient.getClient.setEmailVerify(map)
        call.enqueue(object : Callback<RestResponse<LoginModel>> {
            override fun onResponse(call: Call<RestResponse<LoginModel>>, response: Response<RestResponse<LoginModel>>) {
                if (response.code() == 200) {
                    val mainObject = response.body()
                    if (mainObject!!.status == 0) {
                        Common.dismissLoadingProgress()
                        showErrorFullMsg(this@OTPVerificatinActivity, mainObject.message!!)
                    } else if (mainObject.status == 1) {
                        Common.dismissLoadingProgress()
                        setProfileData(mainObject.data!!,mainObject.message)

                    }
                } else {
                    if(response.code()==500){
                        Common.dismissLoadingProgress()
                        showErrorFullMsg(this@OTPVerificatinActivity, resources.getString(R.string.error_msg))
                    }else{
                        val mainErrorObject = JSONObject(response.errorBody()!!.string())
                        val strMessage = mainErrorObject.getString("message")
                        Common.dismissLoadingProgress()
                        showErrorFullMsg(this@OTPVerificatinActivity, strMessage)
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<LoginModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                showErrorFullMsg(
                    this@OTPVerificatinActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun setProfileData(dataResponse: LoginModel, message: String?) {
        SharePreference.setBooleanPref(this@OTPVerificatinActivity, SharePreference.isLogin, true)
        SharePreference.setStringPref(this@OTPVerificatinActivity, SharePreference.userId, dataResponse.id.toString())
        SharePreference.setStringPref(this@OTPVerificatinActivity, SharePreference.userName, dataResponse.name.toString())
        SharePreference.setStringPref(this@OTPVerificatinActivity, SharePreference.userMobile, dataResponse.mobile.toString())
        SharePreference.setStringPref(this@OTPVerificatinActivity, SharePreference.userEmail, dataResponse.email.toString())
        SharePreference.setStringPref(this@OTPVerificatinActivity, SharePreference.userProfile, dataResponse.profile_image.toString())
        SharePreference.setStringPref(this@OTPVerificatinActivity, SharePreference.userRefralCode, dataResponse.referral_code.toString())
        openActivity(DashboardActivity::class.java)
        finish()
        finishAffinity()
    }

    private fun callApiResendOTP(map: HashMap<String, String>) {
        Common.showLoadingProgress(this@OTPVerificatinActivity)
        val call: Call<SingleResponse> = ApiClient.getClient.setResendEmailVerification(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val registrationResponse: SingleResponse = response.body()!!
                    if (registrationResponse.status.toString().equals("1")) {
                        Common.dismissLoadingProgress()
                        showSuccessFullMsg(this@OTPVerificatinActivity,registrationResponse.message!!)
                    } else if (registrationResponse.status.toString().equals("0")) {
                        Common.dismissLoadingProgress()
                        showErrorFullMsg(this@OTPVerificatinActivity,registrationResponse.message!!)
                    }
                }else  {
                    val error= JSONObject(response.errorBody()!!.string())
                    Common.dismissLoadingProgress()
                     showErrorFullMsg(
                        this@OTPVerificatinActivity,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(
                    this@OTPVerificatinActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onBackPressed() {
        Common.closeKeyBoard(this@OTPVerificatinActivity)
        openActivity(LoginActivity::class.java)
        finish()
        finishAffinity()
    }

}