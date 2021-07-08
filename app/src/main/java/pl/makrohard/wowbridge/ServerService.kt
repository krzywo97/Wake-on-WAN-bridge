package pl.makrohard.wowbridge

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ServerService : Service() {
    companion object {
        const val CHANNEL_ID = "notification_service_running"
        const val NOTIFICATION_ID = 1
    }

    private lateinit var server: TcpServer

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        server = TcpServer("192.168.0.255", 7777)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title_service_running))
            .setContentText(
                getNotificationText(
                    "localhost",
                    7777
                ) //TODO: pass these values as arguments
            )
            .setSmallIcon(R.drawable.power)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        server.start()

        return START_STICKY
    }

    override fun onDestroy() {
        server.interrupt()
        stopForeground(true)
        (application as App).stopService()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        (application as App).startService()
        super.onTaskRemoved(rootIntent)
    }

    private fun getNotificationText(ipAddress: String, port: Int): String {
        return getString(R.string.notification_text_address_info)
            .replace("{ip}", ipAddress)
            .replace("{port}", port.toString())
    }
}