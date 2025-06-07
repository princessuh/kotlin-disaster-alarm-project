package com.example.disasteralert

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.disasteralert.api.DisasterEvent
import com.example.disasteralert.api.RetrofitClient
import com.example.disasteralert.marker.DisasterMarkerManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import regionDataList

class MainMapActivity : BaseActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "MainMapActivity"
    }

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationPermission: ActivityResultLauncher<Array<String>>

    private var isFirstLocationUpdate = true
    private var currentMarker: Marker? = null
    private var currentLatLng: LatLng? = null

    private lateinit var markerManager: DisasterMarkerManager
    private val markerEventMap = mutableMapOf<Marker, DisasterEvent>()
    private var selectedMarker: Marker? = null

    // 정보 패널
    private lateinit var infoPanel: View
    private lateinit var tvDisasterType: TextView
    private lateinit var tvDisasterDesc: TextView
    private lateinit var btnCloseInfo: Button

    // GeoJSON 매니저
    private lateinit var geoJsonManager: GeoJsonManager

    // 폴리곤 표시 대상 (화재·산불 제외)
    private val polygonTypes = setOf(
        "태풍", "호우", "강풍", "대설",
        "폭염", "한파", "지진", "미세먼지"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        Log.d(TAG, "onCreate")

        // 네비게이션 바
        setupBottomNavigation(R.id.bottom_navigation, "MainMapActivity")

        // 뷰 바인딩
        infoPanel      = findViewById(R.id.info_panel)
        tvDisasterType = findViewById(R.id.tv_disaster_type)
        tvDisasterDesc = findViewById(R.id.tv_disaster_desc)
        btnCloseInfo   = findViewById<Button>(R.id.btn_close_info).apply {
            setOnClickListener {
                Log.d(TAG, "▶ Close button clicked")
                hideInfo()
            }
        }
        infoPanel.visibility = View.GONE  // 초기 숨김

        // 권한 요청
        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            if (results.all { it.value }) {
                Log.d(TAG, "Location permissions granted")
                (supportFragmentManager.findFragmentById(R.id.map_fragment)
                        as SupportMapFragment).getMapAsync(this)
            } else {
                Log.w(TAG, "Location permissions denied")
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            }
        }
        locationPermission.launch(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ))

        // 내 위치 버튼
        findViewById<Button>(R.id.myLocationButton).setOnClickListener {
            Log.d(TAG, "MyLocation button clicked")
            currentLatLng?.let {
                mGoogleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(it, 15f)
                )
            } ?: Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady")
        mGoogleMap = googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isZoomGesturesEnabled = true

            // 기본 InfoWindow 완전 차단
            setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoWindow(marker: Marker): View {
                    return View(this@MainMapActivity).apply {
                        layoutParams = ViewGroup.LayoutParams(0, 0)
                    }
                }
                override fun getInfoContents(marker: Marker): View? = null
            })
        }

        // fusedLocationClient 초기화는 updateLocation() 호출 이전에!
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        markerManager  = DisasterMarkerManager(this, mGoogleMap)
        geoJsonManager = GeoJsonManager(this, mGoogleMap, regionDataList)

        // 마커 클릭: 항상 showInfo 호출만, 닫기는 버튼으로만
        mGoogleMap.setOnMarkerClickListener { marker ->
            markerEventMap[marker]?.let { event ->
                selectedMarker = marker
                showInfo(event)
            }
            true
        }

        // 위치 업데이트 시작
        updateLocation()

        // 카메라 움직임에 따른 GeoJSON 레이어 관리
        mGoogleMap.setOnCameraIdleListener {
            val zoom = mGoogleMap.cameraPosition.zoom
            if (infoPanel.visibility == View.GONE && zoom <= 9f) {
                geoJsonManager.removeAllLayers()
            }
        }

        fetchDisasterEvents()
    }

    private fun showInfo(event: DisasterEvent) {
        Log.d(TAG, "▶ showInfo: ${event.disaster_type}")
        infoPanel.bringToFront()
        infoPanel.visibility = View.VISIBLE
        tvDisasterType.text = event.disaster_type
        tvDisasterDesc.text = event.description

        geoJsonManager.removeAllLayers()

        if (event.disaster_type in polygonTypes) {
            val latLng = LatLng(event.latitude, event.longitude)
            val region = regionDataList
                .filter { it.boundingBox.contains(latLng) }
                .minByOrNull { r ->
                    val c = r.boundingBox.center
                    val dLat = latLng.latitude - c.latitude
                    val dLng = latLng.longitude - c.longitude
                    dLat*dLat + dLng*dLng
                }
            region?.let {
                lifecycleScope.launch { geoJsonManager.showRegionAsync(it) }
            }
        }
    }

    private fun hideInfo() {
        Log.d(TAG, "▶ hideInfo")
        infoPanel.visibility = View.GONE
        geoJsonManager.removeAllLayers()
        selectedMarker = null
    }

    private fun fetchDisasterEvents() {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.apiService.getEvents()
                mGoogleMap.clear()
                markerEventMap.clear()
                currentMarker = null

                currentLatLng?.let {
                    currentMarker = mGoogleMap.addMarker(
                        MarkerOptions().position(it).title("현재 위치")
                    )
                }

                res.events.forEach { e ->
                    markerManager.addMarker(e)?.also { m ->
                        markerEventMap[m] = e
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainMapActivity,
                    "재난 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLocation() {
        val req = LocationRequest.create().apply {
            interval        = 1000L
            fastestInterval = 500L
            priority        = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(res: LocationResult) {
                res.locations.forEach { loc ->
                    currentLatLng = LatLng(loc.latitude, loc.longitude)
                    setLastLocation(loc)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.requestLocationUpdates(
            req, locationCallback, Looper.getMainLooper()
        )
    }

    private fun setLastLocation(loc: Location) {
        val pos = LatLng(loc.latitude, loc.longitude)
        if (currentMarker == null) {
            currentMarker = mGoogleMap.addMarker(
                MarkerOptions().position(pos).title("현재 위치")
            )
        } else {
            currentMarker?.position = pos
        }
        if (isFirstLocationUpdate) {
            isFirstLocationUpdate = false
            val cam = CameraPosition.Builder()
                .target(pos).zoom(15f).build()
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam))
        }
    }
}
