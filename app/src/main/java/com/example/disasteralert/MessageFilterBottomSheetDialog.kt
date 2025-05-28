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

        val cbAllInfo = view.findViewById<CheckBox>(R.id.cb_all_info)
        val cbNews = view.findViewById<CheckBox>(R.id.cb_news)
        val cbSpecial = view.findViewById<CheckBox>(R.id.cb_special)
        val cbReport = view.findViewById<CheckBox>(R.id.cb_report)

        infoTypeCheckBoxes = listOf(cbAllInfo, cbNews, cbSpecial, cbReport)

        val cbAll = view.findViewById<CheckBox>(R.id.cb_all)
        val cbTyphoon = view.findViewById<CheckBox>(R.id.cb_typhoon)
        val cbWeather = view.findViewById<CheckBox>(R.id.cb_weather)
        val cbEarthquake = view.findViewById<CheckBox>(R.id.cb_earthquake)
        val cbEpidemic = view.findViewById<CheckBox>(R.id.cb_epidemic)
        val cbFire = view.findViewById<CheckBox>(R.id.cb_fire)
        val cbFineDust = view.findViewById<CheckBox>(R.id.cb_fine_dust)

        disasterTypeCheckBoxes = listOf(cbAll, cbTyphoon, cbWeather, cbEarthquake, cbEpidemic, cbFire, cbFineDust)

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

        // 🟦 cb_all_info 클릭 시 전체 토글
        cbAllInfo.setOnClickListener {
            val check = cbAllInfo.isChecked
            listOf(cbNews, cbSpecial, cbReport).forEach { it.isChecked = check }
        }

        // 개별 정보유형 체크박스 클릭 시 전체 체크 상태 동기화
        listOf(cbNews, cbSpecial, cbReport).forEach { cb ->
            cb.setOnClickListener {
                cbAllInfo.isChecked = listOf(cbNews, cbSpecial, cbReport).all { it.isChecked }
            }
        }

        // cb_all 클릭 시 전체 토글
        cbAll.setOnClickListener {
            val check = cbAll.isChecked
            listOf(cbTyphoon, cbWeather, cbEarthquake, cbEpidemic, cbFire, cbFineDust).forEach { it.isChecked = check }
        }

        // 개별 재난유형 체크박스 클릭 시 전체 체크 상태 동기화
        listOf(cbTyphoon, cbWeather, cbEarthquake, cbEpidemic, cbFire, cbFineDust).forEach { cb ->
            cb.setOnClickListener {
                cbAll.isChecked = listOf(cbTyphoon, cbWeather, cbEarthquake, cbEpidemic, cbFire, cbFineDust).all { it.isChecked }
            }
        }

        view.findViewById<MaterialButton>(R.id.btn_apply_filter).setOnClickListener {
            val selectedInfoTypes = infoTypeCheckBoxes.filter { it != cbAllInfo && it.isChecked }.map { it.text.toString() }
            val selectedDisasterTypes = disasterTypeCheckBoxes.filter { it != cbAll && it.isChecked }.map { it.text.toString() }

            onFilterApplied(selectedInfoTypes, selectedDisasterTypes)
            dismiss()
        }

        return view
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}
