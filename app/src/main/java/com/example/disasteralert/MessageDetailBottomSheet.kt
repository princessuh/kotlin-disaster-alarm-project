package com.example.disasteralert

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// 메시지 내역 본문 - 제보 제외
class MessageDetailBottomSheet(
    private val message: Message,
    private val onDeleteRequest: (Message) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottom_sheet_message_detail, container, false)

        val tvTitle = view.findViewById<TextView>(R.id.tv_detail_title)
        val tvContent = view.findViewById<TextView>(R.id.tv_detail_content)
        val btnReport = view.findViewById<TextView>(R.id.btn_report)

        tvTitle.text = message.title
        tvContent.text = message.content

        btnReport.setOnClickListener {
            onDeleteRequest(message)
            Toast.makeText(context, "해제 요청이 등록되었습니다.", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_top_white)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)

                // 화면의 2/3 높이로 설정
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                val desiredHeight = (screenHeight * 0.66).toInt()
                it.layoutParams.height = desiredHeight
                it.requestLayout()

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            dialog.window?.setDimAmount(0.4f)  // 뒷 배경 회색
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        return dialog
    }
}