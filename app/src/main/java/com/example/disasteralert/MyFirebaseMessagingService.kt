package com.example.disasteralert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM", "From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            val title = it.title ?: "알림"
            val body = it.body ?: ""
            Log.d("FCM", "Message Notification Body: $body")
            showNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")

        // 여기에 서버 전송 로직도 넣을 수 있어
        // ex) sendTokenToServer(token)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "default_channel_id"
        val notificationId = 1

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 이상은 알림 채널이 필요함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "기본 채널",
                NotificationManager.IMPORTANCE_HIGH // 헤드업 알림을 위한 HIGH 중요도
            ).apply {
                description = "DisasterAlert 앱 기본 알림 채널"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 헤드업 알림 조건
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}
