package com.example.ottogether.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import com.example.ottogether.R
import com.example.ottogether.core.model.AuthResult
import com.example.ottogether.ui.theme.OttogetherTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    timeoutMillis: Long = 1200L,
    onQuickLogin: (suspend () -> AuthResult)? = null
) {
    var helper by remember { mutableStateOf<String?>(null) }

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

        onQuickLogin?.let { quickLogin ->
            val scope = rememberCoroutineScope()
            Text(
                text = helper ?: "테스트 계정으로 바로 로그인",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 76.dp)
            )

            Button(
                onClick = {
                    scope.launch {
                        val result = quickLogin()
                        helper = if (result.success) null else result.message
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFFF7A2F)
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "바로 로그인",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
private fun SplashPreview() {
    OttogetherTheme {
        SplashScreen(onTimeout = {})
    }
}
