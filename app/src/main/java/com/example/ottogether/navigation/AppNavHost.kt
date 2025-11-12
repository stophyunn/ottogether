package com.example.ottogether.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ottogether.AppSessionState
import com.example.ottogether.AppSessionViewModel
import com.example.ottogether.feature.auth.login.LoginScreen
import com.example.ottogether.feature.auth.signup.SignupScreen
import com.example.ottogether.feature.home.HomeScreen
import com.example.ottogether.feature.my.calendar.CalendarScreen
import com.example.ottogether.feature.my.detail.AccountScreen
import com.example.ottogether.feature.my.detail.ShareAccountScreen
import com.example.ottogether.feature.my.profile.MyProfileScreen
import com.example.ottogether.feature.my.subscriptions.MySubscriptionsScreen
import com.example.ottogether.feature.my.subscriptions.SubscriptionDetailScreen
import com.example.ottogether.feature.payment.PaymentInfoScreen
import com.example.ottogether.feature.plan.PlanSelectScreen
import com.example.ottogether.feature.splash.SplashScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate

@Composable
fun AppNavHost(
    navController: NavHostController,
    sessionState: AppSessionState,
    sessionViewModel: AppSessionViewModel,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = Route.Splash.path,
        modifier = modifier
    ) {
        composable(Route.Splash.path) {
            SplashScreen(onTimeout = {
                sessionViewModel.onSplashFinished()
                if (sessionState.isLoggedIn) {
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Splash.path) { inclusive = true }
                    }
                } else {
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Splash.path) { inclusive = true }
                    }
                }
            })
        }

        composable(Route.Login.path) {
            LoginScreen(
                onSignupClick = { navController.navigate(Route.SignUp.path) },
                onFindEmailClick = {},
                onFindPasswordClick = {},
                onLoginClick = {
                    sessionViewModel.loginDefault()
                    navController.navigate(Route.Home.path) {
                        popUpTo(Route.Login.path) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.SignUp.path) {
            SignupScreen(
                onLoginClick = { navController.popBackStack() },
                onSubmit = { navController.popBackStack(Route.Login.path, inclusive = false) }
            )
        }

        composable(Route.Home.path) {
            HomeScreen(
                userName = sessionState.currentUser?.name,
                catalogs = sessionState.catalogs,
                subscriptions = sessionState.subscriptions,
                onSelectProvider = { providerId ->
                    navController.navigate(Route.PlanSelect.path(providerId))
                },
                onOpenSubscriptions = { navController.navigate(Route.MySubscriptions.path) },
                onOpenCalendar = { navController.navigate(Route.MyCalendar.path) },
                bottomBar = { MainBottomBar(navController, currentRoute) }
            )
        }

        composable(Route.MyCalendar.path) {
            CalendarScreen(
                onBack = { navController.popBackStack() },
                events = emptyMap(),
                onDateSelected = {
                    val selected = LocalDate.of(it.year, it.month, it.day)
                    sessionViewModel.selectCalendarDate(selected)
                },
                bottomBar = { MainBottomBar(navController, currentRoute) }
            )
        }

        composable(Route.MyProfile.path) {
            val user = sessionState.currentUser
            MyProfileScreen(
                userName = user?.name,
                email = user?.email,
                phone = user?.phone,
                subscriptions = sessionState.subscriptions,
                providerName = { sub ->
                    sessionState.catalogs.firstOrNull { it.provider == sub.provider }?.displayName
                        ?: sub.provider.name
                },
                onBack = { navController.popBackStack() },
                onMyAccount = { navController.navigate(Route.Account.path) },
                onSubscriptions = { navController.navigate(Route.MySubscriptions.path) },
                onSubscriptionItem = { sub ->
                    navController.navigate(Route.SubscriptionDetail.path(sub.id))
                },
                bottomBar = { MainBottomBar(navController, currentRoute) }
            )
        }

        composable(Route.Account.path) {
            AccountScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    sessionViewModel.logout()
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Home.path) { inclusive = true }
                    }
                },
                onWithdrawConfirmed = {
                    sessionViewModel.logout()
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Home.path) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.MySubscriptions.path) {
            MySubscriptionsScreen(
                subscriptions = sessionState.subscriptions,
                onBack = { navController.popBackStack() },
                onItem = { sub -> navController.navigate(Route.SubscriptionDetail.path(sub.id)) },
                providerName = { sub ->
                    sessionState.catalogs.firstOrNull { it.provider == sub.provider }?.displayName
                        ?: sub.provider.name
                }
            )
        }

        composable(Route.SubscriptionDetail.path) { backStack ->
            val id = backStack.arguments?.getString("subscriptionId")?.decode()
            val subscription = sessionState.subscriptions.firstOrNull { it.id == id }
            val nameMap = sessionState.users.associateBy { it.id }
            if (subscription != null) {
                SubscriptionDetailScreen(
                    subscription = subscription,
                    onBack = { navController.popBackStack() },
                    onEditAccount = { navController.navigate(Route.Account.path) },
                    onLeaveConfirmed = {
                        sessionViewModel.leaveSubscription(subscription.id)
                        navController.popBackStack()
                    },
                    onEditBillingDate = { navController.navigate(Route.MyCalendar.path) },
                    nameResolver = { userId -> nameMap[userId]?.name ?: userId }
                )
            }
        }

        composable(Route.PlanSelect.path) { backStack ->
            val providerKey = backStack.arguments?.getString("providerId")?.decode() ?: return@composable
            PlanSelectScreen(
                providerKey = providerKey,
                onBack = { navController.popBackStack() },
                onHost = { providerId, planId ->
                    navController.navigate(Route.HostShare.path(providerId, planId))
                },
                onMember = { providerId, planId ->
                    navController.navigate(Route.MemberPayment.path(providerId, planId))
                }
            )
        }

        composable(Route.HostShare.path) { backStack ->
            val providerKey = backStack.arguments?.getString("providerId")?.decode() ?: return@composable
            val planId = backStack.arguments?.getString("planId")?.decode() ?: return@composable
            val catalog = sessionState.catalogs.firstOrNull { it.provider.name == providerKey }
            val plan = catalog?.plans?.firstOrNull { it.id == planId }
            if (catalog != null && plan != null) {
                ShareAccountScreen(
                    ottName = catalog.displayName,
                    plan = plan.name,
                    logoRes = catalog.logoRes,
                    onBack = { navController.popBackStack() },
                    onRegisterPartyMatch = { navController.navigate(Route.Account.path) }
                )
            }
        }

        composable(Route.MemberPayment.path) { backStack ->
            val providerKey = backStack.arguments?.getString("providerId")?.decode() ?: return@composable
            val planId = backStack.arguments?.getString("planId")?.decode() ?: return@composable
            val catalog = sessionState.catalogs.firstOrNull { it.provider.name == providerKey }
            val plan = catalog?.plans?.firstOrNull { it.id == planId }
            if (catalog != null && plan != null) {
                PaymentInfoScreen(
                    ottName = catalog.displayName,
                    plan = plan.name,
                    logoRes = catalog.logoRes,
                    onBack = { navController.popBackStack() },
                    onPayDone = {
                        sessionViewModel.refreshSubscriptions()
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Home.path) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MainBottomBar(navController: NavHostController, currentRoute: String?) {
    val resolved = when (currentRoute) {
        Route.MyCalendar.path -> Route.MyCalendar.path
        Route.MyProfile.path, Route.Account.path, Route.MySubscriptions.path -> Route.MyProfile.path
        else -> Route.Home.path
    }
    BottomNavigationBar(
        currentRoute = resolved,
        onItemClick = { route ->
            if (route == resolved) return@BottomNavigationBar
            navController.navigate(route) {
                popUpTo(Route.Home.path) { inclusive = false }
                launchSingleTop = true
            }
        }
    )
}

private fun String.decode(): String = URLDecoder.decode(this, StandardCharsets.UTF_8.name())
