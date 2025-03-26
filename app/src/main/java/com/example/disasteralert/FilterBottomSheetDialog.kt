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

        // 체크박스 리스트
        checkBoxes = listOf(
            view.findViewById(R.id.cb_typhoon),
            view.findViewById(R.id.cb_weather),
            view.findViewById(R.id.cb_earthquake),
            view.findViewById(R.id.cb_epidemic),
            view.findViewById(R.id.cb_fire),
            view.findViewById(R.id.cb_fine_dust)
        )

        checkBoxes.forEach { cb ->
            cb.setButtonDrawable(android.R.color.transparent)
            cb.setOnCheckedChangeListener { _, isChecked ->
                val color = ContextCompat.getColor(
                    cb.context,  // 여기 수정!
                    if (isChecked) R.color.blue_50 else R.color.grey_60
                )
                cb.setTextColor(color)
                cb.setBackgroundResource(R.drawable.checkbox_selector)
            }
        }

        view.findViewById<MaterialButton>(R.id.btn_apply_filter).setOnClickListener {
            val selected = checkBoxes.filter { it.isChecked }.map { it.text.toString() }
            onFilterApplied(selected)
            dismiss()
        }

        return view
    }
}
