package com.willkopec.fetchexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.willkopec.fetchexercise.data.datastore.DataStore
import com.willkopec.fetchexercise.ui.navigation.AppContainer
import com.willkopec.fetchexercise.ui.theme.FetchExerciseTheme
import com.willkopec.fetchexercise.ui.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val myViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dataStore = DataStore(this)

        setContent {
            FetchExerciseTheme(dataStore) {
                AppContainer(myViewModel, dataStore)
            }
        }
    }
}