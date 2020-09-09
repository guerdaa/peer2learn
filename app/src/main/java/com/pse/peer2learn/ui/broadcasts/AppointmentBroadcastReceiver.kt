package com.pse.peer2learn.ui.broadcasts

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pse.peer2learn.R
import com.pse.peer2learn.utils.Constants

class AppointmentBroadcastReceiver : BroadcastReceiver() {

    /**
     * This method is used to display an notification whenever the alarmManager launches a signal at a given date
     * [intent] holds all the extra needed to display the notification such as appointmentDetail and cateogry name
     */
    override fun onReceive(context: Context, intent: Intent) {
        val categoryName = intent.getStringExtra(Constants.CATEGORY_NAME_EXTRA)
        val appointmentDetail = intent.getStringExtra(Constants.APPOINTMENT_EXTRA)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = Constants.CHANNEL_ID
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.you_have_appointment))
            .setContentText("$categoryName - $appointmentDetail")
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    channelId,
                    Constants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
            channel.enableLights(true)
            channel.lightColor = Color.GREEN
            mNotificationManager.createNotificationChannel(channel)
        }
        builder.setChannelId(channelId)
        mNotificationManager.notify(Constants.NOTIFICATION_ID, builder.build())
    }
}
