package com.comye1.dontsleepdriver.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.comye1.dontsleepdriver.R

@Composable
fun LoadingAnimation(modifier: Modifier = Modifier) {
    val angle = rememberInfiniteTransition()
    val loadingAnim by angle.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec =
        infiniteRepeatable(
            tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    Image(
        painter = painterResource(id = R.drawable.loading),
        contentDescription = "loading",
        modifier = modifier
            .size(64.dp)
            .rotate(loadingAnim)
    )
}