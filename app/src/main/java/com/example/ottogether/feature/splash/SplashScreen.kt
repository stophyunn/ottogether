package com.example.ottogether.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.ottogether.R
import com.example.ottogether.ui.theme.OttogetherTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    timeoutMillis: Long = 1200L
) {
    LaunchedEffect(Unit) {
        delay(timeoutMillis)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF7A2F)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.signlogo),
            contentDescription = "OTTogether",
            modifier = Modifier.size(200.dp)
        )

    }
}

@Preview
@Composable
private fun SplashPreview() {
    OttogetherTheme {
        SplashScreen(onTimeout = {})
    }
}
