package com.android.myaudioplayer.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.myaudioplayer.MediaPlayerViewModel
import com.android.myaudioplayer.presentation.Constants
import com.android.myaudioplayer.presentation.screens.FavMusicScreen
import com.android.myaudioplayer.presentation.screens.HomeScreen
import com.android.myaudioplayer.presentation.screens.MusicDetailsScreen
import com.android.myaudioplayer.presentation.screens.SplashScreen

@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    mediaPlayerViewModel: MediaPlayerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Constants.Destination
    ) {
        composable(
            route = Destinations.HOME_SCREEN_ROUTE,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }
        ) {
            HomeScreen(navController, mediaPlayerViewModel)
        }
        composable(
            route = Destinations.SPLASH_SCREEN,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }
        ) {
            SplashScreen(navController)
        }
        composable(
            route = Destinations.DETAILS_SCREEN_ROUTE,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(700)
                )
            }
        ) {
            MusicDetailsScreen()
        }
        composable(
            route = Destinations.FAV_SCREEN_ROUTE,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }
        ) {
            FavMusicScreen(navController)
        }
    }
}
