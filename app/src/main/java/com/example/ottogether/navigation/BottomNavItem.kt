package com.example.ottogether.navigation

import androidx.annotation.DrawableRes
import com.example.ottogether.R

object BottomNavItem {
    const val HOME = "home"
    const val CALENDAR = "calendar"
    const val MYPAGE = "my"

    data class Item(
        val route: String,
        val title: String,
        @DrawableRes val icon: Int
    )

    val items = listOf(
        Item(route = HOME, title = "홈", icon = R.drawable.iconmenulist),
        Item(route = CALENDAR, title = "캘린더", icon = R.drawable.calendar),
        Item(route = MYPAGE, title = "마이", icon = R.drawable.iconmenuuser)
    )
}