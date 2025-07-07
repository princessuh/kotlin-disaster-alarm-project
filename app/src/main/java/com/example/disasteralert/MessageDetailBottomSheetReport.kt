package com.example.disasteralert

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.disasteralert.api.ReportDeleteRequest
import com.example.disasteralert.api.RetrofitClient
import com.example.disasteralert.api.VoteRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageDetailBottomSheetReport(
    private val message: Message,
    private val onDeleteRequest: (Message) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottom_sheet_message_detail_report, container, false)

        val tvTitle = view.findViewById<TextView>(R.id.tv_detail_title)
        val tvContent = view.findViewById<TextView>(R.id.tv_detail_content)
        val btnDelete = view.findViewById<TextView>(R.id.btn_delete)
        val btnReport = view.findViewById<TextView>(R.id.btn_report)

        tvTitle.text = message.title
        tvContent.text = message.content

        // SharedPreferences에서 user_id 가져오기
        val userId = context?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            ?.getString("user_id", null) ?: "default_user"

        // ✅ 해제 요청
        btnReport.setOnClickListener {
            val request = VoteRequest(report_id = message.id ?: "", user_id = userId)
            Log.d("VOTE_REQUEST", "보내는 해제요청: ${Gson().toJson(request)}")

            RetrofitClient.reportService.voteReport(request).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "해제 요청이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(context, "해제 요청 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, "네트워크 오류: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // ✅ 삭제 요청
        btnDelete.setOnClickListener {
            val request = ReportDeleteRequest(report_id = message.id ?: "", user_id = userId)
            Log.d("DELETE_REQUEST", "보내는 삭제요청: ${Gson().toJson(request)}")

            RetrofitClient.reportService.deleteReport(request).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "제보가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        onDeleteRequest(message)
                        dismiss()
                    } else {
                        Toast.makeText(context, "삭제 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, "네트워크 오류: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
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
