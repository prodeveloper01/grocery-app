package com.grocery.app.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.grocery.app.R
import com.grocery.app.base.BaseActivity
import com.grocery.app.fragment.*
import com.grocery.app.utils.Common
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.getBooleanPref
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dlg_confomation.view.*

class DashboardActivity:BaseActivity() {
    private var temp = 1
    override fun setLayout(): Int {
        return R.layout.activity_dashboard
    }

    override fun initView() {
        Common.getCurrentLanguage(this@DashboardActivity, false)
        temp = if (intent.getStringExtra("pos") != null) {
            setFragment(intent.getStringExtra("pos")!!.toInt())
            intent.getStringExtra("pos")!!.toInt()
        } else {
            setFragment(1)
            1
        }
    }

     override fun onBackPressed() {
         if(temp != 1){
             temp = 1
             setFragment(1)
         }else{
             mExitDialog()
         }
     }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@DashboardActivity, false)
    }

    override fun onPause() {
        super.onPause()
        Common.getCurrentLanguage(this@DashboardActivity, false)
    }

     fun onClick(v: View?) {
         when (v!!.id) {
             R.id.rl_home -> {
                 if (temp != 1) {
                     setFragment(1)
                     temp = 1
                 }
             }
             R.id.rlSearch -> {
                 if (temp != 2) {
                     setFragment(2)
                     temp = 2
                 }
             }
             R.id.rlFavourite -> {
                 if (getBooleanPref(this@DashboardActivity,SharePreference.isLogin)) {
                     if (temp != 3) {
                         setFragment(3)
                         temp = 3
                     }
                 } else {
                     openActivity(LoginSelectActivity::class.java)
                     finish()
                     finishAffinity()
                 }

             }
             R.id.rlOrder -> {
                 if (getBooleanPref(this@DashboardActivity,SharePreference.isLogin)){
                     if (temp != 4) {
                         setFragment(4)
                         temp = 4
                     }
                 } else {
                     openActivity(LoginSelectActivity::class.java)
                     finish()
                     finishAffinity()
                 }
             }

             R.id.rl_setting -> {
                 if (temp != 5) {
                     setFragment(5)
                     temp = 5
                 }
             }
         }
     }


     @SuppressLint("WrongConstant")
     fun replaceFragment(fragment: Fragment) {
         val fragmentManager: FragmentManager = supportFragmentManager
         val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
         fragmentTransaction.replace(R.id.FramFragment, fragment)
         fragmentTransaction.addToBackStack(fragment.toString())
         fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
         fragmentTransaction.commit()
     }

     @SuppressLint("NewApi")
     fun setFragment(pos: Int) {
         ivHome.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null))
         tvHome.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null)))
         ivSearch.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null))
         tvSearch.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null)))
         ivOrder.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null))
         tvOrderHistory.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null)))
         ivFavourite.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null))
         tvFavourite.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null)))
         ivSetting.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null))
         tvSetting.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.gray,null)))
         when (pos) {
             1 -> {
                 ivHome.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                 tvHome.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null)))
                 replaceFragment(HomeFragment())
             }
             2 -> {
                 ivSearch.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                 tvSearch.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null)))
                 replaceFragment(SearchFragment())
             }
             3 -> {
                 ivFavourite.imageTintList=ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                 tvFavourite.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null)))
                 replaceFragment(FavouriteFragment())
             }
             4 -> {
                 ivOrder.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                 tvOrderHistory.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null)))
                 replaceFragment(HistoryFragment())
             }
             5 -> {
                 ivSetting.imageTintList= ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null))
                 tvSetting.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.colorPrimary,null)))
                 replaceFragment(SettingFragment())
             }
         }
     }

     @SuppressLint("InflateParams")
     fun mExitDialog() {
         var dialog: Dialog? = null
         try {
             dialog?.dismiss()
             dialog = Dialog(this@DashboardActivity, R.style.AppCompatAlertDialogStyleBig)
             dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
             dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT)
             dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
             dialog.setCancelable(false)
             val mInflater = LayoutInflater.from(this@DashboardActivity)
             val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)

             val finalDialog: Dialog = dialog
             mView.tvYes.setOnClickListener {
                 finalDialog.dismiss()
                 ActivityCompat.finishAfterTransition(this@DashboardActivity)
                 ActivityCompat.finishAffinity(this@DashboardActivity)
                 finish()
             }
             mView.tvNo.setOnClickListener {
                 finalDialog.dismiss()
             }
             dialog.setContentView(mView)
             dialog.show()
         } catch (e: java.lang.Exception) {
             e.printStackTrace()
         }
     }
}