package com.example.ottogether.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp

val OttoCardShape: Shape = RoundedCornerShape(16.dp)

/* 카드 색/엘리베이션 */
@Composable
fun OttoCardColors() = CardDefaults.cardColors(
    containerColor = Color.White,
    contentColor   = Color.Black
)

@Composable
fun OttoCardElevationNone() = CardDefaults.cardElevation(
    defaultElevation = 0.dp, pressedElevation = 0.dp,
    focusedElevation = 0.dp, hoveredElevation = 0.dp,
    draggedElevation = 0.dp, disabledElevation = 0.dp
)

/* --- 섀도우 --- */
/* 실제 앱용: 피그마 #001226 @ 3~6% */
private val ShadowProd = Color(0x0A001226)   // ≈4% (원하는 정도로 조절: 0x08=3%, 0x0F≈6%)
/* 프리뷰용: 눈에 확 보이게 강하게 */
private val ShadowPreview = Color(0x33001226) // 20%

/** 내부에서 프리뷰 여부에 따라 적정 섀도우 적용 */
@Composable
fun Modifier.ottoShadowAdaptive(): Modifier {
    val isPreview = LocalInspectionMode.current
    val color = if (isPreview) ShadowPreview else ShadowProd
    val elev  = if (isPreview) 24.dp else 10.dp   // 프리뷰는 더 크게

    return this.graphicsLayer {
        shape = OttoCardShape
        clip = false
        shadowElevation = elev.toPx()
        ambientShadowColor = color
        spotShadowColor = color
    }
}