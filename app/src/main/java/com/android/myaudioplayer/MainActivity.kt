package com.android.myaudioplayer

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.android.myaudioplayer.presentation.Constants
import com.android.myaudioplayer.presentation.Constants.isOpenFromNotification
import com.android.myaudioplayer.presentation.navigation.Destinations
import com.android.myaudioplayer.presentation.navigation.SetUpNavGraph
import com.android.myaudioplayer.ui.theme.MyAudioPlayerTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {
    val mediaPlayerViewModel: MediaPlayerViewModel by viewModels()
    private val permissionGranted= mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            )
        }
        requestPermissions(
            permissionList,
            100
        )

        setContent {
            MyAudioPlayerTheme(darkTheme = true) {
                // A surface container using the 'background' color from the theme
                val bindDone = rememberSaveable {
                    mutableStateOf(false)
                }
                mediaPlayerViewModel.mServiceConnection = object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
                        val mServiceBinder = iBinder as MediaPlayerService.MyMusicServiceBinder
                        mediaPlayerViewModel.mediaPlayerService = mServiceBinder.getService()
                        bindDone.value = true
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {
                        bindDone.value = false
                    }
                }
                val serviceIntent = Intent(this, MediaPlayerService::class.java)
                bindService(
                    serviceIntent,
                    mediaPlayerViewModel.mServiceConnection,
                    BIND_AUTO_CREATE
                )
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
                    if (bindDone.value && permissionGranted.value) {
                        mediaPlayerViewModel.mBound = true
                        val navHostController = rememberNavController()
                        SetUpNavGraph(navController = navHostController, mediaPlayerViewModel)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mediaPlayerViewModel.mediaPlayerService?.appOnBackGround?.value = false
    }

    override fun onStop() {
        super.onStop()
        mediaPlayerViewModel.mediaPlayerService?.appOnBackGround?.value = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionGranted.value=true
    }

}