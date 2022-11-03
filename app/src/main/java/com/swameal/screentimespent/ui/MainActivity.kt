package com.swameal.screentimespent.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.swameal.screentimespent.data.db.ScreenTimeEvent
import com.swameal.screentimespent.domain.LiveStreamInfo
import com.swameal.screentimespent.domain.LocalLiveStreamInfo
import com.swameal.screentimespent.ui.navigation.NavigationGraph
import com.swameal.screentimespent.ui.theme.ScreenTimeSpentTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
@OptIn(ExperimentalMaterialNavigationApi::class)
class MainActivity : ComponentActivity() {


    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScreenTimeSpentTheme {

                val bottomSheetNavigator = rememberBottomSheetNavigator()

                val navController = rememberNavController(bottomSheetNavigator)

                val liveStreamInfo by remember {
                    mutableStateOf(
                        LiveStreamInfo(
                            liveSessionId = UUID.randomUUID().toString(),
                        )
                    )
                }

                val onScreenTimeEvent: (ScreenTimeEvent) -> Unit = {
                    mainViewModel.trackScreenTimeEvent(it)
                }

                CompositionLocalProvider(LocalLiveStreamInfo provides liveStreamInfo) {

                    ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {

                        NavHost(
                            navController = navController,
                            startDestination = NavigationGraph.HOME
                        ) {

                            composable(NavigationGraph.HOME) {
                                BaseScreen(
                                    screen = {
                                        HomeScreen(
                                            navigateToDetails = {
                                                navController.navigate(NavigationGraph.DETAILS)
                                            },
                                            navigateToMiniInfo = {
                                                navController.navigate(NavigationGraph.MINI_INFO)
                                            }
                                        )
                                    },
                                    backStackEntry = it,
                                    onScreenTimeEvent = onScreenTimeEvent
                                )
                            }

                            composable(NavigationGraph.DETAILS) {
                                BaseScreen(
                                    screen = {
                                        DetailsScreen {
                                            navController.popBackStack()
                                        }
                                    },
                                    backStackEntry = it,
                                    onScreenTimeEvent = onScreenTimeEvent
                                )
                            }

                            bottomSheet(NavigationGraph.MINI_INFO) {
                                BaseScreen(
                                    screen = {
                                        BottomSheetScreen()
                                    },
                                    backStackEntry = it,
                                    onScreenTimeEvent = onScreenTimeEvent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}






