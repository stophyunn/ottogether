package com.example.ottogether.feature.auth.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.R
import com.example.ottogether.core.model.AuthResult

private val BrandOrange = Color(0xFFFF7A2F)
private val GrayText    = Color(0xFF6F7682)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onLoginClick: () -> Unit = {},
    onSubmit: (name: String, email: String, password: String, phone: String?) -> AuthResult =
        { _, _, _, _ -> AuthResult(false) }
) {
    var email by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var firstName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var helper by rememberSaveable { mutableStateOf<String?>(null) }
    var helperColor by remember { mutableStateOf(Color(0xFFD32F2F)) }
    val canSubmit = email.isNotBlank() && password.isNotBlank() &&
        (lastName.isNotBlank() || firstName.isNotBlank())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandOrange)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ✅ 상단 로고 (signLogo.xml 사용)
            Image(
                painter = painterResource(id = R.drawable.signlogo),
                contentDescription = "회원가입 로고",
                modifier = Modifier
                    .width(180.dp)
                    .height(80.dp)
            )

            Spacer(Modifier.height(8.dp))

            // 내부 흰색 패널
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.White,
                shape = RoundedCornerShape(28.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // 타이틀 및 로그인 링크
                        Column {
                            Text(
                                "회원가입",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandOrange,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "이미 계정을 갖고 계신가요? ",
                                    color = GrayText,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "로그인하기",
                                    color = BrandOrange,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable(onClick = onLoginClick)
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                            Text(
                                "이메일주소를 입력해주세요",
                                color = Color.Black,
                                fontSize = 13.sp
                            )
                        }

                        // 입력 필드
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("ottogether @ gmail.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                placeholder = { Text("성") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                placeholder = { Text("이름") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            )
                        }

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            placeholder = { Text("휴대폰 번호 (선택)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        )

                        helper?.takeIf { it.isNotBlank() }?.let {
                            Text(text = it, color = helperColor, fontSize = 12.sp)
                        }
                    }

                    // 하단 Sign up 버튼
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 8.dp,
                        color = Color.Transparent
                    ) {
                        Button(
                            onClick = {
                                val fullName = listOf(lastName.trim(), firstName.trim())
                                    .filter { it.isNotBlank() }
                                    .joinToString(" ")
                                val result = onSubmit(fullName, email.trim(), password, phone)
                                if (result.success) {
                                    helperColor = BrandOrange
                                    helper = "회원가입이 완료되었어요!"
                                } else {
                                    helperColor = Color(0xFFD32F2F)
                                    helper = result.message ?: "회원가입에 실패했어요"
                                }
                            },
                            enabled = canSubmit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandOrange,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Sign up", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852, name = "회원가입 – Preview")
@Composable
private fun SignupPreview() {
    SignupScreen()
}