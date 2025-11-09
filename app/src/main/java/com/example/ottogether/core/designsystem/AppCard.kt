package com.example.ottogether.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 홈 카드와 동일한 외형 토큰 */
object AppCardTokens {
    val shape: Shape = RoundedCornerShape(16.dp)
    val elevation: Dp = 4.dp
    val containerColor: Color = Color.White
    val contentColor: Color = Color(0xFF000000)
    val padding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    val borderHighlight: Color = Color(0xFFFF7A2F)
}

/** 홈 카드 스타일 그대로 재사용 가능한 Card */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    padded: Boolean = true,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = AppCardTokens.shape,
        colors = CardDefaults.cardColors(
            containerColor = AppCardTokens.containerColor,
            contentColor = AppCardTokens.contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppCardTokens.elevation),
        border = if (highlighted) BorderStroke(2.dp, AppCardTokens.borderHighlight) else null
    ) {
        if (padded) {
            Box(Modifier.padding(AppCardTokens.padding)) { content() }
        } else {
            content()
        }
    }
}