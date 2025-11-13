package com.example.ottogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import com.example.ottogether.navigation.AppNavHost
import com.example.ottogether.ui.theme.OttogetherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OttogetherTheme {
                val navController = rememberNavController()
                val sessionViewModel: AppSessionViewModel = hiltViewModel()
                val sessionState = sessionViewModel.state.collectAsState()

                AppNavHost(
                    navController = navController,
                    sessionState = sessionState.value,
                    sessionViewModel = sessionViewModel
                )
            }
        }
    }
}