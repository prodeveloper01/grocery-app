package com.grocery.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.RestResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.model.LoginModel
import com.grocery.app.model.RegistrationModel
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.getLog
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.Common.showErrorFullMsg
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.setBooleanPref
import com.grocery.app.utils.SharePreference.Companion.setStringPref
import com.grocery.app.utils.SharePreference.Companion.userEmail
import com.grocery.app.utils.SharePreference.Companion.userId
import com.grocery.app.utils.SharePreference.Companion.userMobile
import com.grocery.app.utils.SharePreference.Companion.userName
import com.grocery.app.utils.SharePreference.Companion.userProfile
import com.grocery.app.utils.SharePreference.Companion.userRefralCode
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edEmail
import kotlinx.android.synthetic.main.activity_login.edPassword
import kotlinx.android.synthetic.main.activity_login.tvLogin
import kotlinx.android.synthetic.main.activity_login.tvSignup
import kotlinx.android.synthetic.main.activity_login_select.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap


class LoginActivity:BaseActivity() {

    //:::::::::::::::Google Login::::::::::::::::://
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 1

    //:::::::::::::::Facebook Login::::::::::::::::://
    private var callbackManager: CallbackManager? = null
    var callback: FacebookCallback<LoginResult>?=null

    var strToken=""

    override fun setLayout(): Int {
       return R.layout.activity_login
    }

    override fun initView() {
        Common.getCurrentLanguage(this@LoginActivity, false)

        FirebaseApp.initializeApp(this@LoginActivity)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            strToken = instanceIdResult.token
        }
        getLog("Token== ", strToken)

        //:::::::::::::::Google Login::::::::::::::::://
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        rlbtnGoogle.setOnClickListener {
            if (Common.isCheckNetwork(this@LoginActivity)) {
                mGoogleSignInClient!!.signOut().addOnCompleteListener(this, object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        signInGoogle()
                    }
                })
            } else {
                alertErrorOrValidationDialog(
                        this@LoginActivity,
                        resources.getString(R.string.no_internet)
                )
            }
        }


        //::::::::::::::Facebook Login::::::::::::::::://
        FacebookSdk.setApplicationId("1234567890");
        FacebookSdk.sdkInitialize(this@LoginActivity)
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    updateFacebookUI(loginResult)
                }

                override fun onCancel() {}
                override fun onError(error: FacebookException) {
                    Toast.makeText(applicationContext, "" + error.message, Toast.LENGTH_LONG)
                        .show()
                }
            })

        rlbtnFacebook.setOnClickListener {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut()
            }
            LoginManager
                .getInstance()
                .logInWithReadPermissions(
                    this,
                    getFacebookPermissions()
                )
        }


        tvLogin.setOnClickListener{
           if (edEmail.text.toString() == "") {
               showErrorFullMsg(this@LoginActivity, resources.getString(R.string.validation_all))
           } else if (!Common.isValidEmail(edEmail.text.toString())) {
               showErrorFullMsg(
                       this@LoginActivity,
                       resources.getString(R.string.validation_valid_email)
               )
           } else if (edPassword.text.toString() == "") {
               showErrorFullMsg(
                       this@LoginActivity,
                       resources.getString(R.string.validation_all)
               )
           } else {
               val hasmap = HashMap<String, String>()
               hasmap["email"] = edEmail.text.toString()
               hasmap["password"] = edPassword.text.toString()
               hasmap["token"] = strToken
               if (isCheckNetwork(this@LoginActivity)) {
                   callApiLogin(hasmap)
               } else {
                   alertErrorOrValidationDialog(
                           this@LoginActivity,
                           resources.getString(R.string.no_internet)
                   )
               }
           }
        }

        tvSignup.setOnClickListener {
           openActivity(RegistrationActivity::class.java)
           finish()
        }

        tvForgetPassword.setOnClickListener {
           openActivity(ForgetPasswordActivity::class.java)
           finish()
        }

        tvSkipAndLogin.setOnClickListener {
            openActivity(DashboardActivity::class.java)
            finish()
            finishAffinity()
        }
    }

    fun getFacebookPermissions(): List<String> {
        return listOf("email")
    }

    private fun callApiLogin(hasmap: HashMap<String, String>) {
        showLoadingProgress(this@LoginActivity)
        val call = ApiClient.getClient.getLogin(hasmap)
        call.enqueue(object : Callback<RestResponse<LoginModel>> {
            override fun onResponse(
                    call: Call<RestResponse<LoginModel>>,
                    response: Response<RestResponse<LoginModel>>
            ) {
               if (response.code() == 200) {
                  val loginResponce: RestResponse<LoginModel> = response.body()!!
                  if (loginResponce.status == 1) {
                      val loginModel: LoginModel = loginResponce.data!!
                      setProfileData(loginModel)
                  }
               } else {
                   val error=JSONObject(response.errorBody()!!.string())
                   val status=error.getInt("status")
                   if(status==2){
                       dismissLoadingProgress()
                       startActivity(Intent(this@LoginActivity,OTPVerificatinActivity::class.java).putExtra("email", edEmail.text.toString()))
                   }else{
                       dismissLoadingProgress()
                       showErrorFullMsg(this@LoginActivity,error.getString("message"))
                   }
               }
            }

            override fun onFailure(call: Call<RestResponse<LoginModel>>, t: Throwable) {
              dismissLoadingProgress()
              alertErrorOrValidationDialog(
                      this@LoginActivity,
                      resources.getString(R.string.error_msg)
              )
            }
        })
    }


    private fun setProfileData(dataResponse: LoginModel) {
        setBooleanPref(this@LoginActivity, SharePreference.isLogin, true)
        setStringPref(this@LoginActivity, userId, dataResponse.id.toString())
        setStringPref(this@LoginActivity, userName, dataResponse.name.toString())
        setStringPref(this@LoginActivity, userMobile, dataResponse.mobile.toString())
        setStringPref(this@LoginActivity, userEmail, dataResponse.email.toString())
        setStringPref(this@LoginActivity, userProfile, dataResponse.profile_image.toString())
        setStringPref(this@LoginActivity, userRefralCode, dataResponse.referral_code.toString())
        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        finishAffinity()
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!
            nextGmailActivity(account)
        } catch (e: ApiException) {
            Log.e("Google Login", "signInResult:failed code=" + e.statusCode)
        }
    }


    @SuppressLint("HardwareIds")
    private fun nextGmailActivity(profile: GoogleSignInAccount?) {
        if (profile != null) {
            val loginType = "google"
            val FristName = profile.displayName
            val profileEmail = profile.email
            val profileId = profile.id
            loginApiCall(FristName!!, profileEmail!!, profileId!!, loginType, strToken)
        }
    }

    private fun mGoToRegistration(name: String, profileEmail: String, profileId: String, loginType: String, strToken: String) {
        val intent=Intent(this@LoginActivity, RegistrationActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("profileEmail", profileEmail)
        intent.putExtra("profileId", profileId)
        intent.putExtra("loginType", loginType)
        intent.putExtra("strToken", strToken)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@LoginActivity, false)
    }

    //::::::::::::::FacebookLogin:::::::::::::://
    private fun updateFacebookUI(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(loginResult.accessToken, object : GraphRequest.GraphJSONObjectCallback {
            override fun onCompleted(
                `object`: JSONObject,
                response: GraphResponse?
            ) {
                getFacebookData(`object`)
            }
        })
        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, first_name, last_name, email,age_range, gender, birthday, location"
        ) // Parámetros que pedimos a facebook
        request.parameters = parameters
        request.executeAsync()
    }

    private fun getFacebookData(`object`: JSONObject) {
        try {
            val profileId = `object`.getString("id")
            var name = ""
            if (`object`.has("first_name")) {
                name = `object`.getString("first_name")
            }
            if (`object`.has("last_name")) {
                name += " " + `object`.getString("last_name")
            }
            var email = ""
            if (`object`.has("email")){
                email = `object`.getString("email")
            }
            val loginType = "facebook"
            loginApiCall(name,email,profileId,loginType,strToken)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun loginApiCall(name: String, email: String, profileId: String, loginType: String, strToken: String) {
        val hasmap= HashMap<String, String>()
        hasmap["name"] = name
        hasmap["email"] = email
        hasmap["mobile"] = ""
        hasmap["token"] = strToken
        hasmap["login_type"] = loginType
        if(loginType=="google"){
            hasmap["google_id"]=profileId
            hasmap["facebook_id"]=""
        }else{
            hasmap["facebook_id"]=profileId
            hasmap["google_id"]=""
        }
        showLoadingProgress(this@LoginActivity)
        val call = ApiClient.getClient.setRegistration(hasmap)
        call.enqueue(object : Callback<RestResponse<RegistrationModel>> {
            override fun onResponse(
                call: Call<RestResponse<RegistrationModel>>,
                response: Response<RestResponse<RegistrationModel>>
            ) {
                if (response.code() == 200) {
                    val registrationResponse: RestResponse<RegistrationModel> = response.body()!!
                    if (registrationResponse.status.toString() == "1") {
                        dismissLoadingProgress()
                        setProfileData(registrationResponse.data!!,registrationResponse.message)
                    } else if (registrationResponse.status.toString() == "0") {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(this@LoginActivity, registrationResponse.message)
                    }else if (registrationResponse.status.toString() == "2") {
                        dismissLoadingProgress()
                        mGoToRegistration(name, email, profileId, loginType, strToken)
                    }else if (registrationResponse.status.toString() == "3") {
                        dismissLoadingProgress()
                        startActivity(Intent(this@LoginActivity,OTPVerificatinActivity::class.java).putExtra("email",email))
                    }
                }else  {
                    val error= JSONObject(response.errorBody()!!.string())
                    if(error.getString("status")=="3"){
                        dismissLoadingProgress()
                        startActivity(Intent(this@LoginActivity,OTPVerificatinActivity::class.java).putExtra("email",email))
                    }else{
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                          this@LoginActivity,
                          error.getString("message")
                        )
                    }

                }
            }

            override fun onFailure(call: Call<RestResponse<RegistrationModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                        this@LoginActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })

    }

    private fun setProfileData(dataResponse: RegistrationModel, message: String?) {
        setBooleanPref(this@LoginActivity, SharePreference.isLogin, true)
        setStringPref(this@LoginActivity, userId, dataResponse.id.toString())
        setStringPref(this@LoginActivity, userName, dataResponse.name.toString())
        setStringPref(this@LoginActivity, userMobile, dataResponse.mobile.toString())
        setStringPref(this@LoginActivity, userEmail, dataResponse.email.toString())
        setStringPref(this@LoginActivity, userProfile, dataResponse.profile_image.toString())
        setStringPref(this@LoginActivity, userRefralCode, dataResponse.referral_code.toString())
        startActivity(Intent(this@LoginActivity,DashboardActivity::class.java))
        finish()
        finishAffinity()
    }


}