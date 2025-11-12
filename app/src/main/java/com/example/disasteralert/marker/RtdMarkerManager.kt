package com.example.disasteralert.marker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.scale
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
            val original = BitmapFactory.decodeResource(context.resources, iconResId)

            // ✅ ARGB_8888 포맷으로 투명도 보장
            val config = Bitmap.Config.ARGB_8888
            val copy = original.copy(config, true)

            val targetHeight = 120
            val ratio = targetHeight.toFloat() / copy.height
            val targetWidth = (copy.width * ratio).toInt()

            val scaled = copy.scale(targetWidth, targetHeight)
            options.icon(BitmapDescriptorFactory.fromBitmap(scaled))
        }

        return map.addMarker(options)
    }

    /** rtd_details에서 disaster type 추출 */
    fun extractDisasterType(details: List<String>): String? {
        if (details.isEmpty()) return null

        // 각 항목을 순회하면서 확인
        for (item in details) {
            when {
                item.contains("type: 태풍") -> return "태풍"
                item.contains("type: 호우") -> return "호우"
                item.contains("type: 홍수") -> return "홍수"
                item.contains("type: 강풍") -> return "강풍"
                item.contains("type: 대설") -> return "대설"
                item.contains("type: 폭염") -> return "폭염"
                item.contains("type: 한파") -> return "한파"
                item.contains("type: 지진") -> return "지진"
                item.contains("type: 화재") || item.contains("type: 산불") -> return "산불"
                item.contains("pm10_grade") || item.contains("pm25_grade") -> return "미세먼지"
            }
        }

        return null
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
