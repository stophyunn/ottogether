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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ottogether.R
import com.example.ottogether.ui.theme.PreviewContainer

// 색 토큰
private val BgSoft   = Color(0xFFF6F6FB)
private val Orange   = Color(0xFFFF7A2F)
private val Blue     = Color(0xFF4C88FF)
private val GrayText = Color(0xFF6F7682)

// 데이터 모델 (파티 인원 추가)
data class SubItem(
    val name: String,
    val plan: String,
    val next: String,
    val price: String,
    val party: String,   // "3 / 5" 형태
    val status: String   // 매칭중 / 매칭대기 / 매칭완료
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySubscriptionsScreen(
    onBack: () -> Unit,
    onItem: (SubItem) -> Unit
) {
    val items = listOf(
        SubItem("넷플릭스", "프리미엄", "10/23", "10,900원", "5 / 5", "finish"),
        SubItem("쿠팡플레이", "", "10/23", "10,900원", "3 / 5", "wait"),
        SubItem("디즈니플러스", "", "10/23", "10,900원", "3 / 5", "wait"),
    )

    Scaffold(
        containerColor = BgSoft,
        topBar = { MySubTopBar(onBack) }
    ) { p ->
        // ✅ 흐름 기반 배치: LazyColumn 하나로 모두 배치 (겹침 없음)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(p), // 안전영역 패딩은 부모에서 한 번만
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 헤더 이미지
            item {
                Box(Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(R.drawable.myottsub),
                        contentDescription = "나의 OTT구독",
                        modifier = Modifier
                            .width(183.dp)
                            .height(58.dp)
                            .align(Alignment.CenterStart) // 왼쪽 정렬
                            .padding(start = 22.dp, top = 8.dp), // 원하던 여백만 padding으로
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // 구분선
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

            // 카드 리스트
            items(items.size) { idx ->
                val s = items[idx]
                SubscriptionCard(
                    item = s,
                    highlighted = idx == 0 && s.status == "finish",
                    onClick = { onItem(s) }
                )
            }

            // 하단 여백(네비 제스처바와 겹치지 않게)
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MySubTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            // No title content
        },
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
    item: SubItem,
    highlighted: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val borderColor = if (highlighted) Color.Transparent else Color.Transparent

    // Use a wrapping Box so the status icon can overflow the card without being clipped.
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Surface(
            shape = shape,
            color = Color.White,
            tonalElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = if (highlighted) 2.dp else 0.dp, color = borderColor, shape = shape)
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 로고
                Image(
                    painter = painterResource(id = logoFor(item.name)),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(Modifier.width(12.dp))

                // 가운데: 결제일, 제목(이름 | 플랜)
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("결제일 ${item.next}", color = Orange, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        if (item.plan.contains("프리미엄")) {
                            Spacer(Modifier.width(6.dp))
                            Image(
                                painter = painterResource(R.drawable.crown),
                                contentDescription = "crown",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("${item.name} | ${item.plan}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }

                // 오른쪽: 가격/파티 + 화살표
                Column(horizontalAlignment = Alignment.End) {
                    Text("월 ${item.price}", color = Color(0xFF333333), fontSize = 13.sp)
                    Text("파티원 ${item.party}", color = GrayText, fontSize = 12.sp)
                }

                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = GrayText)
            }
        }

        // 상태 아이콘을 Surface 밖에서 그려 카드 테두리를 살짝 덮게 배치 (클리핑 X)
        statusIconFor(item.status)?.let { resId ->
            Image(
                painter = painterResource(resId),
                contentDescription = item.status,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-30).dp)   // 위로 살짝 튀어나오게
                    .size(64.dp)
                    .zIndex(1f)                       // 카드 위로
            )
        }
    }
}

private fun logoFor(name: String): Int = when (name) {
    "넷플릭스" -> R.drawable.ic_logo_netflix
    "쿠팡플레이" -> R.drawable.ic_logo_coupang
    "디즈니플러스" -> R.drawable.ic_logo_disney
    else -> R.drawable.ic_logo_netflix
}

private fun statusIconFor(status: String): Int? = when (status.lowercase()) {
    "finish", "매칭완료" -> R.drawable.finish
    "wait", "매칭대기"   -> R.drawable.wait
    else -> null
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "MySubscriptions – Preview")
@Composable
private fun MySubscriptionsPreview() {
    PreviewContainer {
        MySubscriptionsScreen(onBack = {}, onItem = {})
    }
}