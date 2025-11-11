package com.example.ottogether.navigation

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun encodeArg(raw: String): String =
    URLEncoder.encode(raw, StandardCharsets.UTF_8.name())

fun decodeArg(raw: String?): String =
    raw?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) } ?: ""