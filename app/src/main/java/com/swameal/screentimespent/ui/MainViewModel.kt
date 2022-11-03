package com.swameal.screentimespent.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swameal.screentimespent.data.db.ScreenTimeDatabase
import com.swameal.screentimespent.data.db.ScreenTimeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val screenTimeDatabase : ScreenTimeDatabase
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    fun trackScreenTimeEvent(screenTimeEvent: ScreenTimeEvent) {
        viewModelScope.launch {
            val result = screenTimeDatabase.screenTimeDao().insert(screenTimeEvent)
            Log.i(TAG, "trackEvent: event $screenTimeEvent")
        }
    }
}