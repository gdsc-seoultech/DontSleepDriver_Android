package com.comye1.dontsleepdriver.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.comye1.dontsleepdriver.DSDActivity
import com.comye1.dontsleepdriver.R
import com.comye1.dontsleepdriver.other.Constants.ACTION_PAUSE_SERVICE
import com.comye1.dontsleepdriver.other.Constants.ACTION_SHOW_DSD_ACTIVITY
import com.comye1.dontsleepdriver.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.comye1.dontsleepdriver.other.Constants.ACTION_STOP_SERVICE
import com.comye1.dontsleepdriver.other.Constants.FASTEST_LOCATION_INTERVAL
import com.comye1.dontsleepdriver.other.Constants.LOCATION_UPDATE_INTERVAL
import com.comye1.dontsleepdriver.other.Constants.NOTIFICATION_CHANNEL_ID
import com.comye1.dontsleepdriver.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.comye1.dontsleepdriver.other.Constants.NOTIFICATION_ID
import com.comye1.dontsleepdriver.util.TrackingUtility.hasLocationPermissions
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

class TrackingService : LifecycleService() {

    companion object {
        const val TAG = "TrackingService" // 로그 태그
        val isTracking = MutableLiveData<Boolean>() // tracking 상태
        val previousLocation = MutableLiveData<Location>()
    }

    // 위치정보를 주기적으로 주는 Client
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var isFirstDriving = true // start or resume 판단

    private fun postInitialValues() { // 초기값
        isTracking.postValue(false)
        previousLocation.postValue(null)
    }

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            updateLocationTracking(it) // isTracking이 변화하면 위치 수집을 시작 or 중단
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
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d(TAG, "paused")
                }
                ACTION_STOP_SERVICE -> {
                    Log.d(TAG, "stopped")
                }
                else -> {
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
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
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    // 위치 업데이트 콜백
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

//            Log.d(TAG, "result: $result") // 받아온 위치 정보
            if (isTracking.value!!) {
                result.locations.let { locations ->
//                    for (i in 0..locations.size - 2) {
//                        Log.d(TAG, "location : ${locations[i].latitude} ${locations[i].longitude}")
//                        addPoint(locations[i])
//                        Log.d(TAG, locations[i].distanceTo(locations[i + 1]).toString())
//                    }
                    for (location in locations) {
                        Log.d(TAG, "location : ${location.latitude} ${location.longitude}")
                    }
                }
            }
        }
    }

    // 위치정보 어떻게 축적할지..
    private fun addPoint(location: Location?) {
        location?.let {
            previousLocation.value?.apply {
                // 거리, 속력 계산
                previousLocation.postValue(it)
            }
        }
    }

    // Foreground Service 시작
    private fun startForegroundService() {

        // tracking 시작
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        // 오레오 이후 버전에서 알림 띄움
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false) // active
            .setOngoing(true) // not be swiped
            .setSmallIcon(R.drawable.ic_google_icon)
            .setContentTitle("Don't Sleep Driver!")
            .setContentText("getting location & recording with camera")
            .setContentIntent(getPendingIntent())
        // pendingIntent => 알림을 클릭하면 액티비티를 열기 위해


        // Foreground Service 시작
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    // Pending Intent를 가져옴
    private fun getPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, DSDActivity::class.java).also {
            it.action = ACTION_SHOW_DSD_ACTIVITY
        },
        FLAG_UPDATE_CURRENT
    )

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