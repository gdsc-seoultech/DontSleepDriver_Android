package com.comye1.dontsleepdriver.util

import java.text.SimpleDateFormat
import java.util.*

val simpleDateFormat = SimpleDateFormat("kk:mm:ss dd/MM/yyyy", Locale.ROOT)

fun secondToHMS(seconds: Int) = "${seconds / 3600} h ${(seconds % 3600) / 60} m ${seconds % 60} s"