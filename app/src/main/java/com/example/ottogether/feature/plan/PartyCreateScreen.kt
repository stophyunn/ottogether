package com.example.ottogether.feature.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ottogether.core.data.PartyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PartyCreateViewModel @Inject constructor(
    private val repo: PartyRepository
) : androidx.lifecycle.ViewModel() {

    // 데모용: 고정 유저
    private val currentUserId = "u1"

    suspend fun create(providerId: String, planId: String, nextBilling: LocalDate): String {
        val party = repo.create(
            providerId = providerId,
            planId = planId,
            ownerId = currentUserId,
            nextBillingEpochDay = nextBilling.toEpochDay()
        )
        return party.id
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyCreateScreen(
    onCreated: (String) -> Unit,
    onCancel: () -> Unit,
    vm: PartyCreateViewModel = hiltViewModel(),
) {
    // NavArgs로 넘어온 값 사용: rememberSaveable은 AppNavHost에서 넘겨주므로 여기선 간단 버전
    val providerId = remember { androidx.compose.runtime.mutableStateOf("netflix") }
    val planId = remember { androidx.compose.runtime.mutableStateOf("premium") }

    var date by remember { mutableStateOf(LocalDate.now().plusDays(7)) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("파티 생성") }) }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("OTT: ${providerId.value} / 요금제: ${planId.value}")

            // 아주 단순한 날짜 선택 (Material3 DatePicker 사용 가능하나 데모용 간소화)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { date = date.minusDays(1) }) { Text("◀︎") }
                Text("결제일: $date", style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = { date = date.plusDays(1) }) { Text("▶︎") }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch {
                        val id = vm.create(providerId.value, planId.value, date)
                        onCreated(id)
                    }
                }
            ) { Text("파티 생성") }

            TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                Text("취소")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "파티 생성 – Preview")
@Composable
fun PartyCreatePreview() {
    Scaffold(topBar = { TopAppBar(title = { Text("파티 생성 (미리보기)") }) }) { p ->
        Column(
            Modifier.padding(p).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("선택된 OTT: 넷플릭스")
            Text("선택된 요금제: 프리미엄")
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
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("파티 생성") }
            TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("취소") }
        }
    }
}