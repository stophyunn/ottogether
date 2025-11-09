package com.example.ottogether.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 프리뷰에서 항상 앱 테마 + 배경색을 적용해주는 래퍼.
 * 모든 @Preview 안에서 PreviewContainer { ... }로 감싸서 사용.
 */
@Composable
fun PreviewContainer(content: @Composable () -> Unit) {
    OttogetherTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

/** 다크모드 미리보기도 필요하면 이거 사용 */
@Composable
fun PreviewContainerDark(content: @Composable () -> Unit) {
    OttogetherTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}