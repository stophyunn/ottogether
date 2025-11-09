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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.R
import com.example.ottogether.core.designsystem.AppCard
import com.example.ottogether.ui.theme.PreviewContainer

private val BgSoft   = Color(0xFFF6F6FB)
private val Orange   = Color(0xFFFF7A2F)
private val GrayText = Color(0xFF6F7682)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    onBack: () -> Unit = {},
    onMyAccount: () -> Unit = {},
    onSubscriptions: () -> Unit = {},
    onSubscriptionItem: (String) -> Unit = {},
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
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            /** ✅ 상단 프로필 영역 (AppCard 사용 X) */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "프로필",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(Color(0xFFEDEFF3))
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("졸린 무지 님", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "내 계정 >",
                        color = GrayText,
                        modifier = Modifier.clickable(onClick = onMyAccount)
                    )
                }
            }

//            Spacer(Modifier.height(5.dp))
            Divider(color = Color(0xFFE7E8EE), thickness = 1.dp)
//            Spacer(Modifier.height(5.dp))

            /** ✅ 나의 OTT 구독 카드 (실선 제거) */
            AppCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    SubscriptionsSectionHeader(onClick = onSubscriptions)

                    SubscriptionRow(
                        logoRes = R.drawable.ic_logo_netflix,
                        title = "넷플릭스 | 프리미엄",
                        dday = "D - 20",
                        onClick = { onSubscriptionItem("넷플릭스") }
                    )

                    SubscriptionRow(
                        logoRes = R.drawable.ic_logo_coupang,
                        title = "쿠팡플레이",
                        dday = "D - 11",
                        onClick = { onSubscriptionItem("쿠팡플레이") }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

/** 섹션 헤더 */
@Composable
private fun SubscriptionsSectionHeader(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 5.dp),
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
}

/** 구독 아이템 (실선 없음) */
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

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun MyProfilePreview() {
    PreviewContainer {
        MyProfileScreen(
            bottomBar = {
                Surface(color = Color(0xFFF8F8FB)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                }
            }
        )
    }
}