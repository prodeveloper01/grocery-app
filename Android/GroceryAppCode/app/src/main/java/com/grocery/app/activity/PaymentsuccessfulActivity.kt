package com.grocery.app.activity

import com.grocery.app.R
import com.grocery.app.base.BaseActivity
import kotlinx.android.synthetic.main.activity_successpayment.*

class PaymentsuccessfulActivity:BaseActivity() {
    override fun setLayout(): Int = R.layout.activity_successpayment

    override fun initView() {
        tvProceed.setOnClickListener {
            openActivity(DashboardActivity::class.java)
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        return
    }
}