package com.android.myaudioplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.ProgressBar
import android.widget.RemoteViews
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.android.myaudioplayer.presentation.Constants
import com.android.myaudioplayer.presentation.components.getAlbumArt
import com.android.myaudioplayer.presentation.screens.AudioData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import kotlin.math.roundToInt


class MediaPlayerService : Service() {
    private val mBinder: Binder = MyMusicServiceBinder()
    var mPlayer: MediaPlayer? = null
    var isPlaying = mutableStateOf(false)
    var selectedAudioFile: MutableState<AudioData?>? = mutableStateOf(null)
    var manuallyPaused = mutableStateOf(false)
    var currentMusicPosition: MutableState<Int> = mutableStateOf(-1)
    var progress = mutableStateOf(0f)
    private var tempList: ArrayList<AudioData> = arrayListOf()
    var audioList: MutableState<List<AudioData>> = mutableStateOf(emptyList())
    private val currentPosition = mutableStateOf("")
    val searchedMusic = mutableStateOf("")
    val itemClicked = mutableStateOf(false)
    var appOnBackGround = mutableStateOf(false)
    private val handler: Handler = Handler()


    @OptIn(ExperimentalStdlibApi::class)
    private val updateProgressTask: Runnable = object : Runnable {
        override fun run() {
            updateProgress() {
            }
            // Schedule the next update
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        handler.post(updateProgressTask);
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serviceScope = CoroutineScope(Dispatchers.IO) // Use an appropriate dispatcher

        serviceScope.launch {
            Log.d("UsedThread", Thread.currentThread().name.toString())
            if (intent != null) {
                when (intent.action) {
                    "ACTION_STOP_PLAYBACK" -> {
                        // Handle the action to pause playback
                        manuallyPaused.value = true
                        pause()
                    }

                    "ACTION_STOP_SERVICE" -> {
                        // Handle the action to stop the service
                        stopService()
                    }

                    "ACTION_PLAY" -> {
                        // Handle the action to start the service
                        val songUri = intent.data // Get the song from the screen
                        val albumBitmap = intent.getStringExtra("albumImage")
                        songUri?.let {
                            setMediaUri(it)
                            play()
                        }
                    }

                    "ACTION_RESUMED" -> {
                        // Handle the action to Play the service
                        play()
                    }

                    "ACTION_NEXT_MUSIC" -> {
                        if (currentMusicPosition.value in 0 until audioList.value.size - 1) {
                            currentMusicPosition.value += 1
                            selectedAudioFile?.value = audioList.value[currentMusicPosition.value]
                            isPlaying.value = false
                            manuallyPaused.value = false
                        }
                        if (appOnBackGround.value) {
                            stopPlaying()
                            selectedAudioFile?.value?.let {
                                val uri = Uri.parse(it.uri)
                                setMediaUri(uri)
                                play()
                            }
                        }
                    }

                    "ACTION_PREVIOUS_MUSIC" -> {
                        if (currentMusicPosition.value in 1 until audioList.value.size) {
                            isPlaying.value = false
                            manuallyPaused.value = false
                            currentMusicPosition.value -= 1
                            selectedAudioFile?.value =
                                audioList.value[currentMusicPosition.value]
                        }
                        if (appOnBackGround.value) {
                            stopPlaying()
                            selectedAudioFile?.value?.let {
                                val uri = Uri.parse(it.uri)
                                setMediaUri(uri)
                                play()
                            }
                        }
                    }
                }
            }

        }
        return START_STICKY
    }

    private var iconNotification: Bitmap? = null
    private var notification: Notification? = null
    private var mNotificationManager: NotificationManager? = null
    private val mNotificationId = 123
    private fun generateForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Get the layouts to use in the custom notification.
            val notificationLayout =
                RemoteViews(packageName, R.layout.custom_music_notification_layout_small)
            val notificationLayoutExpanded =
                RemoteViews(packageName, R.layout.custom_music_notification_layout)


            val intentMainLanding = Intent(this, MainActivity::class.java)
            intentMainLanding.flags =
                Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP // Preserve the current state
            intentMainLanding.action = "ACTION_OPEN_FROM_NOTIFICATION"
            val goToApkPendingIntent = PendingIntent.getActivity(
                this, 0, intentMainLanding,
                PendingIntent.FLAG_IMMUTABLE
            )
            iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

            //Initialize the notificationManager
            if (mNotificationManager == null) {
                mNotificationManager =
                    this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            //Set the notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(mNotificationManager != null)
                mNotificationManager?.createNotificationChannelGroup(
                    NotificationChannelGroup("my_music", "music")
                )
                val notificationChannel = NotificationChannel(
                    "service_channel", "Service Notification",
                    NotificationManager.IMPORTANCE_MIN
                )
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }

            // Create a PendingIntent for stopping the service
            val stopServiceIntent = Intent(this, MediaPlayerService::class.java)
            stopServiceIntent.action = "ACTION_STOP_SERVICE"
            val stopServicePendingIntent =
                PendingIntent.getService(this, 0, stopServiceIntent, PendingIntent.FLAG_IMMUTABLE)


            val builder = NotificationCompat.Builder(this, "service_channel")
            builder.setSmallIcon(R.drawable.music)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setWhen(0)
                .setOnlyAlertOnce(true)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutExpanded)
                .setContentIntent(goToApkPendingIntent)
                .setOngoing(true)
            // Add custom actions for stopping playback and stopping the service
            // Update the notification based on the playback state
            val togglePlaybackIntent = Intent(this, MediaPlayerService::class.java)
            if (isPlaying.value) {
                togglePlaybackIntent.action = "ACTION_STOP_PLAYBACK"
            } else {
                togglePlaybackIntent.action = "ACTION_RESUMED"
            }
            val togglePlaybackPendingIntent =
                PendingIntent.getService(
                    this,
                    0,
                    togglePlaybackIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            val playButtonIcon = if (isPlaying.value) R.drawable.pause else R.drawable.play_button
            notificationLayoutExpanded.setOnClickPendingIntent(
                R.id.playPause,
                togglePlaybackPendingIntent
            )
            //set the play pause image
            notificationLayoutExpanded.setImageViewResource(R.id.playPause, playButtonIcon)

            //Set the album image
            selectedAudioFile?.value?.albumData?.let {
                notificationLayoutExpanded.setImageViewUri(R.id.albumImage, Uri.parse(it))
            }
            selectedAudioFile?.value?.albumData?.let {
                notificationLayout.setImageViewUri(R.id.albumImage, Uri.parse(it))
            }

            //Pending Intent action for album image click
            notificationLayoutExpanded.setOnClickPendingIntent(
                R.id.albumImage, goToApkPendingIntent
            )

            //Set the audio title
            notificationLayoutExpanded.setTextViewText(
                R.id.audioName,
                selectedAudioFile?.value?.title ?: "NA"
            )
            notificationLayout.setTextViewText(
                R.id.audioName,
                selectedAudioFile?.value?.title ?: "NA"
            )

            //Set the author name
            notificationLayoutExpanded.setTextViewText(
                R.id.authorName,
                selectedAudioFile?.value?.artist ?: "NA"
            )
            notificationLayout.setTextViewText(
                R.id.authorName,
                selectedAudioFile?.value?.artist ?: "NA"
            )

            //Next Music Play Pending Intent
            val nextMusicPlayIntent = Intent(this, MediaPlayerService::class.java)
            nextMusicPlayIntent.action = "ACTION_NEXT_MUSIC"
            val nextMusicPlayPendingIntent = PendingIntent.getService(
                this, 0, nextMusicPlayIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            notificationLayoutExpanded.setOnClickPendingIntent(
                R.id.nextBtn,
                nextMusicPlayPendingIntent
            )

            //Previous Music Play Pending Intent
            val previousMusicPlayIntent = Intent(this, MediaPlayerService::class.java)
            previousMusicPlayIntent.action = "ACTION_PREVIOUS_MUSIC"
            val previousMusicPendingIntent = PendingIntent.getService(
                this, 0,
                previousMusicPlayIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            notificationLayoutExpanded.setOnClickPendingIntent(
                R.id.previousBtn, previousMusicPendingIntent
            )

            //Progressbar update
            notificationLayoutExpanded.setProgressBar(
                R.id.progressBar,
                100,
                (progress.value * 100).toInt(),
                false
            )


            val stopServiceAction = NotificationCompat.Action.Builder(
                R.drawable.close,
                "Stop Service",
                stopServicePendingIntent
            ).build()
            builder.addAction(stopServiceAction)

            if (iconNotification != null) {
                builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
            }
            builder.color = resources.getColor(R.color.purple_200)
            notification = builder.build()
            startForeground(mNotificationId, notification)

            mNotificationManager?.notify(mNotificationId, notification)

        }
    }

    private fun stopService() {
        // Implement logic to stop the service here
        // For example:
        stopPlaying()
        mPlayer = null
        isPlaying.value = false
        itemClicked.value = false
        stopForeground(true)
        stopSelf()
        if (Constants.isOpenFromNotification) {
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    inner class MyMusicServiceBinder : Binder() {
        fun getService(): MediaPlayerService {
            return this@MediaPlayerService
        }
    }


    fun getCurrentPosition(): String {
        return formatDuration(mPlayer?.currentPosition ?: 0)
    }

    private fun formatDuration(currentPosition: Int): String {
        val currentSeconds = currentPosition / 1000
        val currentMinutes = currentSeconds / 60
        val currentHours = currentMinutes / 60
        val formattedCurrentHours = currentHours % 24
        val formattedCurrentMinutes = currentMinutes % 60
        val formattedCurrentSeconds = currentSeconds % 60

        return String.format(
            "%02d:%02d:%02d",
            formattedCurrentHours,
            formattedCurrentMinutes,
            formattedCurrentSeconds
        )
    }

    private fun formatTotalDuration(duration: Int): String {
        val totalSeconds = duration / 1000
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60
        val formattedTotalHours = totalHours % 24
        val formattedTotalMinutes = totalMinutes % 60
        val formattedTotalSeconds = totalSeconds % 60
        return String.format(
            "%02d:%02d:%02d",
            formattedTotalHours,
            formattedTotalMinutes,
            formattedTotalSeconds
        )
    }

    fun getDuration(): String {
        return formatTotalDuration(mPlayer?.duration ?: 0)
    }


    fun play() {
        mPlayer?.start()
        isPlaying.value = true
        manuallyPaused.value = false
        generateForegroundNotification() // Update the notification
    }

    fun updateProgress(playNextOnComplete: () -> Unit) {
        val serviceScope = CoroutineScope(Dispatchers.IO) // Use an appropriate dispatcher
        serviceScope.launch {

            try {
                // Implement code to update progress
                // You need to set progress based on the current playback position.
                mPlayer?.let {
                    if (it.isPlaying) {
                        progress.value = (it.currentPosition.toFloat() / it.duration.toFloat())
                    }
                }
                mPlayer?.setOnCompletionListener {
                    if (currentMusicPosition.value + 1 == audioList.value.size) {
                        manuallyPaused.value = true
                        pause()
                        return@setOnCompletionListener
                    }
                    currentMusicPosition.value += 1
                    selectedAudioFile?.value = audioList.value[currentMusicPosition.value]
                    isPlaying.value = false
                    manuallyPaused.value = false
                    playNextOnComplete()
                    if (appOnBackGround.value) {
                        stopPlaying()
                        selectedAudioFile?.value?.let {
                            val uri = Uri.parse(it.uri)
                            setMediaUri(uri)
                            play()
                        }
                    }
                }
                mPlayer?.let {
                    generateForegroundNotification()
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    fun seekToPosition(position: Int) {
            mPlayer?.seekTo(position)
            this@MediaPlayerService.mPlayer?.let {
                currentPosition.value =
                    formatDuration(mPlayer?.currentPosition ?: 0)
                progress.value = (it.currentPosition.toFloat() / it.duration.toFloat())
            }
    }

    fun pause() {
        mPlayer?.pause()
        isPlaying.value = false
        generateForegroundNotification() // Update the notification
    }

    fun setMediaUri(uri: Uri) {
        mPlayer = MediaPlayer.create(applicationContext, uri)
    }

    fun stopPlaying() {
        mPlayer?.let {
            it.stop()
            it.release()
        }
    }

    fun getSongsFromDevice(
        context: Context
    ) {
        val serviceScope = CoroutineScope(Dispatchers.IO) // Use an appropriate dispatcher
        serviceScope.launch {
            val projection = arrayOf(
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,  //For path
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )
            cursor?.use {
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                while (cursor.moveToNext()) {
                    val album = cursor.getString(albumColumn)
                    val title = cursor.getString(titleColumn)
                    val duration = cursor.getString(durationColumn)
                    val path = cursor.getString(pathColumn)
                    val artist = cursor.getString(artistColumn)
                    val id = cursor.getLong(idColumn)
                    val albumIdc = cursor.getLong(albumIdColumn).toString()
                    val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        .buildUpon()
                        .appendPath(id.toString())
                        .build()
//                    val albumData = getAlbumArt(path)
                    val artUri = Uri.parse("content://media/external/audio/albumart")
                    val albumArtUri = Uri.withAppendedPath(artUri, albumIdc).toString()
                    tempList.add(
                        AudioData(path, title, artist, album, duration, contentUri.toString(), albumArtUri)
                    )
                }
                audioList.value = tempList
            }
        }
    }

}

