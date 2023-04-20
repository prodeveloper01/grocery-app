package com.grocery.app.activity

import android.os.Handler
import android.os.Looper
import com.grocery.app.R
import com.grocery.app.base.BaseActivity
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.printKeyHash
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.getBooleanPref

class SplashActivity :BaseActivity() {
    override fun setLayout(): Int {
       return R.layout.activity_splash
    }

    override fun initView() {
        Common.getLog("getShaKey",printKeyHash(this@SplashActivity)!!)
        Common.getCurrentLanguage(this@SplashActivity, false)
        Handler(Looper.getMainLooper()).postDelayed({
            if(!getBooleanPref(this@SplashActivity, SharePreference.isTutorial)){
                openActivity(TutorialActivity::class.java)
                finish()
            }else{
                if(getBooleanPref(this@SplashActivity, SharePreference.isLogin)){
                   openActivity(DashboardActivity::class.java)
                   finish()
                }else{
                   openActivity(LoginSelectActivity::class.java)
                   finish()
                }
            }
        },3000)
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@SplashActivity, false)
    }
}