package com.comye1.dontsleepdriver.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.comye1.dontsleepdriver.R
import com.comye1.dontsleepdriver.other.Constants.ACTION_PAUSE_SERVICE
import com.comye1.dontsleepdriver.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.comye1.dontsleepdriver.other.Constants.ACTION_STOP_SERVICE
import com.comye1.dontsleepdriver.other.Constants.FASTEST_LOCATION_INTERVAL
import com.comye1.dontsleepdriver.other.Constants.LOCATION_UPDATE_INTERVAL
import com.comye1.dontsleepdriver.other.Constants.NOTIFICATION_CHANNEL_ID
import com.comye1.dontsleepdriver.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.comye1.dontsleepdriver.other.Constants.NOTIFICATION_ID
import com.comye1.dontsleepdriver.other.Constants.TIMER_UPDATE_INTERVAL
import com.comye1.dontsleepdriver.util.TrackingUtility
import com.comye1.dontsleepdriver.util.TrackingUtility.hasLocationPermissions
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    companion object {
        const val TAG = "TrackingService" // 로그 태그
        val isTracking = MutableLiveData<Boolean>() // tracking 상태
        val previousLocation = MutableLiveData<LatLng>()
        val timeDrivingInMillis = MutableLiveData<Long>()
        val alwaysPermissionRequest = MutableLiveData(false)
    }

    // 위치정보를 주기적으로 주는 Client
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder

    var isFirstDriving = true // start or resume 판단

    var serviceKilled = false

    private val timeDrivingInSeconds = MutableLiveData<Long>()

//    private val totalTimeInSeconds = MutableLiveData<Long>(0L)

    private fun postInitialValues() { // 초기값
        isTracking.postValue(false)
        previousLocation.postValue(LatLng(-1.0, 0.0))
        timeDrivingInSeconds.postValue(0L)
        timeDrivingInMillis.postValue(0L)
        alwaysPermissionRequest.postValue(false)
//        totalTimeInSeconds.postValue(0L)
    }

    private var isTimerEnabled = false
    private var labTime = 0L
    private var timeDriving = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer() {
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference
                labTime = System.currentTimeMillis() - timeStarted
                // post the new labTime
                timeDrivingInMillis.postValue(timeDriving + labTime)
                if (timeDrivingInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeDrivingInSeconds.postValue(timeDrivingInSeconds.value!! + 1)
//                    totalTimeInSeconds.postValue(totalTimeInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeDriving += labTime
        }
    }

    private fun pauseService() {
        Log.d("tracking service", "paused")
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            Log.d(TAG, "isTracking $it")
            updateLocationTracking(it) // isTracking이 변화하면 위치 수집을 시작 or 중단
            updateNotificationTrackingState(it)
        })
    }

    // 서비스가 시작될 때 (startService)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstDriving) {
                        startForegroundService()
                        isFirstDriving = false
                        Log.d(TAG, "starting")
                    } else {
                        Log.d(TAG, "resuming")
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d(TAG, "paused")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG, "stopped")
                    killService()
                }
                else -> {
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun killService() {
        serviceKilled = true
        isFirstDriving = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            getService(this, 1, pauseIntent, FLAG_MUTABLE)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            getService(this, 2, resumeIntent, FLAG_MUTABLE)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if (!serviceKilled) {
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.pause, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }
    }

    // 위치 정보 주기적으로 받기 요청
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (hasLocationPermissions(this)) { // permission check
                Log.d(TAG, "permission")
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
                Log.d(TAG, "request")
            }else{
                killService()
                alwaysPermissionRequest.postValue(true)
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    // 위치 업데이트 콜백
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            Log.d(TAG, "location : 왜안돼")

            result.locations.let { locations ->
                for (location in locations) {
                    Log.d(TAG, "location : ${location.latitude} ${location.longitude}")

                    previousLocation.postValue(
                        LatLng(location.latitude, location.longitude)
                    )
                }
            }

        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
            Log.d(TAG, "availability : ${p0.isLocationAvailable}")
        }
    }

    // Foreground Service 시작
    private fun startForegroundService() {

        // timer 시작
        startTimer()

        // tracking 시작
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        // 오레오 이후 버전에서 알림 띄움
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        // Foreground Service 시작
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeDrivingInSeconds.observe(this) {
            if (!serviceKilled) {
                val notification = curNotificationBuilder
                    .setContentText(
                        TrackingUtility.getFormattedStopWatchTime(it * 1000L)
//                            TrackingUtility.getFormattedStopWatchTime(
//                                totalTimeInSeconds.value!!.times(1000L)
//                            )
                    )
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        }
    }

    // Notification Channel
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}