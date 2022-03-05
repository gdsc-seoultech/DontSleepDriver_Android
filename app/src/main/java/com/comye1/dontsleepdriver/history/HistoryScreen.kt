package com.comye1.dontsleepdriver.history

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.comye1.dontsleepdriver.data.model.Driving
import com.comye1.dontsleepdriver.ui.theme.DontSleepDriverTheme

@Composable
fun HistoryScreen(navigateBack: () -> Unit, viewModel: HistoryViewModel = hiltViewModel()) {

    val navController = rememberNavController()

    DontSleepDriverTheme {
        Scaffold() {
            NavHost(navController = navController, startDestination = "history_main") {
                composable("history_main") {
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
                    HistoryDetailScreen(viewModel.drivingList[index])

                }
            }

        }
    }

}

@Composable
fun HistoryDetailScreen(driving: Driving) {

}

@Composable
fun HistoryMainScreen(drivingList: List<Driving>, toDetail: (Int) -> Unit) {

}
