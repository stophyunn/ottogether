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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.R
import com.example.ottogether.core.designsystem.AppCard
import com.example.ottogether.core.model.Money
import com.example.ottogether.core.model.Plan
import com.example.ottogether.core.ui.MembershipSpecSummary
import com.example.ottogether.ui.theme.OttogetherTheme

private val BgSoft = Color(0xFFF6F6FB)
private val Orange = Color(0xFFFF7A2F)
private val TextGray = Color(0xFF6F7682)
private val CardRadius = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanSelectScreen(
    providerKey: String,
    onBack: () -> Unit = {},
    onHost: (providerId: String, planId: String) -> Unit = { _, _ -> },
    onMember: (providerId: String, planId: String) -> Unit = { _, _ -> }
) {
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        val previewState = remember {
            PlanUiState(
                providerId = "NETFLIX",
                ottName = providerKey.ifBlank { "넷플릭스" },
                plans = listOf(
                    Plan(
                        id = "netflix-premium",
                        name = "프리미엄",
                        quality = "가장 좋음",
                        resolution = "4K + HDR",
                        maxScreens = 6,
                        monthlyPrice = Money(17000)
                    ),
                    Plan(
                        id = "netflix-standard",
                        name = "스탠다드",
                        quality = "좋음",
                        resolution = "1080p",
                        maxScreens = 4,
                        monthlyPrice = Money(13500)
                    )
                ),
                selectedPlanId = "netflix-premium",
                logoRes = R.drawable.ic_logo_netflix
            )
        }
        PlanSelectContent(
            ui = previewState,
            onBack = onBack,
            onSelectPlan = {},
            onHost = {},
            onMember = {}
        )
        return
    }

    val vm: PlanViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    LaunchedEffect(providerKey) { vm.loadPlans(providerKey) }
    val uiState = vm.ui.collectAsState().value

    PlanSelectContent(
        ui = uiState,
        onBack = onBack,
        onSelectPlan = vm::selectPlan,
        onHost = {
            val plan = uiState.selectedPlan ?: return@PlanSelectContent
            onHost(uiState.providerId, plan.id)
        },
        onMember = {
            val plan = uiState.selectedPlan ?: return@PlanSelectContent
            onMember(uiState.providerId, plan.id)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanSelectContent(
    ui: PlanUiState,
    onBack: () -> Unit,
    onSelectPlan: (String) -> Unit,
    onHost: () -> Unit,
    onMember: () -> Unit
) {
    Scaffold(
        containerColor = BgSoft,
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
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    MembershipSpecSummary(plan = ui.selectedPlan)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onHost,
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

                        Button(
                            onClick = onMember,
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
            SelectedOttBox(
                logoRes = ui.logoRes ?: R.drawable.ic_logo_netflix,
                title = ui.selectedPlan?.let { "${ui.ottName} | ${it.name}" } ?: ui.ottName
            )

            Spacer(Modifier.height(16.dp))
            Divider(color = Color(0xFFE7E8EE), thickness = 1.dp)
            Spacer(Modifier.height(16.dp))

            SquarePlanGrid(
                plans = ui.plans,
                selectedId = ui.selectedPlanId,
                onSelect = onSelectPlan
            )
        }
    }
}

@Composable
private fun SpecSummaryContent(plan: Plan?) {
    val fallback = "-"
    val labelStyle = MaterialTheme.typography.bodyMedium.copy(color = TextGray, fontSize = 13.sp)
    val valueStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF111111), fontSize = 16.sp)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("월 요금", style = labelStyle)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (plan != null) {
                    Text(
                        text = plan.monthlyPrice.toString(),
                        style = labelStyle.copy(textDecoration = TextDecoration.LineThrough)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = plan.sharedMonthlyPrice.toString(),
                        style = valueStyle.copy(color = Orange, fontWeight = FontWeight.Bold)
                    )
                } else {
                    Text(
                        fallback,
                        style = valueStyle.copy(color = Orange, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        SpecRow(label = "영상 화질", value = plan?.quality ?: fallback, emphasize = true)
        SpecRow(label = "해상도", value = plan?.resolution ?: fallback, emphasize = true)
        SpecRow(label = "동시접속 가능 대수", value = plan?.maxScreens?.toString() ?: fallback)
    }
}

@Composable
private fun SpecRow(
    label: String,
    value: String,
    emphasize: Boolean = false
) {
    val labelStyle = MaterialTheme.typography.bodyMedium.copy(color = TextGray, fontSize = 13.sp)
    val valueStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF111111), fontSize = 15.sp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = labelStyle)
        Text(
            value,
            style = valueStyle.copy(
                color = if (emphasize) Orange else valueStyle.color,
                fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
                fontSize = if (emphasize) 16.sp else valueStyle.fontSize
            )
        )
    }
}

@Composable
private fun SelectedOttBox(
    logoRes: Int,
    title: String
) {
    AppCard(
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

@Composable
private fun SquarePlanGrid(
    plans: List<Plan>,
    selectedId: String?,
    onSelect: (String) -> Unit
) {
    val rows = plans.chunked(2)
    rows.forEach { row ->
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            row.forEach { plan ->
                val isSelected = plan.id == selectedId
                val border = if (isSelected) Orange else Color(0xFFE6E7ED)
                val bg = if (isSelected) Orange else Color.White
                val fg = if (isSelected) Color.White else Color.Black
                val accent = if (isSelected) Color.White else Orange
                val crossedColor = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF9AA0A6)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(CardRadius))
                        .border(2.dp, border, RoundedCornerShape(CardRadius))
                        .background(bg)
                        .clickable { onSelect(plan.id) }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = plan.name,
                            color = fg,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = plan.monthlyPrice.toString(),
                            color = crossedColor,
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Text(
                            text = plan.sharedMonthlyPrice.toString(),
                            color = accent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            if (row.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(14.dp))
    }
}

@Preview(
    showBackground = false,
    widthDp = 393,
    heightDp = 852,
    name = "PlanSelect"
)
@Composable
private fun PlanSelectScreenPreview() {
    val dummy = PlanUiState(
        providerId = "NETFLIX",
        ottName = "넷플릭스",
        plans = listOf(
            Plan(
                id = "netflix-premium",
                name = "프리미엄",
                quality = "가장 좋음",
                resolution = "4K + HDR",
                maxScreens = 6,
                monthlyPrice = Money(17000)
            ),
            Plan(
                id = "netflix-standard",
                name = "스탠다드",
                quality = "좋음",
                resolution = "1080p",
                maxScreens = 4,
                monthlyPrice = Money(13500)
            )
        ),
        selectedPlanId = "netflix-premium",
        logoRes = R.drawable.ic_logo_netflix
    )

    OttogetherTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PlanSelectContent(
                ui = dummy,
                onBack = {},
                onSelectPlan = {},
                onHost = {},
                onMember = {}
            )
        }
    }
}
