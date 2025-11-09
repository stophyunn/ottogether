package com.example.ottogether.feature.auth.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onSubmit: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("OTTOGETHER", fontWeight = FontWeight.Bold) })
        },
        bottomBar = {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) { Text("Sign up") }
        }
    ) { p ->
        Column(
            modifier = Modifier.padding(p).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("회원가입", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("이메일주소를 입력해주세요", style = MaterialTheme.typography.bodyMedium)

            OutlinedTextField(value = "", onValueChange = {}, label = { Text("ottogether @ gmail.com") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("성") }, modifier = Modifier.weight(1f), singleLine = true)
                OutlinedTextField(value = "", onValueChange = {}, label = { Text("이름") }, modifier = Modifier.weight(1f), singleLine = true)
            }
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("비밀번호") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun SignupPreview() { SignupScreen() }