package com.grocery.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.RestResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.model.RegistrationModel
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.showErrorFullMsg
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity:BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_signup
    }

    @SuppressLint("NewApi")
    override fun initView() {
        Common.getCurrentLanguage(this@RegistrationActivity, false)

        if(intent.getStringExtra("loginType")!=null){
            edFullName.setText(intent.getStringExtra("name")!!)
            edEmail.setText(intent.getStringExtra("profileEmail")!!)
            tvPass.visibility= View.GONE
            edEmail.isActivated=false
            edEmail.inputType = InputType.TYPE_NULL;
        }else{
            tvPass.visibility= View.VISIBLE
        }

        tvSkipAndLogin.setOnClickListener {
            openActivity(DashboardActivity::class.java)
            finish()
            finishAffinity()
        }

        tvLogin.setOnClickListener {
            openActivity(LoginActivity::class.java)
            finish()
        }
        tvTermsAndCondition.setOnClickListener {
            startActivity(Intent(this@RegistrationActivity,PrivacyPolicyActivity::class.java).putExtra("privacy_policy","Terms & conditions"))
        }

        tvSignUp.setOnClickListener {
            if(intent.getStringExtra("loginType")!=null){
                if(edMobileNo.text.toString() == ""){
                    showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
                } else if(intent.getStringExtra("loginType")=="facebook"||intent.getStringExtra("loginType")=="google"){
                    val hasmap= HashMap<String, String>()
                    hasmap["name"] = intent.getStringExtra("name")!!
                    hasmap["email"] = intent.getStringExtra("profileEmail")!!
                    hasmap["mobile"] = edMobileNo.text.toString()
                    hasmap["referral_code"] = edRefralCode.text.toString()
                    hasmap["token"] = intent.getStringExtra("strToken")!!
                    hasmap["register_type"] = "email"
                    hasmap["login_type"] = intent.getStringExtra("loginType")!!
                    if(intent.getStringExtra("loginType")=="google"){
                        hasmap["google_id"]=intent.getStringExtra("profileId")!!
                        hasmap["facebook_id"]=""
                    }else{
                        hasmap["facebook_id"]=intent.getStringExtra("profileId")!!
                        hasmap["google_id"]=""
                    }
                    if(Common.isCheckNetwork(this@RegistrationActivity)){
                        if(cbCheck.isChecked){
                            callApiRegistration(hasmap)
                        }else{
                            showErrorFullMsg(this@RegistrationActivity,"Please Check Terms Conditions")
                        }
                    }else{
                        alertErrorOrValidationDialog(this@RegistrationActivity,resources.getString(R.string.no_internet))
                    }
                }
            }else{
                var strToken=""
                FirebaseApp.initializeApp(this@RegistrationActivity)
                strToken=FirebaseInstanceId.getInstance().token.toString()
                if(edFullName.text.toString() == ""){
                    showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
                }else if(edEmail.text.toString() == ""){
                    showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
                }else if(!Common.isValidEmail(edEmail.text.toString())){
                    showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_valid_email))
                }else if(edMobileNo.text.toString() == ""){
                    showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
                } else if(edPassword.text.toString() == ""){
                    showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
                }else{
                    if(cbCheck.isChecked){
                        val hasmap= HashMap<String, String>()
                        hasmap["name"] = edFullName.text.toString()
                        hasmap["email"] = edEmail.text.toString()
                        hasmap["mobile"] = edMobileNo.text.toString()
                        hasmap["password"] = edPassword.text.toString()
                        hasmap["referral_code"] = edRefralCode.text.toString()
                        hasmap["token"] = strToken
                        hasmap["register_type"] = "email"
                        if(Common.isCheckNetwork(this@RegistrationActivity)){
                            callApiRegistration(hasmap)
                        }else{
                            alertErrorOrValidationDialog(this@RegistrationActivity,resources.getString(R.string.no_internet))
                        }
                    }else{
                        showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.terms_condition_error))
                    }

                }
            }
        }
    }

    private fun callApiRegistration(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@RegistrationActivity)
        val call = ApiClient.getClient.setRegistration(hasmap)
        call.enqueue(object : Callback<RestResponse<RegistrationModel>> {
            override fun onResponse(
                call: Call<RestResponse<RegistrationModel>>,
                response: Response<RestResponse<RegistrationModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val registrationResponse: RestResponse<RegistrationModel> = response.body()!!
                    if (registrationResponse.status.toString() == "1") {
                        dismissLoadingProgress()
                        startActivity(Intent(this@RegistrationActivity,OTPVerificatinActivity::class.java).putExtra("email",edEmail.text.toString()))
                    } else if (registrationResponse.status.toString() == "0") {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@RegistrationActivity,
                            registrationResponse.message
                        )
                    }
                }else  {
                    val error= JSONObject(response.errorBody()!!.string())
                    if(error.getString("status")=="2"){
                        dismissLoadingProgress()
                        startActivity(Intent(this@RegistrationActivity,OTPVerificatinActivity::class.java).putExtra("email",edEmail.text.toString()))
                    }else{
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@RegistrationActivity,
                            error.getString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<RegistrationModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@RegistrationActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@RegistrationActivity, false)
    }

}