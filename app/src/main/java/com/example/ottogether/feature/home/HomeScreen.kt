package com.example.ottogether.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ottogether.R
import com.example.ottogether.ui.theme.PreviewContainer

/* ---- spacing / style 토큰 ---- */
private val GUTTER = 16.dp
private val ITEM_SPACING = 16.dp
private val CARD_PADDING = 16.dp
private val LOGO_SIZE = 48.dp
private val BORDER_SELECTED = Color(0xFFFF7A2F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOttClick: (String) -> Unit = {}
) {
    val isPreview = LocalInspectionMode.current
    val uiState: HomeUiState =
        if (isPreview) {
            remember { dummyHomeUi() }
        } else {
            val vm: HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            vm.uiState.collectAsState().value.also {
                LaunchedEffect(Unit) { vm.loadOttList() }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .height(96.dp)
                    .padding(top = 24.dp),    // ✅ 부모에 패딩, 이미지 크기엔 영향 없음
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                title = {
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_logo_ottogether),
                            contentDescription = "OTTOGETHER",
                            modifier = Modifier
                                .width(218.dp)
                                .height(85.dp),   // 👈 정확히 피그마 크기로 유지됨
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = GUTTER, vertical = GUTTER)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(ITEM_SPACING)) {
                uiState.ottList.forEachIndexed { idx, ott ->
                    OttCard(
                        name = ott.name,
                        logoRes = ott.logo,
                        salePercent = SALE_PERCENT,
                        priceNow = PRICE_NOW,
                        priceOriginal = PRICE_ORIGINAL,
                        highlighted = (idx == 0),
                        onClick = { onOttClick(ott.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OttCard(
    name: String,
    logoRes: Int,
    salePercent: String,
    priceNow: String,
    priceOriginal: String,
    highlighted: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = if (highlighted) BorderStroke(2.dp, Color(0xFFFF7A2F)) else null
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // ✅ 왼쪽 이름, 오른쪽 가격
        ) {
            // 왼쪽: 로고 + 이름
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(logoRes),
                    contentDescription = "$name 로고",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000)
                    )
                )
            }

            // 오른쪽: 가격 정보
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column {
                    Text(
                        text = priceOriginal,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFC3C7D0),
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                    Row {
                        Text(
                            text = salePercent,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFFF7A2F),
                                fontWeight = FontWeight.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = priceNow,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF6F7682)
                            )
                        )
                    }


                }


            }
        }
    }
}

/* ---- 프리뷰 ---- */
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "Home – 간격 통일")
@Composable
private fun HomeScreenPreview() {
    PreviewContainer {
        HomeScreen()
    }
}

/* ---- 프리뷰 더미(모델 재정의 없음) ---- */
private fun dummyHomeUi(): HomeUiState = HomeUiState(
    ottList = listOf(
        OttItem("넷플릭스", R.drawable.ic_logo_netflix),
        OttItem("쿠팡플레이", R.drawable.ic_logo_coupang),
        OttItem("왓챠", R.drawable.ic_logo_watcha),
        OttItem("티빙", R.drawable.ic_logo_tving),
        OttItem("디즈니플러스", R.drawable.ic_logo_disney),
        OttItem("웨이브", R.drawable.ic_logo_wavve),
    ),
    isLoading = false
)