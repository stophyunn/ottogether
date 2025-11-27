package com.example.ottogether.feature.my.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ottogether.R
import com.example.ottogether.core.designsystem.AppCard
import com.example.ottogether.core.model.Subscription
import com.example.ottogether.core.ui.logoFor
import java.time.format.DateTimeFormatter

private val BgSoft = Color(0xFFF6F6FB)
private val Orange = Color(0xFFFF7A2F)
private val GrayText = Color(0xFF6F7682)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    userName: String?,
    profileImageRes: Int? = null,
    profileImageUri: String? = null,
    subscriptions: List<Subscription>,
    providerName: (Subscription) -> String = { it.provider.name },
    onBack: () -> Unit = {},
    onMyAccount: () -> Unit = {},
    onSubscriptions: () -> Unit = {},
    onSubscriptionItem: (Subscription) -> Unit = {},
    onOpenCalendar: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        containerColor = BgSoft,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "마이페이지",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF808080)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgSoft)
            )
        },
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(Color(0xFFEDEFF3))
                ) {
                    AsyncImage(
                        model = profileImageUri ?: profileImageRes ?: R.drawable.profile,
                        contentDescription = "프로필",
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(36.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(userName ?: "로그인이 필요해요", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "내 계정 >",
                        color = GrayText,
                        modifier = Modifier.clickable(onClick = onMyAccount)
                    )
                }
            }

            Divider(color = Color(0xFFE7E8EE), thickness = 1.dp)

            SubscriptionReportCard(
                subscriptions = subscriptions,
                onOpenCalendar = onOpenCalendar,
                titleProvider = providerName
            )

            AppCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onSubscriptions),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("나의 OTT구독", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pre),
                            contentDescription = "더보기",
                            tint = GrayText,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    subscriptions.take(3).forEach { sub ->
                        SubscriptionRow(
                            logoRes = logoFor(sub.provider.name),
                            title = "${providerName(sub)} | ${sub.plan.name}",
                            dday = "D - ${sub.billing.cycleDay}",
                            onClick = { onSubscriptionItem(sub) }
                        )
                    }

                    if (subscriptions.isEmpty()) {
                        Text(
                            "구독 정보가 없습니다",
                            color = GrayText,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun SubscriptionRow(
    logoRes: Int,
    title: String,
    dday: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = logoRes),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
        }
        Text(dday, color = GrayText, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SubscriptionReportCard(
    subscriptions: List<Subscription>,
    onOpenCalendar: () -> Unit,
    titleProvider: (Subscription) -> String
) {
    val formatter = DateTimeFormatter.ofPattern("MM월 dd일")
    val totalFull = subscriptions.sumOf { it.plan.monthlyPrice.amountWon }
    val totalShared = subscriptions.sumOf { it.plan.sharedMonthlyPrice.amountWon }
    val totalSaved = (totalFull - totalShared).coerceAtLeast(0)
    val nextBilling = subscriptions.minByOrNull { it.billing.nextBillingDate }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "이번 달 구독 리포트",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "이번달 절약 금액",
                    value = formatMoney(totalSaved),
                    highlight = true,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "총 나의 구독료",
                    value = formatMoney(totalShared),
                    modifier = Modifier.weight(1f)
                )
            }

            nextBilling?.let { upcoming ->
                Text(
                    text = "다가오는 결제 · ${titleProvider(upcoming)} ${upcoming.plan.name} · ${upcoming.billing.nextBillingDate.format(formatter)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = GrayText)
                )
            } ?: run {
                Text(
                    text = "등록된 구독이 없습니다",
                    style = MaterialTheme.typography.bodySmall.copy(color = GrayText)
                )
            }

            Button(
                onClick = onOpenCalendar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Orange),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = SolidColor(Orange))
            ) {
                Text("나의 구독 캘린더")
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = BgSoft,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = GrayText,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (highlight) Orange else Color(0xFF111111)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatMoney(amount: Int): String =
    if (amount <= 0) "0원" else "%,d원".format(amount)

