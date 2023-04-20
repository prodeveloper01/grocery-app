package com.grocery.app.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.core.TorchState.OFF
import androidx.camera.core.TorchState.ON
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.grocery.app.R
import com.grocery.app.api.ApiClient
import com.grocery.app.api.RestResponse
import com.grocery.app.api.SingleResponse
import com.grocery.app.fragment.ScannerResultDialog
import com.grocery.app.model.BarcodeData
import com.grocery.app.model.PaginationModel
import com.grocery.app.model.SearchProductModel
import com.grocery.app.utils.Common
import com.grocery.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_barcode_scanning.*
import kotlinx.android.synthetic.main.fragment_search.*
import maulik.barcodescanner.analyzer.MLKitBarcodeAnalyzer
import maulik.barcodescanner.analyzer.ScanningResultListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val ARG_SCANNING_SDK = "scanning_SDK"

class BarcodeScanningActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, BarcodeScanningActivity::class.java).apply {}
            context.startActivity(starter)
        }
    }

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_barcode_scanning)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
        overlay.post {
            overlay.setViewFinder()
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {

        if (isDestroyed || isFinishing) {
            //This check is to avoid an exception when trying to re-bind use cases but user closes the activity.
            //java.lang.IllegalArgumentException: Trying to create use case mediator with destroyed lifecycle.
            return
        }

        cameraProvider?.unbindAll()

        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(cameraPreview.width, cameraPreview.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation: Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation
            }
        }
        orientationEventListener.enable()

        //switch the analyzers here, i.e. MLKitBarcodeAnalyzer, ZXingBarcodeAnalyzer
        class ScanningListener : ScanningResultListener {
            override fun onScanned(result: String) {
                runOnUiThread {
                    imageAnalysis.clearAnalyzer()
                    cameraProvider?.unbindAll()
                    callApiBarcodeScanner(result, cameraProvider)
         /*           ScannerResultDialog.newInstance(
                        result,
                        object : ScannerResultDialog.DialogDismissListener {
                            override fun onDismiss() {
                                bindPreview(cameraProvider)
                            }
                        }).show(supportFragmentManager, ScannerResultDialog::class.java.simpleName)*/
                }
            }
        }

        val analyzer: ImageAnalysis.Analyzer = MLKitBarcodeAnalyzer(ScanningListener())



        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        preview.setSurfaceProvider(cameraPreview.surfaceProvider)

        val camera =
            cameraProvider?.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            ivFlashControl.visibility = View.VISIBLE

            ivFlashControl.setOnClickListener {
                camera.cameraControl.enableTorch(!flashEnabled)
            }

            camera.cameraInfo.torchState.observe(this) {
                it?.let { torchState ->
                    if (torchState == TorchState.ON) {
                        flashEnabled = true
                        ivFlashControl.setImageResource(R.drawable.ic_round_flash_on)
                    } else {
                        flashEnabled = false
                        ivFlashControl.setImageResource(R.drawable.ic_round_flash_off)
                    }
                }
            }

        }


    }


    private fun callApiBarcodeScanner(barcodeNumber: String, cameraProvider: ProcessCameraProvider?) {

        Common.showLoadingProgress(this@BarcodeScanningActivity)
        val map = HashMap<String, String>()
        map["barcode_number"] = barcodeNumber

        val call = ApiClient.getClient.barcodeDetails(map)
        call.enqueue(object : Callback<RestResponse<BarcodeData>> {
            override fun onResponse(
                call: Call<RestResponse<BarcodeData>>,
                response: Response<RestResponse<BarcodeData>>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val responseData = response.body()?.data
                    if (response.body()?.status == 1) {
                        startActivity(
                            Intent(
                                this@BarcodeScanningActivity,
                                ItemDetailActivity::class.java
                            ).putExtra(
                                "Item_Id",
                                responseData?.id.toString()
                            ).putExtra(
                                "isItemActivity",
                                resources.getString(R.string.is_item_Act_home)
                            )
                        )
                    } else {
                        alertErrorOrValidationDialog(
                            this@BarcodeScanningActivity,
                            "Product not found"
                        ,cameraProvider
                        )

                    }
                    Log.e("responseBody", response.body().toString())
                }
            }

            override fun onFailure(call: Call<RestResponse<BarcodeData>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@BarcodeScanningActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    fun alertErrorOrValidationDialog(act: Activity, msg: String?,cameraProvider: ProcessCameraProvider?) {
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
                bindPreview(cameraProvider)

            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Shut down our background executor
        cameraExecutor.shutdown()
    }


}


