package com.technowave.decathlon.keyboard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.technowave.decathlon.App
import com.technowave.decathlon.R


class NordicForegroundService : Service()  {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notificationIntent = Intent(this, App::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, App.SERVICE_CHANNEL_ID)
            .setContentTitle("Nordic Service")
            .setContentText("Service is Running.....")
            .setSmallIcon(R.drawable.rfid_icon)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(App.SERVICE_NOTIFICATION_ID, notification)
        App().connectToReader()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                App.SERVICE_CHANNEL_ID,
                App.SERVICE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }




}
