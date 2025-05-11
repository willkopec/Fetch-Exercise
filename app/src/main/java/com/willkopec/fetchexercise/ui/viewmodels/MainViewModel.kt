package com.willkopec.fetchexercise.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.willkopec.fetchexercise.data.model.FetchApiItem
import com.willkopec.fetchexercise.data.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: DataRepository
) : ViewModel() {

    //UI state representing the different states of our screen
    sealed class UiState {
        object Loading : UiState()
        data class Success(val data: List<FetchApiItem>) : UiState()
        data class Error(val message: String) : UiState()
    }

    //StateFlow to expose the UI state to the composable
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    //Keeping track of if we're currently refreshing for UI indicators
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    //Cart state
    private val _cartItems = MutableStateFlow<Set<FetchApiItem>>(emptySet())
    val cartItems: StateFlow<Set<FetchApiItem>> = _cartItems

    //Cart item count for notification badge
    val cartItemCount: StateFlow<Int> = _cartItems.map { it.size }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        0
    )

    init {
        //Load data when the ViewModel is created
        fetchData()
    }

    // Cart management functions
    fun addToCart(item: FetchApiItem) {
        _cartItems.update { currentItems ->
            currentItems + item
        }
    }

    fun removeFromCart(item: FetchApiItem) {
        _cartItems.update { currentItems ->
            currentItems - item
        }
    }

    fun isItemInCart(item: FetchApiItem): Boolean {
        return _cartItems.value.contains(item)
    }

    //Function to fetch data from the repository
    fun fetchData(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                // If we're forcing a refresh, set the refreshing state to true
                if (forceRefresh) {
                    _isRefreshing.value = true
                }

                // Collect the data from the repository
                repository.getData(forceRefresh = forceRefresh).collect { data ->
                    _uiState.value = UiState.Success(data)

                    if (forceRefresh) {
                        _isRefreshing.value = false
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
                if (forceRefresh) {
                    _isRefreshing.value = false
                }
            }
        }
    }

    fun refreshData() {
        fetchData(forceRefresh = true)
    }
}