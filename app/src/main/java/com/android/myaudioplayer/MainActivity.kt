package com.android.myaudioplayer

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.android.myaudioplayer.presentation.Constants
import com.android.myaudioplayer.presentation.Constants.isOpenFromNotification
import com.android.myaudioplayer.presentation.navigation.Destinations
import com.android.myaudioplayer.presentation.navigation.SetUpNavGraph
import com.android.myaudioplayer.ui.theme.MyAudioPlayerTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    val mediaPlayerViewModel: MediaPlayerViewModel by viewModels()
    var mediaPlayerService: MediaPlayerService? = null
    var mBound = false
    private lateinit var mServiceConnection: ServiceConnection

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyAudioPlayerTheme {
                // A surface container using the 'background' color from the theme
                val bindDone = rememberSaveable {
                    mutableStateOf(false)
                }
                mServiceConnection = object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
                        val mServiceBinder = iBinder as MediaPlayerService.MyMusicServiceBinder
                        mediaPlayerService = mServiceBinder.getService()
                        bindDone.value = true
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {
                        bindDone.value = false
                    }
                }
                val serviceIntent = Intent(this, MediaPlayerService::class.java)
                bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE)
                if (intent.action == "ACTION_OPEN_FROM_NOTIFICATION") {
                    Constants.Destination = Destinations.DETAILS_SCREEN_ROUTE
                    isOpenFromNotification = true
                } else {
                    Constants.Destination = Destinations.HOME_SCREEN_ROUTE
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (bindDone.value) {
                        mBound = true
                        val navHostController = rememberAnimatedNavController()
                        SetUpNavGraph(navController = navHostController, mediaPlayerViewModel)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
    }
}