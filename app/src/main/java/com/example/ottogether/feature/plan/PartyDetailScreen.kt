package com.example.ottogether.feature.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ottogether.core.data.PartyRepository
import com.example.ottogether.core.model.Party
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartyDetailViewModel @Inject constructor(
    private val repo: PartyRepository
) : androidx.lifecycle.ViewModel() {

    var party by mutableStateOf<Party?>(null)
        private set

    suspend fun load(id: String) { party = repo.get(id) }
    suspend fun leave(id: String) { repo.leave(id, "u1") }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyDetailScreen(
    partyId: String,                     // ✅ 추가: AppNavHost에서 넘겨주는 partyId 매개변수
    onEdit: (String) -> Unit,
    onLeave: () -> Unit,
    vm: PartyDetailViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    // ✅ 전달받은 partyId 바로 사용 (NavBackStackEntry 필요 없음)
    LaunchedEffect(partyId) { vm.load(partyId) }

    Scaffold(topBar = { TopAppBar(title = { Text("파티 상세") }) }) { p ->
        Column(
            Modifier.padding(p).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val pty = vm.party
            if (pty == null) {
                Text("불러오는 중...")
            } else {
                Text("OTT: ${pty.providerId} | 요금제: ${pty.planId}")
                Text("파티장: ${pty.ownerId}")
                Text("파티원: ${pty.members.joinToString()}")
                Text("초대코드: ${pty.inviteCode}")
                Text("다음 결제일: ${pty.nextBillingDate}")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { onEdit(pty.id) }) { Text("수정하기") }
                    OutlinedButton(onClick = {
                        scope.launch {
                            vm.leave(pty.id)
                            onLeave()
                        }
                    }) { Text("파티 나가기") }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "파티 상세 – Preview")
@Composable
fun PartyDetailPreview() {
    // HiltViewModel 없이 프리뷰 전용 더미 데이터 표시
    Scaffold(topBar = { TopAppBar(title = { Text("파티 상세 (미리보기)") }) }) { p ->
        Column(
            Modifier.padding(p).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("OTT: 넷플릭스 | 요금제: 프리미엄")
            Text("파티장: user123")
            Text("파티원: a, b, c, d")
            Text("초대코드: ABC1234")
            Text("다음 결제일: 2025-11-25")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {}) { Text("수정하기") }
                OutlinedButton(onClick = {}) { Text("파티 나가기") }
            }
        }
    }
}