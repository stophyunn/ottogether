package com.example.ottogether.feature.my.subscriptions

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ottogether.R
import com.example.ottogether.core.model.Subscription
import com.example.ottogether.core.ui.logoFor
import java.time.format.DateTimeFormatter

private val BgSoft = Color(0xFFF6F6FB)
private val Orange = Color(0xFFFF7A2F)
private val Blue = Color(0xFF4C88FF)
private val GrayText = Color(0xFF6F7682)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySubscriptionsScreen(
    subscriptions: List<Subscription>,
    currentUserId: String?,
    onBack: () -> Unit,
    onItem: (Subscription) -> Unit,
    providerName: (Subscription) -> String = { it.provider.name }
) {
    val formatter = DateTimeFormatter.ofPattern("MM/dd")

    Scaffold(
        containerColor = BgSoft,
        topBar = { MySubTopBar(onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(R.drawable.myottsub),
                        contentDescription = "나의 OTT구독",
                        modifier = Modifier
                            .width(183.dp)
                            .height(58.dp)
                            .align(Alignment.CenterStart)
                            .padding(start = 22.dp, top = 8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            item {
                Divider(
                    color = Color(0xFFE7E8EE),
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                        .padding(top = 37.dp)
                )
            }

            items(subscriptions.size) { idx ->
                val sub = subscriptions[idx]
                val next = sub.billing.nextBillingDate.format(formatter)
                val status = if (sub.members.size + 1 >= sub.plan.maxScreens) {
                    SubscriptionStatus.Matched
                } else {
                    SubscriptionStatus.InProgress
                }
                SubscriptionCard(
                    subscription = sub,
                    status = status,
                    isOwner = sub.ownerUserId == currentUserId,
                    nextDate = next,
                    onClick = { onItem(sub) },
                    providerName = providerName(sub)
                )
            }

            if (subscriptions.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 1.dp,
                        color = Color.White
                    ) {
                        Text(
                            "구독 내역이 없습니다",
                            modifier = Modifier.padding(24.dp),
                            color = GrayText
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MySubTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BgSoft
        )
    )
}

@Composable
private fun SubscriptionCard(
    subscription: Subscription,
    status: SubscriptionStatus,
    isOwner: Boolean,
    nextDate: String,
    onClick: () -> Unit,
    providerName: String
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = shape,
            color = Color.White,
            tonalElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = logoFor(subscription.provider.name)),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("결제일 $nextDate", color = Orange, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        if (isOwner) {
                            Spacer(Modifier.width(6.dp))
                            Image(
                                painter = painterResource(R.drawable.crown),
                                contentDescription = "파티장",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "$providerName | ${subscription.plan.name}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        if (isOwner) {
                            Spacer(Modifier.width(6.dp))
                            Text("파티장", color = Orange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${subscription.members.size + 1}명 이용중", color = GrayText, fontSize = 12.sp)
                        Text("${subscription.plan.sharedMonthlyPrice}", color = GrayText, fontSize = 12.sp)
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = GrayText
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(1f)
        ) {
            StatusBadge(status)
        }
    }
}

private enum class SubscriptionStatus { InProgress, Matched }

@Composable
private fun StatusBadge(status: SubscriptionStatus) {
    val (text, color) = when (status) {
        SubscriptionStatus.InProgress -> "진행중" to Blue
        SubscriptionStatus.Matched -> "매칭완료" to Orange
    }
    Surface(
        color = color,
        shape = RoundedCornerShape(bottomStart = 16.dp)
    ) {
        Text(
            text,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
