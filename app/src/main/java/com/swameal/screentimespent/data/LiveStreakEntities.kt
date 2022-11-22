package com.swameal.screentimespent.data


data class LiveStreakEntity(
    val streakId: String,
    val streakStartTimeStamp: Long,
    val streakEndTimeStamp: Long,
    val currentTotalTimeSpent: Long,
    val streakStatus: StreakStatus,
    val streakRequiredTimeSpent: Long,
    val liveStreakRewards: List<LiveStreakReward> = emptyList()
)

enum class StreakStatus {
    IN_PROGRESS,
    COMPLETED
}

data class LiveStreakReward(
    val rewardType: RewardType,
    val bonusRequiredTimeSpent: Long,
    val priority: Int,
    val rewardStatus: RewardStatus,
    val rewardID: String
)

sealed class RewardType {
    data class FreeCheers(val quantity: Double) : RewardType()
    data class FreeGift(val giftId: String) : RewardType()
    data class Trophies(val trophiesId: String) : RewardType()
}

enum class RewardStatus {
    LOCKED, UNLOCKED
}