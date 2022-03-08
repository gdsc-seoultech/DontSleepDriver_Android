package com.comye1.dontsleepdriver.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.comye1.dontsleepdriver.data.model.Driving
import com.comye1.dontsleepdriver.util.secondToHMS
import com.comye1.dontsleepdriver.util.simpleDateFormat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.*


@Composable
fun HistoryDetailScreen(driving: Driving) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val startPoint = driving.gpsData.first()
        val endPoint = driving.gpsData.last()
        val cameraPositionState = rememberCameraPositionState()

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.4f),
            cameraPositionState = cameraPositionState
        ) {
            /*
               CameraUpdateFactory is not initialized
               가 발생해
               GoogleMap 안으로 코드를 이동
             */
            val latLngBounds = LatLngBounds.Builder() // 지도가 보이는 범위
            latLngBounds.include(startPoint)
            with(driving.gpsData) {
                for (i in 1 until this.size) {
                    val sleepLevel = driving.sleepData[i]
                    if (sleepLevel == -1) { // 중단된 상태 -> 포함 X

                    } else {
                        latLngBounds.include(this[i]) // 포함
                        Polyline( // 경로 그리기
                            points = this.subList(i - 1, i + 1),
                            color = when (sleepLevel) { // 졸음 정도에 따라 색을 다르게
                                4 -> Color.Red
                                3 -> Color.Magenta
                                2 -> Color.Yellow
                                1 -> Color.Green
                                0 -> Color.Blue

                                else -> Color.Gray
                            }
                        )
                    }
                }
            }

            cameraPositionState.move(
                CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), 160) // bounds에 맞게 카메라 이동
            )

            Marker( // 출발 지점
                position = startPoint,
                title = "start",
                snippet = "Marker in start point"
            )
            Marker( // 도착 지점
                position = endPoint,
                title = "end",
                snippet = "Marker in end point"
            )
        }
        Text(text = driving.startTime)
        Text(text = " ~ ")
        Text(text = driving.endTime)
        Text(text = "Total driving time : ${secondToHMS(driving.totalTime)}")

        driving.subDrivingList.let {

        }
        Text(text = "Average sleep level : ${driving.averageSleepLevel}")
    }
}


