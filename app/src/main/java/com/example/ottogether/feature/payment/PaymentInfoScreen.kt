package com.example.ottogether.feature.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.core.model.AuthResult
import kotlinx.coroutines.launch
import com.example.ottogether.R
import com.example.ottogether.core.designsystem.AppCard
import com.example.ottogether.core.model.Money
import com.example.ottogether.core.model.Plan
import com.example.ottogether.core.ui.MembershipSpecSummary
import com.example.ottogether.ui.theme.PreviewContainer

/* tokens */
private val BgSoft   = Color(0xFFF6F6FB)
private val Orange   = Color(0xFFFF7A2F)
private val TextGray = Color(0xFF6F7682)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentInfoScreen(
    ottName: String,
    plan: Plan,
    logoRes: Int,
    onBack: () -> Unit = {},
    onJoinParty: suspend (String) -> AuthResult = { AuthResult(false) },
    onPayDone: () -> Unit = {}
) {
    var inviteCode by rememberSaveable { mutableStateOf("") }
    var helper by rememberSaveable { mutableStateOf<String?>(null) }
    var helperColor by remember { mutableStateOf(Color(0xFFD32F2F)) }
    val scope = rememberCoroutineScope()
    Scaffold(
        containerColor = BgSoft,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "파티원으로 이용하기",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF808080)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgSoft)
            )
        },
        bottomBar = {
            // 하단 고정: 스펙 + 결제완료 버튼
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MembershipSpecSummary(plan = plan)
                    Button(
                        onClick = onPayDone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange, contentColor = Color.White
                        )
                    ) {
                        Text("결제완료", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 상단 타이틀 이미지(결제 정보)
            Image(
                painter = painterResource(R.drawable.payinfo),
                contentDescription = "결제 정보",
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(160.dp)
                    .height(56.dp)
            )

            Divider(color = Color(0xFFE7E8EE), thickness = 1.dp)

            // 파티원 4칸
            AppCard {
                PartyGrid()
            }

            InviteLinkInfo(
                inviteCode = inviteCode,
                helper = helper,
                helperColor = helperColor,
                onValueChange = { inviteCode = it },
                onJoin = {
                    scope.launch {
                        val result = onJoinParty(inviteCode)
                        helperColor = if (result.success) Color(0xFF1B873C) else Color(0xFFD32F2F)
                        helper = result.message
                        if (result.success) {
                            inviteCode = ""
                        }
                    }
                }
            )

            // 결제일 / 다음 결제일
            AppCard(padded = false) {
                DatePanel(
                    leftLabel = "결제일", leftValue = "10 / 23",
                    rightLabel = "다음 결제일", rightValue = "11 / 23"
                )
            }

            // 결제 수단
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "결제 수단",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MethodChip(text = "체크/신용카드", modifier = Modifier.weight(1f))
                    MethodChip(text = "계좌이체", modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(8.dp)) // bottomBar와의 여유
        }
    }
}

/* ---------- parts ---------- */

@Composable
private fun PartyGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PartyCell("개졸린 무지", modifier = Modifier.weight(1f))
            PartyCell("개졸린 무지", modifier = Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PartyCell("개졸린 무지", modifier = Modifier.weight(1f))
            PartyCell("개졸린 무지", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PartyCell(name: String, modifier: Modifier = Modifier) {
    // ✅ 배경색 제거: 투명 + 그림자 없음
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.profile), // 임시 아바타
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(8.dp))
            // ✅ 크라운 완전 제거
            Text(text = name, fontSize = 14.sp)
        }
    }
}

@Composable
private fun InviteLinkInfo(
    inviteCode: String,
    helper: String?,
    helperColor: Color,
    onValueChange: (String) -> Unit,
    onJoin: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    AppCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "파티방 주소로 참여하기",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            OutlinedTextField(
                value = inviteCode,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "https://ottogether.app/party/ABCD",
                        color = TextGray.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        clipboard.getText()?.text?.let(onValueChange)
                    }
                ) {
                    Text("붙여넣기", color = Orange, fontSize = 13.sp)
                }
                Button(
                    onClick = onJoin,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White
                    )
                ) {
                    Text("파티방 참여하기", fontWeight = FontWeight.SemiBold)
                }
            }
            helper?.let {
                Text(it, color = helperColor, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun DatePanel(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(leftLabel, color = TextGray)
            Spacer(Modifier.height(6.dp))
            Text(
                leftValue,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black, color = Orange
                )
            )
        }
        Divider(
            color = Color(0xFFE7E8EE),
            modifier = Modifier
                .width(1.dp)
                .height(54.dp)
                .padding(vertical = 4.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(rightLabel, color = TextGray)
            Spacer(Modifier.height(6.dp))
            Text(
                rightValue,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black, color = Orange
                )
            )
        }
    }
}

@Composable
private fun MethodChip(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, fontWeight = FontWeight.SemiBold)
        }
    }
}

/* preview */
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "결제 정보 – Preview")
@Composable
private fun PaymentInfoPreview() {
    val samplePlan = Plan(
        id = "netflix-premium",
        name = "프리미엄",
        quality = "가장 좋음",
        resolution = "4K + HDR",
        maxScreens = 6,
        monthlyPrice = Money(17000),
        sharedMonthlyPrice = Money(4250)
    )
    PreviewContainer {
        PaymentInfoScreen(
            ottName = "넷플릭스",
            plan = samplePlan,
            logoRes = R.drawable.ic_logo_netflix
        )
    }
}