package com.example.ottogether.feature.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.R
import com.example.ottogether.core.designsystem.AppCard
import com.example.ottogether.ui.theme.OttogetherTheme

/* ---------- 색/모양 토큰 ---------- */
private val ColorOrange = Color(0xFFFF7A2F)
private val ColorPurple = Color(0xFF8C7BFF)
private val ColorSurfaceSoft = Color(0xFFF6F6FB)
private val BgSoft   = Color(0xFFF6F6FB)
private val Orange   = Color(0xFFFF7A2F)
private val TextGray = Color(0xFF6F7682)
private val CardRadius = 16.dp

/* ===========================================================
 *  Screen
 * =========================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanSelectScreen(
    ottId: String,
    onBack: () -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current
    val ui: PlanUiState =
        if (isPreview) {
            remember {
                PlanUiState(
                    ottName = ottId.ifBlank { "넷플릭스" },
                    plans = listOf("프리미엄", "스탠다드", "베이식", "광고형 스탠다드"),
                    selectedPlan = "프리미엄"
                )
            }
        } else {
            val vm: PlanViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            LaunchedEffect(ottId) { vm.loadPlans(ottId) }
            vm.ui.collectAsState().value
        }

    PlanSelectContent(
        ui = ui,
        onBack = onBack,
        onSelect = { /* TODO: vm.select(it) */ },
        onShareMine = { /* TODO */ },
        onLeader = { /* TODO */ },
        onUseAsLeader = { /* TODO */ }
    )
}

/* ===========================================================
 *  Content
 * =========================================================== */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanSelectContent(
    ui: PlanUiState,
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    onShareMine: () -> Unit,
    onLeader: () -> Unit,
    onUseAsLeader: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "요금제 선택하기",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF808080)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        },
        bottomBar = {
            // 하단을 덮는 큰 박스형 패널 (카드 X)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 30.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp) // 텍스트-버튼 간격 조금 더
                ) {
                    // 멤버십 정보 요약(카드가 아닌 내용만)
                    SpecSummaryContent()

                    // 패널 위에 올라가는 버튼들 (내 계정 공유하기, 파티원으로 이용하기)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 왼쪽: 내 계정 공유하기 (Outlined)
                        OutlinedButton(
                            onClick = onUseAsLeader,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Orange),
                            border = ButtonDefaults.outlinedButtonBorder()
                                .copy(width = 1.dp, brush = SolidColor(Orange))
                        ) {
                            Text(
                                "내 계정 공유하기",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }

                        // 오른쪽: 파티원으로 이용하기 (Filled)
                        Button(
                            onClick = onUseAsLeader,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Orange,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                "파티원",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {
            // 상단 선택된 OTT 박스 (홈 카드 스타일과 동일)
            SelectedOttBox(
                logoRes = R.drawable.ic_logo_netflix,
                title = "${ui.ottName} | ${ui.selectedPlan ?: "요금제"}"
            )

            Spacer(Modifier.height(16.dp))
            Divider(color = Color(0xFFE7E8EE), thickness = 1.dp)
            Spacer(Modifier.height(16.dp))

            // 2×2 정사각형 요금제 카드
            SquarePlanGrid(
                plans = ui.plans,
                selected = ui.selectedPlan,
                badgePlan = "프리미엄",
                onSelect = onSelect
            )
        }
    }
}

/* ===========================================================
 *  Components
 * =========================================================== */

@Composable
private fun SpecSummaryContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 월 요금 행
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("월 요금", color = TextGray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "1,700원",
                    style = TextStyle(
                        color = Color(0xFFB9BFCC),
                        textDecoration = TextDecoration.LineThrough,
                        fontSize = 14.sp
                    )
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "2,500원",
                    color = Orange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // 상세 3줄
        SpecRow(label = "영상 화질", value = "가장 좋음", emphasize = true)
        SpecRow(label = "해상도",   value = "4K + HDR",  emphasize = true)
        SpecRow(label = "동시접속 가능 대수", value = "6")
    }
}

@Composable
private fun SpecRow(
    label: String,
    value: String,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextGray, fontSize = 14.sp)
        Text(
            value,
            color = if (emphasize) Orange else Color.Black,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (emphasize) 16.sp else 14.sp
        )
    }
}

@Composable
private fun SelectedOttBox(
    logoRes: Int,
    title: String
) {
    AppCard( // 홈 카드와 동일: shape=16dp, padding=20x16, elevation=6dp
        modifier = Modifier.fillMaxWidth(),
        highlighted = false,
        padded = true
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = logoRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = Color(0xFF000000)
            )
        }
    }
}

/* 정사각형 2×2 그리드 */
@Composable
private fun SquarePlanGrid(
    plans: List<String>,
    selected: String?,
    badgePlan: String,
    onSelect: (String) -> Unit
) {
    val rows = plans.chunked(2)
    rows.forEach { row ->
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            row.forEach { plan ->
                val isSelected = plan == selected
                val border = if (isSelected) ColorOrange else Color(0xFFE6E7ED)
                val bg = if (isSelected) ColorOrange else Color.White
                val fg = if (isSelected) Color.White else Color.Black

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f) // 정사각형
                        .clip(RoundedCornerShape(CardRadius))
                        .border(2.dp, border, RoundedCornerShape(CardRadius))
                        .background(bg)
                        .clickable { onSelect(plan) }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = plan,
                        color = fg,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

/* ===========================================================
 *  Preview
 * =========================================================== */
@Preview(
    showBackground = false,
    widthDp = 393,
    heightDp = 852,
    name = "PlanSelect – Theme BG"
)
@Composable
private fun PlanSelectScreenPreview() {
    val dummy = PlanUiState(
        ottName = "넷플릭스",
        plans = listOf("프리미엄", "스탠다드", "베이식", "광고형 스탠다드"),
        selectedPlan = "프리미엄"
    )

    OttogetherTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PlanSelectContent(
                ui = dummy,
                onBack = {},
                onSelect = {},
                onShareMine = {},
                onLeader = {},
                onUseAsLeader = {}
            )
        }
    }
}