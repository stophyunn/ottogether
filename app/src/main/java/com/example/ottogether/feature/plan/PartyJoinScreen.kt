package com.example.ottogether.feature.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ottogether.core.data.PartyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartyJoinViewModel @Inject constructor(
    private val repo: PartyRepository
) : androidx.lifecycle.ViewModel() {

    private val currentUserId = "u1"

    suspend fun tryJoin(code: String): String? =
        repo.join(code, currentUserId)?.id
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyJoinScreen(
    onJoined: (String) -> Unit,
    onAskBecomeLeader: () -> Unit,
    onCancel: () -> Unit,
    vm: PartyJoinViewModel = hiltViewModel()
) {
    var invite by remember { mutableStateOf("") }
    var showAsk by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showAsk) {
        AlertDialog(
            onDismissRequest = { showAsk = false },
            title = { Text("파티가 없어요") },
            text = { Text("파티장이 되시겠습니까?") },
            confirmButton = {
                TextButton(onClick = { showAsk = false; onAskBecomeLeader() }) { Text("예") }
            },
            dismissButton = {
                TextButton(onClick = { showAsk = false; onCancel() }) { Text("아니오") }
            }
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("파티 참여") }) }) { p ->
        Column(
            Modifier.padding(p).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = invite,
                onValueChange = { invite = it },
                label = { Text("초대코드 입력") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch {
                        val id = vm.tryJoin(invite.trim())
                        if (id != null) onJoined(id) else showAsk = true
                    }
                }
            ) { Text("참여하기") }

            TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                Text("취소")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "파티 참여 – Preview")
@Composable
fun PartyJoinPreview() {
    Scaffold(topBar = { TopAppBar(title = { Text("파티 참여 (미리보기)") }) }) { p ->
        Column(
            Modifier.padding(p).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = TextFieldValue("ABC1234"),
                onValueChange = {},
                label = { Text("초대코드 입력") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("결제수단 선택")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("카드", "계좌이체", "간편결제").forEach {
                    FilterChip(
                        selected = it == "카드",
                        onClick = {},
                        label = { Text(it) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("참여하기") }
            TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("취소") }
        }
    }
}