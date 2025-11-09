package com.example.ottogether.feature.my.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

/* -------------------------
 *  간단 날짜 구조체 (java.time 대체)
 * ------------------------- */
data class SimpleDate(
    val year: Int,
    /** 1~12 */
    val month: Int,
    /** 1~31 */
    val day: Int
)

private fun today(): SimpleDate {
    val c = Calendar.getInstance()
    return SimpleDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
}

private fun daysInMonth(year: Int, month: Int): Int {
    val c = Calendar.getInstance()
    c.set(Calendar.YEAR, year)
    c.set(Calendar.MONTH, month - 1)
    return c.getActualMaximum(Calendar.DAY_OF_MONTH)
}

/** 월요일=0 … 일요일=6 (Compose 그리드 정렬용) */
private fun firstWeekdayIndex(year: Int, month: Int): Int {
    val c = Calendar.getInstance()
    c.set(Calendar.YEAR, year)
    c.set(Calendar.MONTH, month - 1)
    c.set(Calendar.DAY_OF_MONTH, 1)
    // Calendar.DAY_OF_WEEK: 일=1, 월=2 … 토=7
    val idxSunStart = c.get(Calendar.DAY_OF_WEEK) - 1 // 0..6 (일=0)
    // 월요일 시작으로 변환
    return (idxSunStart + 6) % 7
}

private fun prevMonth(year: Int, month: Int): Pair<Int, Int> =
    if (month == 1) (year - 1) to 12 else year to (month - 1)

private fun nextMonth(year: Int, month: Int): Pair<Int, Int> =
    if (month == 12) (year + 1) to 1 else year to (month + 1)

private val MONTH_LABELS_FULL = arrayOf(
    "January","February","March","April","May","June",
    "July","August","September","October","November","December"
)

/* -------------------------
 *  화면
 * ------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit = {},
    /** 이벤트 표시: (year, month, day) -> 이모지/문자 */
    events: Map<Triple<Int, Int, Int>, String> = emptyMap()
) {
    val t = remember { today() }
    var year by remember { mutableStateOf(t.year) }
    var month by remember { mutableStateOf(t.month) }
    var selected by remember { mutableStateOf(SimpleDate(t.year, t.month, t.day)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("캘린더") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "뒤로") } }
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MonthYearHeader(
                year = year,
                month = month,
                onPrev = {
                    val (y, m) = prevMonth(year, month)
                    year = y; month = m
                },
                onNext = {
                    val (y, m) = nextMonth(year, month)
                    year = y; month = m
                },
                onPickMonth = { m -> month = m },
                onPickYear = { y -> year = y }
            )

            WeekdayHeader()

            DayGrid(
                year = year,
                month = month,
                selected = selected,
                events = events,
                onSelect = { selected = it }
            )
        }
    }
}

/* 헤더(월/년, 이전/다음, 드롭다운) */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthYearHeader(
    year: Int,
    month: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onPickMonth: (Int) -> Unit,
    onPickYear: (Int) -> Unit
) {
    Surface(
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${MONTH_LABELS_FULL[month - 1].uppercase(Locale.ENGLISH)} $year",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onPrev) { Icon(Icons.Default.ChevronLeft, "이전달") }

            // Month dropdown
            var openMonth by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = openMonth, onExpandedChange = { openMonth = !openMonth }) {
                AssistChip(
                    onClick = { openMonth = true },
                    label = { Text(MONTH_LABELS_FULL[month - 1].take(3)) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = openMonth, onDismissRequest = { openMonth = false }) {
                    (1..12).forEach { m ->
                        DropdownMenuItem(
                            text = { Text(MONTH_LABELS_FULL[m - 1]) },
                            onClick = { onPickMonth(m); openMonth = false }
                        )
                    }
                }
            }
            Spacer(Modifier.width(8.dp))

            // Year dropdown (2021..2032 예시)
            var openYear by remember { mutableStateOf(false) }
            val years = (2021..2032).toList()
            ExposedDropdownMenuBox(expanded = openYear, onExpandedChange = { openYear = !openYear }) {
                AssistChip(
                    onClick = { openYear = true },
                    label = { Text("$year") },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = openYear, onDismissRequest = { openYear = false }) {
                    years.forEach { y ->
                        DropdownMenuItem(text = { Text("$y") }, onClick = { onPickYear(y); openYear = false })
                    }
                }
            }

            IconButton(onClick = onNext) { Icon(Icons.Default.ChevronRight, "다음달") }
        }
    }
}

/* 요일 헤더 (Mon..Sun) */
@Composable
private fun WeekdayHeader() {
    val labels = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
    Row(Modifier.fillMaxWidth()) {
        labels.forEach { w ->
            Text(
                text = w,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/* 날짜 그리드 6행(42칸) 고정 */
@Composable
private fun DayGrid(
    year: Int,
    month: Int,
    selected: SimpleDate,
    events: Map<Triple<Int, Int, Int>, String>,
    onSelect: (SimpleDate) -> Unit
) {
    val firstIdx = firstWeekdayIndex(year, month)       // 0..6
    val dim = daysInMonth(year, month)
    val (py, pm) = prevMonth(year, month)
    val (ny, nm) = nextMonth(year, month)
    val prevDim = daysInMonth(py, pm)

    // 42칸 셀 날짜 구성
    val cells = buildList {
        // 앞부분: 이전달 말일에서 채우기
        for (i in firstIdx downTo 1) add(SimpleDate(py, pm, prevDim - i + 1))
        // 이번달
        for (d in 1..dim) add(SimpleDate(year, month, d))
        // 뒷부분: 다음달
        val remain = 42 - size
        for (d in 1..remain) add(SimpleDate(ny, nm, d))
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        userScrollEnabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(7f / 6f)
    ) {
        items(cells) { date ->
            val inMonth = (date.month == month && date.year == year)
            val isSelected = (date == selected)
            val dayTextColor =
                if (!inMonth) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                else MaterialTheme.colorScheme.onSurface

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelect(date) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${date.day}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else dayTextColor,
                        textAlign = TextAlign.Center
                    )
                    events[Triple(date.year, date.month, date.day)]?.let { em ->
                        Text(em, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

/* -------------------------
 *  Preview
 * ------------------------- */

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
private fun CalendarScreenPreview() {
    // 2025-10 기준, 13일 선택 + 22/29 이벤트
    val initYear = 2025
    val initMonth = 10
    val sampleEvents = mapOf(
        Triple(2025, 10, 22) to "🕊️",
        Triple(2025, 10, 29) to "🅽"
    )

    var year by remember { mutableStateOf(initYear) }
    var month by remember { mutableStateOf(initMonth) }
    var selected by remember { mutableStateOf(SimpleDate(initYear, initMonth, 13)) }

    MaterialTheme {
        Scaffold(bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true, onClick = {}, icon = { Text("📅") }, label = { Text("캘린더") }
                )
            }
        }) { padding ->
            Column(Modifier.padding(padding).padding(16.dp)) {
                MonthYearHeader(
                    year = year,
                    month = month,
                    onPrev = { val p = prevMonth(year, month); year = p.first; month = p.second },
                    onNext = { val n = nextMonth(year, month); year = n.first; month = n.second },
                    onPickMonth = { m -> month = m },
                    onPickYear = { y -> year = y }
                )
                Spacer(Modifier.height(12.dp))
                WeekdayHeader()
                DayGrid(
                    year = year,
                    month = month,
                    selected = selected,
                    events = sampleEvents,
                    onSelect = { selected = it }
                )
            }
        }
    }
}