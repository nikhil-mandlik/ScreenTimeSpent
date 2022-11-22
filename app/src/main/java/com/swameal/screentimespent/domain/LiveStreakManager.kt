package com.swameal.screentimespent.domain

import android.util.Log
import com.swameal.screentimespent.data.*
import kotlinx.coroutines.*
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject
import javax.inject.Singleton


sealed class LiveStreakSideEffects {
    data class OnDayEnded(val timeStamp: Long, val totalTimeSpent: Long) : LiveStreakSideEffects()
    data class LiveStreakCompleted(val streakId: String) : LiveStreakSideEffects()
    data class LiveStreakRewardUnlocked(val reward: LiveStreakReward) : LiveStreakSideEffects()
}

sealed class OngoingTimer {
    data class Streak(
        val streakId: String,
        val streakStatus: StreakStatus,
        val totalRequiredTimeToComplete: Long,
        val remainingTimeToComplete: Long,
        val meta: Any? = null
    ) : OngoingTimer()

    data class Reward(
        val rewardID: String,
        val streakId: String,
        val totalRequiredTimeToComplete: Long,
        val remainingTimeToComplete: Long,
        val rewardStatus: RewardStatus,
        val meta: Any? = null
    ) : OngoingTimer()

    data class DayEnded(val streakId: String) : OngoingTimer()

    object None : OngoingTimer()
}

data class LiveStreakState(
    val ongoingTimer: OngoingTimer = OngoingTimer.None,
    val liveStreakEntity: LiveStreakEntity? = null,
)


@Singleton
class LiveStreakManager @Inject constructor(
    private val liveStreakRepo: LiveStreakRepo,
) : ContainerHost<LiveStreakState, LiveStreakSideEffects> {

    companion object {
        private const val TAG = "LiveStreakManager"
    }


    private val liveStreakScope =
        CoroutineScope(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            Log.i(TAG, "liveStreakScope Exception: $throwable")
        })


    override val container: Container<LiveStreakState, LiveStreakSideEffects> =
        liveStreakScope.container(LiveStreakState())


    private var streakTimer: StreakTimer = StreakTimer(object : StreakTimerEvents {
        override fun onTick(timeSpent: Long) {
            intent {
                reduce {
                    state.liveStreakEntity?.let {
                        getActiveState(
                            currentState = state,
                            currentTimeSpent = timeSpent,
                            streakEntity = it
                        )
                    } ?: state
                }
            }
        }
    })

    fun initializeTimer(liveStreakEntity: LiveStreakEntity) {

        intent {
            val ongoingEvent = if (liveStreakEntity.streakStatus == StreakStatus.IN_PROGRESS) {
                OngoingTimer.Streak(
                    streakId = liveStreakEntity.streakId,
                    streakStatus = liveStreakEntity.streakStatus,
                    totalRequiredTimeToComplete = liveStreakEntity.streakRequiredTimeSpent,
                    remainingTimeToComplete = liveStreakEntity.streakRequiredTimeSpent
                )
            } else {
                val ongoingReward = liveStreakEntity.liveStreakRewards.firstOrNull { it.rewardStatus == RewardStatus.LOCKED }

                ongoingReward?.let {
                    OngoingTimer.Reward(
                        streakId = liveStreakEntity.streakId,
                        totalRequiredTimeToComplete = liveStreakEntity.streakRequiredTimeSpent,
                        remainingTimeToComplete = it.bonusRequiredTimeSpent,
                        rewardStatus = it.rewardStatus,
                        rewardID = it.rewardID
                    )
                } ?: OngoingTimer.None
            }

            reduce {
                state.copy(
                    liveStreakEntity = liveStreakEntity,
                    ongoingTimer = ongoingEvent
                )
            }
        }
        streakTimer.initializeTimer(liveStreakEntity.currentTotalTimeSpent)
    }

    fun startStreakTimer() {
        streakTimer.startTimer()
    }

    fun stopStreakTimer() {
        streakTimer.stopTimer()
    }

    private fun onDayEnd(currentTimeStamp: Long, currentTotalTimeSpent: Long) =
        intent {
            stopStreakTimer()
            postSideEffect(
                LiveStreakSideEffects.OnDayEnded(
                    currentTimeStamp, currentTotalTimeSpent
                )
            )
        }

    private fun onStreakCompleted(streakEntity: LiveStreakEntity, currentTimeSpent: Long) = intent {
        intent {
            postSideEffect(LiveStreakSideEffects.LiveStreakCompleted(streakId = streakEntity.streakId))
        }
    }

    private fun onBonusRewardUnlocked(reward: LiveStreakReward) = intent {
        intent {
            postSideEffect(LiveStreakSideEffects.LiveStreakRewardUnlocked(reward = reward))
        }
    }


    private fun getActiveState(
        currentState: LiveStreakState,
        currentTimeSpent: Long,
        streakEntity: LiveStreakEntity
    ): LiveStreakState {
        return when (val ongoingState = currentState.ongoingTimer) {
            is OngoingTimer.Streak -> {
                if (currentTimeSpent == streakEntity.streakRequiredTimeSpent) {
                    val bonusRewards =
                        streakEntity.liveStreakRewards.firstOrNull { it.rewardStatus == RewardStatus.LOCKED }

                    val ongoingTimer = bonusRewards?.let {
                        OngoingTimer.Reward(
                            streakId = streakEntity.streakId,
                            totalRequiredTimeToComplete = it.bonusRequiredTimeSpent,
                            remainingTimeToComplete = it.bonusRequiredTimeSpent,
                            rewardID = it.rewardID,
                            rewardStatus = it.rewardStatus
                        )
                    } ?: OngoingTimer.None

                    onStreakCompleted(streakEntity, currentTimeSpent)

                    currentState.copy(
                        ongoingTimer = ongoingTimer,
                        liveStreakEntity = streakEntity.copy(
                            streakStatus = StreakStatus.COMPLETED
                        )
                    )
                } else {
                    currentState.copy(
                        ongoingTimer = ongoingState.copy(
                            remainingTimeToComplete = (ongoingState.totalRequiredTimeToComplete - currentTimeSpent).coerceIn(
                                0,
                                ongoingState.totalRequiredTimeToComplete
                            )
                        )
                    )
                }
            }
            is OngoingTimer.Reward -> {
                val totalRequiredTimeSpent = ongoingState.totalRequiredTimeToComplete + streakEntity.streakRequiredTimeSpent + streakEntity.liveStreakRewards.filter { it.rewardStatus == RewardStatus.UNLOCKED }.sumOf { it.bonusRequiredTimeSpent }
                if (currentTimeSpent == totalRequiredTimeSpent && ongoingState.rewardStatus != RewardStatus.UNLOCKED) {
                    val updatedBonusIndex = streakEntity.liveStreakRewards.toMutableList()
                        .indexOfFirst { it.rewardID == ongoingState.rewardID }

                    val updatedReward = streakEntity.liveStreakRewards[updatedBonusIndex].copy(
                        rewardStatus = RewardStatus.UNLOCKED
                    )

                    val updatedLiveStreakRewardsList =
                        streakEntity.liveStreakRewards.toMutableList().apply {
                            set(updatedBonusIndex, updatedReward)
                        }

                    val updatedStreakEntity = streakEntity.copy(
                        liveStreakRewards = updatedLiveStreakRewardsList
                    )

                    val bonusRewards =
                        updatedStreakEntity.liveStreakRewards.firstOrNull { it.rewardStatus == RewardStatus.LOCKED }

                    val ongoingTimer = bonusRewards?.let {
                        OngoingTimer.Reward(
                            streakId = streakEntity.streakId,
                            totalRequiredTimeToComplete = it.bonusRequiredTimeSpent,
                            remainingTimeToComplete = it.bonusRequiredTimeSpent,
                            rewardID = it.rewardID,
                            rewardStatus = it.rewardStatus
                        )
                    } ?: OngoingTimer.None

                    onBonusRewardUnlocked(updatedReward)

                    currentState.copy(
                        ongoingTimer = ongoingTimer,
                        liveStreakEntity = updatedStreakEntity
                    )
                } else {
                    val timeSpentOnStreak = streakEntity.streakRequiredTimeSpent
                    val timeSpentOnCompletedBonus = streakEntity.liveStreakRewards.filter { it.rewardStatus == RewardStatus.UNLOCKED }.sumOf { it.bonusRequiredTimeSpent }

                    val remainingTimeForBonusToUnlock =
                        ongoingState.totalRequiredTimeToComplete - (currentTimeSpent - timeSpentOnStreak - timeSpentOnCompletedBonus)

                    currentState.copy(
                        ongoingTimer = ongoingState.copy(
                            remainingTimeToComplete = (remainingTimeForBonusToUnlock).coerceIn(0, ongoingState.totalRequiredTimeToComplete)
                        )
                    )
                }
            }
            is OngoingTimer.None -> {
                currentState
            }
            is OngoingTimer.DayEnded -> {
                currentState
            }
        }
    }
}



