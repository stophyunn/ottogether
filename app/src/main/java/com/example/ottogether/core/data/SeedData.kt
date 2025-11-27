package com.example.ottogether.core.data

import com.example.ottogether.R
import com.example.ottogether.core.model.BillingInfo
import com.example.ottogether.core.model.Money
import com.example.ottogether.core.model.Plan
import com.example.ottogether.core.model.Provider
import com.example.ottogether.core.model.Subscription
import com.example.ottogether.core.model.User
import java.time.LocalDate
import javax.inject.Inject

/**
 * 더미 데이터 모음. 앱의 모든 화면이 동일한 데이터를 바라보도록 한 곳에서 정의한다.
 */
class SeedData @Inject constructor() {

    data class ProviderCatalog(
        val provider: Provider,
        val displayName: String,
        val logoRes: Int,
        val plans: List<Plan>
    )

    val users = listOf(
        User(
            id = "u1",
            name = "졸린 무지",
            email = "song2025@sookmyung.ac.kr",
            phone = "010-2025-2025",
            accountNumber = "국민은행 2020-2020-2020202",
            profileImageRes = R.drawable.profile,
            password = "password1"
        ),
        User(
            id = "u2",
            name = "개졸린 무지",
            email = "sleepy2@ottogether.app",
            phone = "010-3333-3333",
            accountNumber = "우리은행 3333-3333-333333",
            profileImageUri = "https://picsum.photos/200?1",
            password = "password2"
        ),
        User(
            id = "u3",
            name = "베짱이 무지",
            email = "lazy@ottogether.app",
            phone = "010-4444-4444",
            accountNumber = "카카오뱅크 4444-4444-444444",
            profileImageUri = "https://picsum.photos/200?2",
            password = "password3"
        ),
        User(
            id = "u4",
            name = "성실한 무지",
            email = "steady@ottogether.app",
            phone = "010-5555-5555",
            accountNumber = "신한은행 5555-5555-555555",
            password = "password4"
        ),
        User(
            id = "u5",
            name = "열정 무지",
            email = "passion@ottogether.app",
            phone = "010-6666-6666",
            accountNumber = "IBK 6666-6666-666666",
            password = "password5"
        ),
        User(
            id = "u6",
            name = "야근 무지",
            email = "overtime@ottogether.app",
            phone = "010-7777-7777",
            accountNumber = "하나은행 7777-7777-777777",
            password = "password6"
        )
    )

    val catalogs = listOf(
        ProviderCatalog(
            provider = Provider.NETFLIX,
            displayName = "넷플릭스",
            logoRes = R.drawable.ic_logo_netflix,
            plans = listOf(
                Plan(
                    id = "netflix-premium",
                    name = "프리미엄",
                    quality = "가장 좋음",
                    resolution = "4K + HDR",
                    maxScreens = 6,
                    monthlyPrice = Money(17000),
                    sharedMonthlyPrice = Money(4250)
                ),
                Plan(
                    id = "netflix-standard",
                    name = "스탠다드",
                    quality = "좋음",
                    resolution = "1080p",
                    maxScreens = 4,
                    monthlyPrice = Money(13500),
                    sharedMonthlyPrice = Money(3375)
                ),
                Plan(
                    id = "netflix-basic",
                    name = "베이식",
                    quality = "보통",
                    resolution = "720p",
                    maxScreens = 2,
                    monthlyPrice = Money(9500),
                    sharedMonthlyPrice = Money(2375)
                )
            )
        ),
        ProviderCatalog(
            provider = Provider.COUPANG,
            displayName = "쿠팡플레이",
            logoRes = R.drawable.ic_logo_coupang,
            plans = listOf(
                Plan(
                    id = "coupang-wow",
                    name = "와우 멤버십",
                    quality = "Full HD",
                    resolution = "1080p",
                    maxScreens = 5,
                    monthlyPrice = Money(4990),
                    sharedMonthlyPrice = Money(1250)
                )
            )
        ),
        ProviderCatalog(
            provider = Provider.DISNEY,
            displayName = "디즈니플러스",
            logoRes = R.drawable.ic_logo_disney,
            plans = listOf(
                Plan(
                    id = "disney-standard",
                    name = "스탠다드",
                    quality = "최대 4K",
                    resolution = "4K",
                    maxScreens = 4,
                    monthlyPrice = Money(9900),
                    sharedMonthlyPrice = Money(2475)
                )
            )
        ),
        ProviderCatalog(
            provider = Provider.TVING,
            displayName = "티빙",
            logoRes = R.drawable.ic_logo_tving,
            plans = listOf(
                Plan(
                    id = "tving-premium",
                    name = "프리미엄",
                    quality = "Ultra HD",
                    resolution = "4K",
                    maxScreens = 4,
                    monthlyPrice = Money(13900),
                    sharedMonthlyPrice = Money(3475)
                )
            )
        ),
        ProviderCatalog(
            provider = Provider.WAVVE,
            displayName = "웨이브",
            logoRes = R.drawable.ic_logo_wavve,
            plans = listOf(
                Plan(
                    id = "wavve-standard",
                    name = "스탠다드",
                    quality = "Full HD",
                    resolution = "1080p",
                    maxScreens = 4,
                    monthlyPrice = Money(10900),
                    sharedMonthlyPrice = Money(2725)
                )
            )
        ),
        ProviderCatalog(
            provider = Provider.WATCHA,
            displayName = "왓챠",
            logoRes = R.drawable.ic_logo_watcha,
            plans = listOf(
                Plan(
                    id = "watcha-premium",
                    name = "프리미엄",
                    quality = "Full HD",
                    resolution = "1080p",
                    maxScreens = 4,
                    monthlyPrice = Money(12900),
                    sharedMonthlyPrice = Money(3225)
                )
            )
        )
    )

    val subscriptions = listOf(
        Subscription(
            id = "s1",
            provider = Provider.NETFLIX,
            plan = plan(Provider.NETFLIX, "netflix-premium"),
            ownerUserId = users[0].id,
            members = listOf(
                users[1].id,
                users[2].id,
                users[3].id,
                users[4].id,
                users[5].id
            ),
            billing = BillingInfo(
                accountMasked = "국민은행 00000-0000-0000",
                loginId = users[0].email,
                passwordMasked = "********",
                cycleDay = 10,
                nextBillingDate = LocalDate.of(2025, 11, 23)
            )
        ),
        Subscription(
            id = "s2",
            provider = Provider.DISNEY,
            plan = plan(Provider.DISNEY, "disney-standard"),
            ownerUserId = users[0].id,
            members = listOf(users[1].id),
            billing = BillingInfo(
                accountMasked = "신한 22222-2222-2222",
                loginId = users[0].email,
                passwordMasked = "********",
                cycleDay = 23,
                nextBillingDate = LocalDate.of(2025, 11, 23)
            )
        ),
        Subscription(
            id = "s3",
            provider = Provider.TVING,
            plan = plan(Provider.TVING, "tving-premium"),
            ownerUserId = users[1].id,
            members = listOf(users[0].id, users[3].id),
            billing = BillingInfo(
                accountMasked = "카카오뱅크 33333-3333-3333",
                loginId = users[1].email,
                passwordMasked = "********",
                cycleDay = 3,
                nextBillingDate = LocalDate.of(2025, 12, 3)
            ),
            pendingExits = mapOf(users[0].id to LocalDate.now().plusDays(5))
        )
    )

    fun catalog(provider: Provider): ProviderCatalog =
        catalogs.first { it.provider == provider }

    fun plan(provider: Provider, planId: String): Plan =
        catalog(provider).plans.first { it.id == planId }
}
