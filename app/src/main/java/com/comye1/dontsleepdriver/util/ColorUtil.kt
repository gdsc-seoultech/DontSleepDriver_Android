package com.comye1.dontsleepdriver.util

import androidx.compose.ui.graphics.Color
import com.comye1.dontsleepdriver.ui.theme.DSDGreen
import com.comye1.dontsleepdriver.ui.theme.DSDPeach
import com.comye1.dontsleepdriver.ui.theme.DSDRed
import com.comye1.dontsleepdriver.ui.theme.DSDYellow

fun getColorByLevel(level: Int): Color = when {
    level == -1 -> Color.White
    level < 3 -> DSDGreen
    level < 5 -> DSDYellow
    level < 7 -> DSDPeach
    else -> DSDRed
}
