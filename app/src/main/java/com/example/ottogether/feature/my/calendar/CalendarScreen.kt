package com.example.ottogether.feature.my.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ottogether.R
import java.util.Calendar
import java.util.Locale

data class SimpleDate(val year: Int, val month: Int, val day: Int)

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

private fun firstWeekdayIndex(year: Int, month: Int): Int {
    val c = Calendar.getInstance()
    c.set(Calendar.YEAR, year)
    c.set(Calendar.MONTH, month - 1)
    c.set(Calendar.DAY_OF_MONTH, 1)
    val idxSunStart = c.get(Calendar.DAY_OF_WEEK) - 1 // 0..6 (일=0)
    return (idxSunStart + 6) % 7 // 월요일 시작
}

private fun prevMonth(year: Int, month: Int): Pair<Int, Int> =
    if (month == 1) (year - 1) to 12 else year to (month - 1)

private fun nextMonth(year: Int, month: Int): Pair<Int, Int> =
    if (month == 12) (year + 1) to 1 else year to (month + 1)

private val MONTH_LABELS = arrayOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit = {},
    events: Map<Triple<Int, Int, Int>, Int> = emptyMap(),
    onDateSelected: (SimpleDate) -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    val t = remember { today() }
    var year by remember { mutableStateOf(t.year) }
    var month by remember { mutableStateOf(t.month) }
    var selected by remember { mutableStateOf(SimpleDate(t.year, t.month, t.day)) }
    var showPicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF6F6FB),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "캘린더",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF808080)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF6F6FB))
            )
        },
        bottomBar = bottomBar
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    /** 월/년도 헤더 */
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {
                            val (y, m) = prevMonth(year, month)
                            year = y; month = m
                        }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "이전달", tint = Color.Gray)
                        }

                        Text(
                            text = "${MONTH_LABELS[month - 1].take(3).uppercase(Locale.ENGLISH)} $year",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            ),
                            color = Color(0xFF222222),
                            modifier = Modifier.clickable { showPicker = true }
                        )

                        IconButton(onClick = {
                            val (y, m) = nextMonth(year, month)
                            year = y; month = m
                        }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "다음달", tint = Color.Gray)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    WeekHeader()
                    Spacer(Modifier.height(4.dp))

                    /** 날짜 그리드 */
                    DayGrid(
                        year = year,
                        month = month,
                        selected = selected,
                        events = events,
                        onSelect = {
                            selected = it
                            onDateSelected(it)
                        }
                    )
                }
            }

            if (showPicker) {
                MonthYearPickerDialog(
                    currentYear = year,
                    currentMonth = month,
                    onDismiss = { showPicker = false },
                    onConfirm = { y, m ->
                        year = y
                        month = m
                        showPicker = false
                    }
                )
            }
        }
    }
}

@Composable
private fun WeekHeader() {
    val weekLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        weekLabels.forEach {
            Text(
                text = it,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color(0xFF808080),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun DayGrid(
    year: Int,
    month: Int,
    selected: SimpleDate,
    events: Map<Triple<Int, Int, Int>, Int>,
    onSelect: (SimpleDate) -> Unit
) {
    val firstIdx = firstWeekdayIndex(year, month)
    val dim = daysInMonth(year, month)
    val (py, pm) = prevMonth(year, month)
    val (ny, nm) = nextMonth(year, month)
    val prevDim = daysInMonth(py, pm)

    val cells = buildList {
        for (i in firstIdx downTo 1) add(SimpleDate(py, pm, prevDim - i + 1))
        for (d in 1..dim) add(SimpleDate(year, month, d))
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
            val dayColor = if (!inMonth) Color.LightGray else Color(0xFF222222)

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .aspectRatio(1f)
                    .clickable { onSelect(date) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected && inMonth) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF7A2F).copy(alpha = 0.25f))
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${date.day}",
                        color = if (isSelected) Color(0xFFFF7A2F) else dayColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                    events[Triple(date.year, date.month, date.day)]?.let { res ->
                        Icon(
                            painter = painterResource(id = res),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthYearPickerDialog(
    currentYear: Int,
    currentMonth: Int,
    yearRange: IntRange = 2021..2032,
    onDismiss: () -> Unit,
    onConfirm: (year: Int, month: Int) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        MonthYearPickerDialogContent(
            currentYear = currentYear,
            currentMonth = currentMonth,
            yearRange = yearRange,
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 800)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen(
        events = mapOf(
            Triple(2025, 10, 22) to R.drawable.ic_logo_coupang,
            Triple(2025, 10, 31) to R.drawable.ic_logo_netflix
        )
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Month/Year Picker – Preview (No Dialog)")
@Composable
fun MonthYearPickerDialogContentPreview() {
    MaterialTheme {
        Surface(shape = RoundedCornerShape(12.dp), color = Color.White) {
            MonthYearPickerDialogContent(
                currentYear = 2027,
                currentMonth = 10,
                onDismiss = {},
                onConfirm = { _, _ -> }
            )
        }
    }
}
@Composable
private fun MonthYearPickerDialogContent(
    currentYear: Int,
    currentMonth: Int,
    yearRange: IntRange = 2021..2032,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val months = remember {
        listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
    }
    var selYear by remember { mutableStateOf(currentYear) }
    var selMonth by remember { mutableStateOf(currentMonth) }

    val monthState = rememberLazyListState(initialFirstVisibleItemIndex = (currentMonth - 1).coerceAtLeast(0))
    val yearIndex = yearRange.indexOf(currentYear).coerceAtLeast(0)
    val yearState = rememberLazyListState(initialFirstVisibleItemIndex = yearIndex)

    Surface(shape = RoundedCornerShape(12.dp), color = Color.White) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Select month & year",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LazyColumn(
                    state = monthState,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF6F6FB))
                ) {
                    items(12) { i ->
                        val idx = i + 1
                        val selected = (idx == selMonth)
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { selMonth = idx }
                                .background(if (selected) Color(0xFFFF7A2F) else Color.Transparent)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                months[i],
                                color = if (selected) Color.White else Color.Black
                            )
                        }
                    }
                }

                LazyColumn(
                    state = yearState,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF6F6FB))
                ) {
                    items(yearRange.count()) { i ->
                        val y = yearRange.first + i
                        val selected = (y == selYear)
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { selYear = y }
                                .background(if (selected) Color(0xFFFF7A2F) else Color.Transparent)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                y.toString(),
                                color = if (selected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("취소") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onConfirm(selYear, selMonth) }) {
                    Text("확인")
                }
            }
        }
    }
}
