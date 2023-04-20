package com.grocery.app.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grocery.app.R
import com.grocery.app.utils.Common.getCurrentLanguage
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getCurrentLanguage(this@BaseActivity, false)
        setContentView(setLayout())
        initView()
    }

    protected abstract fun setLayout(): Int
    protected abstract fun initView()

    open fun openActivity(destinationClass: Class<*>) {
        startActivity(Intent(this@BaseActivity, destinationClass))
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }


    override fun onPause() {
        super.onPause()
        getCurrentLanguage(this@BaseActivity, false)
        overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(this@BaseActivity, false)
        overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }
}