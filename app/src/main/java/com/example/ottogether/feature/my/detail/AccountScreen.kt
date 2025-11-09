package com.example.ottogether.feature.my.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ottogether.core.ui.BottomConfirmSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onLogout: () -> Unit = {},
    onWithdrawConfirmed: () -> Unit = {}   // 실제 탈퇴 처리 콜백
) {
    var showWithdraw by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("내 계정") }) },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("로그아웃") }

                Button(
                    onClick = { showWithdraw = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("회원 탈퇴") }
            }
        }
    ) { p ->
        Column(
            modifier = Modifier.padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("줄린 무지 님", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            InfoField("이메일", "song2025@sookmyung.ac.kr")
            InfoField("휴대폰 번호", "010-2025-2025")
            InfoField("계좌번호", "국민은행 00000-0000-0000")
        }
    }

    // ⬇️ 하단 경고 팝업
    BottomConfirmSheet(
        show = showWithdraw,
        message = "계정삭제시 복구 불가능합니다\n그래도 탈퇴하시겠습니까?",
        onNegative = { showWithdraw = false },
        onPositive = {
            showWithdraw = false
            onWithdrawConfirmed()
        }
    )
}

@Composable
private fun InfoField(label: String, value: String) {
    Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun AccountPreview() { AccountScreen() }

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 720,
    name = "내 계정 – 팝업 포함(전체 화면)"
)
@Composable
private fun AccountScreenPreview_WithPopupFull() {
    MaterialTheme {
        Box {
            // 배경: 실제 화면
            AccountScreen(
                onLogout = {},
                onWithdrawConfirmed = {}
            )
            // 오버레이: 하단 경고 팝업 (프리뷰는 인라인 모드)
            com.example.ottogether.core.ui.BottomConfirmSheet(
                show = true,
                message = "계정삭제시 복구 불가능합니다\n그래도 탈퇴하시겠습니까?",
                onNegative = {},
                onPositive = {},
                asDialog = false // ★ 프리뷰에서 확실히 보이도록 인라인
            )
        }
    }
}