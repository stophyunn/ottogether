package com.example.ottogether.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ottogether.ui.theme.OttogetherTheme

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    val selectedColor = Color(0xFFFF7A2F)  // 주황색
    val unselectedColor = Color(0xFFB0B0B0) // 회색

    NavigationBar(
        containerColor = Color.White
    ) {
        BottomNavItem.items.forEach { (route, _, icon) ->
            val selected = currentRoute == route

            NavigationBarItem(
                selected = selected,
                onClick = { onItemClick(route) },
                icon = {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = route,
                        tint = if (selected) selectedColor else unselectedColor
                    )
                },
                // ✅ 텍스트 숨기기
                label = null,
                alwaysShowLabel = false,
                // ✅ 기본 선택 배경(보라색 원) 제거
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    indicatorColor = Color.Transparent // ← 요게 핵심
                )
            )
        }
    }
}
/* ───── 프리뷰들 ───── */

@Preview(name = "홈 선택", showBackground = true, widthDp = 360, heightDp = 80)
@Composable
private fun BottomBarPreview_Home() {
    OttogetherTheme {
        BottomNavigationBar(
            currentRoute = BottomNavItem.HOME,
            onItemClick = {}
        )
    }
}

@Preview(name = "캘린더 선택", showBackground = true, widthDp = 360, heightDp = 80)
@Composable
private fun BottomBarPreview_Calendar() {
    OttogetherTheme {
        BottomNavigationBar(
            currentRoute = BottomNavItem.CALENDAR,
            onItemClick = {}
        )
    }
}

@Preview(name = "마이 선택", showBackground = true, widthDp = 360, heightDp = 80)
@Composable
private fun BottomBarPreview_My() {
    OttogetherTheme {
        BottomNavigationBar(
            currentRoute = BottomNavItem.MYPAGE,
            onItemClick = {}
        )
    }
}

/* 하나의 캔버스에서 세 상태를 한 번에 확인하고 싶으면 이 프리뷰 사용 */
@Preview(name = "모든 상태", showBackground = true, widthDp = 360)
@Composable
private fun BottomBarPreview_All() {
    OttogetherTheme {
        Column {
            BottomNavigationBar(currentRoute = BottomNavItem.HOME, onItemClick = {})
            Spacer(Modifier.height(8.dp))
            BottomNavigationBar(currentRoute = BottomNavItem.CALENDAR, onItemClick = {})
            Spacer(Modifier.height(8.dp))
            BottomNavigationBar(currentRoute = BottomNavItem.MYPAGE, onItemClick = {})
        }
    }
}