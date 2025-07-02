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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.disasteralert.api.DisasterEvent
import com.example.disasteralert.api.RetrofitClient
import com.example.disasteralert.marker.DisasterMarkerManager
import com.example.disasteralert.DisasterDetailBottomSheet
import com.example.disasteralert.api.RtdEvent
import com.example.disasteralert.api.RtdResponse
import com.example.disasteralert.marker.RtdMarkerManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import regionDataList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainMapActivity : BaseActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "MainMapActivity"
    }

    private lateinit var mGoogleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationPermission: ActivityResultLauncher<Array<String>>
    private lateinit var rtdMarkerManager: RtdMarkerManager


    private var isFirstLocationUpdate = true
    private var currentMarker: Marker? = null
    private var currentLatLng: LatLng? = null


    private lateinit var markerManager: DisasterMarkerManager
    private val markerEventMap = mutableMapOf<Marker, RtdEvent>()
    private var selectedMarker: Marker? = null

    private lateinit var geoJsonManager: GeoJsonManager
    private val polygonTypes = setOf("태풍", "호우", "강풍", "대설", "폭염", "한파", "지진", "미세먼지")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        setupBottomNavigation(R.id.bottom_navigation, "MainMapActivity")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            if (results.all { it.value }) {
                (supportFragmentManager.findFragmentById(R.id.map_fragment)
                        as SupportMapFragment).getMapAsync(this)
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            }
        }

        locationPermission.launch(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ))

        findViewById<Button>(R.id.myLocationButton).setOnClickListener {
            currentLatLng?.let {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
            } ?: Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRtdInfo(event: RtdEvent) {
        val prev = supportFragmentManager.findFragmentByTag("RtdDetail")
        if (prev != null && prev is BottomSheetDialogFragment) {
            prev.dismissAllowingStateLoss()
            supportFragmentManager.beginTransaction().remove(prev).commitNow()
        }

        val dialog = RtdDetailBottomSheet(event) {
            selectedMarker = null
        }
        dialog.show(supportFragmentManager, "RtdDetail")
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isZoomGesturesEnabled = true

            setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                override fun getInfoWindow(marker: Marker): View {
                    return View(this@MainMapActivity).apply {
                        layoutParams = ViewGroup.LayoutParams(0, 0)
                    }
                }

                override fun getInfoContents(marker: Marker): View? = null
            })
        }

        markerManager = DisasterMarkerManager(this, mGoogleMap)
        geoJsonManager = GeoJsonManager(this, mGoogleMap, regionDataList)
        rtdMarkerManager = RtdMarkerManager(this, mGoogleMap)

        mGoogleMap.setOnMarkerClickListener { marker ->
            Log.d(TAG, "🔥 마커 클릭됨: ${marker.title}")

            marker.hideInfoWindow()
            markerEventMap[marker]?.let { event ->
                Log.d(TAG, "👉 showInfo 호출")
                showRtdInfo(event)
            }
            true
        }

        mGoogleMap.setOnCameraIdleListener {
            val zoom = mGoogleMap.cameraPosition.zoom
            if (zoom <= 9f) geoJsonManager.removeAllLayers()
        }

        updateLocation()
        fetchDisasterEvents()
    }


    private fun showInfo(event: DisasterEvent) {
        Log.d(TAG, "🟢 showInfo 시작: ${event.disaster_type} (${event.latitude}, ${event.longitude})")

        geoJsonManager.removeAllLayers()

        if (event.disaster_type in polygonTypes) {
            val latLng = LatLng(event.latitude, event.longitude)
            val region = regionDataList
                .filter { it.boundingBox.contains(latLng) }
                .minByOrNull { r ->
                    val c = r.boundingBox.center
                    val dLat = latLng.latitude - c.latitude
                    val dLng = latLng.longitude - c.longitude
                    dLat * dLat + dLng * dLng
                }
            region?.let {
                lifecycleScope.launch { geoJsonManager.showRegionAsync(it) }
            }
        }

        val prev = supportFragmentManager.findFragmentByTag("DisasterDetail")
        if (prev != null && prev is BottomSheetDialogFragment) {
            prev.dismissAllowingStateLoss()
            supportFragmentManager.beginTransaction().remove(prev).commitNow()
        }

        val dialog = DisasterDetailBottomSheet(event) {
            geoJsonManager.removeAllLayers()
            selectedMarker = null
            Log.d(TAG, "🧹 바텀시트 닫힘 → 레이어 초기화")
        }
        dialog.show(supportFragmentManager, "DisasterDetail")
        Log.d(TAG, "📦 바텀시트 show 호출 완료")
    }

    private fun updateLocation() {
        val req = LocationRequest.create().apply {
            interval = 1000L
            fastestInterval = 500L
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(res: LocationResult) {
                res.locations.forEach { loc ->
                    currentLatLng = LatLng(loc.latitude, loc.longitude)
                    setLastLocation(loc)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper())
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
            val cam = CameraPosition.Builder().target(pos).zoom(15f).build()
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam))
        }
    }

    private fun fetchDisasterEvents() {
        lifecycleScope.launch {
            try {
                RetrofitClient.rtdService.getRtdEvents().enqueue(object : Callback<RtdResponse> {
                    override fun onResponse(call: Call<RtdResponse>, response: Response<RtdResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            val rtdEvents = response.body()!!.results

                            mGoogleMap.clear()
                            markerEventMap.clear()
                            currentMarker = null

                            currentLatLng?.let {
                                currentMarker = mGoogleMap.addMarker(
                                    MarkerOptions().position(it).title("현재 위치")
                                )
                            }

                            rtdEvents.forEach { event ->

                                val lat = event.latitude
                                val lng = event.longitude

                                if (lat == null || lng == null) {
                                    Log.w(TAG, "❌ 위경도 누락 → 마커 생략됨: ${event.rtd_loc}")
                                    return@forEach
                                }

                                val pos = LatLng(event.latitude, event.longitude)
                                val marker = rtdMarkerManager.addMarker(event)
                                if (marker != null) {
                                    markerEventMap[marker] = event
                                }
                            }
                        } else {
                            Log.e("RTD", "서버 응답 실패: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<RtdResponse>, t: Throwable) {
                        Log.e("RTD", "서버 요청 실패: ${t.localizedMessage}")
                    }
                })

            } catch (e: Exception) {
                Log.e("RTD", "예외 발생", e)
                Toast.makeText(this@MainMapActivity, "재난 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}