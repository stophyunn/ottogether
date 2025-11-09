package com.example.ottogether.feature.my.subscriptions

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

data class PartyMember(val name: String, val role: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailScreen(
    ottAndPlan: String,
    dateText: String = "결제일 25/10/23",
    onLeaveConfirmed: () -> Unit = {}   // 실제 구독 종료 처리 콜백
) {
    var showQuit by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(ottAndPlan) }) },
        bottomBar = {
            Button(
                onClick = { showQuit = true },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("파티 나가기") }
        }
    ) { p ->
        Column(
            modifier = Modifier.padding(p).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(dateText, style = MaterialTheme.typography.labelMedium)
                    Text("아이디 / 비밀번호", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text("파티장", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            MemberCell(PartyMember("개별무지", "👑"))

            Text("파티원", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            repeat(4) { MemberCell(PartyMember("개별무지", "🙂")) }
        }
    }

    // ⬇️ 하단 경고 팝업
    BottomConfirmSheet(
        show = showQuit,
        message = "정말 구독을 그만두시겠어요?",
        onNegative = { showQuit = false },
        onPositive = {
            showQuit = false
            onLeaveConfirmed()
        }
    )
}

@Composable
private fun MemberCell(member: PartyMember) {
    Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(member.name)
            Text(member.role)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun SubscriptionDetailPreview() {
    SubscriptionDetailScreen(ottAndPlan = "넷플릭스 | 프리미엄")
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 720,
    name = "구독 상세 – 팝업 포함(전체 화면)"
)
@Composable
private fun SubscriptionDetailPreview_WithPopupFull() {
    MaterialTheme {
        Box {
            // 배경: 실제 화면
            SubscriptionDetailScreen(
                ottAndPlan = "넷플릭스 | 프리미엄",
                dateText = "결제일 25/10/23",
                onLeaveConfirmed = {}
            )
            // 오버레이: 하단 경고 팝업 (프리뷰는 인라인 모드)
            com.example.ottogether.core.ui.BottomConfirmSheet(
                show = true,
                message = "정말 구독을 그만두시겠어요?",
                onNegative = {},
                onPositive = {},
                asDialog = false // ★ 프리뷰에서 확실히 보이도록 인라인
            )
        }
    }
}