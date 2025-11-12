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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ottogether.feature.home.HomeScreen
import com.example.ottogether.feature.my.calendar.CalendarScreen
import com.example.ottogether.feature.my.profile.MyProfileScreen
import com.example.ottogether.feature.my.subscriptions.MySubscriptionsScreen
import com.example.ottogether.feature.plan.PartyCreateScreen
import com.example.ottogether.feature.plan.PartyDetailScreen
import com.example.ottogether.feature.plan.PartyEditScreen
import com.example.ottogether.feature.plan.PartyJoinScreen
import com.example.ottogether.feature.plan.PlanSelectScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.path,
        modifier = modifier
    ) {
        /* 홈 */
        composable(Route.Home.path) {
            HomeScreen(
                onTapPlan = { ottId ->
                    navController.navigate(Route.PlanSelect.path(ottId))
                },
                onTapCalendar = { navController.navigate(Route.MyCalendar.path) },
                onTapProfile = { navController.navigate(Route.MyProfile.path) },
                onTapSubs = { navController.navigate(Route.MySubscriptions.path) }
            )
        }

        /* 요금제 선택 (plan/{ottId}) */
        composable(
            route = Route.PlanSelect.path,
            arguments = listOf(navArgument("ottId") { type = NavType.StringType })
        ) { backStack ->
            val ottId = backStack.arguments?.getString("ottId").orEmpty()
            PlanSelectScreen(
                ottId = ottId,
                onBack = { navController.popBackStack() }
            )
        }

        /* 마이: 캘린더 */
        composable(Route.MyCalendar.path) {
            CalendarScreen(onBack = { navController.popBackStack() })
        }

        /* 마이: 프로필 */
        composable(Route.MyProfile.path) {
            MyProfileScreen(
                onBack = { navController.popBackStack() },
                bottomBar = { /* 필요 시 바텀바 전달 */ }
            )
        }

        /* 마이: 구독 목록 */
        composable(Route.MySubscriptions.path) {
            MySubscriptionsScreen(
                onBack = { navController.popBackStack() },
                onItem = { /* subItem -> 상세로 이동 시 여기에 작성 */ }
            )
        }

        /* 디버그용 */
        composable("debug") {
            Box(
                Modifier.fillMaxSize().background(Color(0xFFFFF3CD)),
                contentAlignment = Alignment.Center
            ) { Text("It works 🎉", color = MaterialTheme.colorScheme.onBackground) }
        }

        /* 파티 생성 (party/create/{providerId}/{planId}) */
        composable(
            route = Route.PartyCreate.path,
            arguments = listOf(
                navArgument("providerId") { type = NavType.StringType },
                navArgument("planId") { type = NavType.StringType }
            )
        ) {
            PartyCreateScreen(
                onCreated = { partyId ->
                    navController.navigate(Route.PartyDetail.path(partyId)) {
                        launchSingleTop = true
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        /* 파티 상세 (party/detail/{partyId}) */
        composable(
            route = Route.PartyDetail.path,
            arguments = listOf(navArgument("partyId") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("partyId") ?: return@composable
            PartyDetailScreen(
                partyId = id,
                onEdit = { editId -> navController.navigate(Route.PartyEdit.path(editId)) },
                onLeave = { navController.popBackStack() }
            )
        }

        /* 파티 수정 (party/edit/{partyId}) */
        composable(
            route = Route.PartyEdit.path,
            arguments = listOf(navArgument("partyId") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("partyId") ?: return@composable
            PartyEditScreen(
                partyId = id,
                onDone = { doneId ->
                    navController.navigate(Route.PartyDetail.path(doneId)) {
                        launchSingleTop = true
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        /* 파티 참여 (party/join) */
        composable(Route.PartyJoin.path) {
            PartyJoinScreen(
                onJoined = { partyId ->
                    navController.navigate(Route.PartyDetail.path(partyId)) {
                        launchSingleTop = true
                    }
                },
                onAskBecomeLeader = {
                    // 파티장이 되시겠습니까? → 필요 시 요금제 선택으로 유도
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}