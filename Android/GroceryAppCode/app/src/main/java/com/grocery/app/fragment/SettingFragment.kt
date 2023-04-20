package com.grocery.app.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.grocery.app.R
import com.grocery.app.activity.*
import com.grocery.app.base.BaseFragmnet
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.getCurrentLanguage
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.getBooleanPref
import com.grocery.app.utils.SharePreference.Companion.setStringPref
import kotlinx.android.synthetic.main.activity_setaddress.*
import kotlinx.android.synthetic.main.dlg_logout.view.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.fragment_setting.view.tvLogout

class SettingFragment:BaseFragmnet() {
    override fun setView(): Int {
        return R.layout.fragment_setting
    }

    override fun init(view: View) {
        getCurrentLanguage(activity!!, false)

        cvBtnEditProfile.setOnClickListener {
            if(getBooleanPref(activity!!,SharePreference.isLogin)){
                openActivity(EditProfileActivity::class.java)
            }else {
                openActivity(LoginSelectActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }


        cvManageAddress.setOnClickListener {
            if (getBooleanPref(activity!!, SharePreference.isLogin)) {
                val intent = Intent(requireContext(), GetAddressActivity::class.java)
                intent.putExtra("isComeFromSelectAddress", false)
                startActivity(intent)
            } else {
                openActivity(LoginActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }
        cvBtnPassword.setOnClickListener {
            if(getBooleanPref(activity!!,SharePreference.isLogin)){
                openActivity(ChangePasswordActivity::class.java)
            }else{
                openActivity(LoginSelectActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }

        cvRatting.setOnClickListener {
            openActivity(RattingActivity::class.java)
        }

        cvPrivacyPolicy.setOnClickListener {
            startActivity(Intent(activity!!,PrivacyPolicyActivity::class.java).putExtra("privacy_policy","Privacy policy"))
        }


        cvAboutUs.setOnClickListener {
            startActivity(Intent(activity!!,PrivacyPolicyActivity::class.java).putExtra("privacy_policy","About us"))
        }

        cvRefarAndEarn.setOnClickListener {
            if(getBooleanPref(activity!!,SharePreference.isLogin)){
                openActivity(RefarAndEarnActivity::class.java)
            }else{
                openActivity(LoginSelectActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }

        if(getBooleanPref(activity!!,SharePreference.isLogin)){
            tvLogout.visibility=View.VISIBLE
        }else{
            tvLogout.visibility=View.GONE
        }

        tvLogout.setOnClickListener {
            alertLogOutDialog()
        }

        llArabic.setOnClickListener {
            setStringPref(activity!!, SharePreference.SELECTED_LANGUAGE,activity!!.resources.getString(R.string.language_hindi))
            getCurrentLanguage(activity!!, true)
        }
        llEnglish.setOnClickListener {
            setStringPref(activity!!, SharePreference.SELECTED_LANGUAGE,activity!!.resources.getString(R.string.language_english))
            getCurrentLanguage(activity!!, true)
        }
        /*cvShare.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Food App")
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage = "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }
        }*/

    }

    fun alertLogOutDialog() {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
            }
            dialog = Dialog(activity!!, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(activity!!)
            val m_view = m_inflater.inflate(R.layout.dlg_logout, null, false)

            val finalDialog: Dialog = dialog
            m_view.tvLogout.setOnClickListener {
                finalDialog.dismiss()
                Common.setLogout(activity!!)

            }
            m_view.tvCancel.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(activity!!, false)
    }
}