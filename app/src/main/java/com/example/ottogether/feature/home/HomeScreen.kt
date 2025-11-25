package com.example.ottogether.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.R
import com.example.ottogether.core.data.SeedData
import com.example.ottogether.core.model.Subscription
import java.time.format.DateTimeFormatter

private val Highlight = Color(0xFFFF7A2F)
private val SectionBg = Color(0xFFF6F6FB)

private data class Recommendation(
    val title: String,
    val subtitle: String,
    val accent: String,
    val background: Color,
    val imageRes: Int
)

private val recommendedContents = listOf(
    Recommendation(
        title = "오늘은 영화의 밤",
        subtitle = "취향저격 추천작을 만나보세요",
        accent = "에디터's Pick",
        background = Color(0xFF1E1F2B),
        imageRes = R.drawable.ic_logo_netflix
    ),
    Recommendation(
        title = "가족과 함께 보기 좋은",
        subtitle = "디즈니+ 신작 라인업",
        accent = "따뜻한 이야기",
        background = Color(0xFF11263A),
        imageRes = R.drawable.ic_logo_disney
    ),
    Recommendation(
        title = "스포츠 라이브",
        subtitle = "오늘의 빅매치를 놓치지 마세요",
        accent = "실시간 시청",
        background = Color(0xFF0F223A),
        imageRes = R.drawable.ic_logo_coupang
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userName: String?,
    catalogs: List<SeedData.ProviderCatalog>,
    subscriptions: List<Subscription>,
    onSelectProvider: (String) -> Unit,
    onOpenSubscriptions: () -> Unit,
    onOpenCalendar: () -> Unit,
    bottomBar: @Composable () -> Unit = {}
) {
    val providerNames = catalogs.associate { it.provider.name to it.displayName }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = SectionBg,
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HomeLogoHeader()

            RecommendedCarousel(items = recommendedContents)

            SubscriptionSummary(
                subscriptions = subscriptions,
                onOpenCalendar = onOpenCalendar,
                titleProvider = { providerNames[it.provider.name] ?: it.provider.name }
            )

            Divider(color = Color(0xFFE7E8EE), thickness = 1.dp)

            Text(
                text = "어떤 OTT를 함께 보실래요?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            ProviderGrid(catalogs = catalogs, onSelect = onSelectProvider)

            Button(
                onClick = onOpenSubscriptions,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Highlight, contentColor = Color.White)
            ) {
                Text("나의 구독 현황 보기", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HomeLogoHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_ottogether),
            contentDescription = "오티투게더 로고",
            modifier = Modifier.size(width = 200.dp, height = 60.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecommendedCarousel(items: List<Recommendation>) {
    val pagerState = rememberPagerState(pageCount = { items.size })

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp)
        ) { page ->
            val item = items[page]
            Surface(
                color = item.background,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = item.title,
                            modifier = Modifier
                                .size(56.dp)
                                .padding(12.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.accent,
                            color = Highlight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFDBE1EE))
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(items.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .width(if (isSelected) 16.dp else 6.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(if (isSelected) Highlight else Color(0xFFD7D9E0))
                )
            }
        }
    }
}

@Composable
private fun SubscriptionSummary(
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
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 0.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        shape = MaterialTheme.shapes.large
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
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6F7682))
                )
            } ?: run {
                Text(
                    text = "등록된 구독이 없습니다",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF9AA0A6))
                )
            }

            Button(
                onClick = onOpenCalendar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Highlight),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = SolidColor(Highlight))
            ) {
                Text("나의 구독 캘린더")
            }
        }
    }
}

@Composable
private fun ProviderGrid(
    catalogs: List<SeedData.ProviderCatalog>,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        catalogs.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { catalog ->
                    ProviderCard(
                        name = catalog.displayName,
                        logoRes = catalog.logoRes,
                        onClick = { onSelect(catalog.provider.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ProviderCard(
    name: String,
    logoRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = MaterialTheme.shapes.large,
        shadowElevation = 2.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = name,
                modifier = Modifier.size(40.dp)
            )
            Text(text = name, style = MaterialTheme.typography.titleMedium)
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
        modifier = Modifier,
        color = SectionBg,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF6F7682),
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (highlight) Highlight else Color(0xFF111111)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatMoney(amount: Int): String =
    if (amount <= 0) "0원" else "%,d원".format(amount)
