package com.example.ottogether.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ottogether.R
import com.example.ottogether.feature.my.subscriptions.MySubscriptionsScreen
import com.example.ottogether.feature.payment.PaymentInfoScreen
import com.example.ottogether.feature.plan.PlanSelectScreen

// ---------- Routes ----------
sealed interface AppRoute {
    val path: String
    data object Home : AppRoute { override val path = "home" }
    data object Calendar : AppRoute { override val path = "calendar" }
    data object My : AppRoute { override val path = "my" }

    data object PlanSelect : AppRoute { override val path = "plan_select/{ottId}" }
    data object PaymentInfo : AppRoute {
        // 현재 PaymentInfoScreen(ottName/plan/logoRes)만 필요
        override val path = "payment_info/{ottName}/{plan}/{logoRes}"
    }
    data object MySubscriptions : AppRoute { override val path = "my_subscriptions" }
    data object SubscriptionDetail : AppRoute { override val path = "subscription_detail/{subId}" }
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomItems = listOf(
    BottomItem(AppRoute.Home.path, "홈", Icons.Filled.Home),
    BottomItem(AppRoute.Calendar.path, "달력", Icons.Filled.CalendarMonth),
    BottomItem(AppRoute.My.path, "마이", Icons.Filled.Person)
)

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppRoute.Home.path
) {
    val bottomBarRoutes = setOf(
        AppRoute.Home.path, AppRoute.Calendar.path, AppRoute.My.path
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route?.substringBefore("?")

    androidx.compose.material3.Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                NavigationBar {
                    val current = currentRoute
                    bottomItems.forEach { item ->
                        val selected = current == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            // ---- Bottom tabs ----
            composable(AppRoute.Home.path) {
                // TODO: 실제 홈 화면으로 교체 (리스트에서 OTT 선택 시 아래로 이동)
                PlaceholderScreen("홈 화면") {
                    navController.navigate("plan_select/netflix")
                }
            }

            composable(AppRoute.Calendar.path) {
                // TODO: 결제 캘린더 화면으로 교체
                PlaceholderScreen("결제 캘린더 화면")
            }

            composable(AppRoute.My.path) {
                // TODO: 마이 홈 화면으로 교체 (내 OTT구독 진입 버튼 제공)
                PlaceholderScreen("마이 화면") {
                    navController.navigate(AppRoute.MySubscriptions.path)
                }
            }

            // ---- Flows ----
            composable(
                route = AppRoute.PlanSelect.path,
                arguments = listOf(navArgument("ottId") { type = NavType.StringType })
            ) { back ->
                val ottId = back.arguments?.getString("ottId").orEmpty()
                PlanSelectScreen(
                    ottId = ottId,
                    onBack = { navController.popBackStack() }
                )
                // PlanSelectScreen 내부에서 plan 클릭 시:
                // navController.navigate("payment_info/넷플릭스/프리미엄/${R.drawable.ic_logo_netflix}")
            }

            composable(
                route = AppRoute.PaymentInfo.path,
                arguments = listOf(
                    navArgument("ottName") { type = NavType.StringType },
                    navArgument("plan") { type = NavType.StringType },
                    navArgument("logoRes") { type = NavType.IntType }
                )
            ) { back ->
                val ottName = back.arguments?.getString("ottName").orEmpty()
                val plan = back.arguments?.getString("plan").orEmpty()
                val logoRes = back.arguments?.getInt("logoRes") ?: R.drawable.ic_logo_netflix

                // 현재 PaymentInfoScreen 시그니처에 맞춰 titleText 없이 호출
                PaymentInfoScreen(
                    ottName = ottName,
                    plan = plan,
                    logoRes = logoRes,
                    onBack = { navController.popBackStack() },
                    onRegisterShareMine = {
                        navController.navigate(AppRoute.MySubscriptions.path) {
                            popUpTo(AppRoute.Home.path) { inclusive = false }
                        }
                    },
                    onRegisterAsMember = {
                        navController.navigate(AppRoute.MySubscriptions.path) {
                            popUpTo(AppRoute.Home.path) { inclusive = false }
                        }
                    }
                )
            }

            composable(AppRoute.MySubscriptions.path) {
                MySubscriptionsScreen(
                    onBack = { navController.popBackStack() },
                    onItem = { /* item -> navController.navigate("subscription_detail/${itemId}") */ }
                )
            }

            composable(
                route = AppRoute.SubscriptionDetail.path,
                arguments = listOf(navArgument("subId") { type = NavType.StringType })
            ) { back ->
                val subId = back.arguments?.getString("subId").orEmpty()
                PlaceholderScreen("구독 상세: $subId") {
                    navController.popBackStack()
                }
            }
        }
    }
}

/* ---------- 임시 플레이스홀더 (필요 import 포함) ---------- */
@Composable
private fun PlaceholderScreen(
    title: String,
    onNext: (() -> Unit)? = null
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            if (onNext != null) {
                Button(onClick = onNext) { Text("다음으로 이동") }
            }
        }
    }
}