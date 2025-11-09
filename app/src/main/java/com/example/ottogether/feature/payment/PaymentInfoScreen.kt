package com.example.ottogether.feature.payment

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.ottogether.ui.theme.PreviewContainer

/* ---------- Local tokens ---------- */
private val BgSoft   = Color(0xFFF6F6FB)
private val Orange   = Color(0xFFFF7A2F)
private val TextGray = Color(0xFF6F7682)

/* =========================
 *  Screen
 * ========================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentInfoScreen(
    ottName: String,
    plan: String,
    logoRes: Int,
    onBack: () -> Unit = {},
    onRegisterShareMine: () -> Unit = {},
    onRegisterAsMember: () -> Unit = {},
) {
    Scaffold(
        containerColor = BgSoft,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "파티원으로 이용하기",
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
            // Bottom sheet-like panel that spans the width (not a Card)
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
                        .padding(horizontal = 30.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Membership summary (plain content, not Card)
                    SpecSummaryContent()

                    // 두 개 버튼 (좌: 내 계정 공유하기 - Outlined, 우: 파티원으로 이용하기 - Filled)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRegisterShareMine,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Orange)
                        ) {
                            Text("내 계정 공유하기", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Orange)
                        }

                        Button(
                            onClick = onRegisterAsMember,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Orange,
                                contentColor = Color.White
                            )
                        ) {
                            Text("파티원으로 이용", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        }
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
            Divider(color = Color(0xFFE7E8EE), thickness = 1.dp)

            Spacer(Modifier.height(12.dp)) // space above the fixed bottom panel
        }
    }
}

/* =========================
 *  Components
 * ========================= */
@Composable
private fun SelectedOttBox(
    logoRes: Int,
    title: String
) {
    AppCard( // shape=16dp, padding=20x16, elevation=6dp
        modifier = Modifier.fillMaxWidth(),
        highlighted = false,
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
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp // size up per request
                ),
                color = Color(0xFF000000),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SpecSummaryContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // 월 요금
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
        SpecRow(label = "영상 화질", value = "가장 좋음", emphasize = true)
        SpecRow(label = "해상도",   value = "4K + HDR",  emphasize = true)
        SpecRow(label = "동시접속 가능 대수", value = "6")
    }
}

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
    PreviewContainer {
        PaymentInfoScreen(
            ottName = "넷플릭스",
            plan = "프리미엄",
            logoRes = R.drawable.ic_logo_netflix
        )
    }
}