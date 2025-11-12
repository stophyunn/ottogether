package com.example.ottogether.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.example.ottogether.R
import com.example.ottogether.core.data.SeedData
import com.example.ottogether.core.model.Subscription
import java.time.format.DateTimeFormatter

private val Highlight = Color(0xFFFF7A2F)
private val SectionBg = Color(0xFFF6F6FB)

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

    Scaffold(
        containerColor = SectionBg,
        bottomBar = bottomBar
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HomeGreeting(userName)

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
private fun HomeGreeting(userName: String?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = userName?.let { "${it}님 안녕하세요" } ?: "환영합니다!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "파티를 열고 함께 나눠보세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6F7682)
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "다가오는 결제",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            if (subscriptions.isEmpty()) {
                Text("등록된 구독이 없습니다", color = Color(0xFF6F7682))
            } else {
                val formatter = DateTimeFormatter.ofPattern("MM월 dd일")
                subscriptions.take(3).forEach { sub ->
                    val next = sub.billing.nextBillingDate.format(formatter)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${titleProvider(sub)} | ${sub.plan.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = next,
                            color = Highlight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Button(
                onClick = onOpenCalendar,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Highlight),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = SolidColor(Highlight))
            ) {
                Text("결제일 수정하기")
            }
        }
    }
}

@Composable
private fun ProviderGrid(
    catalogs: List<SeedData.ProviderCatalog>,
    onSelect: (String) -> Unit
) {
    val rows = catalogs.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { catalog ->
                    ProviderCard(
                        name = catalog.displayName,
                        logoRes = catalog.logoRes,
                        onClick = { onSelect(catalog.provider.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
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
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = name,
                modifier = Modifier.size(48.dp)
            )
            Text(text = name, style = MaterialTheme.typography.titleMedium)
        }
    }
}
