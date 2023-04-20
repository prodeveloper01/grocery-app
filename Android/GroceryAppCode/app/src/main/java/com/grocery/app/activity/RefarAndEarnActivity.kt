package com.grocery.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.grocery.app.R
import com.grocery.app.base.BaseActivity
import com.grocery.app.utils.Common
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.getStringPref
import com.grocery.app.utils.SharePreference.Companion.userRefralCode
import kotlinx.android.synthetic.main.activity_referandearn.*
import kotlinx.android.synthetic.main.activity_referandearn.ivBack
import java.util.*


class RefarAndEarnActivity : BaseActivity() {
    override fun setLayout(): Int = R.layout.activity_referandearn

    @SuppressLint("SetTextI18n")
    override fun initView() {
        Common.getCurrentLanguage(this@RefarAndEarnActivity, false)
        tvRefareAndEarn.text = "Share this code with a friend and you both \n could be eligble for $${
            String.format(
                    Locale.US,
                    "%,.2f",
                    getStringPref(
                            this@RefarAndEarnActivity,
                            SharePreference.userRefralAmount
                    )?.toDouble()
            )
        } bonus amount \n under our Referral Program."
        ivBack.setOnClickListener {
            finish()
        }
        if(SharePreference.getStringPref(this@RefarAndEarnActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        edPromocode.text = getStringPref(
                this@RefarAndEarnActivity,
                userRefralCode
        )
        tvBtnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Use this code ${getStringPref(this@RefarAndEarnActivity, userRefralCode)} to register with Grocery User & get bonus amount ${
                Common.getCurrancy(this@RefarAndEarnActivity) + String.format(
                        Locale.US,
                        "%,.2f",
                        getStringPref(
                                this@RefarAndEarnActivity,
                                SharePreference.userRefralAmount
                        )?.toDouble()
                )
            }")
            startActivity(Intent.createChooser(intent, "choose one"))
        }


    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@RefarAndEarnActivity, false)
    }
}