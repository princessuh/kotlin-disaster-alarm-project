package com.example.disasteralert

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.example.disasteralert.api.RtdEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RtdDetailBottomSheet(
    private val event: RtdEvent,
    private val onClose: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_rtd_detail, container, false)

        val tvLocation = view.findViewById<TextView>(R.id.tv_rtd_loc)
        val tvDetails = view.findViewById<TextView>(R.id.tv_rtd_details)
        val btnClose = view.findViewById<Button>(R.id.btn_close_rtd)

        tvLocation.text = event.rtd_loc
        tvDetails.text = event.rtd_details.joinToString("\n")

        btnClose.setOnClickListener {
            onClose()
            dismiss()
        }

        return view
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}
