// ShareAccountScreen.kt
package com.example.ottogether.feature.my.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.R
import com.example.ottogether.core.designsystem.AppCard

/* ---------- 토큰(이 파일에서만 사용) ---------- */
private val BgSoft   = Color(0xFFF6F6FB)
private val Orange   = Color(0xFFFF7A2F)
private val TextGray = Color(0xFF6F7682)

/* =========================
 *  Screen
 * ========================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAccountScreen(
    ottName: String,
    plan: String,
    logoRes: Int,
    onBack: () -> Unit = {},
    onRegisterPartyMatch: () -> Unit = {}
) {
    Scaffold(
        containerColor = BgSoft,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "내 계정 공유하기",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF808080)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgSoft)
            )
        },
        bottomBar = {
            // 하단을 덮는 큰 박스형 패널 (카드 X)
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 멤버십 정보 요약(카드가 아닌 내용만)
                    SpecSummaryContent()

                    // 패널 위에 올라가는 버튼
                    Button(
                        onClick = onRegisterPartyMatch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "파티매칭 등록하기",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp   // ✅ 폰트 크기 추가
                        )
                    }
                }
            }
        }
    ) { inner ->
        val scroll = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 16.dp)
                .verticalScroll(scroll)
                .statusBarsPadding()
                .padding(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /* 상단 OTT 박스 — PlanSelect의 SelectedOttBox와 동일 스타일 */
            SelectedOttBox(
                logoRes = logoRes,
                title = "$ottName | $plan"
            )
//            Spacer(Modifier.height(16.dp))
            Divider(color = Color(0xFFE7E8EE), thickness = 1.dp)
//            Spacer(Modifier.height(16.dp))

            AppCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LabelValueRow(
                        label = "아이디",
                        value = "song2025@sookmyung.ac.kr",
                        trailingAction = "수정하기"
                    )

                    Divider(
                        color = Color(0xFFE7E8EE),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LabelValueRow(
                        label = "비밀번호",
                        value = "************",
                        trailingAction = "수정하기"
                    )
                }
            }

            /* 계좌번호 카드 */
            AppCard {
                LabelValueRow(label = "계좌번호", value = "국민은행 2020-2020-2020202", trailingAction = "수정하기")
            }

            /* 결제/다음 결제 */
            AppCard {
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DateCell(
                            modifier = Modifier.weight(1f),
                            label = "결제일",
                            value = "10 / 23"
                        )
                        DateCell(
                            modifier = Modifier.weight(1f),
                            label = "다음 결제일",
                            value = "11 / 23"
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp)) // 본문과 고정 바 간격
        }
    }
}

/* =========================
 *  Helpers (이 파일 전용 간단 구현)
 * ========================= */
@Composable
private fun SpecSummaryContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 월 요금 행
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("월 요금", color = TextGray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "1,700원",
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFFB9BFCC),
                        textDecoration = TextDecoration.LineThrough,
                        fontSize = 14.sp
                    )
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "2,500원",
                    color = Orange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // 상세 3줄
        SpecRow(label = "영상 화질", value = "가장 좋음", emphasize = true)
        SpecRow(label = "해상도",   value = "4K + HDR",  emphasize = true)
        SpecRow(label = "동시접속 가능 대수", value = "6")
    }
}
@Composable
private fun SelectedOttBox(
    logoRes: Int,
    title: String
) {
    AppCard(                              // ✅ 홈 카드와 동일: shape=16dp, padding=20x16, elevation=6dp
        modifier = Modifier.fillMaxWidth(),
        highlighted = false,              // 필요하면 true로 주황 2dp 테두리
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
                modifier = Modifier.size(48.dp)          // ✅ 홈과 동일 로고 사이즈
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp // ✅ 사이즈 업
                ),
                color = Color(0xFF000000)
            )
        }
    }
}

@Composable
private fun LabelValueRow(
    label: String,
    value: String,
    trailingAction: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        // 1) 윗줄: 라벨 + 수정하기
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = TextGray, fontSize = 13.sp)
            if (trailingAction != null) {
                Text(
                    text = trailingAction,
                    color = Color(0xFFB9BFCC),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                    //.clickable { onActionClick?.invoke() } // 클릭 필요 시 해제
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        // 2) 아랫줄: 값(왼쪽 정렬, 여러 줄 허용)
        Text(
            text = value,
            fontSize = 15.sp,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,                     // 길면 2줄까지
            overflow = TextOverflow.Ellipsis  // 넘치면 말줄임
        )
    }
}


@Composable
private fun SmallGhostText(text: String) {
    Text(
        text = text,
        color = Color(0xFFB9BFCC),
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun DateCell(modifier: Modifier = Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = TextGray, modifier = Modifier.padding(bottom = 6.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = Orange
            )
        )
    }
}

/* 홈/플랜과 동일한 스타일의 SpecRow */
@Composable
private fun SpecRow(
    label: String,
    value: String,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextGray, fontSize = 14.sp)
        Text(
            value,
            color = if (emphasize) Orange else Color.Black,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (emphasize) 16.sp else 14.sp
        )
    }
}

/* =========================
 *  Preview
 * ========================= */
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "내 계정 공유하기 – Preview")
@Composable
private fun PaymentInfoPreview() {
    MaterialTheme {
        ShareAccountScreen(
            ottName = "넷플릭스",
            plan = "프리미엄",
            logoRes = R.drawable.ic_logo_netflix
        )
    }
}