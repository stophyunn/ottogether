package com.example.ottogether.feature.my.subscriptions

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ottogether.R
import com.example.ottogether.core.model.AuthResult
import com.example.ottogether.core.model.Subscription
import com.example.ottogether.core.model.User
import com.example.ottogether.core.ui.BottomConfirmSheet
import com.example.ottogether.core.ui.logoFor
import kotlinx.coroutines.launch
import com.example.ottogether.core.util.toEpochMillis
import com.example.ottogether.core.util.toLocalDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val BgSoft = Color(0xFFF6F6FB)
private val Orange = Color(0xFFFF7A2F)
private val TextSub = Color(0xFF6F7682)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionDetailScreen(
    subscription: Subscription,
    currentUserId: String? = null,
    onBack: () -> Unit = {},
    onEditAccount: () -> Unit = {},
    onLeaveConfirmed: () -> Unit = {},
    onLeaveScheduled: (LocalDate) -> Unit = {},
    onBillingDateChanged: (LocalDate) -> Unit = {},
    onTransferHost: suspend (String) -> AuthResult = { AuthResult(false) },
    userResolver: (String) -> User? = { null }
) {
    var showQuit by remember { mutableStateOf(false) }
    var showMemberLeave by remember { mutableStateOf(false) }
    var showScheduledNotice by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var transferMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var transferMessageColor by remember { mutableStateOf(Color(0xFFD32F2F)) }
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    val isOwner = subscription.ownerUserId == currentUserId
    val isFull = subscription.members.size + 1 >= subscription.plan.maxScreens
    val hasPendingExit = subscription.pendingExits.containsKey(currentUserId)
    val scheduledExitDate = subscription.pendingExits[currentUserId]
    val inviteLink = "https://ottogether.app/party/${subscription.id}"
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = BgSoft,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgSoft)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val canEditBilling = isOwner
                Text(
                    text = "결제일 수정하기",
                    color = if (canEditBilling) Orange else TextSub.copy(alpha = 0.5f),
                    modifier = Modifier.then(
                        if (canEditBilling) Modifier.clickable { showDatePicker = true } else Modifier
                    )
                )
                Text(
                    text = "파티 나가기",
                    color = TextSub,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .padding(4.dp)
                        .clickable {
                            when {
                                isOwner -> showQuit = true
                                hasPendingExit -> showScheduledNotice = true
                                else -> showMemberLeave = true
                            }
                        }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = logoFor(subscription.provider.name)),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${subscription.provider.name} | ${subscription.plan.name}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "결제일 ${subscription.billing.nextBillingDate.format(formatter)}",
                    color = Orange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                scheduledExitDate?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "파티 나가기 예정: ${it.format(formatter)}",
                        color = TextSub,
                        fontSize = 13.sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val showCredentials = isOwner || isFull
                    val loginValue = if (showCredentials) {
                        subscription.billing.loginId ?: "-"
                    } else {
                        "파티원이 모두 모이면 공개돼요"
                    }
                    val passwordValue = if (showCredentials) {
                        subscription.billing.passwordMasked ?: "-"
                    } else {
                        "파티가 완성되면 확인할 수 있어요"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoRow(label = "아이디", value = loginValue)
                            InfoRow(label = "비밀번호", value = passwordValue)
                        }
                        if (isOwner) {
                            IconButton(onClick = onEditAccount) {
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "수정하기",
                                    tint = TextSub
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "수정하기",
                                tint = TextSub.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }

            if (isOwner && !isFull) {
                InviteLinkCard(link = inviteLink)
            }

            SectionHeader(title = "파티장")
            val owner = userResolver(subscription.ownerUserId)
            MemberRow(user = owner, fallbackName = owner?.name ?: subscription.ownerUserId, leader = true)

            SectionHeader(title = "파티원")
            if (subscription.members.isEmpty()) {
                Text("아직 파티원이 없습니다", color = TextSub)
            } else {
                subscription.members.forEach { memberId ->
                    val user = userResolver(memberId)
                    MemberRow(user = user, fallbackName = user?.name ?: memberId, leader = false)
                }
            }

            if (isOwner && subscription.members.isNotEmpty()) {
                OutlinedButton(
                    onClick = { showTransferDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Orange),
                    border = ButtonDefaults.outlinedButtonBorder()
                ) {
                    Text("파티장 양도하기", fontWeight = FontWeight.Bold)
                }
                transferMessage?.let {
                    Text(it, color = transferMessageColor, fontSize = 12.sp)
                }
            }
        }
    }

    if (showMemberLeave) {
        val exitDate = scheduledExitDate ?: subscription.billing.nextBillingDate
        AlertDialog(
            onDismissRequest = { showMemberLeave = false },
            confirmButton = {
                TextButton(onClick = {
                    showMemberLeave = false
                    onLeaveScheduled(exitDate)
                    showScheduledNotice = true
                }) {
                    Text("예", color = Orange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMemberLeave = false }) {
                    Text("아니오", color = TextSub)
                }
            },
            text = {
                Text(
                    "환불은 불가하며 다음 결제일까지 이용할 수 있어요.\n나가기를 예약할까요?",
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = AlertDialogDefaults.dialogColors(containerColor = Color.White)
        )
    }

    if (showScheduledNotice) {
        val exitDate = scheduledExitDate ?: subscription.billing.nextBillingDate
        AlertDialog(
            onDismissRequest = { showScheduledNotice = false },
            confirmButton = {
                TextButton(onClick = { showScheduledNotice = false }) {
                    Text("확인", color = Orange, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                val scheduledText = exitDate.format(formatter)
                Text(
                    "파티 종료 예정일: $scheduledText\n이 날짜에 자동으로 파티에서 나가게 돼요.",
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = AlertDialogDefaults.dialogColors(containerColor = Color.White)
        )
    }

    BottomConfirmSheet(
        show = showQuit,
        message = "정말 구독을 그만두시겠어요?",
        onNegative = { showQuit = false },
        onPositive = {
            showQuit = false
            onLeaveConfirmed()
        }
    )

    if (showDatePicker && isOwner) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = subscription.billing.nextBillingDate.toEpochMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        pickerState.selectedDateMillis?.let {
                            onBillingDateChanged(it.toLocalDate())
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
                    Text("취소", color = TextSub)
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    if (showTransferDialog) {
        AlertDialog(
            onDismissRequest = { showTransferDialog = false },
            title = { Text("파티장을 누구에게 넘길까요?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    subscription.members.forEach { memberId ->
                        val name = nameResolver(memberId)
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    val result = onTransferHost(memberId)
                                    transferMessageColor = if (result.success) Color(0xFF1B873C) else Color(0xFFD32F2F)
                                    transferMessage = result.message
                                    showTransferDialog = false
                                }
                            }
                        ) {
                            Text(name, color = Orange, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTransferDialog = false }) {
                    Text("닫기", color = TextSub)
                }
            },
            colors = AlertDialogDefaults.dialogColors(containerColor = Color.White)
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSub, fontSize = 14.sp)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MemberRow(user: User?, fallbackName: String, leader: Boolean) {
    val displayName = user?.name ?: fallbackName
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box {
            if (user?.profileImageUri != null || user?.profileImageRes != null) {
                AsyncImage(
                    model = user.profileImageUri ?: user.profileImageRes,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFFEDEFF3)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(displayName.take(2), fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (leader) {
                Image(
                    painter = painterResource(R.drawable.crown),
                    contentDescription = "파티장",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Text(displayName, fontWeight = FontWeight.SemiBold)
            if (leader) {
                Text("파티장", color = TextSub, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun InviteLinkCard(link: String) {
    val clipboard = LocalClipboardManager.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("파티 초대 링크", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(link, color = TextSub, fontSize = 13.sp)
            OutlinedButton(
                onClick = { clipboard.setText(AnnotatedString(link)) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = Orange)
                Spacer(Modifier.width(6.dp))
                Text("주소 복사하기", color = Orange, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
