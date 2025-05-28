package com.example.disasteralert

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import androidx.core.content.ContextCompat

class FilterBottomSheetDialog(
    private val onFilterApplied: (List<String>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var checkBoxes: List<CheckBox>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_filter_disaster, container, false)

        val cbAll = view.findViewById<CheckBox>(R.id.cb_all)

        checkBoxes = listOf(
            view.findViewById(R.id.cb_typhoon),
            view.findViewById(R.id.cb_weather),
            view.findViewById(R.id.cb_earthquake),
            view.findViewById(R.id.cb_epidemic),
            view.findViewById(R.id.cb_fire),
            view.findViewById(R.id.cb_fine_dust)
        )

        val allCheckBoxes = checkBoxes + cbAll

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

        /** 전체 체크박스 클릭 시 모든 체크박스 체크 및 해제 */
        cbAll.setOnCheckedChangeListener { _, isChecked ->
            checkBoxes.forEach { it.isChecked = isChecked }
        }

        /** 개별 체크 박스 선택 시 전체 체크 박스 업데이트 */
        checkBoxes.forEach { cb ->
            cb.setOnCheckedChangeListener { _, _ ->
                cbAll.setOnCheckedChangeListener(null) // 무한 루프 방지
                cbAll.isChecked = checkBoxes.all { it.isChecked }
                cbAll.setOnCheckedChangeListener { _, isChecked ->
                    checkBoxes.forEach { it.isChecked = isChecked }
                }
            }
        }

        view.findViewById<MaterialButton>(R.id.btn_apply_filter).setOnClickListener {
            val selected = checkBoxes.filter { it.isChecked }.map { it.text.toString() }
            onFilterApplied(selected)
            dismiss()
        }

        return view
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}
