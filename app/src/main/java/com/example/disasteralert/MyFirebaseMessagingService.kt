package com.example.disasteralert

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")

        // 서버로 전송
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val url = "https://your-server.com/api/save-token" // 너 서버 주소로 바꿔줘
        val jsonBody = JSONObject().apply {
            put("token", token)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response -> Log.d("FCM", "Token saved successfully: $response") },
            { error -> Log.e("FCM", "Failed to save token: $error") }
        )

        requestQueue.add(request)
    }

}
