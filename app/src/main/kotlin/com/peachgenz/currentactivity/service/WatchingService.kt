package com.peachgenz.currentactivity.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.peachgenz.currentactivity.FloatingWindow
import com.peachgenz.currentactivity.IFloatingWindowState
import com.peachgenz.currentactivity.MainActivity
import java.util.Timer
import java.util.TimerTask

class WatchingService : Service(), IFloatingWindowState {

    private var timer: Timer? = null

    private val mHandler by lazy { Handler(Looper.getMainLooper()) }

    private var lastName = ""

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d("WatchingService", "Watching Service onCreate")
        super.onCreate()
        FloatingWindow.callback = this
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("WatchingService", "Watching Service start")
        if (null == timer) {
            timer = Timer().apply {
                scheduleAtFixedRate(RefreshTask(), 0, 500)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.d("WatchingService", "Watching Service onTaskRemoved")
        val restartServiceIntent = Intent(applicationContext, this.javaClass).apply {
            setPackage(packageName)
        }
        val restartServicePendingIntent = PendingIntent.getService(
            applicationContext, 1, restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmService =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 500] =
            restartServicePendingIntent
        super.onTaskRemoved(rootIntent)
    }

    internal inner class RefreshTask : TimerTask() {
        override fun run() {
            val name = getCurrentActivityName().substringAfterLast(".")
            if (lastName == name) {
                return
            }
            lastName = name
            Log.d("WatchingService", "top running app is : $name")
            mHandler.post {
                MainActivity.windowChange(this@WatchingService, name)
            }
        }
    }

    private fun getCurrentActivityName(): String {
        var topActivityName = ""

        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(now - 600000, now)
        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                topActivityName = "${event.packageName}\n${event.className}"
            }
        }

        return topActivityName
    }

    override fun windowHide(isHide: Boolean) {
        if (isHide) {
            lastName = ""
            timer?.cancel()
            timer = null
        }
    }
}
