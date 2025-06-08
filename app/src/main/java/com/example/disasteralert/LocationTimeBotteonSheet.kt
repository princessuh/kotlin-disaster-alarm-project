package com.example.disasteralert

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class LocationTimeBottomSheet(
    private val onConfirm: (province: String, city: String, district: String, timestamp: Long, textLocation: String) -> Unit
) : BottomSheetDialogFragment() {

    private var selectedTimestamp: Long = System.currentTimeMillis()

    private val regionData = mapOf(
        "서울특별시" to mapOf("강남구" to listOf("역삼동", "논현동")),
        "경기도" to mapOf("수원시" to listOf("영통동", "팔달구"))
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_location_time, container, false)

        // Spinner 참조
        val spinnerProvince = view.findViewById<Spinner>(R.id.spinner_province)
        val spinnerCity = view.findViewById<Spinner>(R.id.spinner_city)
        val spinnerDistrict = view.findViewById<Spinner>(R.id.spinner_district)

        // EditText for 텍스트 위치 정보
        val etTextLocation = view.findViewById<EditText>(R.id.et_text_location)

        // 날짜/시간 선택 텍스트
        val tvSelectedDate = view.findViewById<TextView>(R.id.tv_selected_date)
        val tvSelectedTime = view.findViewById<TextView>(R.id.tv_selected_time)

        // 확인 버튼
        val btnConfirm = view.findViewById<MaterialButton>(R.id.btn_confirm_location_time)

        // 시/도 초기화
        val provinces = regionData.keys.toList()
        spinnerProvince.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, provinces)

        spinnerProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val cities = regionData[provinces[position]]?.keys?.toList() ?: emptyList()
                spinnerCity.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cities)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedProvince = spinnerProvince.selectedItem.toString()
                val selectedCity = spinnerCity.selectedItem.toString()
                val districts = regionData[selectedProvince]?.get(selectedCity) ?: emptyList()
                spinnerDistrict.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, districts)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 날짜 선택
        tvSelectedDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                cal.set(Calendar.YEAR, y)
                cal.set(Calendar.MONTH, m)
                cal.set(Calendar.DAY_OF_MONTH, d)
                selectedTimestamp = cal.timeInMillis
                val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                tvSelectedDate.text = fmt.format(Date(selectedTimestamp))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 시간 선택
        tvSelectedTime.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, min ->
                cal.set(Calendar.HOUR_OF_DAY, h)
                cal.set(Calendar.MINUTE, min)
                selectedTimestamp = cal.timeInMillis
                val fmt = SimpleDateFormat("HH:mm", Locale.getDefault())
                tvSelectedTime.text = fmt.format(Date(selectedTimestamp))
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        // 확인 버튼 클릭
        btnConfirm.setOnClickListener {
            val province = spinnerProvince.selectedItem?.toString() ?: ""
            val city = spinnerCity.selectedItem?.toString() ?: ""
            val district = spinnerDistrict.selectedItem?.toString() ?: ""
            val textLocation = etTextLocation.text.toString().trim()
            onConfirm(province, city, district, selectedTimestamp, textLocation)
            dismiss()
        }

        return view
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}
