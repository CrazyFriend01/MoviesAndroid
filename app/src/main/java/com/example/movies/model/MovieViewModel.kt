package com.example.movies.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.data.datastore.DataStoreManager
import com.example.movies.domain.IMovieRepository
import com.example.movies.presentation.mapper.MovieUiMapper
import com.example.movies.presentation.model.MovieUiModel
import com.example.movies.state.ListState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.logging.Logger
import java.net.UnknownHostException

class MovieViewModel(
    private val repository: IMovieRepository,
    private val uiMapper: MovieUiMapper,
    val context: Context,

) : ViewModel() {
    private val mutableState = MutableListState()
    val viewState = mutableState as ListState
    val dataStoreManager = DataStoreManager(context)

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        mutableState.loading = false
        mutableState.error = when (exception) {
            is IOException -> "Проблемы с подключением к интернету. Проверьте ваше подключение."
            is UnknownHostException -> "Не удается найти сервер. Проверьте ваше подключение."
            else -> "Произошла ошибка: ${exception.localizedMessage}"
        }
    }
    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch(exceptionHandler) {
            mutableState.loading = true
            mutableState.error = null
            mutableState.items = emptyList()
            val movies = repository.getMovie(viewState.searchName)
            mutableState.items = movies.map{uiMapper.mapMovie(it)}
            mutableState.loading = false
        }
    }


    private class MutableListState : ListState {
        override var searchName: String by mutableStateOf(DEFAULT_SEARCH_NAME)
        override var filterContentStatus: String by mutableStateOf(DEFAULT_CONTENT_STATUS)
        override var items: List<MovieUiModel> by mutableStateOf(emptyList())
        override var error: String? by mutableStateOf(null)
        override var loading: Boolean by mutableStateOf(false)
    }

    companion object {
        private const val DEFAULT_CONTENT_STATUS = "completed"
        private const val DEFAULT_SEARCH_NAME = "movie"
        private val LOG = Logger.getLogger(MovieViewModel::class.java.name)
    }
}
