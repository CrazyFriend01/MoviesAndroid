package com.example.movies.data.api

import com.example.movies.data.model.MoviesPagingResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MovieApi {
    @GET("movie")
    suspend fun getMovies(
        @Header("X-API-KEY") apiKey: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("selectFields") selectFields: List<String>,
        @Query("notNullFields") notNullFields: List<String>,
        @Query("sortField") sortField: List<String>,
        @Query("type") type: List<String>,
        @Query("sortType") sortType: String,
        @Query("year") year: String,
    ): MoviesPagingResponse

}