package com.android.myaudioplayer.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.myaudioplayer.R
import com.android.myaudioplayer.presentation.navigation.Destinations
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(key1 = true){
        delay(500)
        navController.navigate(Destinations.HOME_SCREEN_ROUTE){
            this.popUpTo(Destinations.SPLASH_SCREEN){
                inclusive=true
            }
        }
    }

    Column(modifier=Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(id = R.drawable.music), contentDescription = "",
            modifier=Modifier.size(80.dp))
    }

}