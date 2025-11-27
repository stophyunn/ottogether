package com.example.ottogether.feature.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ottogether.core.data.remote.MovieRepository
import com.example.ottogether.core.data.remote.dto.MovieResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val movieRepository = MovieRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun loadHome() {
        viewModelScope.launch {
            try {
                val movies: List<MovieResult> = movieRepository.getTrendingMovies()
                _uiState.value = _uiState.value.copy(
                    trendingMovies = movies,
                    isLoading = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
        }
    }
}