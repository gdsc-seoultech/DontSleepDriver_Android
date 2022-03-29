package com.comye1.dontsleepdriver.util

import androidx.compose.ui.graphics.Color
import com.comye1.dontsleepdriver.ui.theme.DSDGreen
import com.comye1.dontsleepdriver.ui.theme.DSDPeach
import com.comye1.dontsleepdriver.ui.theme.DSDRed
import com.comye1.dontsleepdriver.ui.theme.DSDYellow

fun getColorByLevel(level: Int): Color = when {
    level == -1 -> Color.White
    level < 2 -> DSDGreen
    level < 4 -> DSDYellow
    level < 6 -> DSDPeach
    else -> DSDRed
}
