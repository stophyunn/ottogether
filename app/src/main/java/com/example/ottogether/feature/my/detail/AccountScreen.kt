package com.example.ottogether.feature.my.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.matchParentSize
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    password: String? = null,
    profileImageRes: Int? = null,
    onBack: () -> Unit = {},
    onChangeProfileImage: () -> Unit = {},
    onUpdateEmail: (String) -> Unit = {},
    onUpdatePhone: (String) -> Unit = {},
    onUpdatePassword: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onWithdrawConfirmed: () -> Unit = {},
    /** 프로젝트 하단 네비게이션을 외부에서 넣고 싶다면 주입 */
    bottomBar: @Composable () -> Unit = {},
) {
    var showWithdraw by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf<AccountEditableField?>(null) }
    var editingValue by remember { mutableStateOf("") }
    val maskedPassword = password?.takeIf { it.isNotBlank() }
        ?.let { "•".repeat(it.length.coerceAtMost(12)) } ?: "-"

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

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color(0xFFEDEFF3))
                    .clickable(onClick = onChangeProfileImage)
            ) {
                Image(
                    painter = painterResource(profileImageRes ?: R.drawable.profile),
                    contentDescription = "프로필",
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(60.dp))
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(onClick = onChangeProfileImage),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "프로필 변경",
                            tint = Orange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = userName?.let { "$it 님" } ?: "로그인이 필요해요",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF222222)
                )
            )

            Spacer(Modifier.height(20.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
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
                        InfoRow(
                            label = "아이디",
                            value = email ?: "-",
                            actionLabel = "수정",
                            onAction = {
                                editingField = AccountEditableField.Email
                                editingValue = email.orEmpty()
                            }
                        )
                        InfoRow(
                            label = "비밀번호",
                            value = maskedPassword,
                            actionLabel = "변경",
                            onAction = {
                                editingField = AccountEditableField.Password
                                editingValue = password.orEmpty()
                            }
                        )
                        InfoRow(
                            label = "휴대폰 번호",
                            value = phone ?: "-",
                            actionLabel = "수정",
                            onAction = {
                                editingField = AccountEditableField.Phone
                                editingValue = phone.orEmpty()
                            }
                        )
                        InfoRow(label = "계좌번호", value = "국민은행 00000-0000-0000")
                    }
                }
                Spacer(Modifier.height(12.dp))
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(0.dp)
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

    BottomConfirmSheet(
        show = showWithdraw,
        message = "계정삭제시 복구 불가능합니다\n그래도 탈퇴하시겠습니까?",
        onNegative = { showWithdraw = false },
        onPositive = {
            showWithdraw = false
            onWithdrawConfirmed()
        }
    )

    editingField?.let { field ->
        AccountEditDialog(
            title = when (field) {
                AccountEditableField.Email -> "아이디를 수정할게요"
                AccountEditableField.Password -> "비밀번호를 수정할게요"
                AccountEditableField.Phone -> "휴대폰 번호를 수정할게요"
            },
            value = editingValue,
            onValueChange = { editingValue = it },
            keyboardType = when (field) {
                AccountEditableField.Email -> KeyboardType.Email
                AccountEditableField.Password -> KeyboardType.Password
                AccountEditableField.Phone -> KeyboardType.Phone
            },
            isPassword = field == AccountEditableField.Password,
            onDismiss = {
                editingField = null
                editingValue = ""
            },
            onConfirm = { newValue ->
                when (field) {
                    AccountEditableField.Email -> onUpdateEmail(newValue)
                    AccountEditableField.Password -> onUpdatePassword(newValue)
                    AccountEditableField.Phone -> onUpdatePhone(newValue)
                }
                editingField = null
                editingValue = ""
            }
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
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
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                color = Orange,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clickable(enabled = onAction != null) { onAction?.invoke() }
            )
        }
    }
}

@Composable
private fun AccountEditDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    isPassword: Boolean,
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
            TextButton(onClick = { onConfirm(value) }, enabled = value.isNotBlank()) {
                Text("확인", color = Orange, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = TextSub)
            }
        }
    )
}

private enum class AccountEditableField { Email, Password, Phone }

@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "내 계정 – Preview")
@Composable
private fun AccountPreview() {
    MaterialTheme {
        AccountScreen()
    }
}
