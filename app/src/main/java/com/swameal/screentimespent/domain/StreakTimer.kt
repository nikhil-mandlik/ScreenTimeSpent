package com.swameal.screentimespent.domain

import android.util.Log
import kotlinx.coroutines.*


class StreakTimer(private val streakTimerEvents: StreakTimerEvents) {

    companion object {
        private const val TAG = "LiveStreakManager"
    }

    private var timerJob: Job? = null
    private var timeSpent: Long = 0L
    private val timerExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.i(TAG, "timerExceptionHandler $throwable ")
    }
    private val timerScope = CoroutineScope(Dispatchers.IO + timerExceptionHandler)

    fun initializeTimer(initialTimeSpent: Long = 0L) {
        timeSpent = initialTimeSpent
        timerJob = null
    }

    fun resetTimer() {
        timeSpent = 0L
        timerJob?.cancel()
        timerJob = null
    }

    fun startTimer() {
        timerJob = timerScope.launch {
            while (isActive) {
                timeSpent += 1000
                delay(1000)
                streakTimerEvents.onTick(timeSpent)
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    fun getCurrentTimeSpent() = timeSpent

}

interface StreakTimerEvents {
    fun onTick(timeSpent: Long)
}