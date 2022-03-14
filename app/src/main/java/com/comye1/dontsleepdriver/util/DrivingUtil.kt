package com.comye1.dontsleepdriver.util

fun getAverageSleepLevel(list: List<Int>): Double = list.filterNot { it == -1 }.average()
