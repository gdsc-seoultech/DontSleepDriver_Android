package com.comye1.dontsleepdriver.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comye1.dontsleepdriver.data.model.DrivingResponse
import com.comye1.dontsleepdriver.repository.DSDRepository
import com.comye1.dontsleepdriver.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: DSDRepository
) : ViewModel() {

    val totalPages = mutableStateOf(0)
    val curPage = mutableStateOf(0)
    var drivingList by mutableStateOf(listOf<DrivingResponse>())
        private set

    var selectedItem by mutableStateOf<DrivingResponse?>(null)

    init {
//     drivingList.value = try {
//         repository
//     }
//        viewModelScope.launch {
            getHistoryPages()
            getHistoryByPage(1)
            curPage.value = 1
//            if (totalPages.value > 0) {
//                getHistoryByPage(1)
//                curPage.value = 1
//            }
//        }
    }

    fun getHistoryByPage(page: Int) {
        viewModelScope.launch {
            repository.getHistoryByPage(page).also {
                when (it) {
                    is Resource.Success -> {
                        drivingList = it.data ?: listOf()
                    }
                    is Resource.Error -> {
                        // 에러
                    }
                }
            }
        }
    }

    private fun getHistoryPages() {
        viewModelScope.launch {
            repository.getHistoryPages().also {
                when (it) {
                    is Resource.Success -> {
                        totalPages.value = it.data!!
                    }
                    is Resource.Error -> {
                        // 에러
                        totalPages.value = 0
                    }
                }
            }
        }
    }

    fun getDrivingItem(id: Int) {
        viewModelScope.launch {
            repository.getDrivingItem(id).also {
                when (it) {
                    is Resource.Success -> {
                        selectedItem = it.data
                    }
                    is Resource.Error -> {
                        // 에러
                    }
                }
            }
        }
    }

//    val drivingList = listOf(
//        DrivingResponse(
//            startTime = "04:33:10 06/03/2022",
//            endTime = "04:42:11 06/03/2022",
//            gpsData = listOf(
//                LatLng(37.625500, 127.079877),
//                LatLng(37.627699, 127.081415),
//                LatLng(37.627962, 127.084926),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.622602, 127.086884),
//                LatLng(37.620631, 127.087520),
//                LatLng(37.618024, 127.091351),
//                LatLng(37.616212, 127.100279),
//                LatLng(37.614720, 127.105728)
//            ),
//            gpsLevel = listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0),
//            avgSleepLevel = getAverageSleepLevel(listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0))
//        ),
//        DrivingResponse(
//            startTime = "04:33:10 06/03/2022",
//            endTime = "04:42:11 06/03/2022",
//            gpsData = listOf(
//                LatLng(37.625500, 127.079877),
//                LatLng(37.627699, 127.081415),
//                LatLng(37.627962, 127.084926),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.622602, 127.086884),
//                LatLng(37.620631, 127.087520),
//                LatLng(37.618024, 127.091351),
//                LatLng(37.616212, 127.100279),
//                LatLng(37.614720, 127.105728)
//            ),
//            gpsLevel = listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0),
//            avgSleepLevel = getAverageSleepLevel(listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0))
//        ),
//        DrivingResponse(
//            startTime = "04:33:10 06/03/2022",
//            endTime = "04:42:11 06/03/2022",
//            gpsData = listOf(
//                LatLng(37.625500, 127.079877),
//                LatLng(37.627699, 127.081415),
//                LatLng(37.627962, 127.084926),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.622602, 127.086884),
//                LatLng(37.620631, 127.087520),
//                LatLng(37.618024, 127.091351),
//                LatLng(37.616212, 127.100279),
//                LatLng(37.614720, 127.105728)
//            ),
//            gpsLevel = listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0),
//            avgSleepLevel = getAverageSleepLevel(listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0))
//        ),
//        DrivingResponse(
//            startTime = "04:33:10 06/03/2022",
//            endTime = "04:42:11 06/03/2022",
//            gpsData = listOf(
//                LatLng(37.625500, 127.079877),
//                LatLng(37.627699, 127.081415),
//                LatLng(37.627962, 127.084926),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.622602, 127.086884),
//                LatLng(37.620631, 127.087520),
//                LatLng(37.618024, 127.091351),
//                LatLng(37.616212, 127.100279),
//                LatLng(37.614720, 127.105728)
//            ),
//            gpsLevel = listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0),
//            avgSleepLevel = getAverageSleepLevel(listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0))
//        ),
//        DrivingResponse(
//            startTime = "04:33:10 06/03/2022",
//            endTime = "04:42:11 06/03/2022",
//            gpsData = listOf(
//                LatLng(37.625500, 127.079877),
//                LatLng(37.627699, 127.081415),
//                LatLng(37.627962, 127.084926),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.622602, 127.086884),
//                LatLng(37.620631, 127.087520),
//                LatLng(37.618024, 127.091351),
//                LatLng(37.616212, 127.100279),
//                LatLng(37.614720, 127.105728)
//            ),
//            gpsLevel = listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0),
//            avgSleepLevel = getAverageSleepLevel(listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0))
//        ),
//        DrivingResponse(
//            startTime = "04:33:10 06/03/2022",
//            endTime = "04:42:11 06/03/2022",
//            gpsData = listOf(
//                LatLng(37.625500, 127.079877),
//                LatLng(37.627699, 127.081415),
//                LatLng(37.627962, 127.084926),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.626305, 127.085760),
//                LatLng(37.622602, 127.086884),
//                LatLng(37.620631, 127.087520),
//                LatLng(37.618024, 127.091351),
//                LatLng(37.616212, 127.100279),
//                LatLng(37.614720, 127.105728)
//            ),
//            gpsLevel = listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0),
//            avgSleepLevel = getAverageSleepLevel(listOf(0, 4, 1, 2, -1, -1, -1, 0, 0, 1, 2, 0))
//        ),
//    )
}