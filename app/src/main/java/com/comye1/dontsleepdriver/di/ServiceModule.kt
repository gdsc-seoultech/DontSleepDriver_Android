package com.comye1.dontsleepdriver.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.comye1.dontsleepdriver.DSDActivity
import com.comye1.dontsleepdriver.R
import com.comye1.dontsleepdriver.other.Constants
import com.comye1.dontsleepdriver.other.Constants.NOTIFICATION_CHANNEL_ID
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(
        @ApplicationContext app: Context
    ): PendingIntent = PendingIntent.getActivity(
        app,
        0,
        Intent(app, DSDActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_DSD_ACTIVITY
        },
        PendingIntent.FLAG_MUTABLE
    )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false) // active
        .setOngoing(true) // not be swiped
        .setSmallIcon(R.drawable.d_notification)
        .setContentTitle("You are driving for")
        .setContentText("00:00:00")
//            .setContentText("getting location & recording with camera")
        .setContentIntent(pendingIntent)
    // pendingIntent => 알림을 클릭하면 액티비티를 열기 위해


}