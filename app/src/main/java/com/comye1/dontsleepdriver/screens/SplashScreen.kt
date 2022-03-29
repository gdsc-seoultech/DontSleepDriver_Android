package com.comye1.dontsleepdriver.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.comye1.dontsleepdriver.R
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(toNext: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(2000L)
        toNext()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Don't Sleep", style = MaterialTheme.typography.h3, fontWeight = FontWeight.Bold)
        Text(text = "Driver!", style = MaterialTheme.typography.h3, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(50.dp))
        Image(
            painter = painterResource(id = R.drawable.driving_img),
            contentDescription = "driving image",
            modifier = Modifier.size(200.dp)
        )
    }
}