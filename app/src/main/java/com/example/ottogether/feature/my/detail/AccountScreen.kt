package com.example.ottogether.feature.my.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.R
import com.example.ottogether.core.ui.BottomConfirmSheet

private val BgSoft = Color(0xFFF6F6FB)
private val Orange = Color(0xFFFF7A2F)
private val TextSub = Color(0xFF6F7682)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    userName: String? = null,
    email: String? = null,
    phone: String? = null,
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    onWithdrawConfirmed: () -> Unit = {},
    /** 프로젝트 하단 네비게이션을 외부에서 넣고 싶다면 주입 */
    bottomBar: @Composable () -> Unit = {}
) {
    var showWithdraw by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BgSoft,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "내 계정",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF808080)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로"
                        )
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
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            // 프로필 이미지
            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = "프로필",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(16.dp))

            // 이름
            Text(
                text = userName?.let { "$it 님" } ?: "로그인이 필요해요",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF222222)
                )
            )

            Spacer(Modifier.height(20.dp))

            // 정보 카드 + 하단 액션(로그아웃/회원 탈퇴)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 정보 카드
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        InfoRow(label = "이메일", value = email ?: "-")
                        InfoRow(label = "휴대폰 번호", value = phone ?: "-")
                        InfoRow(label = "계좌번호", value = "국민은행 00000-0000-0000")
                    }
                }
                Spacer(Modifier.height(12.dp))
                // 카드 바로 아래 오른쪽 정렬 액션 (버튼 간격을 정말 작게 보이도록)
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(0.dp) // 눈으로도 좁아 보이는 간격
                    ) {
                        TextButton(
                            onClick = onLogout,
                            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                            modifier = Modifier.heightIn(min = 0.dp)
                        ) {
                            Text("로그아웃", color = TextSub)
                        }
                        TextButton(
                            onClick = { showWithdraw = true },
                            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                            modifier = Modifier.heightIn(min = 0.dp)
                        ) {
                            Text("회원 탈퇴", color = TextSub)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    // 하단 경고 시트
    BottomConfirmSheet(
        show = showWithdraw,
        message = "계정삭제시 복구 불가능합니다\n그래도 탈퇴하시겠습니까?",
        onNegative = { showWithdraw = false },
        onPositive = {
            showWithdraw = false
            onWithdrawConfirmed()
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextSub,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.width(84.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1E1E1E), fontSize = 13.sp)
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "내 계정 – Preview")
@Composable
private fun AccountPreview() {
    MaterialTheme {
        AccountScreen()
    }
}