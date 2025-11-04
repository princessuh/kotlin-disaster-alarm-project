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
    override fun onStart() {
        super.onStart()

        val dialog = dialog ?: return
        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)
            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0 // peekHeight 없애서 완전 확장
        }

        // 상태바까지 꽉 차게 높이 조정
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

}
