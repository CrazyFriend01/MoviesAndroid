package com.example.movies.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.example.movies.model.FavoriteViewModel
import com.example.movies.model.MovieViewModel
import com.example.movies.presentation.model.MovieUiModel
import com.example.movies.utils.MovieEntityMapper

@Composable
fun ListScreen(viewModel: MovieViewModel, onMovieClick: (Long) -> Unit) {
    val pagingItems = viewModel.pagedMovies.collectAsLazyPagingItems()
    val state = viewModel.viewState

    Log.d("MovieListScreen", "Movies loaded: ${pagingItems.itemSnapshotList.size}")
    pagingItems.apply {
        when {
            loadState.refresh is androidx.paging.LoadState.Loading -> {
                Log.d("MovieListScreen", "Initial loading...")
            }
            loadState.append is androidx.paging.LoadState.Loading -> {
                Log.d("MovieListScreen", "Loading more items...")
            }
            loadState.refresh is androidx.paging.LoadState.Error -> {
                val error = loadState.refresh as androidx.paging.LoadState.Error
                Log.e("MovieListScreen", "Error refreshing data: ${error.error}")
            }
            loadState.append is androidx.paging.LoadState.Error -> {
                val error = loadState.append as androidx.paging.LoadState.Error
                Log.e("MovieListScreen", "Error appending data: ${error.error}")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    )
    {
        val lazyColumnState = rememberSaveable(saver = LazyListState.Saver) {
            LazyListState(
                0,
                0
            )
        }

        LazyColumn(
            Modifier.fillMaxSize(),
            lazyColumnState
        ) {
            items(count = pagingItems.itemCount) { index ->
                pagingItems[index]?.let { ConstructorItem(movie = it,  onMovieClick = onMovieClick)}
                    ?: MessagePlaceholder()
            }
            pagingItems.apply {
                when {
                    loadState.refresh is androidx.paging.LoadState.Loading -> {
                        item {
                            LoadingIndicator()
                        }
                    }
                    loadState.append is androidx.paging.LoadState.Loading -> {
                        item {
                            LoadingMoreIndicator()
                        }
                    }
                    loadState.append is androidx.paging.LoadState.Error -> {
                        val error = loadState.append as androidx.paging.LoadState.Error
                        item {
                            ErrorItem(message = error.error.localizedMessage ?: "Unknown error") {
                                pagingItems.retry()
                            }
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun ErrorItem(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
            Text("Повторить")
        }
    }
}

@Composable
fun LoadingMoreIndicator() {
    LinearProgressIndicator (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = Color(0xFFF89224),
    )
}

@Composable
fun LoadingIndicator() {
    LinearProgressIndicator (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = Color(0xFFF89224),
    )
}

@Composable
fun MessagePlaceholder() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        LinearProgressIndicator (
            color = Color(0xFFF89224)
        )
    }
}


@Composable
private fun ConstructorItem(movie: MovieUiModel, onMovieClick: (Long) -> Unit) {
    val favoriteViewMovie: FavoriteViewModel = viewModel()
    val isFavorite = favoriteViewMovie.favoriteMovieList.any { it.id.toLong() == movie.id }
    ListItem(modifier = Modifier
        .clickable { onMovieClick(movie.id) }
        .padding(8.dp)
        .shadow(10.dp)
        .clip(
            RoundedCornerShape(10.dp)
        ), headlineContent = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(movie.posterUrl),
                contentDescription = null,
                modifier = Modifier.size(170.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column (
                modifier = Modifier.weight(1f)
            ){
                Text(movie.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    movie.description.take(80) + "...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            IconButton(
                onClick = {
                    val movieEntity = MovieEntityMapper.toEntity(movie)
                    if (isFavorite) {
                        favoriteViewMovie.removeMovieFromFavorite(movieEntity)
                    } else {
                        favoriteViewMovie.addMovieToFavorite(movieEntity, movie.posterUrl)
                    }
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (isFavorite) Color(0xFFF89224) else Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    })
}
