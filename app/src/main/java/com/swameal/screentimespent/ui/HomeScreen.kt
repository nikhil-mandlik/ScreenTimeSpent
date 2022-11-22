package com.swameal.screentimespent.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swameal.screentimespent.data.DataConstants
import com.swameal.screentimespent.domain.LiveStreakManager
import com.swameal.screentimespent.domain.OngoingTimer


@Composable
fun HomeScreen(
    navigateToDetails: () -> Unit,
    navigateToMiniInfo: () -> Unit,
    liveStreakManager: LiveStreakManager
) {

    LaunchedEffect(key1 = true) {
        liveStreakManager.initializeTimer(DataConstants.liveStreakEntity)
    }

    val liveStreakState by liveStreakManager.container.stateFlow.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        val ongoingTimerHeading by remember(liveStreakState.ongoingTimer) {
            mutableStateOf(
                when(val ongoingEvent = liveStreakState.ongoingTimer) {
                    is OngoingTimer.DayEnded -> "Day Ended"
                    OngoingTimer.None -> "No Active Timer"
                    is OngoingTimer.Reward -> "Reward ${ongoingEvent.rewardID} Timer in progress"
                    is OngoingTimer.Streak -> "Streak Timer in Progress"
                }
            )
        }

        val remainingSeconds by remember(liveStreakState.ongoingTimer) {
            mutableStateOf(
                when(val ongoingEvent = liveStreakState.ongoingTimer) {
                    is OngoingTimer.DayEnded -> 0L
                    OngoingTimer.None -> 0L
                    is OngoingTimer.Reward -> ongoingEvent.remainingTimeToComplete
                    is OngoingTimer.Streak -> ongoingEvent.remainingTimeToComplete
                }
            )
        }


        Card() {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(text = "LiveStreak Status : ")
                    Text(text = "${liveStreakState.liveStreakEntity?.streakStatus}")

                }
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                liveStreakState.liveStreakEntity?.liveStreakRewards?.forEach {
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp), horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(text = "Reward ${it.rewardID} Status : ")
                        Text(text = "${it.rewardStatus}")
                    }
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                }


                Text(text = ongoingTimerHeading, modifier = Modifier.height(56.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Remaining Time : $remainingSeconds", modifier = Modifier.height(56.dp))

            }
        }

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Button(onClick = {
                liveStreakManager.startStreakTimer()
            }) {
                Text(text = "Start Timer")
            }
            Button(onClick = {
                liveStreakManager.stopStreakTimer()
            }) {
                Text(text = "Stop Timer")
            }
        }


    }


}