package com.comye1.dontsleepdriver.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.comye1.dontsleepdriver.data.model.DrivingResponse
import com.comye1.dontsleepdriver.util.getColorByLevel
import com.comye1.dontsleepdriver.util.secondToHMS
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun HistoryDetailScreen(drivingResponse: DrivingResponse?) {
    drivingResponse?.let {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val locationList = drivingResponse.gpsData.map { LatLng(it.lat, it.lng) }
            val startPoint = locationList.first()
            val endPoint = locationList.last()
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
                with(locationList) {
                    for (i in 1 until this.size) {
                        val sleepLevel = drivingResponse.gpsLevel[i]

                        latLngBounds.include(this[i]) // 포함
                        Polyline( // 경로 그리기
                            points = this.subList(i - 1, i + 1),
                            color = getColorByLevel(sleepLevel)
                        )
                    }

                }

                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngBounds(
                        latLngBounds.build(),
                        160
                    ) // bounds에 맞게 카메라 이동
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
            HistoryTitleText(text = "Starting Time")
            HistoryContentText(text = drivingResponse.startTime)
            HistoryTitleText(text = "Ending Time")
            HistoryContentText(text = drivingResponse.endTime)
            HistoryTitleText(text = "Total driving time")
            HistoryContentText(text = secondToHMS(drivingResponse.totalTime))

            HistoryTitleText(text = "Average sleep level")
            HistoryContentText(text = "%.1f".format(drivingResponse.avgSleepLevel))
        }
    }

}

@Composable
fun HistoryTitleText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.h6,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun HistoryContentText(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background(Color.LightGray)
            .padding(4.dp)
    )
}

