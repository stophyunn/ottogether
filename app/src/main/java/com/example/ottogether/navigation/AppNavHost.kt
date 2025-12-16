package com.example.ottogether.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ottogether.AppSessionState
import com.example.ottogether.AppSessionViewModel
import com.example.ottogether.core.model.Subscription
import com.example.ottogether.feature.auth.find.FindEmailScreen
import com.example.ottogether.feature.auth.find.FindPasswordScreen
import com.example.ottogether.feature.auth.login.LoginScreen
import com.example.ottogether.feature.auth.signup.SignupScreen
import com.example.ottogether.feature.home.HomeScreen
import com.example.ottogether.feature.home.HomeViewModel
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
            SplashScreen(
                onTimeout = {
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
                }
            )
        }

        composable(Route.Login.path) {
            LoginScreen(
                onSignupClick = { navController.navigate(Route.SignUp.path) },
                onFindEmailClick = { navController.navigate(Route.FindEmail.path) },
                onFindPasswordClick = { navController.navigate(Route.FindPassword.path) },
                onLogin = sessionViewModel::loginWithCredentials,
                onLoginSuccess = {
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
                onSubmit = { name, email, password, phone ->
                    val result = sessionViewModel.registerUser(name, email, password, phone)
                    if (result.success) {
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Login.path) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                    result
                }
            )
        }

        composable(Route.FindEmail.path) {
            FindEmailScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.FindPassword.path) {
            FindPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.Home.path) {
            // 🔸 HomeViewModel 가져오기 (Hilt)
            val homeViewModel: HomeViewModel = hiltViewModel()
            val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

            // 🔸 최초 진입 시 TMDB 불러오기
            LaunchedEffect(Unit) {
                homeViewModel.loadHome()   // 내가 이전에 적어준 loadHome() (getTrendingMovies 호출)
            }

            HomeScreen(
                userName = sessionState.currentUser?.name,
                catalogs = sessionState.catalogs,
                subscriptions = sessionState.subscriptions,
                trendingMovies = homeUiState.trendingMovies,   // ⬅️ 여기!
                onSelectProvider = { providerId ->
                    navController.navigate(Route.PlanSelect.path(providerId))
                },
                onOpenSubscriptions = {
                    navController.navigate(Route.MySubscriptions.path)
                },
                bottomBar = {
                    MainBottomBar(navController, currentRoute)
                }
            )
        }

        composable(Route.MyCalendar.path) {
            CalendarScreen(
                onBack = { navController.popBackStack() },
                subscriptions = sessionState.subscriptions,
                providerName = { sub ->
                    sessionState.catalogs.firstOrNull { it.provider == sub.provider }?.displayName
                        ?: sub.provider.name
                },
                selectedDate = sessionState.selectedCalendarDate,
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
                profileImageRes = user?.profileImageRes,
                profileImageUri = user?.profileImageUri,
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
                onOpenCalendar = { navController.navigate(Route.MyCalendar.path) },
                bottomBar = { MainBottomBar(navController, currentRoute) }
            )
        }

        composable(Route.Account.path) {
            AccountScreen(
                userName = sessionState.currentUser?.name,
                email = sessionState.currentUser?.email,
                phone = sessionState.currentUser?.phone,
                accountNumber = sessionState.currentUser?.accountNumber,
                password = sessionState.currentUser?.password,
                profileImageRes = sessionState.currentUser?.profileImageRes,
                profileImageUri = sessionState.currentUser?.profileImageUri,
                onBack = { navController.popBackStack() },
                onChangeProfileImage = sessionViewModel::cycleProfileImage,
                onProfileImageSelected = { uri -> sessionViewModel.updateProfileImage(uri) },
                onUpdateName = sessionViewModel::updateCurrentUserName,
                onUpdateEmail = sessionViewModel::updateCurrentUserEmail,
                onUpdatePhone = sessionViewModel::updateCurrentUserPhone,
                onUpdateAccount = sessionViewModel::updateCurrentUserAccount,
                onUpdatePassword = sessionViewModel::updateCurrentUserPassword,
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
                currentUserId = sessionState.currentUser?.id,
                onBack = { navController.popBackStack() },
                onItem = { sub -> navController.navigate(Route.SubscriptionDetail.path(sub.id)) },
                providerName = { sub ->
                    sessionState.catalogs.firstOrNull { it.provider == sub.provider }?.displayName
                        ?: sub.provider.name
                },
                bottomBar = { MainBottomBar(navController, currentRoute) }
            )
        }

        composable(Route.SubscriptionDetail.path) { backStack ->
            val id = backStack.arguments?.getString("subscriptionId")?.decode()
            val subscription = sessionState.subscriptions.firstOrNull { it.id == id }
            val nameMap = sessionState.users.associateBy { it.id }
            if (subscription != null) {
                SubscriptionDetailScreen(
                    subscription = subscription,
                    currentUserId = sessionState.currentUser?.id,
                    onBack = { navController.popBackStack() },
                    onEditAccount = { navController.navigate(Route.Account.path) },
                    onLeaveImmediately = {
                        sessionViewModel.leaveSubscription(subscription.id)
                        navController.popBackStack()
                    },
                    onLeaveScheduled = { leaveDate ->
                        sessionViewModel.scheduleLeaveSubscription(subscription.id, leaveDate)
                    },
                    onBillingDateChanged = { date ->
                        sessionViewModel.updateNextBillingDate(subscription.id, date)
                    },
                    onTransferHost = { memberId ->
                        sessionViewModel.transferOwnership(subscription.id, memberId)
                    },
                    onOwnerLeave = { newOwnerId ->
                        val result = sessionViewModel.leaveAsOwner(subscription.id, newOwnerId)
                        if (result.success) {
                            navController.popBackStack()
                        }
                        result
                    },
                    userResolver = { userId -> nameMap[userId] }
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
                    plan = plan,
                    logoRes = catalog.logoRes,
                    onBack = { navController.popBackStack() },
                    onRegisterPartyMatch = { form ->
                        sessionViewModel.hostNewSubscription(
                            provider = catalog.provider,
                            plan = plan,
                            loginId = form.loginId,
                            password = form.password,
                            account = form.account,
                            firstBillingDate = form.firstBillingDate
                        )
                        true
                    },
                    onOpenMySubscriptions = {
                        navController.navigate(Route.MySubscriptions.path) {
                            popUpTo(Route.HostShare.path) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(Route.MemberPayment.path) { backStack ->
            val providerKey = backStack.arguments?.getString("providerId")?.decode() ?: return@composable
            val planId = backStack.arguments?.getString("planId")?.decode() ?: return@composable
            val catalog = sessionState.catalogs.firstOrNull { it.provider.name == providerKey }
            val plan = catalog?.plans?.firstOrNull { it.id == planId }
            if (catalog != null && plan != null) {
                val recommended = remember { mutableStateOf<List<Subscription>>(emptyList()) }
                LaunchedEffect(providerKey, planId, sessionState.currentUser?.id) {
                    recommended.value = sessionViewModel.getRecommendedParties(catalog.provider, plan.id)
                }
                PaymentInfoScreen(
                    ottName = catalog.displayName,
                    plan = plan,
                    logoRes = catalog.logoRes,
                    recommendedParty = recommended.value.firstOrNull(),
                    users = sessionState.users,
                    onBack = { navController.popBackStack() },
                    onJoinParty = { code -> sessionViewModel.joinPartyByCode(code) },
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
            if (route == currentRoute) return@BottomNavigationBar
            navController.navigate(route) {
                popUpTo(Route.Home.path) { inclusive = false }
                launchSingleTop = true
            }
        }
    )
}

private fun String.decode(): String = URLDecoder.decode(this, StandardCharsets.UTF_8.name())
