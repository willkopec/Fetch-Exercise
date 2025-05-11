package com.willkopec.fetchexercise.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.willkopec.fetchexercise.data.model.FetchApiItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStore(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "app_preferences")
        private val LAST_FETCH_TIMESTAMP = longPreferencesKey("last_fetch_timestamp")
        private val CACHED_ITEMS = stringPreferencesKey("cached_items")
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    //Function to check if user is in Dark Mode
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false // Default to false (light mode)
        }

    //Update dark mode
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    //Save the timestamp of the last successful API fetch
    suspend fun saveLastFetchTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_FETCH_TIMESTAMP] = timestamp
        }
    }

    //Get the timestamp of the last successful API fetch
    suspend fun getLastFetchTimestamp(): Long {
        return context.dataStore.data.map { preferences ->
            preferences[LAST_FETCH_TIMESTAMP] ?: 0L
        }.first()
    }

    //Save API items to DataStore as JSON string
    suspend fun saveItems(items: List<FetchApiItem>) {
        val gson = Gson()
        val json = gson.toJson(items)

        context.dataStore.edit { preferences ->
            preferences[CACHED_ITEMS] = json
        }
    }

    //Get API items from DataStore
    suspend fun getItems(): List<FetchApiItem> {
        val gson = Gson()
        val listType = object : TypeToken<List<FetchApiItem>>() {}.type

        return context.dataStore.data.map { preferences ->
            val json = preferences[CACHED_ITEMS] ?: "[]"
            gson.fromJson(json, listType) ?: emptyList<FetchApiItem>()
        }.first()
    }

}