// core/util/NavArgs.kt
package com.example.ottogether.core.util
import android.net.Uri

fun String.encode(): String = Uri.encode(this)