package com.grocery.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.SingleResponse
import com.grocery.app.base.BaseActivity
import com.grocery.app.utils.Common
import com.grocery.app.utils.Common.getDatePicker
import com.grocery.app.utils.FieldSelector
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_setaddress.*
import kotlinx.android.synthetic.main.activity_setaddress.ivBack
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class OrderSelectAddressActivity:BaseActivity() {
    private var selectDelivery=0
    var lat:Double=19.169815
    var lon:Double=77.319717
    var AUTOCOMPLETE_REQUEST_CODE: Int = 2
    var fieldSelector: FieldSelector? = null
    override fun setLayout(): Int {
        return R.layout.activity_setaddress
    }


    @SuppressLint("SetTextI18n")
    override fun initView() {
        Common.getCurrentLanguage(this@OrderSelectAddressActivity, false)
        Places.initialize(applicationContext,SharePreference.getStringPref(this@OrderSelectAddressActivity,SharePreference.map_key)!!)
        fieldSelector = FieldSelector()
      //  edAddress.text ="Empire State Building, 350, 5th Avenue, Korea Town, Midtown South, New York, New York, 10001, USA"
       /* edBuilding.text ="350, 5th Avenue"
        edLandmark.text ="Midtown South"*/

        Common.getCurrentDate(tvDatePicker)

        tvDatePicker.setOnClickListener {
           getDatePicker(this@OrderSelectAddressActivity,tvDatePicker)
        }
        if(SharePreference.getStringPref(this@OrderSelectAddressActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack?.rotation= 180F

        }else{
            ivBack?.rotation= 0F

        }
        /*llDeliveryAddress.setOnClickListener {
            getLocation()
        }*/

        selectDelivery=1
        cvDeliveryAddress.visibility= View.VISIBLE
        cvPickup.setCardBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
        cvDelivery.setCardBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                R.color.colorPrimary,
                null
            )
        )

        cvPickup.setOnClickListener {
            selectDelivery=2
            tvDateTitle.text=resources.getString(R.string.pickup_date)
            cvDeliveryAddress.visibility= View.GONE
            cvPickup.setCardBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorPrimary,
                    null
                )
            )
            cvDelivery.setCardBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.white,
                    null
                )
            )

            tvPickup.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
                    tvDelivery.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
        }

        cvDelivery.setOnClickListener {
            selectDelivery=1
            tvDateTitle.text=resources.getString(R.string.delivery_date)

            cvDeliveryAddress.visibility= View.VISIBLE
            cvPickup.setCardBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.white,
                    null
                )
            )
            cvDelivery.setCardBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorPrimary,
                    null
                )
            )

            tvPickup.setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
                    tvDelivery.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
        }

        ivBack.setOnClickListener {
            finish()
        }
        tvSelectAddress?.setOnClickListener {
            val intent=Intent(this@OrderSelectAddressActivity,GetAddressActivity::class.java)
            intent.putExtra("isComeFromSelectAddress",true)
            startActivityForResult(intent,500)
        }
        tvProceed.setOnClickListener {
             if(selectDelivery==1){
                 if(tvDatePicker.text.toString() == ""){
                     Common.showErrorFullMsg(this@OrderSelectAddressActivity, resources.getString(R.string.validation_date))
                 }else if(edAddress.text.toString() == ""){
                     Common.showErrorFullMsg(
                         this@OrderSelectAddressActivity,
                         resources.getString(R.string.validation_delivery_address)
                     )
                 }else if(edBuilding.text.toString() == ""){
                     Common.showErrorFullMsg(
                         this@OrderSelectAddressActivity,
                         resources.getString(R.string.validation_delivery_Building_No)
                     )
                 }else if(edLandmark.text.toString() == ""){
                     Common.showErrorFullMsg(
                         this@OrderSelectAddressActivity,
                         resources.getString(R.string.validation_delivery_Landmark)
                     )
                 }else if(edPinCode.text.toString() == ""){
                     Common.showErrorFullMsg(
                         this@OrderSelectAddressActivity,
                         resources.getString(R.string.validation_pincode)
                     )
                 }else{
                     val hasmap = HashMap<String, String>()
                     hasmap["pincode"] = edPinCode.text.toString()
                     if (Common.isCheckNetwork(this@OrderSelectAddressActivity)) {
                         callApiCheckPinCode(hasmap)
                     } else {
                         Common.alertErrorOrValidationDialog(
                             this@OrderSelectAddressActivity,
                             resources.getString(R.string.no_internet)
                         )
                     }

                 }
             }else{
                 val intent=Intent(this@OrderSelectAddressActivity,OrderSummaryActivity::class.java)
                 intent.putExtra("select_type",selectDelivery.toString())
                 intent.putExtra("address","")
                 intent.putExtra("building","")
                 intent.putExtra("Landmark","")
                 intent.putExtra("lat","")
                 intent.putExtra("lon","")
                 intent.putExtra("pincode","")
                 intent.putExtra("date",tvDatePicker.text.toString())
                 startActivity(intent)
             }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun getLocation() {
        val autocompleteIntent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN,
            fieldSelector!!.allFields
        ).build(this@OrderSelectAddressActivity)
        startActivityForResult(autocompleteIntent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun callApiCheckPinCode(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@OrderSelectAddressActivity)
        val call = ApiClient.getClient.setCheckPinCode(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if(response.code()==200){
                    Common.dismissLoadingProgress()
                    val singleResponse: SingleResponse = response.body()!!
                    if (singleResponse.status==0) {
                        edPinCode.setText("")
                        Common.showErrorFullMsg(
                            this@OrderSelectAddressActivity,
                            singleResponse.message!!
                        )
                    }else if (singleResponse.status==1) {
                        val intent=Intent(this@OrderSelectAddressActivity,OrderSummaryActivity::class.java)
                        intent.putExtra("select_type",selectDelivery.toString())
                        intent.putExtra("address",edAddress.text.toString())
                        intent.putExtra("building",edBuilding.text.toString())
                        intent.putExtra("Landmark",edLandmark.text.toString())
                        intent.putExtra("pincode",edPinCode.text.toString())
                        intent.putExtra("lat",lat.toString())
                        intent.putExtra("lon",lon.toString())
                        intent.putExtra("date",tvDatePicker.text.toString())
                        startActivity(intent)
                    }
                } else  {
                    val error= JSONObject(response.errorBody()!!.string())
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@OrderSelectAddressActivity,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@OrderSelectAddressActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@OrderSelectAddressActivity, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                edAddress.text=place.getAddress().toString()
                val latLng: String = place.latLng.toString()
                val tempArray = latLng.substring(latLng.indexOf("(") + 1, latLng.lastIndexOf(")")).split(",")
                    .toTypedArray()
                lat = tempArray[1].toDouble()
                lon = tempArray[0].toDouble()
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status: Status = Autocomplete.getStatusFromIntent(data!!)
                Common.showErrorFullMsg(this@OrderSelectAddressActivity,resources.getString(R.string.invalid_key))
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                Common.getLog("Nice", " RESULT_CANCELED : AutoComplete Places")
            }
        }else if(resultCode==500)
        {

            llAddress.visibility=View.VISIBLE
            llLandmark.visibility=View.VISIBLE
            llBuilding.visibility=View.VISIBLE
            llZipCode.visibility=View.VISIBLE
            edAddress.visibility=View.VISIBLE
            edAddress.text = data?.getStringExtra("Address")
            edBuilding.text = data?.getStringExtra("FlatNo")
            edLandmark.text = data?.getStringExtra("landMark")
            edPinCode.text = data?.getStringExtra("PinCode")

            val addresstype=data?.getIntExtra("addressType",0)
            val addressTypeArray= arrayOf(resources.getString(R.string.home), resources.getString(R.string.work),resources.getString(R.string.other))

            when (addresstype) {
                1 -> {
                    tvType.text = addressTypeArray[0]
                }
                2 -> {
                    tvType.text = addressTypeArray[1]

                }
                3 -> {
                    tvType.text = addressTypeArray[2]
                }
            }
        }
    }

}