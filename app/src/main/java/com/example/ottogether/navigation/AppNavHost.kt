package com.example.ottogether.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ottogether.feature.home.HomeScreen
import com.example.ottogether.feature.my.calendar.CalendarScreen
import com.example.ottogether.feature.my.profile.MyProfileScreen
import com.example.ottogether.feature.my.subscriptions.MySubscriptionsScreen
import com.example.ottogether.feature.plan.PlanSelectScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.path,   // ← 시작 목적지
        modifier = modifier
    ) {
        // 홈
        composable(Route.Home.path) {
            HomeScreen(
                onTapPlan = { ottId -> navController.navigate(Route.PlanSelect.path(ottId)) },
                onTapCalendar = { navController.navigate(Route.MyCalendar.path) },
                onTapProfile = { navController.navigate(Route.MyProfile.path) },
                onTapSubs = { navController.navigate(Route.MySubscriptions.path) }
            )
        }

        // 요금제 선택
        composable(Route.PlanSelect.path) { backStack ->
            val ottId = backStack.arguments?.getString("ottId").orEmpty()
            PlanSelectScreen(
                ottId = ottId,
                onBack = { navController.popBackStack() }
            )
        }

        // 마이: 캘린더
        composable(Route.MyCalendar.path) {
            CalendarScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 마이: 프로필
        composable(Route.MyProfile.path) {
            MyProfileScreen(
                onBack = { navController.popBackStack() },
                bottomBar = { /* 필요하면 하단바 주입 */ }
            )
        }

        // 마이: 구독 목록
        composable(Route.MySubscriptions.path) {
            MySubscriptionsScreen(
                onBack = { navController.popBackStack() },
                onItem = { subItem ->
                    // 필요 시 상세로 이동하도록 이어 붙이세요
                    // navController.navigate(Route.SubscriptionDetail.path(subItem.name))
                }
            )
        }

        // 디버그용(혹시 위 컴포저블이 비어 있을 때 대비)
        composable("debug") {
            Box(
                Modifier.fillMaxSize().background(Color(0xFFFFF3CD)),
                contentAlignment = Alignment.Center
            ) {
                Text("It works 🎉", color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}