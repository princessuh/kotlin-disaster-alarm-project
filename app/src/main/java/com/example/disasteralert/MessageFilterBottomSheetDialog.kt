package com.example.disasteralert

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat

class MessageFilterBottomSheetDialog(
    private val onFilterApplied: (selectedInfoTypes: List<String>, selectedDisasterTypes: List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var infoTypeCheckBoxes: List<CheckBox>
    private lateinit var disasterTypeCheckBoxes: List<CheckBox>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_filter_message, container, false)

        // 🟦 정보유형 체크박스 (전체, 기사, 특보, 제보)
        infoTypeCheckBoxes = listOf(
            view.findViewById(R.id.cb_all),
            view.findViewById(R.id.cb_news),
            view.findViewById(R.id.cb_special),
            view.findViewById(R.id.cb_report)
        )

        // 🟨 재난유형 체크박스 (풍수해 ~ 미세먼지)
        disasterTypeCheckBoxes = listOf(
            view.findViewById(R.id.cb_typhoon),
            view.findViewById(R.id.cb_weather),
            view.findViewById(R.id.cb_earthquake),
            view.findViewById(R.id.cb_epidemic),
            view.findViewById(R.id.cb_fire),
            view.findViewById(R.id.cb_fine_dust)
        )

        // 공통 스타일 설정
        val allCheckBoxes = infoTypeCheckBoxes + disasterTypeCheckBoxes
        allCheckBoxes.forEach { cb ->
            cb.setButtonDrawable(android.R.color.transparent)
            cb.setOnCheckedChangeListener { _, isChecked ->
                val color = ContextCompat.getColor(
                    cb.context,
                    if (isChecked) R.color.blue_50 else R.color.grey_60
                )
                cb.setTextColor(color)
                cb.setBackgroundResource(R.drawable.checkbox_selector)
            }
        }

        // 적용 버튼
        view.findViewById<MaterialButton>(R.id.btn_apply_filter).setOnClickListener {
            val selectedInfoTypes = infoTypeCheckBoxes.filter { it.isChecked }.map { it.text.toString() }
            val selectedDisasterTypes = disasterTypeCheckBoxes.filter { it.isChecked }.map { it.text.toString() }

            onFilterApplied(selectedInfoTypes, selectedDisasterTypes)
            dismiss()
        }

        return view
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}
