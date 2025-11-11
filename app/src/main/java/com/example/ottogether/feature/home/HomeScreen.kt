package com.example.ottogether.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ottogether.R

/**
 * 홈 화면 (AppNavHost와 호환 버전)
 *
 * @param onTapPlan 요금제 선택 화면으로 이동
 * @param onTapCalendar 캘린더로 이동
 * @param onTapProfile 내 프로필 화면으로 이동
 * @param onTapSubs 내 구독 목록 화면으로 이동
 */
@Composable
fun HomeScreen(
    onTapPlan: (String) -> Unit = {},
    onTapCalendar: () -> Unit = {},
    onTapProfile: () -> Unit = {},
    onTapSubs: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "홈 이미지"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "OTTogether 홈",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onTapPlan("netflix") }) {
            Text("요금제 선택하기")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onTapCalendar) {
            Text("캘린더 보기")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onTapProfile) {
            Text("내 프로필")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onTapSubs) {
            Text("나의 구독 보기")
        }
    }
}