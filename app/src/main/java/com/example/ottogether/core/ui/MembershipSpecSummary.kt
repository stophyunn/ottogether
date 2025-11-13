package com.example.ottogether.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ottogether.core.model.Plan

private val LabelColor = Color(0xFF6F7682)
private val ValueColor = Color(0xFF111111)
private val Highlight = Color(0xFFFF7A2F)

@Composable
fun MembershipSpecSummary(
    plan: Plan?,
    modifier: Modifier = Modifier,
    fallbackText: String = "-"
) {
    val labelStyle = MaterialTheme.typography.bodyMedium.copy(color = LabelColor, fontSize = 13.sp)
    val valueStyle = MaterialTheme.typography.bodyLarge.copy(color = ValueColor, fontSize = 16.sp)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("월 요금", style = labelStyle)
            Row {
                if (plan != null) {
                    Text(
                        text = plan.monthlyPrice.toString(),
                        style = labelStyle.copy(textDecoration = TextDecoration.LineThrough)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = plan.sharedMonthlyPrice.toString(),
                        style = valueStyle.copy(color = Highlight, fontWeight = FontWeight.Bold)
                    )
                } else {
                    Text(
                        text = fallbackText,
                        style = valueStyle.copy(color = Highlight, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        MembershipSpecRow(label = "영상 화질", value = plan?.quality ?: fallbackText, emphasize = true)
        MembershipSpecRow(label = "해상도", value = plan?.resolution ?: fallbackText, emphasize = true)
        MembershipSpecRow(label = "동시접속 가능 대수", value = plan?.maxScreens?.toString() ?: fallbackText)
    }
}

@Composable
fun MembershipSpecRow(
    label: String,
    value: String,
    emphasize: Boolean = false
) {
    val labelStyle = MaterialTheme.typography.bodyMedium.copy(color = LabelColor, fontSize = 13.sp)
    val valueStyle = MaterialTheme.typography.bodyLarge.copy(color = ValueColor, fontSize = 15.sp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = labelStyle)
        Text(
            value,
            style = valueStyle.copy(
                color = if (emphasize) Highlight else valueStyle.color,
                fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
                fontSize = if (emphasize) 16.sp else valueStyle.fontSize
            )
        )
    }
}
