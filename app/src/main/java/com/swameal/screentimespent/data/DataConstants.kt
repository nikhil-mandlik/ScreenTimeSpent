package com.swameal.screentimespent.data

import com.swameal.screentimespent.domain.*

object DataConstants {
    const val SCREEN_TIME_DATABASE = "screen_time_db"

    val currentTimeStamp = System.currentTimeMillis()

    val liveStreakEntity = LiveStreakEntity(
        streakId = "s1",
        streakStartTimeStamp = currentTimeStamp,
        streakEndTimeStamp = currentTimeStamp + 24 * 60 * 60 * 1000,
        currentTotalTimeSpent = 0,
        streakStatus = StreakStatus.IN_PROGRESS,
        streakRequiredTimeSpent = 15 * 1000,
        liveStreakRewards = listOf(
            LiveStreakReward(
                rewardType = RewardType.FreeGift(giftId = "g1"),
                bonusRequiredTimeSpent = 10 * 1000,
                priority = 1,
                rewardStatus = RewardStatus.LOCKED,
                rewardID = "r1"
            ),
            LiveStreakReward(
                rewardType = RewardType.FreeGift(giftId = "g2"),
                bonusRequiredTimeSpent = 8 * 1000,
                priority = 2,
                rewardStatus = RewardStatus.LOCKED,
                rewardID = "r2"
            )
        )
    )
}