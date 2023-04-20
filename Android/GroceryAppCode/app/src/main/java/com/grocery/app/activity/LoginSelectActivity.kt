package com.grocery.app.activity

import com.grocery.app.R
import com.grocery.app.base.BaseActivity
import com.grocery.app.utils.Common
import kotlinx.android.synthetic.main.activity_login_select.*

class LoginSelectActivity:BaseActivity() {
    override fun setLayout(): Int {
       return R.layout.activity_login_select
    }

    override fun initView() {
        Common.getCurrentLanguage(this@LoginSelectActivity, false)
       tvSignup.setOnClickListener {
          openActivity(RegistrationActivity::class.java)
       }

       tvLogin.setOnClickListener {
          openActivity(LoginActivity::class.java)
       }

       tvSkip.setOnClickListener {
          openActivity(DashboardActivity::class.java)
          finish()
          finishAffinity()
       }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@LoginSelectActivity, false)
    }
}