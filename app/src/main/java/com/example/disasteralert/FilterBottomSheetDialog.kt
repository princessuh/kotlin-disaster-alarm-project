package com.example.disasteralert

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FilterBottomSheetDialog(
    private val onFilterApplied: (List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var checkBoxes: List<CheckBox>
    private lateinit var chipGroupDetail: ChipGroup
    private lateinit var cbAll: CheckBox
    private lateinit var checkBoxListener: CompoundButton.OnCheckedChangeListener

    private val allDisasterLabels = listOf(
        "태풍", "호우", "홍수", "강풍", "대설",
        "폭염", "한파",
        "지진",
        "감염병",
        "산불", "일일화재",
        "미세먼지"
    )

    private val cbAllListener: CompoundButton.OnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            checkBoxes.forEach {
                it.setOnCheckedChangeListener(null)
                it.isChecked = isChecked
                updateCheckBoxStyle(it, isChecked)
                it.setOnCheckedChangeListener(checkBoxListener)
            }
            updateCheckBoxStyle(cbAll, isChecked)

            chipGroupDetail.removeAllViews()
            if (isChecked) {
                allDisasterLabels.forEach { label ->
                    val chip = Chip(requireContext()).apply {
                        text = label
                        isCheckable = true
                        isClickable = true
                    }
                    chipGroupDetail.addView(chip)
                }
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_filter_disaster, container, false)

        cbAll = view.findViewById(R.id.cb_all)

        checkBoxes = listOf(
            view.findViewById(R.id.cb_typhoon),
            view.findViewById(R.id.cb_weather),
            view.findViewById(R.id.cb_earthquake),
            view.findViewById(R.id.cb_epidemic),
            view.findViewById(R.id.cb_fire),
            view.findViewById(R.id.cb_fine_dust)
        )

        chipGroupDetail = view.findViewById(R.id.chip_group_detail)

        // ✅ 버튼 외형 세팅
        (checkBoxes + cbAll).forEach { cb ->
            cb.setButtonDrawable(android.R.color.transparent)
        }

        // ✅ 개별 체크박스 리스너 정의 (초기화 문제 방지)
        checkBoxListener = object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(clickedCb: CompoundButton?, isChecked: Boolean) {
                val cb = clickedCb as CheckBox
                updateCheckBoxStyle(cb, isChecked)

                cbAll.setOnCheckedChangeListener(null)
                cbAll.isChecked = checkBoxes.all { it.isChecked }
                updateCheckBoxStyle(cbAll, cbAll.isChecked)
                cbAll.setOnCheckedChangeListener(cbAllListener)

                // ✅ 다른 체크박스 해제 및 스타일 초기화
                checkBoxes.forEach { otherCb ->
                    if (otherCb != cb) {
                        otherCb.setOnCheckedChangeListener(null)
                        otherCb.isChecked = false
                        updateCheckBoxStyle(otherCb, false)
                        otherCb.setOnCheckedChangeListener(this)
                    }
                }

                chipGroupDetail.removeAllViews()
                if (isChecked) {
                    val chipLabels = when (cb.id) {
                        R.id.cb_typhoon -> listOf("태풍", "호우", "홍수", "강풍", "대설")
                        R.id.cb_weather -> listOf("폭염", "한파")
                        R.id.cb_earthquake -> listOf("지진")
                        R.id.cb_epidemic -> listOf("감염병")
                        R.id.cb_fire -> listOf("산불", "일일화재")
                        R.id.cb_fine_dust -> listOf("미세먼지")
                        else -> emptyList()
                    }

                    chipLabels.forEach { label ->
                        val chip = Chip(requireContext()).apply {
                            text = label
                            isCheckable = true
                            isClickable = true
                        }
                        chipGroupDetail.addView(chip)
                    }
                }
            }
        }

        // ✅ 리스너 등록
        cbAll.setOnCheckedChangeListener(cbAllListener)
        checkBoxes.forEach { cb ->
            cb.setOnCheckedChangeListener(checkBoxListener)
        }

        // ✅ 필터 적용 버튼
        view.findViewById<MaterialButton>(R.id.btn_apply_filter).setOnClickListener {
            val selectedChips = (0 until chipGroupDetail.childCount)
                .mapNotNull { chipGroupDetail.getChildAt(it) as? Chip }
                .filter { it.isChecked }
                .map { it.text.toString() }

            onFilterApplied(selectedChips.ifEmpty { emptyList() })
            dismiss()
        }

        return view
    }

    private fun updateCheckBoxStyle(cb: CheckBox, isChecked: Boolean) {
        val color = ContextCompat.getColor(
            cb.context,
            if (isChecked) R.color.blue_50 else R.color.grey_60
        )
        cb.setTextColor(color)
        cb.setBackgroundResource(R.drawable.checkbox_selector)
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private fun mapDisasterToCode(type: String): Int {
        return when (type) {
            "태풍" -> 31
            "호우" -> 32
            "홍수" -> 33
            "강풍" -> 34
            "대설" -> 35
            "폭염" -> 41
            "한파" -> 42
            "지진" -> 51
            "산불" -> 61
            "일일화재" -> 62
            "감염병", "미세먼지" -> 11
            else -> -1
        }
    }
}
