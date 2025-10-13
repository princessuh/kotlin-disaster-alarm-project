package com.example.disasteralert

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.*

class FilterBottomSheetDialog(
    private val onFilterApplied: (
        selectedDisasters: List<String>,
        province: String?,
        city: String?,
        district: String?,
        dateTime: String?
    ) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var checkBoxes: List<CheckBox>
    private lateinit var chipGroupDetail: ChipGroup
    private lateinit var cbAll: CheckBox
    private lateinit var checkBoxListener: CompoundButton.OnCheckedChangeListener

    private lateinit var spinnerProvince: Spinner
    private lateinit var spinnerCity: Spinner
    private lateinit var spinnerDistrict: Spinner
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedTime: TextView

    private var selectedProvince: String? = null
    private var selectedCity: String? = null
    private var selectedDistrict: String? = null
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    private val allDisasterLabels = listOf(
        "태풍", "호우", "홍수", "강풍", "대설",
        "폭염", "한파",
        "지진",
        "감염병",
        "산불", "일일화재",
        "미세먼지"
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
        tvSelectedDate = view.findViewById(R.id.tv_selected_date)
        tvSelectedTime = view.findViewById(R.id.tv_selected_time)

        setupSpinners()
        setupDateTimePickers()

        (checkBoxes + cbAll).forEach { cb ->
            cb.setButtonDrawable(android.R.color.transparent)
        }

        setupCheckBoxListeners()

        // 적용 버튼
        view.findViewById<MaterialButton>(R.id.btn_apply_filter).setOnClickListener {
            var selectedChips = (0 until chipGroupDetail.childCount)
                .mapNotNull { chipGroupDetail.getChildAt(it) as? Chip }
                .filter { it.isChecked }
                .map { it.text.toString() }

            if (selectedChips.isEmpty()) {
                selectedChips = (0 until chipGroupDetail.childCount)
                    .mapNotNull { chipGroupDetail.getChildAt(it) as? Chip }
                    .map { it.text.toString() }
            }

            val dateTime = if (!selectedDate.isNullOrBlank() && !selectedTime.isNullOrBlank()) {
                "${selectedDate} ${selectedTime}"
            } else null

            onFilterApplied(
                selectedChips,
                selectedProvince,
                selectedCity,
                selectedDistrict,
                dateTime
            )
            dismiss()
        }

        return view
    }

    private fun setupSpinners() {
        // TODO: 실제 지역 데이터 소스와 연결 필요
        val provinces = listOf("서울", "부산", "대구")
        val cities = listOf("강남구", "서초구", "송파구")
        val districts = listOf("역삼동", "잠실동", "방이동")

        spinnerProvince.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provinces)
        spinnerCity.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cities)
        spinnerDistrict.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, districts)

        spinnerProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedProvince = provinces[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedCity = cities[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedDistrict = districts[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDateTimePickers() {
        tvSelectedDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    tvSelectedDate.text = selectedDate
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        tvSelectedTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    tvSelectedTime.text = selectedTime
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

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

        cbAll.setOnCheckedChangeListener(cbAllListener)
        checkBoxes.forEach { cb -> cb.setOnCheckedChangeListener(checkBoxListener) }
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
}
