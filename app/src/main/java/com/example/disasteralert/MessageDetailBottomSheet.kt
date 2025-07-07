package com.example.disasteralert

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.disasteralert.api.ReportDeleteRequest
import com.example.disasteralert.api.RetrofitClient
import com.example.disasteralert.api.RtdEvent
import com.example.disasteralert.api.VoteRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val userId = context?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            ?.getString("user_id", null) ?: "default_user"

        tvTitle.text = message.title
        tvContent.text = message.content

        btnReport.setOnClickListener {
            val reportId = message.id ?: ""
            val request = VoteRequest(report_id = reportId, user_id = userId)

            // ✅ 요청 객체를 JSON으로 로그 출력
            val requestJson = com.google.gson.Gson().toJson(request)
            android.util.Log.d("VoteRequest_JSON", "Sending request: $requestJson")

            RetrofitClient.reportService.voteReport(request).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    android.util.Log.d("VoteRequest_Response", "Status: ${response.code()} - success: ${response.isSuccessful}")
                    Toast.makeText(context, "해제 요청이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                    dismiss()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    android.util.Log.e("VoteRequest_Failure", "요청 실패: ${t.localizedMessage}", t)
                    Toast.makeText(context, "해제 요청 실패: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
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

                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                val desiredHeight = (screenHeight * 0.66).toInt()
                it.layoutParams.height = desiredHeight
                it.requestLayout()

                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            dialog.window?.setDimAmount(0.4f)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        return dialog
    }
}