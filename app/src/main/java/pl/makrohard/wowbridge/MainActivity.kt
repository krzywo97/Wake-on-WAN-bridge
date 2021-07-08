package pl.makrohard.wowbridge

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import pl.makrohard.wowbridge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var app: App
    private lateinit var view: ActivityMainBinding

    private var timer: CountDownTimer? = null
    private var serviceStatus = App.SERVICE_STATUS_UNKNOWN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)

        app = application as App
        app.getServiceStatus().observe(this) { status ->
            serviceStatus = status

            when (status) {
                App.SERVICE_STATUS_UNKNOWN -> {
                    view.serviceStatus.text = getString(R.string.service_status_unknown)
                    view.serviceStatus.setTextColor(getColor(R.color.service_unknown))
                    view.toggleService.text = getString(R.string.start_service)
                }
                App.SERVICE_STATUS_RUNNING -> {
                    view.serviceStatus.text = getString(R.string.service_status_running)
                    view.serviceStatus.setTextColor(getColor(R.color.service_running))
                    view.toggleService.text = getString(R.string.stop_service)
                }
                App.SERVICE_STATUS_STOPPED -> {
                    view.serviceStatus.text = getString(R.string.service_status_stopped)
                    view.serviceStatus.setTextColor(getColor(R.color.service_stopped))
                    view.toggleService.text = getString(R.string.start_service)
                }
            }
        }

        view.toggleService.setOnClickListener {
            if (serviceStatus != App.SERVICE_STATUS_RUNNING) {
                app.startService()
            } else {
                app.stopService()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        timer = object : CountDownTimer(1500000, 1500) {
            override fun onTick(millisUntilFinished: Long) {
                app.updateServiceStatus()
            }

            override fun onFinish() {
                start()
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()

        timer?.cancel()
        timer = null
    }
}