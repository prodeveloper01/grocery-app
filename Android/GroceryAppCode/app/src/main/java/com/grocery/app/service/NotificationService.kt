package com.grocery.app.service


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.grocery.app.R
import com.grocery.app.activity.DashboardActivity
import org.json.JSONException
import org.json.JSONObject


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class NotificationService : FirebaseMessagingService() {

    private val REQUEST_CODE = 1
    private var NOTIFICATION_ID = 6578


    @SuppressLint("WrongThread")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        try {
            Log.e("getData", Gson().toJson(remoteMessage.toString()))
            val `object` = JSONObject(Gson().toJson(remoteMessage.notification))
            Log.e("isPeram", `object`.toString())
            val title = `object`.getString("title")
            val message = `object`.getString("body")
            showNotifications(title, message)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    @Suppress("DEPRECATION")
    private fun showNotifications(title1: String, message: String) {
        val intent = Intent(this, DashboardActivity::class.java)
        val channelId = "channel-01"
        val channelName = "Channel Name"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            manager.createNotificationChannel(mChannel)
            val mBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(getIcon())
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle(title1)
                .setContentText(message)
            val stackBuilder = TaskStackBuilder.create(this)
            stackBuilder.addNextIntent(intent)
            val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            mBuilder.setContentIntent(resultPendingIntent)
            manager.notify(NOTIFICATION_ID, mBuilder.build())
        } else {
            val pendingIntent = PendingIntent.getActivity(
                this, REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notification = NotificationCompat.Builder(this)
                .setContentText(message)
                .setContentTitle(title1)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .setSmallIcon(getIcon())
                .build()
            manager.notify(NOTIFICATION_ID, notification)
        }
        NOTIFICATION_ID++
    }

    private fun getIcon(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) R.drawable.ic_small_notification else R.drawable.ic_small_notification
    }
}