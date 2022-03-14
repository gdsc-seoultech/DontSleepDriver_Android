package com.comye1.dontsleepdriver.data.model

import com.google.android.gms.maps.model.LatLng

data class Driving(
    val startTime: String,
    val endTime: String,
    val gpsData: List<LatLng>,
    val sleepData: List<Int>,
    val averageSleepLevel: Double
) {
    val subDrivingList: List<Pair<Int, Int>>
        get() {
            val list = mutableListOf<Pair<Int, Int>>()
            var isDriving = false
            var start = 0
            // -1이 아닌 구간을 쪼개서 구간의 길이를 리스트로..
            sleepData.forEachIndexed { index, i ->
                if (isDriving) {
                    if (i == -1) { // 운전 정지
                        list.add(Pair(start, index))
                        isDriving = false
                    }
                } else {
                    if (i > -1) { // 운전 시작
                        start = index
                        isDriving = true
                    }
                }
            }
            return list
        }

    val totalTime: Int
        get() = subDrivingList.sumOf { (it.second - it.first) * 2 }
}
