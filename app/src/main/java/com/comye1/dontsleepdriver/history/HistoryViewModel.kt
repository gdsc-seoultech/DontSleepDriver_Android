package com.comye1.dontsleepdriver.history

import android.util.Log
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

    val totalPages = mutableStateOf(1)
    val curPage = mutableStateOf(0)
    private val curMaxPage = mutableStateOf(0)

    var drivingList by mutableStateOf(listOf<DrivingResponse>())
        private set

    var subList by mutableStateOf(listOf<DrivingResponse>())
        private set

    var selectedItem by mutableStateOf<DrivingResponse?>(null)

    init {
        getHistoryPages()
        getHistoryByPage(1){
            curPage.value = 1
            curMaxPage.value = 1
        }
    }

    fun historyByPage(page: Int){
        if (curMaxPage.value < page) {
            // 새로 받아온다
            getHistoryByPage(page = page){
                curMaxPage.value = page
                curPage.value = page

                Log.d("curpage, totalPages", "${curPage.value} ${totalPages.value}")
            }
        }else {
            val startIndex = (page - 1) * 6
            subList = if (page == totalPages.value) {
                Log.d("curpage == totalPages", (drivingList.size % 6).toString())
                drivingList.subList(startIndex, startIndex + drivingList.size % 6)
            }else drivingList.subList(startIndex, startIndex + 6)

            curPage.value = page

            Log.d("curpage, totalPages", "${curPage.value} ${totalPages.value}")
        }
    }

    // page를 새로 받아온다
    fun getHistoryByPage(page: Int, onResponse: () -> Unit) {
        viewModelScope.launch {
            repository.getHistoryByPage(page).also {
                when (it) {
                    is Resource.Success -> {
                        val startIndex = (page - 1) * 6
                        drivingList = drivingList + (it.data ?: listOf())
                        subList = drivingList.subList(startIndex, startIndex + (it.data?.size?: 0))
                        onResponse()
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