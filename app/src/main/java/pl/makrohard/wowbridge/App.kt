package pl.makrohard.wowbridge

import android.app.ActivityManager
import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class App : Application() {
    companion object {
        const val SERVICE_STATUS_UNKNOWN = 0
        const val SERVICE_STATUS_RUNNING = 1
        const val SERVICE_STATUS_STOPPED = 2
    }

    private lateinit var activityManager: ActivityManager
    private val serviceStatus = MutableLiveData(SERVICE_STATUS_UNKNOWN)

    override fun onCreate() {
        super.onCreate()
        activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    }

    fun startService() {
        val intent = Intent(this, ServerService::class.java)
        startService(intent)
    }

    fun stopService() {
        stopService(Intent(this, ServerService::class.java))
    }

    @Suppress("DEPRECATION")
    fun updateServiceStatus() {
        serviceStatus.value = if (activityManager.getRunningServices(Int.MAX_VALUE)
                .find { service -> service::class.qualifiedName == ServerService::class.qualifiedName } != null
        ) SERVICE_STATUS_RUNNING else SERVICE_STATUS_STOPPED
    }

    fun getServiceStatus(): LiveData<Int> {
        return serviceStatus
    }
}