package com.example.ottogether.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ottogether.R
import com.example.ottogether.core.data.SeedData
import com.example.ottogether.core.data.remote.dto.MovieResult
import com.example.ottogether.core.model.Subscription
import kotlinx.coroutines.delay

private val Highlight = Color(0xFFFF7A2F)
private val SectionBg = Color(0xFFF6F6FB)

private const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

/** 캐러셀에서 사용할 뷰모델용 데이터 */
private data class Recommendation(
    val title: String,
    val subtitle: String,
    val accent: String,
    val background: Color,
    val imageRes: Int? = null,     // 로컬 기본 배너용
    val imageUrl: String? = null   // TMDB 포스터/배너용
)

/** TMDB가 안 불러와졌을 때 사용할 기본 배너들 */
private val defaultRecommendedContents = listOf(
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
    trendingMovies: List<MovieResult>,          // TMDB에서 받아온 인기 영화 리스트
    onSelectProvider: (String) -> Unit,
    onOpenSubscriptions: () -> Unit,
    bottomBar: @Composable () -> Unit = {}
) {
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

            // 상단 배너 – API 결과가 있으면 TMDB 영화, 없으면 기본 배너
            RecommendedCarousel(movies = trendingMovies)

            Divider(color = Color(0xFFE7E8EE), thickness = 1.dp)

            Text(
                text = "어떤 OTT를 함께 보실래요?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            ProviderGrid(catalogs = catalogs, onSelect = onSelectProvider)

            Button(
                onClick = onOpenSubscriptions,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Highlight,
                    contentColor = Color.White
                )
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

/** TMDB 영화 리스트를 받아 캐러셀에 넘기기 좋은 형태로 변환 후 뿌려주는 래퍼 */
@Composable
private fun RecommendedCarousel(movies: List<MovieResult>) {
    val items: List<Recommendation> =
        if (movies.isNotEmpty()) {
            movies.take(5).mapIndexed { index, movie ->
                val imagePath = movie.backdrop_path ?: movie.poster_path
                val url = imagePath?.let { TMDB_IMAGE_BASE_URL + it }

                Recommendation(
                    title = movie.title,
                    subtitle = movie.overview,
                    accent = "지금 인기 OTT 콘텐츠",
                    background = when (index % 3) {
                        0 -> Color(0xFF1E1F2B)
                        1 -> Color(0xFF11263A)
                        else -> Color(0xFF0F223A)
                    },
                    imageUrl = url
                )
            }
        } else {
            defaultRecommendedContents
        }

    RecommendedCarouselContent(items = items)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RecommendedCarouselContent(items: List<Recommendation>) {
    // 최소 1페이지는 있어야 해서 maxOf(1, size)
    val pagerState = rememberPagerState(pageCount = { maxOf(1, items.size) })

    // 자동 슬라이드
    LaunchedEffect(pagerState, items.size) {
        if (items.size <= 1) return@LaunchedEffect

        while (true) {
            delay(3000)
            val next = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 170.dp)
                .clipToBounds()              // 양옆 잘라서 “중간에 걸쳐 보이는” 문제 방지
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 170.dp),
                userScrollEnabled = false    // 자동 슬라이드만 허용 (충돌 방지)
            ) { page ->
                val item = items.getOrNull(page) ?: return@HorizontalPager

                Surface(
                    color = Color.Transparent,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    tonalElevation = 6.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        // 🔹 뒷 배경: 포스터 / 이미지 + 블러
                        if (item.imageUrl != null) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.title,
                                modifier = Modifier
                                    .matchParentSize()
                                    .graphicsLayer { alpha = 0.99f }
                                    .blur(12.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(item.background)
                            )
                        }

                        // 🔹 진한 그라데이션 오버레이 (불투명도 ↑)
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x66000000),  // 위쪽 40%
                                            Color(0x88000000),  // 중간 53%
                                            Color(0xAA000000)   // 아래 67%
                                        )
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // 왼쪽: 텍스트 영역
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = item.accent,
                                    color = Highlight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    ),
                                    maxLines = 2
                                )
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFDBE1EE),
                                        lineHeight = 16.sp
                                    ),
                                    maxLines = 3
                                )
                            }

                            // 오른쪽: 포스터 썸네일 카드
                            if (item.imageUrl != null || item.imageRes != null) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    shadowElevation = 8.dp,
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(130.dp)
                                ) {
                                    if (item.imageUrl != null) {
                                        AsyncImage(
                                            model = item.imageUrl,
                                            contentDescription = item.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else if (item.imageRes != null) {
                                        Image(
                                            painter = painterResource(id = item.imageRes),
                                            contentDescription = item.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 인디케이터
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val indicatorCount = items.size.coerceAtLeast(1)
            repeat(indicatorCount) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .width(if (isSelected) 16.dp else 6.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(
                            if (isSelected) Highlight
                            else Color(0xFF9FA4B3)
                        )
                )
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
        modifier = modifier,
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