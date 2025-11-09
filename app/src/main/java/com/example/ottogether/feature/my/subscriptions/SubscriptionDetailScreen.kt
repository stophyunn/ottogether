package com.example.ottogether.feature.my.subscriptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.R
import com.example.ottogether.core.ui.BottomConfirmSheet

data class PartyMember(val name: String, val leader: Boolean = false)

private val BgSoft = Color(0xFFF6F6FB)
private val Orange = Color(0xFFFF7A2F)
private val TextSub = Color(0xFF6F7682)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailScreen(
    ottAndPlan: String,
    dateText: String = "결제일 25/10/23",
    onBack: () -> Unit = {},
    onEditAccount: () -> Unit = {},
    onLeaveConfirmed: () -> Unit = {},
) {
    var showQuit by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgSoft,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgSoft)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "파티 나가기",
                    color = TextSub,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .padding(4.dp)
                        .clickable { showQuit = true }
                )
            }
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 상단 타이틀/결제일 (탑바 밖, 중앙 정렬)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    ottAndPlan,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    dateText,
                    color = Orange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 아이디/비밀번호 카드
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoRow(label = "아이디", value = "song2025@sookmyung.ac.kr")
                            InfoRow(label = "비밀번호", value = "************")
                        }
                        IconButton(onClick = onEditAccount) {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "수정하기",
                                tint = TextSub
                            )
                        }
                    }
                }
            }

            // 파티장 섹션
            SectionHeader(title = "파티장")
            MemberRow(PartyMember("개졸린 무지", leader = true))

            // 파티원 섹션
            SectionHeader(title = "파티원")
            repeat(4) { MemberRow(PartyMember("개졸린 무지", leader = false)) }
        }
    }

    // 하단 경고 팝업
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
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSub, fontSize = 14.sp)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MemberRow(member: PartyMember) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 아바타
        Surface(
            color = Color(0xFFEDEFF3),
            shape = CircleShape
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }

        // 이름 + 왕관(옵션)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(member.name, style = MaterialTheme.typography.bodyMedium)
            if (member.leader) {
                Image(
                    painter = painterResource(id = R.drawable.crown),
                    contentDescription = "리더",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun SubscriptionDetailPreview() {
    SubscriptionDetailScreen(ottAndPlan = "넷플릭스 | 프리미엄")
}