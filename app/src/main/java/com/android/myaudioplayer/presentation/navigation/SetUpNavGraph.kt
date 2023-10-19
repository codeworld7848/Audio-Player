package com.android.myaudioplayer.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.android.myaudioplayer.MediaPlayerViewModel
import com.android.myaudioplayer.presentation.Constants
import com.android.myaudioplayer.presentation.screens.HomeScreen
import com.android.myaudioplayer.presentation.screens.MusicDetailsScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun SetUpNavGraph(
    navController: NavHostController,
    mediaPlayerViewModel: MediaPlayerViewModel
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Constants.Destination
    ) {
        composable(
            route = Destinations.HOME_SCREEN_ROUTE,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { 1500 },
                    animationSpec = tween(1500)
                ) + fadeIn(animationSpec = tween(1000))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -1500 },
                    animationSpec = tween(1500)
                ) + fadeOut(animationSpec = tween(1500))
            },
            popEnterTransition = {
                popEnterTransition()
            }
        ) {
            HomeScreen(navController, mediaPlayerViewModel)
        }
        composable(
            route = Destinations.DETAILS_SCREEN_ROUTE,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { 1500 },
                    animationSpec = tween(1500)
                ) + fadeIn(animationSpec = tween(1000))
            }
        ) {
            MusicDetailsScreen()
        }
    }
}

private fun popEnterTransition() =
    slideInHorizontally(
        initialOffsetX = { -300 },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))


private fun exitTransition() =
    slideOutHorizontally(
        targetOffsetX = { -300 },
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))


private fun enterTransition() =
    slideInHorizontally(
        initialOffsetX = { 300 },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))