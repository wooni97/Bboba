package com.example.bboba

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase


class PushNotification {
    private val reqData = ArrayList<Prints_Request>()
    private var tempreqData = ArrayList<Prints_Request>()//슬라이드바
    private val database = FirebaseDatabase.getInstance()
    private val reqRef = database.getReference("PRINTS_REQUEST")
    private val dateRef = reqRef.child("date")

    /*private val notificationManager: NotificationManager by lazy {
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private const val CHANNEL_ID = "com.ran.todolist"
        private const val NOTIFICATION_ID = 1001
    }

    fun createNotificationChannel(id: String, name: String, description: String) {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)

        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(contentText: String, resultIntent: Intent) {
        val pendingIntent = PendingIntent.getActivity(ctx, 0, resultIntent, 0)

        val notification = Notification.Builder(ctx, CHANNEL_ID)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher)
            .setChannelId(CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .build()

        notification.flags = Notification.FLAG_NO_CLEAR

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun dismissNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }*/
}

