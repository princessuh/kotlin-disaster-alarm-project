package com.example.disasteralert.marker

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.example.disasteralert.R
import com.example.disasteralert.api.RtdEvent
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

class RtdMarkerManager(
    private val context: Context,
    private val map: GoogleMap
) {

    /** RTD 이벤트 전용 마커 생성 함수 */
    fun addMarker(event: RtdEvent): Marker? {
        val lat = event.latitude ?: return null
        val lng = event.longitude ?: return null
        val position = LatLng(lat, lng)

        // 🔥 rtd_details에서 disaster type 추출
        val disasterType = extractDisasterType(event.rtd_details)
        val iconResId = getDisasterIconFromType(disasterType)

        val options = MarkerOptions()
            .position(position)
            .title(event.rtd_loc)
            .snippet(event.rtd_details.joinToString())

        if (iconResId != null) {
            val original = BitmapFactory.decodeResource(context.resources, iconResId)
            val scaled = Bitmap.createScaledBitmap(original, 128, 128, false)
            options.icon(BitmapDescriptorFactory.fromBitmap(scaled))
        }

        return map.addMarker(options)
    }

    /** RTD details에서 disaster type 추출 */
    private fun extractDisasterType(details: List<String>): String? {
        return details.find { it.startsWith("type:") }
            ?.substringAfter("type:")
            ?.trim()
    }

    /** 재난 유형 기반 아이콘 매핑 */
    private fun getDisasterIconFromType(type: String?): Int? {
        return when (type) {
            "태풍" -> R.drawable.ic_typhoon1
            "호우" -> R.drawable.ic_downpour1
            "홍수" -> R.drawable.ic_flood1
            "강풍" -> R.drawable.ic_gale1
            "대설" -> R.drawable.ic_heavysnow1
            "폭염" -> R.drawable.ic_heatwave1
            "한파" -> R.drawable.ic_coldwave1
            "지진" -> R.drawable.ic_earthquake1
            "산불", "일일화재", "화재" -> R.drawable.ic_fire1
            "미세먼지", "대기질" -> R.drawable.ic_airpollution1
            else -> null
        }
    }
}
