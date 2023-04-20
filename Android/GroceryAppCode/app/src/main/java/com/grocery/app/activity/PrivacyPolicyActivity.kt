package com.grocery.app.activity

import android.view.View
import android.webkit.WebViewClient
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.base.BaseActivity
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_privacypolicy.*
import kotlinx.android.synthetic.main.activity_privacypolicy.ivBack

class PrivacyPolicyActivity:BaseActivity() {
    override fun setLayout(): Int = R.layout.activity_privacypolicy

    override fun initView() {
        tvTitle.text=intent.getStringExtra("privacy_policy")

        ivBack.setOnClickListener {
            finish()
        }
        if(SharePreference.getStringPref(this@PrivacyPolicyActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        webView.webViewClient = WebViewClient()
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        when {
            intent.getStringExtra("privacy_policy")=="Privacy policy" -> {
                webView.loadUrl(ApiClient.PrivacyPolicy)
            }
            intent.getStringExtra("privacy_policy")=="About us" -> {
                webView.loadUrl(ApiClient.AboutUs)
            }
            intent.getStringExtra("privacy_policy")=="Terms & conditions" -> {
                webView.loadUrl(ApiClient.TermsCondition)
            }
        }
    }
}