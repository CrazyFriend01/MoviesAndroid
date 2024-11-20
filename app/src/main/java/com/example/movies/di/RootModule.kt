package com.example.movies.di

import com.example.movies.data.mapper.MovieResponseToEntityMapper
import com.example.movies.data.repository.MovieRepository
import com.example.movies.domain.IMovieRepository
import com.example.movies.model.MovieViewModel
import com.example.movies.presentation.mapper.MovieUiMapper
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val rootModule = module {
    single<IMovieRepository> { MovieRepository(get(), get()) }
    factory { MovieResponseToEntityMapper() }
    factory { MovieUiMapper() }

    viewModel { MovieViewModel(get(), get(), get()) }
}