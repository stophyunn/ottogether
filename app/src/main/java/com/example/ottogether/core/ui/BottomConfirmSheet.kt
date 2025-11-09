package com.example.ottogether.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * 화면 하단 오렌지 경고 팝업 (확인/취소).
 * @param asDialog true면 Dialog(런타임), false면 인라인 오버레이(프리뷰용)
 */
@Composable
fun BottomConfirmSheet(
    show: Boolean,
    message: String,
    negativeText: String = "아니오",
    positiveText: String = "네",
    onNegative: () -> Unit,
    onPositive: () -> Unit,
    onDismiss: () -> Unit = onNegative,
    asDialog: Boolean = true
) {
    if (!show) return

    val content: @Composable () -> Unit = {
        // 배경 살짝 어둡게
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                color = Color(0xFFFF7A2F), // 오렌지
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(message, color = Color.White, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 아니오(흰 배경)
                        OutlinedButton(
                            onClick = onNegative,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFFFF7A2F)
                            ),
                            border = ButtonDefaults.outlinedButtonBorder().copy(width = 0.dp)
                        ) { Text(negativeText) }

                        // 네(흰 배경 + 오렌지 테두리)
                        OutlinedButton(
                            onClick = onPositive,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFFFF7A2F)
                            ),
                            border = ButtonDefaults.outlinedButtonBorder().copy(
                                width = 1.dp,
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFF7A2F))
                            )
                        ) { Text(positiveText) }
                    }
                }
            }
        }
    }

    if (asDialog) {
        Dialog(onDismissRequest = onDismiss) { content() }
    } else {
        // 프리뷰/스낵 테스트용 인라인 오버레이
        content()
    }
}

/* ─────────── Previews ─────────── */

@Preview(showBackground = true, widthDp = 360, heightDp = 200, name = "BottomConfirm – Inline(Preview OK)")
@Composable
private fun BottomConfirmSheetPreviewInline() {
    // 인라인 모드로 강제 표시 → 프리뷰에서 확실히 렌더됨
    Box(Modifier.fillMaxSize()) {
        BottomConfirmSheet(
            show = true,
            message = "정말 구독을 그만두시겠어요?",
            onNegative = {},
            onPositive = {},
            asDialog = false // ★ 중요: 프리뷰에서는 인라인
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 200, name = "BottomConfirm – Dialog(미리보기는 일부 IDE에서 빈화면)")
@Composable
private fun BottomConfirmSheetPreviewDialog() {
    // 참고용: 일부 IDE에서는 Dialog가 비어 보일 수 있음
    BottomConfirmSheet(
        show = true,
        message = "계정삭제시 복구 불가능합니다\n그래도 탈퇴하시겠습니까?",
        onNegative = {},
        onPositive = {},
        asDialog = true
    )
}