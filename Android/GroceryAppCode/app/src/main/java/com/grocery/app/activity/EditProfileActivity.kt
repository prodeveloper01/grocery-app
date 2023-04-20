package com.grocery.app.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.RestResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.model.LoginModel
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.alertErrorOrValidationDialog
import com.grocery.app.utils.Common.dismissLoadingProgress
import com.grocery.app.utils.Common.isCheckNetwork
import com.grocery.app.utils.Common.setImageUpload
import com.grocery.app.utils.Common.setRequestBody
import com.grocery.app.utils.Common.showErrorFullMsg
import com.grocery.app.utils.Common.showLoadingProgress
import com.grocery.app.utils.SharePreference
import com.grocery.app.utils.SharePreference.Companion.getStringPref
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_editprofile.*
import kotlinx.android.synthetic.main.activity_editprofile.ivBack
import kotlinx.android.synthetic.main.dlg_externalstorage.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class EditProfileActivity:BaseActivity() {

    private val SELECT_FILE = 201
    private val REQUEST_CAMERA = 202
    private var mSelectedFileImg: File? = null

    override fun setLayout(): Int {
       return R.layout.activity_editprofile
    }

    override fun initView() {
        Common.getCurrentLanguage(this@EditProfileActivity, false)
        edEmailAddress!!.setText(getStringPref(this@EditProfileActivity,SharePreference.userEmail))
        edUserName!!.setText(getStringPref(this@EditProfileActivity,SharePreference.userName))
        tvMobileNumber!!.text=getStringPref(this@EditProfileActivity,SharePreference.userMobile)
        Glide.with(this@EditProfileActivity).load(getStringPref(this@EditProfileActivity,SharePreference.userProfile)).placeholder(ResourcesCompat.getDrawable(resources,R.drawable.ic_placeholder,null)).into(ivProfile)
        if(SharePreference.getStringPref(this@EditProfileActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
    }

    fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                finish()
            }
            R.id.tvSave->{
                if (edUserName.text.toString() == "") {
                    showErrorFullMsg(this@EditProfileActivity,resources.getString(R.string.validation_all))
                } else {
                    if (isCheckNetwork(this@EditProfileActivity)) {
                        mCallApiEditProfile()
                    } else {
                        alertErrorOrValidationDialog(this@EditProfileActivity, resources.getString(R.string.no_internet))
                    }
                }
            }
            R.id.ivGellary->{
                getExternalStoragePermission()
            }

        }
    }

    /*-------------Image Upload Code-------------*/
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data)
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data!!)
            }
        }
    }

    private fun getExternalStoragePermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        imageSelectDialog(this@EditProfileActivity)
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Common.settingDialog(this@EditProfileActivity)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }

    @SuppressLint("InlinedApi")
    fun imageSelectDialog(act: Activity) {
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
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_externalstorage, null, false)

            val finalDialog: Dialog = dialog
            mView.tvSetImageCamera.setOnClickListener {
                finalDialog.dismiss()
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(
                    intent,
                    REQUEST_CAMERA
                )
            }
            mView.tvSetImageGallery.setOnClickListener {
                finalDialog.dismiss()
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                //   intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
            }
            dialog.setContentView(mView)
            if (!act.isFinishing) dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    private fun onSelectFromGalleryResult(data: Intent?) {
        var bm: Bitmap? = null
        if (data != null) {
            try {
                bm  = when {
                    Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        data.data
                    )
                    else -> {
                        val source = ImageDecoder.createSource(this.contentResolver, data.data!!)
                        ImageDecoder.decodeBitmap(source)
                    }
                }
                data.data
                //  bm=getCorrectlyOrientedImage(this@EditProfileActivity,selectedImageUri,340)
                val bytes = ByteArrayOutputStream()
                bm!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
                mSelectedFileImg = File(
                    Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis().toString() + ".jpg"
                )
                val fo: FileOutputStream
                try {
                    mSelectedFileImg!!.createNewFile()
                    fo = FileOutputStream(mSelectedFileImg!!)
                    fo.write(bytes.toByteArray())
                    fo.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        ivProfile.setImageBitmap(bm);
    }

    @Suppress("DEPRECATION")
    private fun onCaptureImageResult(data: Intent) {
        val thumbnail = data.extras!!["data"] as Bitmap?
        val bytes = ByteArrayOutputStream()
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        mSelectedFileImg = File(
            Environment.getExternalStorageDirectory(),
            System.currentTimeMillis().toString() + ".jpeg"
        )
        val fo: FileOutputStream
        try {
            mSelectedFileImg!!.createNewFile()
            fo = FileOutputStream(mSelectedFileImg)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Glide.with(this@EditProfileActivity)
            .load(Uri.parse("file://" + mSelectedFileImg!!.getPath()))
            .into(ivProfile)
    }



    private fun mCallApiEditProfile() {
        showLoadingProgress(this@EditProfileActivity)
        val call: Call<RestResponse<LoginModel>>? = if(mSelectedFileImg!=null){
            ApiClient.getClient.setProfile(setRequestBody(getStringPref(this@EditProfileActivity,SharePreference.userId)!!),setRequestBody(edUserName.text.toString()),setImageUpload("image",mSelectedFileImg!!))
        }else {
            ApiClient.getClient.setProfile(setRequestBody(getStringPref(this@EditProfileActivity,SharePreference.userId)!!),setRequestBody(edUserName.text.toString()),null)
        }
        call!!.enqueue(object : Callback<RestResponse<LoginModel>> {
            override fun onResponse(call: Call<RestResponse<LoginModel>>, response: Response<RestResponse<LoginModel>>) {
                if(response.code()==200){
                    dismissLoadingProgress()
                    val editProfileResponce: RestResponse<LoginModel> = response.body()!!
                    if(editProfileResponce.status==1){
                        if (editProfileResponce.status == 1) {
                            val loginModel: LoginModel = editProfileResponce.data!!
                            setProfileData(loginModel,editProfileResponce.message)
                        }
                    }else if(editProfileResponce.status==0){
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(this@EditProfileActivity,editProfileResponce.message)
                    }
                }else{
                    val restResponse = response.errorBody()!!.string()
                    val jsonObject= JSONObject(restResponse)
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        this@EditProfileActivity,
                        jsonObject.getString("message")
                    )
                }

            }

            override fun onFailure(call: Call<RestResponse<LoginModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@EditProfileActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })

    }

    private fun setProfileData(dataResponse: LoginModel, message: String?) {
        SharePreference.setStringPref(
            this@EditProfileActivity,
            SharePreference.userId,
            dataResponse.id.toString()
        )
        SharePreference.setStringPref(
            this@EditProfileActivity,
            SharePreference.userName,
            dataResponse.name.toString()
        )
        SharePreference.setStringPref(
            this@EditProfileActivity,
            SharePreference.userMobile,
            dataResponse.mobile.toString()
        )
        SharePreference.setStringPref(
            this@EditProfileActivity,
            SharePreference.userEmail,
            dataResponse.email.toString()
        )
        SharePreference.setStringPref(
            this@EditProfileActivity,
            SharePreference.userProfile,
            dataResponse.profile_image.toString()
        )
        finish()
    }


    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@EditProfileActivity, false)
    }

    override fun onPause() {
        super.onPause()
        Common.getCurrentLanguage(this@EditProfileActivity, false)
    }
}