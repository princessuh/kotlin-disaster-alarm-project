package com.example.disasteralert

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.disasteralert.api.RetrofitClient
import com.example.disasteralert.api.UserReportRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class PostActivity : AppCompatActivity() {

    private lateinit var tvLocationTime: TextView
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var btnSubmit: Button
    private lateinit var cbTyphoon: CheckBox
    private lateinit var cbWeather: CheckBox
    private lateinit var cbEarthquake: CheckBox
    private lateinit var cbEpidemic: CheckBox
    private lateinit var cbFire: CheckBox
    private lateinit var cbDust: CheckBox
    private lateinit var disasterCheckBoxes: List<CheckBox>

    private var selectedProvince: String? = null
    private var selectedCity: String? = null
    private var selectedDistrict: String? = null
    private var selectedTimestamp: Long? = null
    private var selectedLat: Double? = null
    private var selectedLng: Double? = null
    private var selectedTextLocation: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        etTitle = findViewById(R.id.et_title)
        etContent = findViewById(R.id.et_content)
        tvLocationTime = findViewById(R.id.tv_location_time)
        chipGroup = findViewById(R.id.chip_group)
        btnSubmit = findViewById(R.id.btn_submit)

        cbTyphoon = findViewById(R.id.cb_typhoon)
        cbWeather = findViewById(R.id.cb_weather)
        cbEarthquake = findViewById(R.id.cb_earthquake)
        cbEpidemic = findViewById(R.id.cb_epidemic)
        cbFire = findViewById(R.id.cb_fire)
        cbDust = findViewById(R.id.cb_fine_dust)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setLocationAndTime()
        setupDisasterCheckboxes()

        disasterCheckBoxes = listOf(cbTyphoon, cbWeather, cbEarthquake, cbEpidemic, cbFire, cbDust)

        disasterCheckBoxes.forEach { cb ->
            cb.setButtonDrawable(android.R.color.transparent) // 체크 박스 기본 아이콘 숨기기
            updateCheckBoxStyle(cb, cb.isChecked) // 초기 스타일 반영
        }

        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reportLat = selectedLat
            val reportLng = selectedLng
            val timestamp = selectedTimestamp
            if (reportLat == null || reportLng == null || timestamp == null) {
                Toast.makeText(this, "위치와 시간을 설정해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reportTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

            val selectedChip = (0 until chipGroup.childCount)
                .mapNotNull { chipGroup.getChildAt(it) as? Chip }
                .firstOrNull { it.isChecked }

            val selectedDisaster = selectedChip?.text.toString()
            val disasterCode = mapDisasterToCode(selectedDisaster)

            val disasterPos = "$selectedProvince $selectedCity $selectedDistrict $selectedTextLocation"


            val request = UserReportRequest(
                userId = "plz",
                disasterType = disasterCode.toString(),
                disasterTime = reportTime,
                reportContent = content,
                disasterPos = disasterPos,
                latitude = reportLat?.toFloat(),
                longitude = reportLng?.toFloat()
            )

            Log.d("POST_REQUEST", Gson().toJson(request))

            RetrofitClient.userReportService.submitReport(request)
                .enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PostActivity, "제보가 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@PostActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        Toast.makeText(this@PostActivity, "네트워크 오류: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        tvLocationTime.setOnClickListener {
            val dialog = LocationTimeBottomSheet { province, city, district, timestamp, textLocation ->
                selectedProvince = province
                selectedCity = city
                selectedDistrict = district
                selectedTimestamp = timestamp
                selectedTextLocation = textLocation

                val geocodeAddress = "$province $city $district"           // 좌표 변환용
                val fullTextAddress = "$geocodeAddress $textLocation"      // 서버 전송용

                fetchLatLngWithVWorld(geocodeAddress) { lat, lng ->
                    selectedLat = lat
                    selectedLng = lng
                    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(Date(timestamp))
                    if (lat != null && lng != null) {
                        tvLocationTime.text = "위치: $fullTextAddress\n위도: $lat, 경도: $lng\n시각: $dateStr"
                    } else {
                        tvLocationTime.text = "위치: $fullTextAddress\n 위치 좌표 변환 실패\n시각: $dateStr"
                    }
                }

            }
            dialog.show(supportFragmentManager, "LocationTimeBottomSheet")
        }

    }

    private fun setupDisasterCheckboxes() {
        cbTyphoon.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chipGroup.removeAllViews()
                listOf("태풍", "호우", "홍수", "강풍", "대설").forEach { addChip(it) }
            }
        }
        cbWeather.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chipGroup.removeAllViews()
                listOf("폭염", "한파").forEach { addChip(it) }
            }
        }
        cbEarthquake.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chipGroup.removeAllViews()
                addChip("지진")
            }
        }
        cbEpidemic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chipGroup.removeAllViews()
                addChip("감염병")
            }
        }
        cbFire.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chipGroup.removeAllViews()
                listOf("산불", "일일화재").forEach { addChip(it) }
            }
        }
        cbDust.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                chipGroup.removeAllViews()
                addChip("미세먼지")
            }
        }
    }

    private fun addChip(label: String) {
        val chip = Chip(this).apply {
            text = label
            isCheckable = true
            isClickable = true
        }
        chipGroup.addView(chip)
    }

    private fun setLocationAndTime() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val time = System.currentTimeMillis()
                val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(time))
                tvLocationTime.text = "위치: ${location.latitude}, ${location.longitude} | 시간: $dateStr"
            }
        }
    }

    fun fetchLatLngWithVWorld(address: String, callback: (Double?, Double?) -> Unit) {
        val apiKey = "3E669EE1-18EB-352F-95E6-1A6D59122188"  // 🔑 VWorld 발급키
        val encodedAddress = URLEncoder.encode(address, "UTF-8")
        val urlStr = "https://api.vworld.kr/req/address?" +
                "service=address&request=getCoord&version=2.0&crs=EPSG:4326&" +
                "address=$encodedAddress&type=road&refine=true&simple=false&format=json&key=$apiKey"

        Thread {
            try {
                val url = URL(urlStr)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connect()

                val response = conn.inputStream.bufferedReader().use { it.readText() }
                Log.d("VWorldAPI", "응답 원문: $response")  // 디버깅용 로그

                val json = JSONObject(response)
                val status = json.getJSONObject("response").getString("status")

                if (status == "OK") {
                    val point = json.getJSONObject("response")
                        .getJSONObject("result")
                        .getJSONObject("point")

                    val lng = point.getDouble("x")
                    val lat = point.getDouble("y")
                    runOnUiThread { callback(lat, lng) }
                } else {
                    Log.e("VWorldAPI", "상태 오류: $status")
                    runOnUiThread { callback(null, null) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { callback(null, null) }
            }
        }.start()
    }

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
    /** 개별 체크박스 스타일 적용 */
    private fun updateCheckBoxStyle(cb: CheckBox, isChecked: Boolean) {
        cb.setBackgroundResource(R.drawable.checkbox_selector)
        cb.setTextColor(if (isChecked) Color.parseColor("#007AFF") else Color.parseColor("#757575"))
    }
}
