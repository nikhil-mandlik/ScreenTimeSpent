package com.swameal.screentimespent.data

import javax.inject.Inject

class LiveStreakRepo @Inject constructor() {

    private val startTimeStamp = System.currentTimeMillis()
    private val endTimeStamp = startTimeStamp + 24 * 60 * 60 * 1000
    private var liveStreakInfoEntity: LiveStreakInfoEntity? = null

    private fun refreshLiveStreakInfo(): LiveStreakInfoEntity {
        LiveStreakInfoEntity(
            id = "1",
            startTimeStamp = startTimeStamp,
            endTimeStamp = endTimeStamp
        ).apply {
            this@LiveStreakRepo.liveStreakInfoEntity = this
            return this
        }
    }

    fun getLiveStreakInfo(shouldFetchFromNetwork: Boolean): LiveStreakInfoEntity {
        return if (shouldFetchFromNetwork) {
            refreshLiveStreakInfo()
        } else {
            liveStreakInfoEntity ?: refreshLiveStreakInfo()
        }
    }
}