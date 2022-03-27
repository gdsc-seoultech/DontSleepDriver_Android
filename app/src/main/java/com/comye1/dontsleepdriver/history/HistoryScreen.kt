package com.comye1.dontsleepdriver.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.comye1.dontsleepdriver.data.model.DrivingResponse
import com.comye1.dontsleepdriver.ui.theme.DontSleepDriverTheme
import com.comye1.dontsleepdriver.util.getColorByLevel

@Composable
fun HistoryScreen(
    navigateBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {

    val navController = rememberNavController()

    val appBarTitle = remember {
        mutableStateOf("")
    }

    DontSleepDriverTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(appBarTitle.value) },
                    navigationIcon = {
                        IconButton(onClick = { navigateBack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "navigate back"
                            )
                        }
                    })
            },
        ) {
            NavHost(navController = navController, startDestination = "history_main") {
                composable("history_main") {
                    appBarTitle.value = "Driving History"
                    HistoryMainScreen(
                        curPageList = viewModel.subList,
                        curPage = viewModel.curPage.value,
                        totalPages = viewModel.totalPages.value,
                        getPage = viewModel::historyByPage
                    ) { idx ->
                        navController.navigate("history_detail/${idx}")
                    }
                }
                composable(
                    "history_detail/{index}",
                    arguments = listOf(navArgument("index") {
                        type = NavType.IntType
                        defaultValue = 0
                    })
                ) { backStackEntry ->
                    val index = backStackEntry.arguments?.getInt("index") ?: 0
                    // index에 대항하는 아이템의 id를 통해 가져온다.
                    val id = viewModel.drivingList[index].id
                    viewModel.getDrivingItem(id)
                    appBarTitle.value = "Driving Detail"
                    HistoryDetailScreen(viewModel.selectedItem)
                }
            }

        }
    }

}

@Composable
fun HistoryMainScreen(
    curPageList: List<DrivingResponse>,
    curPage: Int,
    totalPages: Int,
    getPage: (Int) -> Unit,
    toDetail: (Int) -> Unit
) {
    if (curPageList.isEmpty()) {
        Text(text = "No data found", modifier = Modifier.padding(16.dp))
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                curPageList.forEachIndexed { index, driving ->
                    HistoryItem(item = driving) {
                        toDetail((curPage - 1) * 6 + index)
                    }
                }
            }
            /*
            2페이지 이상일 때만 버튼을 표시
             */
            if (totalPages > 1) {
                PageButton(
                    currentPage = curPage,
                    totalPages = totalPages,
                    toPreviousPage = {
                        getPage(curPage - 1)
                    },
                    toNextPage = {
                        getPage(curPage + 1)
                    }
                )
            }
        }

    }
}

@Composable
fun PageButton(
    currentPage: Int,
    totalPages: Int,
    toPreviousPage: () -> Unit,
    toNextPage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (currentPage > 1) toPreviousPage() },
            enabled = currentPage in 2..totalPages
        ) {
            Icon(
                imageVector = Icons.Default.NavigateBefore, contentDescription = "previous page",
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "$currentPage / $totalPages", fontSize = dpToSp(dp = 16.dp))
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { if (currentPage <= totalPages) toNextPage() },
            enabled = currentPage < totalPages
        ) {
            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = "next page",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun HistoryItem(item: DrivingResponse, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = item.startTime)
            Text(text = " ~ ")
            Text(text = item.endTime)
        }
        Column(
            Modifier
                .clip(RoundedCornerShape(4.dp))
//                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                .background(getColorByLevel(item.avgSleepLevel.toInt()))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Sleep Level")
            Text(text = "%.1f".format(item.avgSleepLevel))
        }
    }
}

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }
