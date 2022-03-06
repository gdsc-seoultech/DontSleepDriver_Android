package com.comye1.dontsleepdriver.history

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.comye1.dontsleepdriver.data.model.Driving
import com.comye1.dontsleepdriver.ui.theme.DontSleepDriverTheme
import com.comye1.dontsleepdriver.util.simpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(navigateBack: () -> Unit, viewModel: HistoryViewModel = hiltViewModel()) {

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
                    HistoryMainScreen(viewModel.drivingList) { idx ->
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
                    appBarTitle.value = "Driving Detail"
                    HistoryDetailScreen(viewModel.drivingList[index])
                }
            }

        }
    }

}

@Composable
fun HistoryMainScreen(drivingList: List<Driving>, toDetail: (Int) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(drivingList) { index, it ->
                HistoryItem(item = it) {
                    toDetail(index)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(item: Driving, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = simpleDateFormat.format(Date(item.startTime)))
            Text(text = " ~ ")
            Text(text = simpleDateFormat.format(Date(item.endTime)))
        }
        Column(
            Modifier
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Sleep Level")
            Text(text = "0.3")
        }
    }
}
