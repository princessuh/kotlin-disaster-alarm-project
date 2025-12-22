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
        tvDetails.text = event.rtd_details?.joinToString("\n") ?: getString(R.string.detail_info_none)

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
