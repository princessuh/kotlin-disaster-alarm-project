package com.example.disasteralert.marker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.disasteralert.R
import com.example.disasteralert.api.RtdEvent
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

class RtdMarkerManager(
    private val context: Context,
    private val map: GoogleMap
) {

    /** RTD / Report 이벤트 마커 생성 */
    fun addMarker(event: RtdEvent): Marker? {
        val lat = event.latitude ?: return null
        val lng = event.longitude ?: return null
        val pos = LatLng(lat, lng)

        val disasterType = if (event.type == "rtd") extractDisasterType(event.rtd_details ?: emptyList()) else null
        val iconResId = getDisasterIconFromType(disasterType, event.type)

        val options = MarkerOptions()
            .position(pos)
            .title(if (event.type == "rtd") event.rtd_loc else event.report_location)
            .snippet(if (event.type == "rtd") event.rtd_details?.joinToString() else event.content)

        if (iconResId != null) {
            val bmp: Bitmap = BitmapFactory.decodeResource(context.resources, iconResId)
            val scaled = Bitmap.createScaledBitmap(bmp, 128, 128, false)
            options.icon(BitmapDescriptorFactory.fromBitmap(scaled))
        }

        return map.addMarker(options)
    }

    /** rtd_details에서 disaster type 추출 */
    fun extractDisasterType(details: List<String>): String? {
        if (details.isEmpty()) return null
        val first = details.first()
        return when {
            first.contains("태풍") -> "태풍"
            first.contains("호우") -> "호우"
            first.contains("홍수") -> "홍수"
            first.contains("강풍") -> "강풍"
            first.contains("대설") -> "대설"
            first.contains("폭염") -> "폭염"
            first.contains("한파") -> "한파"
            first.contains("지진") -> "지진"
            first.contains("화재") || first.contains("산불") -> "산불"
            first.contains("미세먼지") || first.contains("대기질") -> "미세먼지"
            else -> null
        }
    }

    /** 재난 유형별 아이콘 반환 */
    private fun getDisasterIconFromType(type: String?, eventType: String): Int? {
        return when (eventType) {
            "rtd" -> when (type) {
                "태풍" -> R.drawable.ic_typhoon1
                "호우" -> R.drawable.ic_downpour1
                "홍수" -> R.drawable.ic_flood1
                "강풍" -> R.drawable.ic_gale1
                "대설" -> R.drawable.ic_heavysnow1
                "폭염" -> R.drawable.ic_heatwave1
                "한파" -> R.drawable.ic_coldwave1
                "지진" -> R.drawable.ic_earthquake1
                "산불" -> R.drawable.ic_fire1
                "미세먼지" -> R.drawable.ic_airpollution1
                else -> null
            }
            //"report" -> R.drawable.ic_report1
            else -> null
        }
    }
}
