package com.willkopec.fetchexercise.data.repository

import com.willkopec.fetchexercise.data.api.ApiService
import com.willkopec.fetchexercise.data.datastore.DataStore
import com.willkopec.fetchexercise.data.model.ApiItemDto
import com.willkopec.fetchexercise.data.model.FetchApiItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

class DataRepository(
    private val apiService: ApiService,
    private val dataStoreManager: DataStore
) {

    // Cache duration in milliseconds (30 minutes)
    private val CACHE_DURATION = TimeUnit.MINUTES.toMillis(30)

    suspend fun getData(forceRefresh: Boolean): Flow<List<FetchApiItem>> = flow {
        //Check if we should use cache or fetch new data
        val shouldFetchFromNetwork = forceRefresh || isCacheExpired()

        if (shouldFetchFromNetwork) {
            try {
                //Fetch fresh data from network
                val apiResponse = apiService.fetchData()

                //Map API response to domain models
                val apiItems = apiResponse
                    .filter { it.name != null && it.name.isNotEmpty() }
                    .sortedWith(
                        compareBy<ApiItemDto> { it.listId }
                            .thenBy { it.name }
                    )
                    .mapNotNull { apiItemDto ->
                        apiItemDto.name?.let { name ->
                            FetchApiItem(
                                id = apiItemDto.id,
                                listId = apiItemDto.listId,
                                name = name
                            )
                        }
                    }

                //Save to cache
                saveToCache(apiItems)

                //Update last fetch timestamp
                dataStoreManager.saveLastFetchTimestamp(System.currentTimeMillis())

                //Emit the result
                emit(apiItems)
            } catch (e: Exception) {
                //If network fetch fails, try to get data from cache as fallback
                val cachedData = getFromCache()
                if (cachedData.isNotEmpty()) {
                    emit(cachedData)
                } else {
                    throw e
                }
            }
        } else {
            //Use cached data
            emit(getFromCache())
        }
    }

    private suspend fun isCacheExpired(): Boolean {
        val lastFetchTime = dataStoreManager.getLastFetchTimestamp()
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastFetchTime) > CACHE_DURATION || getFromCache().isEmpty()
    }

    private suspend fun saveToCache(items: List<FetchApiItem>) {
        dataStoreManager.saveItems(items)
    }

    private suspend fun getFromCache(): List<FetchApiItem> {
        return dataStoreManager.getItems()
    }
}