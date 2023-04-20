package com.grocery.app.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.grocery.app.R
import com.grocery.app.adaptor.PinCodeListAdapter
import com.grocery.app.api.ApiClient
import com.grocery.app.api.ListResopone
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.model.PinCodeResponse
import com.grocery.app.utils.Common
import com.grocery.app.utils.FieldSelector
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_new_address.*
import kotlinx.android.synthetic.main.activity_new_address.ivBack
import kotlinx.android.synthetic.main.cell_address_list.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ActivityNewAddress : BaseActivity() {
    override fun setLayout(): Int = R.layout.activity_new_address
    var type = 0
    private var isClick = false
    private var isFirstTime = false
    private var addressId = 0
    var lat: Double = 0.0
    var lon: Double = 0.0
    var AUTOCOMPLETE_REQUEST_CODE: Int = 2
    var fieldSelector: FieldSelector? = null
    val addressTypeArray = ArrayList<String>()
//        arrayOf(resources.getString(R.string.select_address),resources.getString(R.string.home), resources.getString(R.string.work),resources.getString(R.string.other))
    var pinCodeArray = ArrayList<String>()

    private var addressType = 0
    override fun initView() {
//        Places.initialize(applicationContext, ApiClient.MapKey)
          Places.initialize(
              applicationContext,
              SharePreference.getStringPref(this@ActivityNewAddress, SharePreference.map_key)!!
          )


        fieldSelector = FieldSelector()
        addressType()
        callGetNeighbourhood()
        tvSaveAddress?.setOnClickListener {
            if (Common.isCheckNetwork(this@ActivityNewAddress)) {
                validation()
            } else {
                Common.alertErrorOrValidationDialog(
                        this@ActivityNewAddress,
                        resources.getString(R.string.no_internet)
                )

            }
        }

        if(SharePreference.getStringPref(this@ActivityNewAddress, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        getdata()
        type = intent.getIntExtra("Type", 0)
        if (type == 1) {
            isClick = true
            tvAddressTitle?.text = resources.getString(R.string.edit_address)
            tvSaveAddress?.text = resources.getString(R.string.update_address)
        } else {
            tvAddressTitle?.text = resources.getString(R.string.new_address)
            tvSaveAddress?.text = resources.getString(R.string.save_address)
//            edtAddress.text = "New York, NY, USA"
//            lat = 40.7127753
//            lon = -74.0059728
//            edtLandMark.setText("Central Park")
//            edtDoorNo.setText("4043")
        }
//        edtAddress.isEnabled = false
//        edtLandMark.isEnabled = false
//        edtDoorNo.isEnabled = false


        tvAddressType.setOnClickListener {
            isClick = true
            spAddressType.performClick()
        }
        ivBack?.setOnClickListener {
            finish()
        }
        edtZipCode.setOnClickListener {
            if(pinCodeArray.size>0)
            {
                openPinCodeDialog()

            }else
            {
                Common.alertErrorOrValidationDialog(this@ActivityNewAddress,resources.getString(R.string.pincode_not_found))
            }
        }
             edtAddress?.setOnClickListener {
                 getLocation()
             }

        val adapter = ArrayAdapter(
                this@ActivityNewAddress,
                R.layout.textview_spinner,
                addressTypeArray
        )
        adapter.setDropDownViewResource(R.layout.textview_spinner)
        spAddressType.adapter = adapter


        spAddressType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.e("erore", "error")
            }

            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                if (isClick) {
                    if (position != 0) {
                        tvAddressType.text = addressTypeArray[position]
                    }
                    when (position) {
                        1 -> {
                            addressType = 1
                        }
                        2 -> {
                            addressType = 2

                        }
                        3 -> {
                            addressType = 3

                        }
                    }
                    Log.e("addressType", addressType.toString())
                }

            }
        }


    }

    private fun addressType()
    {
        addressTypeArray.add(resources.getString(R.string.select_address))
        addressTypeArray.add(resources.getString(R.string.home))
        addressTypeArray.add(resources.getString(R.string.work))
        addressTypeArray.add(resources.getString(R.string.other))
    }

    private fun getLocation() {
        val autocompleteIntent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fieldSelector!!.allFields
        ).build(this@ActivityNewAddress)
        startActivityForResult(autocompleteIntent, AUTOCOMPLETE_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                AutocompleteActivity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    edtAddress.text=place.address
                    val latLng: String = place.latLng.toString()
                    val tempArray =
                            latLng.substring(latLng.indexOf("(") + 1, latLng.lastIndexOf(")"))
                                    .split(",")
                                    .toTypedArray()
                    lat = tempArray[1].toDouble()
                    lon = tempArray[0].toDouble()
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    Common.showErrorFullMsg(this@ActivityNewAddress, resources.getString(R.string.invalid_key))
                }
                AutocompleteActivity.RESULT_CANCELED -> {
                    Common.getLog("Nice", " RESULT_CANCELED : AutoComplete Places")
                }
            }
        }
    }

    private fun getdata() {
//        edtFullName.setText(intent.getStringExtra("FullName"))
        addressId = intent.getIntExtra("address_id", 0)
        lat = intent.getStringExtra("lat")?.toDouble() ?: 0.00
        lon = intent.getStringExtra("long")?.toDouble() ?: 0.00
        addressId = intent.getIntExtra("address_id", 0)
        edtAddress.text = intent.getStringExtra("Address")
        edtZipCode.text = intent.getStringExtra("PinCode")
//        edtPhoneNumber.setText(intent.getStringExtra("PhoneNumber"))
        edtDoorNo.setText(intent.getStringExtra("FlatNo"))
        edtLandMark.setText(intent.getStringExtra("landMark"))
        addressType = intent.getIntExtra("addressType", 0)

        when (addressType) {
            1 -> {
                tvAddressType.text = addressTypeArray[1]
            }
            2 -> {
                tvAddressType.text = addressTypeArray[2]

            }
            3 -> {
                tvAddressType.text = addressTypeArray[3]
            }
        }
    }


    private fun callGetNeighbourhood() {
        val call = ApiClient.getClient.getNeighbourhood()
        call.enqueue(object : Callback<ListResopone<PinCodeResponse>> {
            override fun onResponse(call: Call<ListResopone<PinCodeResponse>>, response: Response<ListResopone<PinCodeResponse>>) {
                if (response.code() == 200) {
                    pinCodeArray.clear()
                    for (i in 0 until response.body()?.data?.size!!) {
                        val array = response.body()?.data
                        pinCodeArray.add(array?.get(i)?.pincode.toString())
                    }

                }
            }
            override fun onFailure(call: Call<ListResopone<PinCodeResponse>>, t: Throwable) {
            }


        })
    }

    private fun openPinCodeDialog() {
        var dialog: Dialog? = null

        dialog?.dismiss()
        dialog = Dialog(this@ActivityNewAddress, R.style.AppCompatAlertDialogStyleBig)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        val mInflater = LayoutInflater.from(this@ActivityNewAddress)
        val mView = mInflater.inflate(R.layout.dlg_picode, null, false)
        val adapter = PinCodeListAdapter(pinCodeArray) { s: String, i: Int ->
            if (s == "ItemClick") {
                edtZipCode.text = pinCodeArray[i]
                dialog.dismiss()
            }
        }

        val rvPinCodeList: RecyclerView = mView.findViewById(R.id.rvPinCodeList)
        rvPinCodeList.adapter = adapter
        rvPinCodeList.layoutManager = LinearLayoutManager(this@ActivityNewAddress)
        rvPinCodeList.itemAnimator = DefaultItemAnimator()
        rvPinCodeList.isNestedScrollingEnabled = true


        dialog.setContentView(mView)
        dialog.show()
    }

    private fun validation() {

        when {
//            edtFullName.text.toString() == "" -> {
//                Common.showErrorFullMsg(
//                        this@ActivityNewAddress,
//                        resources.getString(R.string.validation_all)
//                )
//            }


//            edtPhoneNumber.text.toString() == "" -> {
//                Common.showErrorFullMsg(
//                        this@ActivityNewAddress,
//                        resources.getString(R.string.validation_all)
//                )
//            }
            tvAddressType.text.toString() == "" -> {
                Common.showErrorFullMsg(
                        this@ActivityNewAddress,
                        resources.getString(R.string.validation_all)
                )
            }
            edtAddress.text.toString() == "" -> {
                Common.showErrorFullMsg(
                        this@ActivityNewAddress,
                        resources.getString(R.string.validation_all)
                )
            }
            edtDoorNo.text.toString() == "" -> {
                Common.showErrorFullMsg(
                        this@ActivityNewAddress,
                        resources.getString(R.string.validation_all)
                )
            }
            edtLandMark.text.toString() == "" -> {
                Common.showErrorFullMsg(
                        this@ActivityNewAddress,
                        resources.getString(R.string.validation_all)
                )
            }
            edtZipCode.text.toString() == "" -> {
                Common.showErrorFullMsg(
                        this@ActivityNewAddress,
                        resources.getString(R.string.validation_all)
                )
            }
            else -> {
                val request = HashMap<String, String>()
                request["address_type"] = addressType.toString()
                request["address"] = edtAddress.text.toString()
                request["building"] = edtDoorNo.text.toString()
//                request["full_name"] = edtFullName.text.toString()
//                request["mobile"] = edtPhoneNumber.text.toString()
                request["landmark"] = edtLandMark.text.toString()
                request["pincode"] = edtZipCode.text.toString()
                request["lang"] = lon.toString()
                request["lat"] = lat.toString()
                request["user_id"] =
                        SharePreference.getStringPref(this@ActivityNewAddress, SharePreference.userId)
                                ?: ""

                if (type == 1) {
                    request["address_id"] = addressId.toString()
                    Log.e("request", request.toString())
                    callApiUpdateAddress(request)
                } else {
                    callApiAddAddress(request)
                }
            }
        }
    }

    private fun callApiAddAddress(addressRequest: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActivityNewAddress)

        val call = ApiClient.getClient.addAddress(addressRequest)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                Common.dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        Common.isAddOrUpdated = true
                        finish()
                    } else {
                        Common.showErrorFullMsg(
                                this@ActivityNewAddress,
                                response.body()?.message.toString()
                        )
                    }
                } else {
                    Common.alertErrorOrValidationDialog(
                            this@ActivityNewAddress,
                            resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                        this@ActivityNewAddress,
                        resources.getString(R.string.error_msg)
                )

            }
        })
    }


    private fun callApiUpdateAddress(addressRequest: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActivityNewAddress)


        val call = ApiClient.getClient.updateAddress(addressRequest)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                    call: Call<SingleResponse>,
                    response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    Common.isAddOrUpdated = true

                    if (response.body()?.status == 1) {

                        finish()
                    } else {
                        Common.showErrorFullMsg(
                                this@ActivityNewAddress,
                                response.body()?.message.toString()
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                            this@ActivityNewAddress,
                            resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                        this@ActivityNewAddress,
                        resources.getString(R.string.error_msg)
                )

            }
        })
    }
}