package co.touchlab.droidcon.android.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.ProfileDetail
import co.touchlab.droidcon.android.ui.agenda.MyAgenda
import co.touchlab.droidcon.android.ui.feedback.Feedback
import co.touchlab.droidcon.android.ui.schedule.Schedule
import co.touchlab.droidcon.android.ui.sessions.SessionDetail
import co.touchlab.droidcon.android.ui.settings.Settings
import co.touchlab.droidcon.android.ui.sponsors.SponsorDetail
import co.touchlab.droidcon.android.ui.sponsors.SponsorList
import co.touchlab.droidcon.android.viewModel.MainViewModel
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.entity.Sponsor

sealed class MainTab(val route: String, @StringRes val titleRes: Int, @DrawableRes val image: Int) {
    data object Schedule : MainTab("schedule", R.string.schedule_title, R.drawable.menu_schedule)
    data object MyAgenda : MainTab("myAgenda", R.string.my_agenda_title, R.drawable.menu_my_agenda)
    data object Sponsors : MainTab("sponsors", R.string.sponsors_title, R.drawable.menu_sponsor)
    data object Settings : MainTab("settings", R.string.settings_title, R.drawable.menu_settings)
}

sealed class SettingsScreen(val route: String) {
    data object Main : SettingsScreen("settings/main")
    data object About : SettingsScreen("settings/about")
}

sealed class ScheduleScreen(val route: String) {
    data object Main : ScheduleScreen("schedule/main")
    data object SessionDetail : ScheduleScreen("schedule/sessionDetail-{sessionId}") {

        fun createRoute(sessionId: Session.Id) = "schedule/sessionDetail-${sessionId.value}"
    }

    data object SpeakerDetail : ScheduleScreen("schedule/speakerDetail-{speakerId}") {

        fun createRoute(speakerId: Profile.Id) = "schedule/speakerDetail-${speakerId.value}"
    }
}

sealed class SponsorsScreen(val route: String) {
    data object Main : SponsorsScreen("sponsors/main")
    data object Detail : SponsorsScreen("sponsors/detail/{sponsorGroup}-{sponsorName}") {

        fun createRoute(sponsorId: Sponsor.Id) = "sponsors/detail/${sponsorId.group}-${sponsorId.name}"
    }

    data object RepresentativeDetail : ScheduleScreen("sponsors/representativeDetail-{representativeId}") {

        fun createRoute(representativeId: Profile.Id) = "sponsors/representativeDetail-${representativeId.value}"
    }
}

val tabs: List<MainTab> = listOf(MainTab.Schedule, MainTab.MyAgenda, MainTab.Sponsors, MainTab.Settings)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Main(main: MainViewModel) {
    val feedback by main.showFeedback.collectAsState()
    feedback?.let {
        Feedback(it)
    }

    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                tabs.forEach { tab ->
                    BottomNavigationItem(
                        icon = { Icon(painterResource(id = tab.image), contentDescription = null) },
                        label = { Text(text = stringResource(id = tab.titleRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            if (currentDestination?.hierarchy?.any { it.route == tab.route } == true) {
                                return@BottomNavigationItem
                            }
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = MainTab.Schedule.route, Modifier.padding(innerPadding)) {
            navigation(ScheduleScreen.Main.route, MainTab.Schedule.route) {
                composable(ScheduleScreen.Main.route) { Schedule(navController) }
                composable(
                    route = ScheduleScreen.SessionDetail.route,
                    enterTransition = {
                        if (initialState.destination.route == ScheduleScreen.Main.route) {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Companion.Left)
                        } else {
                            fadeIn()
                        }
                    },
                    popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Companion.Up) },
                    exitTransition = {
                        if (targetState.destination.route == ScheduleScreen.Main.route) {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Companion.Right)
                        } else {
                            fadeOut()
                        }
                    }
                ) { backStackEntry ->
                    val sessionId = backStackEntry.arguments?.getString("sessionId")
                    requireNotNull(sessionId) { "Parameter sessionId not found." }
                    SessionDetail(navController, Session.Id(sessionId))
                }
                composable(
                    route = ScheduleScreen.SpeakerDetail.route,
                    enterTransition = {
                        if (initialState.destination.route == ScheduleScreen.SessionDetail.route) {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Companion.Left)
                        } else {
                            fadeIn()
                        }
                    },
                    exitTransition = {
                        if (targetState.destination.route == ScheduleScreen.SessionDetail.route) {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Companion.Right)
                        } else {
                            fadeOut()
                        }
                    }
                ) { backStackEntry ->
                    val speakerId = backStackEntry.arguments?.getString("speakerId")
                    requireNotNull(speakerId) { "Parameter speakerId not found." }
                    ProfileDetail(navController, Profile.Id(speakerId))
                }
            }

            composable(MainTab.MyAgenda.route) { MyAgenda(navController) }

            navigation(SponsorsScreen.Main.route, MainTab.Sponsors.route) {
                composable(SponsorsScreen.Main.route) { SponsorList(navController) }
                composable(
                    route = SponsorsScreen.Detail.route,
                    enterTransition = {
                        if (initialState.destination.route == SponsorsScreen.Main.route) {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Companion.Left)
                        } else {
                            fadeIn()
                        }
                    },
                    exitTransition = {
                        if (targetState.destination.route == SponsorsScreen.Main.route) {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Companion.Right)
                        } else {
                            fadeOut()
                        }
                    }
                ) { backStackEntry ->
                    val sponsorName = requireNotNull(backStackEntry.arguments?.getString("sponsorName")) {
                        "Parameter sponsorName not found."
                    }
                    val sponsorGroup = requireNotNull(backStackEntry.arguments?.getString("sponsorGroup")) {
                        "Parameter sponsorGroup not found."
                    }

                    SponsorDetail(navController, Sponsor.Id(name = sponsorName, group = sponsorGroup))
                }
                composable(
                    route = SponsorsScreen.RepresentativeDetail.route,
                    enterTransition = {
                        if (initialState.destination.route == SponsorsScreen.Detail.route) {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Companion.Left)
                        } else {
                            fadeIn()
                        }
                    },
                    exitTransition = {
                        if (targetState.destination.route == SponsorsScreen.Detail.route) {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Companion.Right)
                        } else {
                            fadeOut()
                        }
                    }
                ) { backStackEntry ->
                    val representativeId = backStackEntry.arguments?.getString("representativeId")
                    requireNotNull(representativeId) { "Parameter representativeId not found." }
                    ProfileDetail(navController, Profile.Id(representativeId))
                }
            }

            navigation(SettingsScreen.Main.route, MainTab.Settings.route) {
                composable(SettingsScreen.Main.route) { Settings(navController) }
            }
        }
    }
}
