@file:Suppress("DEPRECATION")

package com.grocery.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.androidadvance.topsnackbar.TSnackbar
import com.grocery.app.R
import com.grocery.app.activity.DashboardActivity
import com.grocery.app.activity.LoginSelectActivity
import com.grocery.app.utils.SharePreference.Companion.SELECTED_LANGUAGE
import com.grocery.app.utils.SharePreference.Companion.getStringPref
import com.grocery.app.utils.SharePreference.Companion.isCurrancy
import com.grocery.app.utils.SharePreference.Companion.setStringPref
import kotlinx.android.synthetic.main.dlg_setting.view.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import android.util.Base64
import android.view.inputmethod.InputMethodManager
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.NumberFormat

object Common {
    var isCartTrue:Boolean=false
    var isCartTrueOut:Boolean=false
    var isCartCategoryOut:Boolean=false
    var isFavouriteOut:Boolean=false
    var isCancelledOrder:Boolean=false
    var isAddOrUpdated:Boolean=false

    val colorArray= arrayOf("D0EEF9", "FEEBD1", "FEE3DC", "F69089", "F0D7F9", "EAF8ED")

    fun getToast(activity: Activity, strTxtToast: String) {
        Toast.makeText(activity, strTxtToast, Toast.LENGTH_SHORT).show()
    }

    fun getLog(strKey: String, strValue: String) {
        Log.e(">>>---  $strKey  ---<<<", strValue)
    }

    fun isValidEmail(strPattern: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(strPattern).matches();
    }
    fun isValidAmount(strPattern: String): Boolean {
        return Pattern.compile(
            "([0-9]*)\\.([0-9]*)"
        ).matcher(strPattern).matches()
    }
    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password!!)
        return matcher.matches()
    }

    fun getFormattedNumber(number: String): String {
        try {
            return if (number.isNotEmpty()) {
                val `val` = number.toDouble()
             val currentValue=NumberFormat.getNumberInstance(Locale.US).format(`val`)
           /*     val dec = DecimalFormat("#,##0.00")
                dec.format(currentValue.toDouble())*/
//                NumberFormat.getCurrencyInstance().format(`val`)
//               DecimalFormat("#,###,###").format(number).toString()
                String.format("%,.2f", number.toDouble())
            } else {
                ""
            }
        } catch (e: NumberFormatException) {
            Log.e("NumberFormatExp", e.message.toString())
        }
        return ""
    }



    fun isValidUrl(urlString: String): Boolean {
        try {
            val url = URL(urlString)
            return URLUtil.isValidUrl(url.toString()) && Patterns.WEB_URL.matcher(url.toString())
                .matches()
        } catch (e: MalformedURLException) {
        }
        return false
    }

    @SuppressLint("NewApi")
    fun isCheckNetwork(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }
        return result
    }

    fun openActivity(activity: Activity, destinationClass: Class<*>?) {
        activity.startActivity(Intent(activity, destinationClass))
        activity.overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }

    var dialog: Dialog? = null

    fun dismissLoadingProgress() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    fun showLoadingProgress(context: Activity) {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dlg_progress)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    fun alertErrorOrValidationDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = m_view.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = m_view.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }





    fun settingDialog(act: Activity) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_setting, null, false)

            val finalDialog: Dialog = dialog
            m_view.tvOkSetting.setOnClickListener {
                val i = Intent()
                i.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                i.addCategory(Intent.CATEGORY_DEFAULT)
                i.setData(android.net.Uri.parse("package:" + act.getPackageName()))
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                act.startActivity(i)
                dialog.dismiss()
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            if (!act.isFinishing) dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun setLogout(activity: Activity) {
        val isTutorialsActivity:Boolean=SharePreference.getBooleanPref(
            activity,
            SharePreference.isTutorial
        )
        val preference = SharePreference(activity)
        preference.mLogout()
        SharePreference.setBooleanPref(activity, SharePreference.isTutorial, isTutorialsActivity)
        val intent = Intent(activity, LoginSelectActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent);
        activity.finish()
    }

    @SuppressLint("NewApi", "SimpleDateFormat")
    fun getDayAndMonth(strDate: String): String {
        val sd = SimpleDateFormat("dd-MM-yyyy")
        val sdout = SimpleDateFormat("dd-MMMM-yyyy")
        val sdday = SimpleDateFormat("EEEE")
        val date: Date = sd.parse(strDate)!!
        val getDay = sdday.format(date)
        val getDate = sdout.format(date)
        val stringArray = getDate.split("-").toTypedArray()
        val strDay = stringArray.get(0).plus("th")
        return getDay.plus(" ".plus(stringArray.get(1))).plus(" ".plus(strDay))
    }

    fun setImageUpload(strParameter: String, mSelectedFileImg: File): MultipartBody.Part{
      return  MultipartBody.Part.createFormData(
          strParameter, mSelectedFileImg.getName(), RequestBody.create(
              "image/*".toMediaType(),
              mSelectedFileImg
          )
      )
    }

    fun setRequestBody(bodyData: String):RequestBody{
        return bodyData.toRequestBody("text/plain".toMediaType())
    }

    @SuppressLint("SetTextI18n")
    fun getDatePicker(activity: Activity, tvDate: TextView){
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]

        Locale.setDefault(Locale.US);
        val datePickerDialog = DatePickerDialog(
            activity,

            { view, year, monthOfYear, dayOfMonth ->
                if (dayOfMonth.toString().length == 1) {
                    tvDate.text =
                        year.toString() + "-" + (monthOfYear + 1) + "-" + "0" + dayOfMonth.toString()
                } else {
                    tvDate.text =
                        year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.setMinDate(System.currentTimeMillis());
        datePickerDialog.show()
    }

    fun getCurrentDate(tvDate: TextView){
        tvDate.text=SimpleDateFormat("yyyy-MM-dd",Locale.US).format(Date())
    }

    fun getCurrentTime():String{
        val todayDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.US)
        val todayString = formatter.format(todayDate)
        return todayString
    }

    fun getCurrentLanguage(context: Activity, isChangeLanguage: Boolean) {
        if (getStringPref(context, SELECTED_LANGUAGE) == null || getStringPref(
                context,
                SELECTED_LANGUAGE
            ).equals("", true)) {
            setStringPref(
                context,
                SELECTED_LANGUAGE,
                context.resources.getString(R.string.language_english)
            )
        }
        val locale = if (getStringPref(context, SELECTED_LANGUAGE).equals(
                context.resources.getString(
                    R.string.language_english
                ), true
            )) {
            Locale("en-us")
        } else {
            Locale("ar")
        }

        //start
        val activityRes = context.resources
        val activityConf = activityRes.configuration
        val newLocale = locale
        if (getStringPref(context, SELECTED_LANGUAGE).equals(
                context.resources.getString(R.string.language_english),
                true
            )) {
            activityConf.setLocale(Locale("en-us")) // API 17+ only.
        } else {
            activityConf.setLocale(Locale("ar"))
        }
        activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)
        val applicationRes = context.applicationContext.resources
        val applicationConf = applicationRes.configuration
        applicationConf.setLocale(newLocale)
        applicationRes.updateConfiguration(applicationConf, applicationRes.displayMetrics)

        if (isChangeLanguage) {
            val intent = Intent(context, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            context.finish()
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }


    fun showErrorFullMsg(activity: Activity, message: String){
        val snackbar: TSnackbar = TSnackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            TSnackbar.LENGTH_SHORT
        )
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView: View = snackbar.getView()
        snackbarView.setBackgroundColor(Color.RED)
        val textView =snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackbar.show()
    }

    fun showSuccessFullMsg(activity: Activity, message: String){
        val snackbar: TSnackbar = TSnackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            TSnackbar.LENGTH_SHORT
        )
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView: View = snackbar.getView()
        snackbarView.setBackgroundColor(
            ResourcesCompat.getColor(
                activity.resources,
                R.color.colorPrimary,
                null
            )
        )
        val textView =snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackbar.show()
    }

    fun getCurrancy(act: Activity):String{
        return getStringPref(act, isCurrancy)!!
    }

    fun getDate(strDate: String):String{
        val curFormater = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dateObj = curFormater.parse(strDate)
        val postFormater = SimpleDateFormat("EEE dd MMM yyyy", Locale.US)
        return postFormater.format(dateObj!!)
    }

    @SuppressLint("PackageManagerGetSignatures")
    fun printKeyHash(context: Activity): String? {
        val packageInfo: PackageInfo
        var key: String? = null
        try {
            //getting application package name, as defined in manifest
            val packageName = context.applicationContext.packageName

            //Retriving package info
            packageInfo = context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            Log.e("Package Name=", context.applicationContext.packageName)
            for (signature in packageInfo.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = String(Base64.encode(md.digest(), 0))

                // String key = new String(Base64.encodeBytes(md.digest()));
                //getLog("Key Hash", key)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("Name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("No such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
        return key
    }

    fun closeKeyBoard(activity: Activity) {
        val view: View? = activity.currentFocus
        if (view != null) {
            try {
                val imm: InputMethodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

}