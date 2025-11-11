package com.example.ottogether.navigation

import android.net.Uri
import androidx.navigation.NavController

private fun String.encode() = Uri.encode(this)

/** placeholder({key})를 값으로 치환 */
fun Route.buildPath(vararg args: Pair<String, String>): String {
    var built = path
    args.forEach { (k, v) ->
        built = built.replace("{$k}", v.encode())
    }
    return built
}

/** 파라미터 없는 라우트 이동 */
fun NavController.navigate(route: Route) {
    navigate(route.path)
}

/** 파라미터 있는 라우트 이동 (placeholder 자동 치환) */
fun NavController.navigate(route: Route, vararg args: Pair<String, String>) {
    navigate(route.buildPath(*args))
}