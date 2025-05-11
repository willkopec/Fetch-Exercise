package com.willkopec.fetchexercise.data.api

import com.willkopec.fetchexercise.data.model.ApiItemDto
import retrofit2.http.GET

/*
Function used to fetch the API data and return data in a list of items
 */
interface ApiService {
    @GET("hiring.json")
    suspend fun fetchData(): List<ApiItemDto>
}