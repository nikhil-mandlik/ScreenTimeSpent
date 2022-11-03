package com.swameal.screentimespent.ui

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import com.swameal.screentimespent.data.db.ScreenTimeEvent
import com.swameal.screentimespent.domain.LocalLiveStreamInfo

@Composable
fun BaseScreen(
    screen: @Composable () -> Unit,
    backStackEntry: NavBackStackEntry,
    onScreenTimeEvent: (ScreenTimeEvent) -> Unit
) {
    val lifecycle = backStackEntry.lifecycle
    val liveSessionId = LocalLiveStreamInfo.current.liveSessionId
    val meta = LocalLiveStreamInfo.current.meta

    DisposableEffect(key1 = lifecycle) {
        var startTime = SystemClock.elapsedRealtime()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                startTime = SystemClock.elapsedRealtime()
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                onScreenTimeEvent(
                    ScreenTimeEvent(
                        screenName = backStackEntry.destination.route.orEmpty(),
                        liveSessionId = liveSessionId,
                        timeSpent = SystemClock.elapsedRealtime() - startTime,
                        meta = meta
                    )
                )
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
    screen()
}



