package com.example.disasteralert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /** 재난 유형 리스트 ― SettingsActivity, 서버와 동일한 순서/이름을 유지하세요. */
    companion object {
        val DISASTER_TYPES = listOf(
            "태풍", "지진", "호우‧홍수", "기상특보", "화재",
            "미세먼지", "감염병", "속보", "제보"
        )
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)

        /* ❶ 전체 알림 스위치(switchPush) OFF → 바로 종료 */
        if (!prefs.getBoolean("push_enabled", true)) {
            Log.d("FCM", "push_enabled = false → 알림 무시")
            return
        }

        /* ❷ data payload 꺼내기 (notification-only 메시지는 필터 불가) */
        val data = remoteMessage.data
        val type      = data["type"] ?: return      // 재난 유형
        val lat       = data["lat"] ?.toDoubleOrNull() ?: return
        val lon       = data["lon"] ?.toDoubleOrNull() ?: return
        val titleTxt  = data["title"] ?: "재난 알림"
        val bodyTxt   = data["body"]  ?: ""

        /* ❸ 재난 유형 필터 */
        val allowedTypes = DISASTER_TYPES.filterIndexed { i, _ ->
            prefs.getBoolean("disaster_$i", true)   // 기본값 = 전체 ON
        }
        if (type !in allowedTypes) {
            Log.d("FCM", "[$type] 관심 없는 재난 유형 → 알림 무시")
            return
        }

        /* ❹ 반경(거리) 필터 */
        val radiusKm = prefs.getInt("news_radius", 10)
        val myLat = prefs.getFloat("my_lat", 0f).toDouble()
        val myLon = prefs.getFloat("my_lon", 0f).toDouble()

        // 캐시된 위치가 없으면 일단 알림을 보여 준다.
        if (myLat != 0.0 || myLon != 0.0) {
            val dist = FloatArray(1)
            Location.distanceBetween(myLat, myLon, lat, lon, dist)
            if (dist[0] > radiusKm * 1_000) {
                Log.d("FCM", "거리 ${dist[0]/1000}km > $radiusKm km → 알림 무시")
                return
            }
        }

        /* ❺ 조건 통과 → 실제 알림 표시 */
        showNotification(titleTxt, bodyTxt)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // TODO: 서버에 새 토큰 전송
    }

    /** 헤드-업 알림 표시 */
    private fun showNotification(title: String, body: String) {
        val channelId = "default_channel_id"
        val notificationId = System.currentTimeMillis().toInt()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "DisasterAlert 기본 채널",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "사용자 지정 필터를 통과한 재난 알림"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}
