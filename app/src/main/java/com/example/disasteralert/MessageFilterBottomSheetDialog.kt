package com.example.disasteralert

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MessageFilterBottomSheetDialog(
    private val onFilterApplied: (
        selectedDisasters: List<String>,
        province: String?,
        city: String?,
        district: String?,
        period: String?
    ) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var checkBoxes: List<CheckBox>
    private lateinit var chipGroupDetail: ChipGroup
    private lateinit var cbAll: CheckBox
    private lateinit var checkBoxListener: CompoundButton.OnCheckedChangeListener

    private lateinit var spinnerProvince: Spinner
    private lateinit var spinnerCity: Spinner
    private lateinit var spinnerDistrict: Spinner

    private lateinit var cb1Month: CheckBox
    private lateinit var cb1Week: CheckBox
    private lateinit var cb1Day: CheckBox
    private lateinit var timeCheckBoxes: List<CheckBox>

    private var selectedProvince: String? = null
    private var selectedCity: String? = null
    private var selectedDistrict: String? = null
    private var selectedPeriod: String? = null

    private val allDisasterLabels = listOf(
        "태풍", "호우", "홍수", "강풍", "대설",
        "폭염", "한파", "지진", "감염병",
        "산불", "일일화재", "미세먼지"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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

        spinnerProvince = view.findViewById(R.id.spinner_province)
        spinnerCity = view.findViewById(R.id.spinner_city)
        spinnerDistrict = view.findViewById(R.id.spinner_district)

        cb1Month = view.findViewById(R.id.cb_1month)
        cb1Week = view.findViewById(R.id.cb_1week)
        cb1Day = view.findViewById(R.id.cb_1day)
        timeCheckBoxes = listOf(cb1Month, cb1Week, cb1Day)

        setupSpinners()
        setupCheckBoxListeners()
        setupTimeCheckBoxListeners()

        (checkBoxes + cbAll + timeCheckBoxes).forEach { cb ->
            cb.setButtonDrawable(android.R.color.transparent)
        }

        view.findViewById<MaterialButton>(R.id.btn_apply_filter).setOnClickListener {
            var selectedChips = (0 until chipGroupDetail.childCount)
                .mapNotNull { chipGroupDetail.getChildAt(it) as? Chip }
                .filter { it.isChecked }
                .map { it.text.toString() }

            if (selectedChips.isEmpty()) selectedChips = allDisasterLabels

            onFilterApplied(
                selectedChips,
                selectedProvince,
                selectedCity,
                selectedDistrict,
                selectedPeriod
            )
            dismiss()
        }

        return view
    }

    /** ✅ 지역 스피너: RegionDataProvider 연동 */
    private fun setupSpinners() {
        val provinces = listOf("전체") + RegionDataProvider.regionData.keys.toList()
        val provinceAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provinces)
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProvince.adapter = provinceAdapter

        spinnerProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = provinces[position]
                selectedProvince = if (selected == "전체") null else selected

                val cities = if (selectedProvince != null) {
                    listOf("전체") + (RegionDataProvider.regionData[selectedProvince]?.keys?.toList() ?: emptyList())
                } else listOf("전체")

                val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cities)
                cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCity.adapter = cityAdapter

                selectedCity = null
                selectedDistrict = null
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val province = selectedProvince
                val cities = if (province != null) {
                    listOf("전체") + (RegionDataProvider.regionData[province]?.keys?.toList() ?: emptyList())
                } else listOf("전체")

                val selected = cities[position]
                selectedCity = if (selected == "전체") null else selected

                val districts = if (province != null && selectedCity != null) {
                    listOf("전체") + (RegionDataProvider.regionData[province]?.get(selectedCity)?.toList() ?: emptyList())
                } else listOf("전체")

                val districtAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, districts)
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDistrict.adapter = districtAdapter

                selectedDistrict = null
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val province = selectedProvince
                val city = selectedCity
                val districts = if (province != null && city != null) {
                    listOf("전체") + (RegionDataProvider.regionData[province]?.get(city)?.toList() ?: emptyList())
                } else listOf("전체")

                val selected = districts[position]
                selectedDistrict = if (selected == "전체") null else selected
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /** ✅ 기간 체크박스 (라디오버튼처럼 작동) */
    private fun setupTimeCheckBoxListeners() {
        lateinit var listener: CompoundButton.OnCheckedChangeListener
        listener = CompoundButton.OnCheckedChangeListener { clickedCb, isChecked ->
            if (isChecked) {
                timeCheckBoxes.forEach { other ->
                    if (other != clickedCb) {
                        other.setOnCheckedChangeListener(null)
                        other.isChecked = false
                        updateCheckBoxStyle(other, false)
                        other.setOnCheckedChangeListener(listener)
                    }
                }
                selectedPeriod = (clickedCb as CheckBox).text.toString()
            } else if (timeCheckBoxes.none { it.isChecked }) {
                selectedPeriod = null
            }
            updateCheckBoxStyle(clickedCb as CheckBox, isChecked)
        }
        timeCheckBoxes.forEach { cb -> cb.setOnCheckedChangeListener(listener) }
    }

    /** ✅ 재난유형 체크박스 + 칩 연결 */
    private fun setupCheckBoxListeners() {
        val cbAllListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
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

        checkBoxListener = CompoundButton.OnCheckedChangeListener { clickedCb, isChecked ->
            val cb = clickedCb as CheckBox
            updateCheckBoxStyle(cb, isChecked)

            cbAll.setOnCheckedChangeListener(null)
            cbAll.isChecked = checkBoxes.all { it.isChecked }
            updateCheckBoxStyle(cbAll, cbAll.isChecked)
            cbAll.setOnCheckedChangeListener(cbAllListener)

            checkBoxes.forEach { otherCb ->
                if (otherCb != cb) {
                    otherCb.setOnCheckedChangeListener(null)
                    otherCb.isChecked = false
                    updateCheckBoxStyle(otherCb, false)
                    otherCb.setOnCheckedChangeListener(checkBoxListener)
                }
            }

            chipGroupDetail.removeAllViews()
            if (isChecked) {
                val chipLabels = when (cb.id) {
                    R.id.cb_typhoon -> listOf(
                        getString(R.string.disaster_typhoon),      // 태풍
                        getString(R.string.disaster_heavy_rain),   // 호우
                        getString(R.string.disaster_flood),        // 홍수
                        getString(R.string.disaster_strong_wind),  // 강풍
                        getString(R.string.disaster_heavy_snow)    // 대설
                    )
                    R.id.cb_weather -> listOf(
                        getString(R.string.disaster_heat_wave),    // 폭염
                        getString(R.string.disaster_cold_wave)     // 한파
                    )
                    R.id.cb_earthquake -> listOf(
                        getString(R.string.disaster_earthquake)    // 지진
                    )
                    R.id.cb_epidemic -> listOf(
                        getString(R.string.epidemic)               // 감염병
                    )
                    R.id.cb_fire -> listOf(
                        getString(R.string.disaster_wildfire),     // 산불
                        getString(R.string.disaster_fire)          // 화재 (일일화재)
                    )
                    R.id.cb_fine_dust -> listOf(
                        getString(R.string.disaster_fine_dust)     // 미세먼지
                    )
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

        cbAll.setOnCheckedChangeListener(cbAllListener)
        checkBoxes.forEach { cb -> cb.setOnCheckedChangeListener(checkBoxListener) }
    }

    /** ✅ 체크박스 색상 스타일 */
    private fun updateCheckBoxStyle(cb: CheckBox, isChecked: Boolean) {
        val color = ContextCompat.getColor(
            cb.context,
            if (isChecked) R.color.blue_50 else R.color.grey_60
        )
        cb.setTextColor(color)
        cb.setBackgroundResource(R.drawable.checkbox_selector)
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
