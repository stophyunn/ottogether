package com.example.ottogether.feature.auth

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ottogether.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController? = null, // preview에서는 null
    isLoggedIn: Boolean = false              // 로그인 여부에 따라 분기
) {
    val scale = remember { Animatable(0f) }

    // ✅ 간단한 확대 애니메이션
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = { OvershootInterpolator(2f).getInterpolation(it) })
        )
        delay(1200)
        // 실제 네비게이션 분기
        navController?.navigate(if (isLoggedIn) "home" else "auth/login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // ✅ 화면 구성
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 앱 로고 (예시)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "앱 로고",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "OTTOGETHER",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "한눈에 관리하는 OTT 파티",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
    }
}
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "스플래시 – Preview")
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen(navController = null)
    }
}