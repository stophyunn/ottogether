
import com.example.ottogether.R
import com.example.ottogether.core.model.BillingInfo
import com.example.ottogether.core.model.Money
import com.example.ottogether.core.model.Plan
import com.example.ottogether.core.model.Provider
import com.example.ottogether.core.model.Subscription
import com.example.ottogether.core.model.User
import java.time.LocalDate

class SeedData {
    val users = listOf(
        User(
            id = "u1",
            name = "졸린 무지",
            email = "song2025@sookmyung.ac.kr",
            phone = "010-2025-2025",
            profileImageRes = R.drawable.profile
        ),
        User(id = "u2", name = "개졸린 무지")
    )

    val plans = listOf(
        Plan(
            id = "p1",
            name = "프리미엄",
            quality = "가장 좋음",
            resolution = "4K + HDR",
            maxScreens = 6,               // ✅ 이름 맞추기
            monthlyPrice = Money(2500)    // ✅ 이름 맞추기
        )
    )

    val subscriptions = listOf(
        Subscription(
            id = "s1",
            provider = Provider.NETFLIX,
            plan = plans[0],
            ownerUserId = "u1",
            members = listOf("u2"),
            billing = BillingInfo(
                accountMasked = "국민은행 00000-0000-0000",
                loginId = "song2025@sookmyung.ac.kr",
                passwordMasked = "********",
                cycleDay = 10,
                nextBillingDate = LocalDate.of(2025, 11, 23) // ✅ LocalDate로
            )
        )
    )
}