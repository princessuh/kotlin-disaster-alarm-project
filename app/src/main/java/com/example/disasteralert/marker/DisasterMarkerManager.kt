package com.example.disasteralert.marker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.disasteralert.R
import com.example.disasteralert.api.DisasterEvent
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

/**
 * 배경용 drawable(marker_circle_bg.xml) 위에 작은 재난 아이콘을 합성하여
 * BitmapDescriptor로 반환하는 유틸 함수
 */
fun makeMarkerIcon(
    context: Context,
    @DrawableRes backgroundRes: Int,   // R.drawable.marker_circle_bg
    @DrawableRes iconRes: Int,         // ex) R.drawable.ic_typhoon 등
    iconSizeDp: Int = 24
): BitmapDescriptor {
    // 1) 배경 drawable
    val bgDr = ContextCompat.getDrawable(context, backgroundRes)!!
    // 2) 아이콘 drawable
    val iconDr = ContextCompat.getDrawable(context, iconRes)!!

    // dp → px
    val density = context.resources.displayMetrics.density
    val iconPx = (iconSizeDp * density).toInt()

    // 배경 크기 설정
    val w = bgDr.intrinsicWidth
    val h = bgDr.intrinsicHeight
    bgDr.setBounds(0, 0, w, h)

    // 아이콘은 배경 중앙에
    val left = (w - iconPx) / 2
    val top = (h - iconPx) / 2
    iconDr.setBounds(left, top, left + iconPx, top + iconPx)

    // 캔버스에 합성
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    bgDr.draw(canvas)
    iconDr.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bmp)
}

// [기능] 지도 위에 재난 마커를 생성하고 추가하는 클래스
// [설명] DisasterEvent를 받아 지도에 마커를 표시하며, InfoWindow에 정보 제공
class DisasterMarkerManager(
    private val context: Context,
    private val map: GoogleMap
) {

    /**
     * 재난 이벤트를 받아 해당 좌표에 마커를 추가하고,
     * InfoWindow 자동 팝업까지 처리.
     */
    fun addMarker(event: DisasterEvent): Marker? {
        val position = LatLng(event.latitude, event.longitude)
        val iconResId = getDisasterIcon(event.disaster_type)

        val options = MarkerOptions()
            .position(position)
            .title(event.disaster_type)
            .snippet(event.description)


        // 아이콘을 스케일 조절해서 사용
        if (iconResId != null) {
             val original = BitmapFactory.decodeResource(context.resources, iconResId)
             val scaled = Bitmap.createScaledBitmap(original, 128, 128, false)
             options.icon(BitmapDescriptorFactory.fromBitmap(scaled))
                }

        // 지도에 마커 추가
        val marker = map.addMarker(options)


        return marker
    }

    // 재난 별 아이콘 매핑
    private fun getDisasterIcon(type: String): Int? {
        return when (type) {
            "태풍"      -> R.drawable.ic_typhoon1
            "호우"      -> R.drawable.ic_downpour1
            "홍수"      -> R.drawable.ic_flood1
            "강풍"      -> R.drawable.ic_gale1
            "대설"      -> R.drawable.ic_heavysnow1
            "폭염"      -> R.drawable.ic_heatwave1
            "한파"      -> R.drawable.ic_coldwave1
            "지진"      -> R.drawable.ic_earthquake1
            "화재","산불" -> R.drawable.ic_fire1
            "미세먼지"  -> R.drawable.ic_airpollution1
            else        -> null
        }
    }
}
