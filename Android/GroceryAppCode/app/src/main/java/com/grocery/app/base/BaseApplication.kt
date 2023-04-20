package com.grocery.app.base


import android.app.Application
import androidx.multidex.MultiDex
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.grocery.app.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(
            CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Poppins-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .disableCustomViewInflation()
                .build()
        )
        MultiDex.install(this)

    }
}