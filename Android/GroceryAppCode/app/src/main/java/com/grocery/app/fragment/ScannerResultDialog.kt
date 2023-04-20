package com.grocery.app.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.grocery.app.R
import kotlinx.android.synthetic.main.fragment_scanner_result_dialog_list_dialog.*

const val ARG_SCANNING_RESULT = "scanning_result"

class ScannerResultDialog(private val listener: DialogDismissListener) :
    BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_scanner_result_dialog_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannedResult = arguments?.getString(ARG_SCANNING_RESULT)
        edtResult.setText(scannedResult)
        btnCopy.setOnClickListener {
            val clipboard =
                ContextCompat.getSystemService(requireContext(), ClipboardManager::class.java)
            val clip = ClipData.newPlainText("label", scannedResult)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
            dismissAllowingStateLoss()
        }
    }


    companion object {

        fun newInstance(
            scanningResult: String,
            listener: DialogDismissListener
        ): ScannerResultDialog =
            ScannerResultDialog(listener).apply {
                arguments = Bundle().apply {
                    putString(ARG_SCANNING_RESULT, scanningResult)
                }
            }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onDismiss()
    }

    interface DialogDismissListener {
        fun onDismiss()
    }
}