package com.example.ottogether.core.ui

import androidx.annotation.DrawableRes
import com.example.ottogether.R

@DrawableRes
fun logoFor(name: String): Int = when (name.lowercase()) {
    "넷플릭스", "netflix" -> R.drawable.ic_logo_netflix
    "디즈니플러스", "disney", "disney+" -> R.drawable.ic_logo_disney
    "쿠팡플레이", "coupang" -> R.drawable.ic_logo_coupang
    "티빙", "tving" -> R.drawable.ic_logo_tving
    "왓챠", "watcha" -> R.drawable.ic_logo_watcha
    "웨이브", "wavve" -> R.drawable.ic_logo_wavve
    else -> R.drawable.ic_logo_netflix
}