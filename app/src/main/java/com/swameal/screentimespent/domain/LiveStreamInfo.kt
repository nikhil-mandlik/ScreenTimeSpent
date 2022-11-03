package com.swameal.screentimespent.domain

import androidx.compose.runtime.compositionLocalOf
import java.util.*

data class LiveStreamInfo(
    val liveSessionId: String = UUID.randomUUID().toString(),
    val meta: Any? = null
)


val LocalLiveStreamInfo = compositionLocalOf {
    LiveStreamInfo()
}


