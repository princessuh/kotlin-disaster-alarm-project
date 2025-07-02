package com.example.disasteralert

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.example.disasteralert.R
import com.example.disasteralert.api.DisasterEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DisasterDetailBottomSheet(
    private val event: DisasterEvent,
    private val onClose: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_disaster_detail, container, false)

        val tvType = view.findViewById<TextView>(R.id.tv_disaster_type)
        val tvDesc = view.findViewById<TextView>(R.id.tv_disaster_desc)
        val btnClose = view.findViewById<Button>(R.id.btn_close)

        tvType.text = event.disaster_type
        tvDesc.text = event.description

        btnClose.setOnClickListener {
            onClose()
            dismiss()
        }

        return view
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}
