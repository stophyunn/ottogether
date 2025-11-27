package com.example.ottogether.feature.home

import com.example.ottogether.core.data.remote.dto.MovieResult

data class HomeUiState(
    val ottList: List<OttItem> = emptyList(),
    val isLoading: Boolean = false,
    val trendingMovies: List<MovieResult> = emptyList()
)

/** 홈 화면에서 OTT 항목 한 개를 표현하는 데이터 모델 */
data class OttItem(
    val name: String,
    val logo: Int,
    val price: String = PRICE_NOW,      // 기본값
    val discount: String = SALE_PERCENT // 기본값
)

///** 미리보기나 초기 표시용 더미 데이터 */
//fun sampleOttList() = listOf(
//    OttItem("넷플릭스", android.R.drawable.ic_menu_gallery, "10,900원", "20%"),
//    OttItem("쿠팡플레이", android.R.drawable.ic_menu_gallery, "10,900원", "20%"),
//    OttItem("왓챠", android.R.drawable.ic_menu_gallery, "10,900원", "20%"),
//    OttItem("티빙", android.R.drawable.ic_menu_gallery, "10,900원", "20%"),
//    OttItem("디즈니플러스", android.R.drawable.ic_menu_gallery, "10,900원", "20%"),
//    OttItem("웨이브", android.R.drawable.ic_menu_gallery, "10,900원", "20%")
//)