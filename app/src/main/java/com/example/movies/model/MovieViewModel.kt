package com.example.movies.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.domain.IMovieRepository
import com.example.movies.state.ListState
import com.example.movies.utils.launchLoadingAndError
import java.io.IOException


class MovieViewModel(
    private val repository: IMovieRepository
) : ViewModel() {
    private val mutableState = MutableListState()
    val viewState = mutableState as ListState

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launchLoadingAndError(
            handleError = { error ->
                mutableState.error = when (error) {
                    is IOException -> "Проверьте подключение к интернету."
                    else -> error.localizedMessage
                }},
            updateLoading = { mutableState.loading = it }
        ) {
            mutableState.error = null
            mutableState.items = repository.getMovie(viewState.searchName)
        }
    }

    private class MutableListState : ListState {
        override var searchName: String by mutableStateOf("movie")
        override var items: List<Movie> by mutableStateOf(emptyList())
        override var error: String? by mutableStateOf(null)
        override var loading: Boolean by mutableStateOf(false)
    }




}