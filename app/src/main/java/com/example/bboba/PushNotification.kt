package com.example.bboba

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response


class PushNotification(private val ctx : Context) {

    private lateinit var mNotifyManager : NotificationManager
    private val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    private val NOTIFICATION_ID: Int = 0
    private val ACTION_UPDATE_NOTIFICATION: String = "com.android.segunfrancis.notifymekotlin.ACTION_UPDATE_NOTIFICATION"





    fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(ctx,
            NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)
        val notifyBuilder: NotificationCompat.Builder = getNotificationBuilder()
        notifyBuilder.addAction(R.drawable.alert_notify, "Update Notification", updatePendingIntent)
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build())

    }

    fun createNotificationChannel() {
        mNotifyManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        /* Notification Channels are required for android 26 and above */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create Notification Channel
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Mascot Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification from Mascot"
            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(ctx, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(
            ctx, NOTIFICATION_ID,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(ctx, PRIMARY_CHANNEL_ID)
            .setContentTitle("You have been notified")
            .setContentText("This is your notification")
            .setSmallIcon(R.drawable.alert_notify)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true) /* This closes the notification when the user taps on it */
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
    }



}


