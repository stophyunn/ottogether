package com.example.ottogether.feature.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
class PartyEditViewModel @Inject constructor(
    private val repo: PartyRepository
) : androidx.lifecycle.ViewModel() {

    suspend fun updateDate(partyId: String, date: LocalDate) {
        repo.updateNextBillingDate(partyId, date.toEpochDay())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyEditScreen(
    partyId: String,                        // ✅ 명시적으로 NavHost에서 전달받음
    onDone: (String) -> Unit,
    onCancel: () -> Unit,
    vm: PartyEditViewModel = hiltViewModel()
) {
    var date by remember { mutableStateOf(LocalDate.now().plusDays(7)) }
    val scope = rememberCoroutineScope()

    Scaffold(topBar = { TopAppBar(title = { Text("파티 수정") }) }) { p ->
        Column(
            Modifier
                .padding(p)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { date = date.minusDays(1) }) { Text("◀︎") }
                Text("다음 결제일: $date", style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = { date = date.plusDays(1) }) { Text("▶︎") }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch {
                        vm.updateDate(partyId, date)
                        onDone(partyId)
                    }
                }
            ) { Text("저장") }

            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("취소")
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "파티 수정 – Preview")
@Composable
fun PartyEditPreview() {
    Scaffold(topBar = { TopAppBar(title = { Text("파티 수정 (미리보기)") }) }) { p ->
        Column(
            Modifier
                .padding(p)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var date by remember { mutableStateOf(LocalDate.now().plusDays(7)) }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { date = date.minusDays(1) }) { Text("◀︎") }
                Text("다음 결제일: $date", style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = { date = date.plusDays(1) }) { Text("▶︎") }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            ) { Text("저장") }

            TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("취소")
            }
        }
    }
}