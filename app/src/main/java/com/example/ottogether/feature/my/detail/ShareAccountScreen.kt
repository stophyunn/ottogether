// ShareAccountScreen.kt
package com.example.ottogether.feature.my.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.R
import com.example.ottogether.core.designsystem.AppCard
import com.example.ottogether.core.model.Money
import com.example.ottogether.core.model.Plan
import com.example.ottogether.core.ui.MembershipSpecSummary
import com.example.ottogether.core.util.toEpochMillis
import com.example.ottogether.core.util.toLocalDate
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    plan: Plan,
    logoRes: Int,
    onBack: () -> Unit = {},
    onRegisterPartyMatch: suspend (ShareAccountForm) -> Boolean = { true },
    onOpenMySubscriptions: () -> Unit = {},
) {
    var loginId by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var account by rememberSaveable { mutableStateOf("국민은행 2020-2020-2020202") }
    var editingField by remember { mutableStateOf<EditableField?>(null) }
    var editingValue by remember { mutableStateOf("") }
    var billingDate by remember { mutableStateOf(LocalDate.now().plusDays(3)) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val formatter = remember { DateTimeFormatter.ofPattern("MM / dd") }
    val scope = rememberCoroutineScope()

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
                    MembershipSpecSummary(plan = plan)

                    // 패널 위에 올라가는 버튼
                    Button(
                        onClick = {
                            if (isRegistering) return@Button
                            scope.launch {
                                isRegistering = true
                                val success = onRegisterPartyMatch(
                                    ShareAccountForm(
                                        loginId = loginId,
                                        password = password,
                                        account = account,
                                        firstBillingDate = billingDate
                                    )
                                )
                                isRegistering = false
                                if (success) {
                                    showSuccessDialog = true
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = loginId.isNotBlank() && password.isNotBlank() && !isRegistering,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Orange,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "파티장으로 파티매칭 등록하기",
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
                title = "$ottName | ${plan.name}"
            )
//            Spacer(Modifier.height(16.dp))
            Divider(color = Color(0xFFE7E8EE), thickness = 1.dp)
//            Spacer(Modifier.height(16.dp))

            AppCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LabelValueRow(
                        label = "아이디",
                        value = loginId,
                        trailingAction = "수정하기",
                        onActionClick = {
                            editingField = EditableField.Login
                            editingValue = loginId
                        }
                    )

                    Divider(
                        color = Color(0xFFE7E8EE),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LabelValueRow(
                        label = "비밀번호",
                        value = maskPassword(password),
                        trailingAction = "수정하기",
                        onActionClick = {
                            editingField = EditableField.Password
                            editingValue = password
                        }
                    )
                }
            }

            /* 계좌번호 카드 */
            AppCard {
                LabelValueRow(
                    label = "계좌번호",
                    value = account,
                    trailingAction = "수정하기",
                    onActionClick = {
                        editingField = EditableField.Account
                        editingValue = account
                    }
                )
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
                            value = formatter.format(billingDate),
                            onClick = { showDatePicker = true }
                        )
                        DateCell(
                            modifier = Modifier.weight(1f),
                            label = "다음 결제일",
                            value = formatter.format(billingDate.plusMonths(1)),
                            onClick = { showDatePicker = true }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp)) // 본문과 고정 바 간격
        }
    }

    editingField?.let { field ->
        EditValueDialog(
            title = when (field) {
                EditableField.Login -> "아이디를 수정할게요"
                EditableField.Password -> "비밀번호를 수정할게요"
                EditableField.Account -> "계좌번호를 수정할게요"
            },
            value = editingValue,
            onValueChange = { editingValue = it },
            isPassword = field == EditableField.Password,
            keyboardType = KeyboardType.Text,
            onDismiss = { editingField = null },
            onConfirm = { newValue ->
                when (field) {
                    EditableField.Login -> loginId = newValue
                    EditableField.Password -> password = newValue
                    EditableField.Account -> account = newValue
                }
                editingField = null
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onOpenMySubscriptions()
                }) {
                    Text("확인", color = Orange, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("파티방이 정상적으로 등록되었습니다!\n파티원 결성시 구독이 시작됩니다!", fontWeight = FontWeight.SemiBold)
            },
            containerColor = Color.White
        )
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = billingDate.toEpochMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        pickerState.selectedDateMillis?.let {
                            billingDate = it.toLocalDate()
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("확정", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("취소", color = TextGray)
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
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
                        .clickable(enabled = onActionClick != null) { onActionClick?.invoke() }
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
private fun EditValueDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean,
    keyboardType: KeyboardType,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(value) },
                enabled = value.isNotBlank()
            ) {
                Text("확인", color = Orange, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = TextGray)
            }
        },
        containerColor = Color.White
    )
}

private fun maskPassword(password: String): String {
    if (password.isBlank()) return "-"
    return "•".repeat(password.length.coerceAtMost(12))
}

private enum class EditableField { Login, Password, Account }

data class ShareAccountForm(
    val loginId: String,
    val password: String,
    val account: String,
    val firstBillingDate: LocalDate
)


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
private fun DateCell(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
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

/* =========================
 *  Preview
 * ========================= */
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "내 계정 공유하기 – Preview")
@Composable
private fun PaymentInfoPreview() {
    val samplePlan = Plan(
        id = "netflix-premium",
        name = "프리미엄",
        quality = "가장 좋음",
        resolution = "4K + HDR",
        maxScreens = 6,
        monthlyPrice = Money(17000),
        sharedMonthlyPrice = Money(4250)
    )
    MaterialTheme {
        ShareAccountScreen(
            ottName = "넷플릭스",
            plan = samplePlan,
            logoRes = R.drawable.ic_logo_netflix
        )
    }
}